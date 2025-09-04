package com.jerryliang.ywclab.model;

import lombok.Data;

import java.util.List;

@Data
public class CWaveEntity {

    private String expGroupName;
    private List<Double> expRightEyeRawData1;
    private List<Double> expRightEyeRawData2;
    private List<Double> expLeftEyeRawData1;
    private List<Double> expLeftEyeRawData2;
    private List<Double> dataMilliSec;
}
