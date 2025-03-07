package com.jangyujin.recruitHubBack.apiController.user;

import com.jangyujin.recruitHubBack.config.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/api")
@RequiredArgsConstructor
public class UserApiController {

    /**
     * 로그인 성공 후, 사용자 정보 조회
     *
     * @param principal the principal
     * @return the response entity
     */
    @GetMapping("/userInfo")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal PrincipalDetails principal){
        if(principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        return ResponseEntity.ok(principal.getUser());
    }
}
