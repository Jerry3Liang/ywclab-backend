package com.jerryliang.ywclab.dto;

import lombok.Data;

import java.util.List;

@Data
public class OPsAnalyzeResponse {

    private String expGroupName;
    private List<Double> rightEyeOPsData;
    private List<Double> leftEyeOPsData;
    private Double rightEyeOPs234;
    private Double leftEyeOPs234;
    private Double rightEyeOPsTotal;
    private Double leftEyeOPsTotal;
    private List<Double> rightEyeOPsMilliSec;
    private List<Double> leftEyeOPsMilliSec;
    private Double rightEyeOPs234MilliSec;
    private Double leftEyeOPs234MilliSec;
    private Double rightEyeOPsTotalMilliSec;
    private Double leftEyeOPsTotalMilliSec;
}
