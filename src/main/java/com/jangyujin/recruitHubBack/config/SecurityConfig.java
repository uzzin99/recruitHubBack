package com.jangyujin.recruitHubBack.config;

import com.jangyujin.recruitHubBack.config.jwt.JwtAuthenticationFilter;
import com.jangyujin.recruitHubBack.config.jwt.JwtTokenProvider;
import com.jangyujin.recruitHubBack.config.jwt.JwtAuthenticationFailureHandler;
import com.jangyujin.recruitHubBack.config.jwt.JwtAuthenticationSuccessHandler;
import com.jangyujin.recruitHubBack.config.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
    private final JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/", "/error").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(auth -> auth
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .loginPage("/loginForm")
                        .loginProcessingUrl("/loginProcess")
                        .successHandler(jwtAuthenticationSuccessHandler)
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/loginForm")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(principalOauth2UserService)
                        )
                        .successHandler(jwtAuthenticationSuccessHandler)
                        .failureHandler(jwtAuthenticationFailureHandler)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
