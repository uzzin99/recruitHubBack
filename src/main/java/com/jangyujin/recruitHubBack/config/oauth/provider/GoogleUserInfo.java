package com.jangyujin.recruitHubBack.config.oauth.provider;

import java.util.Map;

/**
 * OAuth2UserInfo 인터페이스를 구현하여
 * Google OAuth2 사용자 정보를 추출함.
 */
public class GoogleUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getUserImgUrl() {
        return (String) attributes.get("picture");
    }
}
