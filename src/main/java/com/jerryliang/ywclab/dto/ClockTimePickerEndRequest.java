package com.jerryliang.ywclab.dto;

import lombok.Data;

@Data
public class ClockTimePickerEndRequest {

    private String startTime;
    private String endTime;
    private Integer finishedStatus;
    private Integer memberNo;
}
