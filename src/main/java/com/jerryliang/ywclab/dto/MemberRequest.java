package com.jerryliang.ywclab.dto;

import lombok.Data;

@Data
public class MemberRequest {

    private Integer memberNo;
    private String email;
    private String memberNickName;
    private String password;
    private String createdTime;
    private String lastLoginTime;
    private Integer openFunction;
    private String roleName;
}
