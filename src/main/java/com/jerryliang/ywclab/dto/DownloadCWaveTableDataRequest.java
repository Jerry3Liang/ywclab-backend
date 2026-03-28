package com.jerryliang.ywclab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jerryliang.ywclab.model.CWaveTableEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DownloadCWaveTableDataRequest {

    @JsonProperty("cWaveTableDataMapSet")
    private Map<String, List<CWaveTableEntity>> cWaveTableDataMapSet;

    @JsonProperty("expDateMapSet")
    private Map<String, String> expDateMapSet;

    @JsonProperty("luxDataMapSet")
    private Map<String, Double> luxDataMapSet;
}
