package com.jangyujin.recruitHubBack.apiController;

import com.jangyujin.recruitHubBack.config.auth.PrincipalDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/api")
public class AuthApiController {
    @GetMapping("/test/oauth/login")
    public String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth) {
        // OAuth2 로그인 테스트
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        return "OAuth 세션 정보 확인하기";
    }

    @GetMapping("/test/login")
    public String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) {
        // DI를 통해 주입된 authentication 객체에서 principal을 가져와 PrincipalDetails로 캐스팅하여 사용
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return "세션 정보 확인하기";
    }

    @GetMapping("/user")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal PrincipalDetails principal){
        if(principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        return ResponseEntity.ok(principal.getUser());
    }

}
