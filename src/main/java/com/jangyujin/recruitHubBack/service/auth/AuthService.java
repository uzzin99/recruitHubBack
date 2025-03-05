package com.jangyujin.recruitHubBack.service.auth;

import com.jangyujin.recruitHubBack.dto.UserRequest;
import com.jangyujin.recruitHubBack.dto.UserResponse;
import com.jangyujin.recruitHubBack.model.User;
import com.jangyujin.recruitHubBack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //@Transactional
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

    public UserResponse findUserId(String username, String phone) {
        Optional<UserResponse> user = userRepository.findByUsernameAndPhone(username, phone);
        return user.orElse(null); // 사용자가 존재하지 않으면 null 반환
    }
}
