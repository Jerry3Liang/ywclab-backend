package com.jerryliang.ywclab.model;

import com.jerryliang.ywclab.dto.CWaveDataDto;
import lombok.Data;

@Data
public class CWaveTableEntity {
    private String whichEye;
    private CWaveDataDto aWave;
    private CWaveDataDto bWave;
    private CWaveDataDto cWave;
}
