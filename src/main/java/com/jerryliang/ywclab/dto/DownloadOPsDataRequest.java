package com.jerryliang.ywclab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class DownloadOPsDataRequest {

    @JsonProperty("opsDataMapSet")
    private Map<String, OPsAnalyzeDTO> opsDataMapSet;

}
