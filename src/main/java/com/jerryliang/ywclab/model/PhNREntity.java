package com.jerryliang.ywclab.model;

import lombok.Data;

import java.util.List;

@Data
public class PhNREntity {

    private String expGroupName;
    private List<Double> expRightEyeRawData;
    private List<Double> expLeftEyeRawData;
    private List<Double> dataMilliSec;
}
