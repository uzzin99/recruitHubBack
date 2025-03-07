package com.jangyujin.recruitHubBack.service.redis;

import com.jangyujin.recruitHubBack.config.jwt.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    // key를 통해 value 리턴
    public String getData(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        //return valueOperations.get(key);
        // 🔹 Null 값 처리 (Optional 사용)
        String value = valueOperations.get(key); // Redis에서 데이터 조회
        return Optional.ofNullable(value).orElse("false");
    }

    public void setData(String key, String value) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    // 유효 시간 동안 (key, value) 저장
    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    // 삭제
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    // 로그인 시 refreshToken 저장
    public void saveRefreshToken(String username, String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(username, refreshToken, 7, TimeUnit.DAYS);
    }

    // Reids에서 refreshToken 가져오기
    public String getRefreshToken(String username) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(username);
    }

    // RefreshToken 삭제 (로그아웃 시)
    public void deleteRefreshToken(String username) {
        redisTemplate.delete(username);
    }


}
