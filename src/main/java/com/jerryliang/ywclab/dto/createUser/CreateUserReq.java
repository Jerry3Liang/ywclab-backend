package com.jerryliang.ywclab.dto.createUser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jerryliang.ywclab.enums.AdType;
import com.jerryliang.ywclab.enums.LevelType;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class CreateUserReq {

    private String userName;
    private String password;
    private String name;
    private String nickName;
    private String email;
    private String groupNo;
    private LevelType levelType;
    private JsonNode extraPermission;
    private AdType adType;
    private JsonNode envData;

    public static CreateUserReq parse(@Valid CreateUserReq createUserReq, ObjectNode envData) {

        CreateUserReq result = new CreateUserReq();
        BeanUtils.copyProperties(createUserReq, result);
        result.setEnvData(envData);

        return result;
    }

    public ObjectNode createUserNewEnvDataWithUserName(JsonNode extraPermission) throws JsonProcessingException {

        ObjectNode objectNode;
        String str = "{}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(str);
        objectNode = node.deepCopy();
        objectNode.set("extraPermission", extraPermission);

        return objectNode;
    }
}
