package com.jangyujin.recruitHubBack.dto;

import lombok.*;
public class UserRequest {

    @Data
    public static class JoinDto {
        private String userid;
        private String username;
        private String password;
        private String email;
        private String phone;
    }

    @Data
    public static class FindUserDto {
        private String username;
        private String phone;
        private String email;
    }

    @Data
    public static class ResetPwdDto {
        private String email;
        private String pwd;
        private String confirmPwd;
    }

    @Data
    public static class LoginDto {
        private String username;
        private String password;
    }

}
