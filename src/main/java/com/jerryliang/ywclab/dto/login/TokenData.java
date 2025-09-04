package com.jerryliang.ywclab.dto.login;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class TokenData {

    private String codeH;
    private String message;
    private String token;
    private String refreshToken;
    private JsonNode envData;
    private UserData userData;
    private Boolean isExpiryDate;
}
