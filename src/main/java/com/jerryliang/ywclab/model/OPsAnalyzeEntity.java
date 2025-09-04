package com.jerryliang.ywclab.model;

import lombok.Data;

@Data
public class OPsAnalyzeEntity {

    private double value;
    private double seconds;

    public OPsAnalyzeEntity(double value, double seconds) {
        this.value = value;
        this.seconds = seconds;
    }
}
