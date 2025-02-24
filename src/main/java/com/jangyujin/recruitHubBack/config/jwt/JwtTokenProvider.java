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
import java.util.Optional;
import java.util.stream.Collectors;

@Component
//@RequiredArgsConstructor
@Getter
public class JwtTokenProvider {

    @Value("${jwt.key}")
    private String secretKey;
    private final long validityInMillisecond = 3600000;

    @Autowired
    private UserRepository userRepository;

    public String createToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
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
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        String email = claims.getSubject();

        if (claims.get("role") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }
        List<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("role").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("NOT found" + email));

        PrincipalDetails principal = new PrincipalDetails(user);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);

    }
}
