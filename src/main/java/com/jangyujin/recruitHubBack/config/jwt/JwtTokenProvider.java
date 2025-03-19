package com.jangyujin.recruitHubBack.config.jwt;

import com.jangyujin.recruitHubBack.config.auth.PrincipalDetails;
import com.jangyujin.recruitHubBack.model.User;
import com.jangyujin.recruitHubBack.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Jwt token provider.
 */
@Component
//@RequiredArgsConstructor
@Getter
public class JwtTokenProvider {

    @Value("${jwt.key}")
    private String secretKey;
    private final long validityInMillisecond = 3600000;

    @Autowired
    private UserRepository userRepository;

    /**
     * 일반 로그인 JWT 생성 (accessToken)
     *
     * @param authentication the authentication
     * @return the jwt token
     */
    public JwtToken createAccessToken(Authentication authentication) {
        // 권한  가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date().getTime());
        // AccessToken 생성
        Date accessTokenExpiresIn = new Date(now + 86400000);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }

    public String createRefreshToken(Authentication authentication) {
        // refreshToken 생성
        long now = (new Date().getTime());

        Date refreshTokenExpiresIn = new Date(now + 604800000);

        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return refreshToken;

    }

    /**
     * 소셜 로그인 JWT 토큰 생성
     *
     * @param email the email
     * @param role  the role
     * @return the string
     */
    public String createToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("auth", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * JWT Token 유효성 검사
     *
     * @param token the token
     * @return the boolean
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT Token 복호화해서, 토큰에 들어있는 정보를 꺼내는 메서드
     *
     * @param token the token
     * @return 사용자 정보
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        String username = claims.getSubject();

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }
        List<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        User user;
        if(username.contains("@")) {
             user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("NOT found" + username));
        } else {
            user = userRepository.findByUserid(username)
                    .orElseThrow(() -> new UsernameNotFoundException("NOT found" + username));
        }

        PrincipalDetails principal = new PrincipalDetails(user);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);

    }
}
