package com.jerryliang.ywclab.model;

import lombok.Data;

import java.util.List;

@Data
public class PLREntity {

    private String expGroupName;
    private List<Double> expRightEyeRawData;
}
