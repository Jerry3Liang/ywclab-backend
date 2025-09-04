package com.jerryliang.ywclab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DownloadOCTFourLayerDataRequest {

    @JsonProperty("octFourLayerDataMapSet")
    private Map<String, List<Object>> octFourLayerDataMapSet;
}
