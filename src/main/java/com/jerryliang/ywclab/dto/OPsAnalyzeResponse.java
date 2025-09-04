package com.jerryliang.ywclab.dto;

import lombok.Data;

import java.util.List;

@Data
public class OPsAnalyzeResponse {

    private String expGroupName;
    private List<Double> rightEyeOPsData;
    private List<Double> leftEyeOPsData;
    private List<Double> rightEyeOPsMilliSec;
    private List<Double> leftEyeOPsMilliSec;
}
