package com.ybcharlog.config;

import com.ybcharlog.common.util.EncryptUtil;
import com.ybcharlog.common.util.JwtUtil;
import com.ybcharlog.common.util.ProfileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.UnsupportedEncodingException;

@Configuration
@RequiredArgsConstructor
public class InitBeanConfig {

    @Value("${util.encrypt.secretKey}")
    private String encSecretKey;

    @Value("${util.jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${util.jwt.refreshKey}")
    private String jwtReFreshKey;

    private final Environment env;

    @Bean
    public ProfileUtil profileUtil() {
        return new ProfileUtil(this.env);
    }

    @Bean
    public EncryptUtil encryptUtil() throws UnsupportedEncodingException {
        return new EncryptUtil(encSecretKey);
    }

    @Bean
    public JwtUtil jwtUtil() throws UnsupportedEncodingException {
        return new JwtUtil(jwtSecretKey, jwtReFreshKey);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
