package com.ybcharlog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybcharlog.config.handler.Http401Handler;
import com.ybcharlog.config.handler.Http403Handler;
import com.ybcharlog.config.handler.LoginFailHandler;
import com.ybcharlog.api.domain.user.User;
import com.ybcharlog.filter.ExceptionHandlerFilter;
import com.ybcharlog.filter.TokenFilter;
import com.ybcharlog.api.repository.user.UserRepository;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsUtils;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final TokenFilter tokenFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final ObjectMapper objectMapper;

    private static final String[] permitAllUrl = {
            "/",
            "/index.html",
            "/favicon.ico",
            "/robots.txt",
            "/posts/**",
            "/api/posts/**",
            "/assets/**", // static 경로 추가
            "/public/**",
            "/public/fonts/**",
            "/fonts/**",
            "/css/**",
            "/images/**",
            "/js/**",
            "/enums/**",
            "/join/verification-url",
            "/view/users/change-password",
            "/gProfile.jpeg", // static 경로 추가
//            "/api/**",
//            "/static/**", // static 경로 추가
    };

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring().requestMatchers(String.valueOf(PathRequest.toStaticResources().atCommonLocations()))
                .requestMatchers(
                    permitAllUrl
                );
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        log.warn("accessDeniedHandler");
        return (request, response, e) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("ACCESS DENIED");
            response.getWriter().flush();
            response.getWriter().close();
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, e) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("UNAUTHORIZED");
            response.getWriter().flush();
            response.getWriter().close();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf().disable().cors().disable()
                .authorizeHttpRequests(req -> req
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                                .requestMatchers(permitAllUrl).permitAll()
                                .requestMatchers("/api/posts/save", "/api/posts/update/{postId}", "/api/posts/delete/{postId}",
                                        "/api/posts/thumbnail/image", "/api/files/images", "/api/category/save", "/api/category/delete/**").hasRole("ROLE_ADMIN")
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                .anyRequest().permitAll()
                )
                .formLogin()
                .failureHandler(new LoginFailHandler(objectMapper))
                .disable()
                .headers()
                    .frameOptions()
                    .sameOrigin()
                .and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout"))
                    .logoutSuccessHandler(
                        (httpServletRequest, httpServletResponse, authentication) -> {
                            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                            httpServletResponse.setHeader("Access-Control-Allow-Origin" , "*");
                        })
                    .logoutSuccessUrl("/api/posts/list?page=1&size=12")
                    .invalidateHttpSession(true)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling(e -> {
                    e.accessDeniedHandler(new Http403Handler(objectMapper));
                    e.authenticationEntryPoint(new Http401Handler(objectMapper));
                })
//                .accessDeniedHandler(accessDeniedHandler())
//                .authenticationEntryPoint(authenticationEntryPoint())
//                .and()
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, TokenFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException(username + "을 찾을 수 없습니다."));
            return new UserPrinciple(user);
        };
    }

}