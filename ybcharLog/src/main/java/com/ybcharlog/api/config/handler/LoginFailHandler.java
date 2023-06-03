package com.ybcharlog.api.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ybcharlog.api.ResponseDto.common.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class LoginFailHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
      log.error("[인증 오류] 아이디 혹은 비밀번호가 일치하지 않습니다.");

      ErrorResponse errorResponse = ErrorResponse.builder()
              .code("401")
              .message("아이디 혹은 비밀번호가 일치하지 않습니다.")
              .build();

      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
