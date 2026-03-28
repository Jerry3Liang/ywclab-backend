package com.jerryliang.ywclab.dto;

import lombok.Data;

import java.util.List;

@Data
public class CWaveTableToDownloadEntity {
    private List<Double> leftOneData;
    private List<Double> leftTwoData;
    private List<Double> rightOneData;
    private List<Double> rightTwoData;
}
