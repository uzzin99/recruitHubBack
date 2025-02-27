package com.jangyujin.recruitHubBack.controller.mail;

import com.jangyujin.recruitHubBack.service.mail.MailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/email/api")
public class MailController {

    @Autowired
    MailService mailService;

    @PostMapping("/send-code")
    public Map<String, String> requestAuthCode(@RequestBody Map<String, String> request) throws MessagingException {
        System.out.println("00000");
        Map<String, String> response = new HashMap<>();
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            response.put("message", "이메일을 입력해주세요.");
            return response;
        }

        boolean isPass = mailService.sendSimpleMessage(email);

        System.out.println("000000"+ isPass);
        if(isPass) {
            response.put("message", "인증 코드가 전송되었습니다.");
        } else{
            response.put("message", "메일 전송 실패: " );
        }

        return response;
//        boolean isPass = mailService.sendSimpleMessage(email);
//        System.out.println("11111"+isPass);
//        return isPass ? ResponseEntity.status(HttpStatus.OK).body("인증코드가 전송되었습니다") : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 코드 발급에 실패하였습니다.");
    }
}
