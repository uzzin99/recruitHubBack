package com.jangyujin.recruitHubBack.controller.auth;

import com.jangyujin.recruitHubBack.config.auth.PrincipalDetails;
import com.jangyujin.recruitHubBack.dto.UserRequest;
import com.jangyujin.recruitHubBack.model.User;
import com.jangyujin.recruitHubBack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm"; // 로그인 폼 페이지로 이동
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm"; // 회원가입 폼 페이지로 이동
    }

    @PostMapping("/join")
    public String join(UserRequest.JoinDto requestDto) {
        //DTO → Entity 변환
        User user = User.builder()
                .userid(requestDto.getUserid())
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword())) // 암호화된 비밀번호 저장
                .role("ROLE_USER")
                .build();
        userRepository.save(user);

        return "redirect:/loginForm"; // 로그인 폼 페이지로 리다이렉트
    }

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal PrincipalDetails principal) {
        model.addAttribute("principal", principal);
        return "index";
    }

    @GetMapping("/error")
    public String error(){
        return "error";
    }
}
