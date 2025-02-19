package com.jangyujin.recruitHubBack.config.oauth.provider;

/**
 * OAuth2 사용자 정보를 추출하는 메서드를 정의하는 인터페이스입니다.
 */
public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
    String getUserImgUrl();
}
