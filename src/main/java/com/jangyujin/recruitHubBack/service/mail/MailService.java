package com.jangyujin.recruitHubBack.service.mail;

import com.jangyujin.recruitHubBack.model.User;
import com.jangyujin.recruitHubBack.repository.UserRepository;
import com.jangyujin.recruitHubBack.service.redis.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
    //private final JavaMailSender javaMailSender;
    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisService redisService;

    @Value("${spring.mail.username}")
    private static String senderEmail;

    //@Value("${spring.mail.auth-code-expiration-millis}")
    //private long authCodeExpirationMillis;

    public Boolean checkMail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isPresent()) { // optional null 처리
            return true;
        }
        return false;
    }


    // 6자리 랜덤 코드 생성
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public void sendVerificationEmail(String to, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("이메일 인증 코드");
        helper.setText("인증 코드: <b>" + code + "</b>", true);

        mailSender.send(message);

        // 이메일 인증 요청 시 인증 번호 Redis에 저장 (key = "AuthCode " + Email / value = AuthCode) 유효 시간(5분)동안
        redisService.setDataExpire(code, to, 60 * 5L);
    }

    public boolean checkCode(String code, String email) {
        //code값으로 value(이메일) 가지고오기
        String value = redisService.getData(code);

        if(value == "false") {return false;}
        else {if(value.equals(email)) return true;}

        return false;
    }

}
