package com.jerryliang.ywclab.dto;

import lombok.Data;

@Data
public class ClockTimePickerStartRequest {

    private String startTime;
    private Integer finishedStatus;
    private Integer memberNo;
}
