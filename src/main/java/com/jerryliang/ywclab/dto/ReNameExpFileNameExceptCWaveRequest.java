package com.jerryliang.ywclab.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Data
public class ReNameExpFileNameExceptCWaveRequest {

    @JsonProperty("files")
    private MultipartFile[] files;

    @JsonProperty("mapExcelFile")
    private MultipartFile mapExcelFile;

    private String inputCaseName;

    private String fileNameMap;

}
