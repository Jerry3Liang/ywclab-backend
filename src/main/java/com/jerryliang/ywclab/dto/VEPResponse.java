package com.jerryliang.ywclab.dto;

import lombok.Data;

import java.util.List;

@Data
public class VEPResponse {

    private String groupName;
    private List<Object> rightEyeData;
    private List<Object> leftEyeData;
}
