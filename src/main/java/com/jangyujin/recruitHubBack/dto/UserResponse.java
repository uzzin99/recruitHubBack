package com.jangyujin.recruitHubBack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor //모든생성자
@RequiredArgsConstructor
public class UserResponse {
    private String userid;
    private String email;
}
