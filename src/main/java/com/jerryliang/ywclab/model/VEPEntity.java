package com.jerryliang.ywclab.model;

import lombok.Data;

import java.util.List;

@Data
public class VEPEntity {

    private String expGroupName;
    private List<Double> expRightEyeRawData;
    private List<Double> expLeftEyeRawData;
    private List<Double> dataMilliSec;
}
