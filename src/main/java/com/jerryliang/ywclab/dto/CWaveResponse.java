package com.jerryliang.ywclab.dto;

import com.jerryliang.ywclab.model.CWaveTableEntity;
import lombok.Data;

import java.util.List;

@Data
public class CWaveResponse {

    private String groupName;
    private String expDate;
    private Double lux;
    private List<CWaveTableEntity> eyeDataOne;
    private List<CWaveTableEntity> eyeDataTwo;
//    private List<Object> eyeDataOne;
//    private List<Object> eyeDataTwo;
}
