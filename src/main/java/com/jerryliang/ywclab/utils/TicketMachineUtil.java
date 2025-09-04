package com.jerryliang.ywclab.utils;

import com.jerryliang.ywclab.dto.createUser.UserReq;
import com.jerryliang.ywclab.dto.createUser.UserResp;
import com.jerryliang.ywclab.dto.login.LoginData;
import com.jerryliang.ywclab.dto.login.TokenData;
import com.jerryliang.ywclab.dto.login.ValidationData;
import com.jerryliang.ywclab.dto.login.ValidationTokenData;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TicketMachineUtil {

    RestTemplate restTemplate;

    @Value("${ticket.m.url}")
    String TICKET_MACHINE_URL;

    /**
     *
     * 登入並取得 token
     *
     */
    public TokenData login(LoginData loginData) {

        String loginUrl = TICKET_MACHINE_URL + "api/login";
        ResponseEntity<TokenData> result = restTemplate.postForEntity(loginUrl, loginData, TokenData.class);

        return result.getBody();
    }

    /**
     *
     * 驗證 token 是否合法
     *
     */
    public ValidationData validateToken(String token){

        ValidationTokenData data = new ValidationTokenData();
        data.setEnvName(SystemUtil.sysName());
        data.setToken(token);

        String validationUrl = TICKET_MACHINE_URL + "api/validate";
        ResponseEntity<ValidationData> result = restTemplate.postForEntity(validationUrl, data, ValidationData.class);

        return result.getBody();
    }

    /**
     * 創立 User (系統預設用)
     *
     */
    @Transactional
    public UserResp createUser(UserReq userReq){

        String url = TICKET_MACHINE_URL + "api/createUser";
        ResponseEntity<UserResp> result = restTemplate.postForEntity(url, userReq, UserResp.class);

        return result.getBody();

    }
}
