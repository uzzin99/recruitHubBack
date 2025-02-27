package com.jangyujin.recruitHubBack.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Table(name = "user")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userid")
    private String userid;

    @Column(nullable = false, unique = true, name = "username")
    private String username;

    @Column(nullable = false, name = "email")
    private String email;

    @Column(name = "user_img_url")
    private String userImgUrl;

    @Column(nullable = false, name = "role")
    private String role; // ROLE_USER, ROLE_ADMIN 등

    @Column(name = "provider")
    private String provider; // OAuth2 제공자 (google, kakao, naver)

    @Column(name = "provider_id")
    private String providerId; // 제공자의 사용자 ID

    @Column(name = "pwd")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Builder
    public User(String username, String email, String userImgUrl, String role, String provider, String providerId, String password, String phone) {
        this.username = username;
        this.email = email;
        this.userImgUrl = userImgUrl;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.phone = phone;
    }
}
