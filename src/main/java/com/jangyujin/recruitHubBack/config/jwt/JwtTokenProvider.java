package com.jangyujin.recruitHubBack.config.jwt;

import com.jangyujin.recruitHubBack.config.auth.PrincipalDetails;
import com.jangyujin.recruitHubBack.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
//@RequiredArgsConstructor
@Getter
public class JwtTokenProvider {

    @Value("${jwt.key}")
    private String secretKey;
    private final long validityInMillisecond = 3600000;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes()); // ✅ SecretKey 객체 생성
    }

    public String createToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMillisecond);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJwt(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @param token
     * @return 사용자 정보
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJwt(token).getBody();

        String email = claims.getSubject();

        //UserDetails userDetails = new User(email, "", List.of(new SimpleGrantedAuthority("ROLE_" + claims.get("role"))));

        //return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
        //return new PrincipalDetails(userEntity, oAuth2User.getAttributes());

        if (claims.get("role") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }
        List<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("role").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User user = new User();
        user.setEmail(email);
        user.setId(Long.parseLong(claims.get("id").toString()));
        user.setRole(claims.get("role").toString());
        PrincipalDetails principal = new PrincipalDetails(user);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}
