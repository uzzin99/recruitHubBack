package com.jangyujin.recruitHubBack.config.oauth;

import com.jangyujin.recruitHubBack.config.auth.PrincipalDetails;
import com.jangyujin.recruitHubBack.config.oauth.provider.GoogleUserInfo;
import com.jangyujin.recruitHubBack.config.oauth.provider.KakaoUserInfo;
import com.jangyujin.recruitHubBack.config.oauth.provider.NaverUserInfo;
import com.jangyujin.recruitHubBack.config.oauth.provider.OAuth2UserInfo;
import com.jangyujin.recruitHubBack.model.User;
import com.jangyujin.recruitHubBack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if(registrationId.equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(registrationId.equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }else {
            oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        //String username = provider+"_"+providerId;
        String username = oAuth2UserInfo.getName();
        String userImgUrl = oAuth2UserInfo.getUserImgUrl();
        String email = oAuth2UserInfo.getEmail();

        Optional<User> userOptional = userRepository.findByEmail(email);

        User userEntity = userOptional.orElseGet(() -> {
            return userRepository.save(User.builder()
                    .username(username)
                    .email(email)
                    .role("ROLE_USER")
                    .provider(provider)
                    .providerId(providerId)
                    .userImgUrl(userImgUrl)
                    .build());
        });

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
