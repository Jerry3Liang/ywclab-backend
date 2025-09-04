package com.jerryliang.ywclab.dto;

import lombok.Data;

@Data
public class ReClockTimePickerRequest {

    private String startTime;
    private String endTime;
    private Integer finishedStatus;
    private Integer memberNo;
}
