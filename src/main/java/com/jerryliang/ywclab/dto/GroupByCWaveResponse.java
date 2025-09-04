package com.jerryliang.ywclab.dto;

import lombok.Data;

@Data
public class GroupByCWaveResponse {

    private String groupName;
    private Double rightEyeAverageAWave;
    private Double rightEyeAverageBWave;
    private Double rightEyeAverageCWave;
    private Double leftEyeAverageAWave;
    private Double leftEyeAverageBWave;
    private Double leftEyeAverageCWave;
}
