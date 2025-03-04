package com.jangyujin.recruitHubBack.apiController.auth;

import com.jangyujin.recruitHubBack.config.auth.PrincipalDetails;
import com.jangyujin.recruitHubBack.dto.UserRequest;
import com.jangyujin.recruitHubBack.dto.UserResponse;
import com.jangyujin.recruitHubBack.repository.UserRepository;
import com.jangyujin.recruitHubBack.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/api")
@RequiredArgsConstructor
public class AuthApiController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/join")
    public ResponseEntity<String> joinUser(@RequestBody  UserRequest.JoinDto requestDto) { //@RequestBody를 사용하여 JSON 데이터를 JoinDto로 매핑
        try {
            authService.joinUser(requestDto);
            return new ResponseEntity<>("회원가입이 완료되었습니다.", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user/find-id")
    public ResponseEntity<?> findUserId(@RequestBody UserRequest.FindUserDto findUserDto){
        UserResponse user = authService.findUserId(findUserDto.getUsername(), findUserDto.getPhone());
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자가 존재하지 않습니다.");
        }
    }

    //@PostMapping("/user/reset-password")

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
