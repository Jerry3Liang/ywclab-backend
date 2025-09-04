package com.jerryliang.ywclab.dto.createUser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

@Data
public class UserResp {

    private String codeH;
    private String message;
    private String publicKey;
    private List<JsonNode> envData;
    private User user;
    private List<User> users;
}
