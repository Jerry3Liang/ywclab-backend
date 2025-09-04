package com.jerryliang.ywclab.dto.createUser;

import com.fasterxml.jackson.databind.JsonNode;
import com.jerryliang.ywclab.enums.LevelType;
import lombok.Data;

@Data
public class User {

    private String id;
    private String userName;
    private String password;
    private String name;
    private String email;
    private String groupNO;
    private LevelType levelType;
    private JsonNode extraPermission;
    private String groupName;
    private String lastLoginTime;
}
