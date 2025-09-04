package com.jerryliang.ywclab.dto.createUser;

import com.fasterxml.jackson.databind.JsonNode;
import com.jerryliang.ywclab.enums.LevelType;
import com.jerryliang.ywclab.utils.SystemUtil;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
public class UserReq {

    private String password;
    private String oldPassword;
    private String envName = SystemUtil.sysName();
    private String userName;
    private String name;
    private Boolean isDefault = false;
    private String email;
    private String groupNo;
    private List<LevelType> levelTypes;
    private String extraPermission;
    private JsonNode envData;
    private Date lastChangePasswordTime;

    public static UserReq parse(Object input) {

        UserReq result = new UserReq();
        BeanUtils.copyProperties(input, result);

        return result;
    }
}
