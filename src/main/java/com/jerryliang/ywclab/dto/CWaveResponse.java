package com.jerryliang.ywclab.dto;

import lombok.Data;

import java.util.List;

@Data
public class CWaveResponse {

    private String groupName;
    private String expDate;
    private Double lux;
    private List<Object> eyeDataOne;
    private List<Object> eyeDataTwo;
}
