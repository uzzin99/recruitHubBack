package com.jangyujin.recruitHubBack.dto;

import lombok.*;

//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class UserRequest {

    @Data
    public static class JoinDto{
        private String username;
        private String password;
        private String email;
    }
//    @Builder
//    public JoinRequestDto(String username, String password, String email){
//        this.username = username;
//        this.password = password;
//        this.email = email;
//    }
}
