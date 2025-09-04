package com.jerryliang.ywclab.dto.login;

import lombok.Data;

@Data
public class ValidationTokenData {

    private String token;
    private String envName;
}
