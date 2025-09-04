package com.jerryliang.ywclab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ReNameCWaveDataFileRequest {

    @JsonProperty("cWaveNewAndOldFileNameMapSet")
    private Map<String, String> cWaveNewAndOldFileNameMapSet;

    @JsonProperty("inputCaseName")
    private String inputCaseName;
}
