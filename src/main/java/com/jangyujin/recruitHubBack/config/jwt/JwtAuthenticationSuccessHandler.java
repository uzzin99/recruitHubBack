package com.jangyujin.recruitHubBack.config.jwt;

import com.jangyujin.recruitHubBack.config.auth.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        //JWT 토큰 생성
        String jwtToken = jwtTokenProvider.createToken(principalDetails.getUser().getEmail(), principalDetails.getUser().getRole());

        //클라이언트에게 JWT 전달
        //response.setHeader("Authorization", "Bearer " + jwtToken);
        response.sendRedirect(""+baseUrl+":3000/oauth2/success?token=" + jwtToken);
        //response.sendRedirect("http://localhost:8080/");
    }
}
