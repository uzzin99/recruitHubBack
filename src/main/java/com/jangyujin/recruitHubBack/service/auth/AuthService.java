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
