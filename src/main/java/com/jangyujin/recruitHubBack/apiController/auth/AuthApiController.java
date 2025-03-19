package com.jangyujin.recruitHubBack.apiController.auth;

import com.jangyujin.recruitHubBack.config.auth.PrincipalDetails;
import com.jangyujin.recruitHubBack.config.jwt.JwtToken;
import com.jangyujin.recruitHubBack.dto.UserRequest;
import com.jangyujin.recruitHubBack.dto.UserResponse;
import com.jangyujin.recruitHubBack.repository.UserRepository;
import com.jangyujin.recruitHubBack.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * AUTH API CONTROLLER
 * 사용자 인증, 권한에 관한 컨트롤러 입니다.
 */
@RestController
@RequestMapping("/auth/api")
@RequiredArgsConstructor
public class AuthApiController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody UserRequest.LoginDto loginDto) {

        String username = loginDto.getUsername();
        String password = loginDto.getPassword();

        JwtToken jwtToken = authService.signIn(username, password);

        return jwtToken;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshToken, @RequestParam String username) {
        try {
            //JwtToken newAccessToken = authService.refreshAccessToken(refreshToken, username);
            //System.out.println("11111"+newAccessToken);
            //return ResponseEntity.ok().body(newAccessToken);
            return new ResponseEntity<>("성공", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 로그인 > 비밀번호 찾기 > 비밀번호 재설정
     *
     * @param resetPwdDto the reset pwd dto
     * @return the response entity
     */
    @PostMapping("/user/pwd")
    public ResponseEntity<String> resetPwd(@RequestBody UserRequest.ResetPwdDto resetPwdDto) {
        try {
            authService.resetPwd(resetPwdDto);
            return new ResponseEntity<>("비밀번호 재설정이 완료되었습니다", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("서버 오류가 발생했습니다", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 로그인 > 회원가입
     *
     * @param requestDto the request dto
     * @return the response entity
     */
    @PostMapping("/join")
    public ResponseEntity<String> joinUser(@RequestBody  UserRequest.JoinDto requestDto) { //@RequestBody를 사용하여 JSON 데이터를 JoinDto로 매핑
        try {
            authService.joinUser(requestDto);
            return new ResponseEntity<>("회원가입이 완료되었습니다.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 로그인 > 아이디 찾기
     *
     * @param findUserDto the find user dto
     * @return the response entity
     */
    @PostMapping("/user/find-id")
    public ResponseEntity<?> findUserId(@RequestBody UserRequest.FindUserDto findUserDto){
        UserResponse user = authService.findUserId(findUserDto.getUsername(), findUserDto.getPhone());
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자가 존재하지 않습니다.");
        }
    }
}
