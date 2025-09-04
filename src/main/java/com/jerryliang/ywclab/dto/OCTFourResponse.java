package com.jerryliang.ywclab.dto;

import lombok.Data;

import java.util.List;

@Data
public class OCTFourResponse {

    private String groupName;
    private List<Double> oCTFourLayerDataList;
}
