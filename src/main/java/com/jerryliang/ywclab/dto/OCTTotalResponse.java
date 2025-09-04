package com.jerryliang.ywclab.dto;

import lombok.Data;

import java.util.List;

@Data
public class OCTTotalResponse {

    private String groupName;
    private List<Double> oCTToTalLayerdataList;
}
