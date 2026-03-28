package com.jerryliang.ywclab.dto;

import lombok.Data;

@Data
public class CWaveDataDto {
    private String waveName;
    private Double value;
    private Double milliSeconds;
}
