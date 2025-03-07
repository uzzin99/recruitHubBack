package com.jangyujin.recruitHubBack.apiController.mail;

import com.jangyujin.recruitHubBack.model.User;
import com.jangyujin.recruitHubBack.service.mail.MailService;
import com.jangyujin.recruitHubBack.service.redis.RedisService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/email/api")
public class MailApiController {

    @Autowired
    private MailService mailService;


    /**
     * 비밀번호찾기 > 인증코드 전송
     *
     * @param request the request
     * @return the map
     */
    @PostMapping("/send-code")
    public Map<String, String> sendVerificationCode(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            response.put("message", "이메일을 입력해주세요.");
            return response;
        }

        //boolean passMail = mailService.checkMail(email);

        Optional<User> user = mailService.checkMail(email);

        if (!user.isPresent()) {
            response.put("message", "가입된 이메일이 없습니다.");
            return response;
        } else {
            if (user.get().getUserid() == null || user.get().getUserid().isEmpty()) {
                response.put("message", "이메일로 가입된 아이디가 없습니다.");
                return response;
            }
        }

        try {
            String code = mailService.generateVerificationCode();
            mailService.sendVerificationEmail(email, code);
            response.put("message", "인증 코드가 전송되었습니다.");
            response.put("flag", "success");
        } catch (MessagingException e) {
            response.put("message", "메일 전송 실패: " + e.getMessage());
            response.put("flag", "fail");
        }

        return response;
    }

    /**
     * 비밀번호 찾기 > 인증코드 체크 로직
     *
     * @param request the request
     * @return the response entity
     */
    @PostMapping("/check-code")
    public ResponseEntity<String> checkCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String email = request.get("email");

        boolean pass = mailService.checkCode(code, email);

        if (pass) {
            return new ResponseEntity<>("인증에 성공했습니다.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("인증에 실패했습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
