package com.jerryliang.ywclab.dto;

import lombok.Data;

import java.util.List;

@Data
public class OPsAnalyzeResponse {

    private String expGroupName;
    private List<Double> rightEyeOPsData;
    private List<Double> leftEyeOPsData;
    private Double rightEyeOPsTotal;
    private Double leftEyeOPsTotal;
    private List<Double> rightEyeOPsMilliSec;
    private List<Double> leftEyeOPsMilliSec;
    private Double rightEyeOPsTotalMilliSec;
    private Double leftEyeOPsTotalMilliSec;
}
