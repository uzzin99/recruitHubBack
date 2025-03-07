package com.jangyujin.recruitHubBack.service.auth;

import com.jangyujin.recruitHubBack.config.jwt.JwtToken;
import com.jangyujin.recruitHubBack.config.jwt.JwtTokenProvider;
import com.jangyujin.recruitHubBack.dto.UserRequest;
import com.jangyujin.recruitHubBack.dto.UserResponse;
import com.jangyujin.recruitHubBack.model.User;
import com.jangyujin.recruitHubBack.repository.UserRepository;
import com.jangyujin.recruitHubBack.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

/**
 * AUTH SERVICE
 * 사용자 인증, 권한에 관한 서비스 입니다.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisService redisService;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 일반 로그인 > 로그인
     *
     * @param username the username
     * @param password the password
     * @return the jwt token
     */
    @Transactional
    public JwtToken signIn(String username, String password) {
        // 1. username + password 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 2. 실제 거증 authentication() 메서드를 통해 요청된 User에 대한 검증 진행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        // accessToken
        JwtToken accessToken = jwtTokenProvider.createAccessToken(authentication);
        // refreshToken
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 4. redis에 refreshToken 저장
        redisService.saveRefreshToken(username, refreshToken);


        return accessToken;
    }

//    public JwtToken refreshAccessToken(String refreshToken, String username) {
//        String storedRefreshToken = redisService.getRefreshToken(refreshToken);
//
//        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
//            throw new RuntimeException("Invalid or expired refresh token");
//        }
//
//        return jwtTokenProvider.createAccessToken();
//    }

    /**
     * 로그인 > 비밀번호 찾기 > 비밀번호 재설정
     *
     * @param resetPwdDto the reset pwd dto
     */
    public void resetPwd(@RequestBody UserRequest.ResetPwdDto resetPwdDto) {
        if (resetPwdDto.getEmail() == null||resetPwdDto.getEmail().isEmpty()){
            throw new IllegalArgumentException("이메일을 입력해주세요");
        }

        Optional<User> userOptional = userRepository.findByEmail(resetPwdDto.getEmail());

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다.");
        }

        User user = userOptional.get();

        if (user.getUserid().isEmpty()) {
            throw new IllegalArgumentException("해당 이메일의 아이디를 찾을 수 없습니다.");
        }

        if (!resetPwdDto.getPwd().equals(resetPwdDto.getConfirmPwd())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        String encPassword = passwordEncoder.encode(resetPwdDto.getPwd());

        user.setPassword(encPassword);
        userRepository.save(user);
    }

    /**
     * 로그인 > 회원가입
     *
     * @param joinDto the join dto
     */
    @Transactional
    public void joinUser(@RequestBody UserRequest.JoinDto joinDto) {
        // 유효성 검사 (아이디 중복, 이메일 형식 등)
        if (userRepository.findByUserid(joinDto.getUserid()) != null) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }

        User user = User.builder()
                .userid(joinDto.getUserid())
                .username(joinDto.getUsername())
                .email(joinDto.getEmail())
                .password(passwordEncoder.encode(joinDto.getPassword())) // 암호화된 비밀번호 저장
                .role("ROLE_USER")
                .phone(joinDto.getPhone())
                .build();
        userRepository.save(user);
    }

    /**
     * 로그인 > 아이디 찾기
     *
     * @param username the username
     * @param phone    the phone
     * @return the user response
     */
    public UserResponse findUserId(String username, String phone) {
        Optional<UserResponse> user = userRepository.findByUsernameAndPhone(username, phone);
        return user.orElse(null); // 사용자가 존재하지 않으면 null 반환
    }
}
