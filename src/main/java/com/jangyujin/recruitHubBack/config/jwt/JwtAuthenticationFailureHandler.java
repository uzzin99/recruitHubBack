package com.jangyujin.recruitHubBack.config.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        // 실패 원인 로깅
        log.error("❌ OAuth2 로그인 실패: {}", exception.getMessage());

        // 에러 메시지 UTF-8 인코딩 (URL에서 안전하게 사용하기 위해)
        String errorMessage = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);

        // 실패 시 리다이렉트할 URL (프론트엔드 로그인 페이지로 이동)
        String failureRedirectUrl = ""+baseUrl+":8080/login?error=" + errorMessage;

        // 클라이언트에게 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, failureRedirectUrl);
    }
}

