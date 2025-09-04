package com.jerryliang.ywclab.model;

import lombok.Data;

import java.util.List;

@Data
public class OPsEntity {

    private String expGroupName;
    private List<Double> expRightEyeRawData;
    private List<Double> expLeftEyeRawData;
    private List<Double> dataMilliSec;
}
