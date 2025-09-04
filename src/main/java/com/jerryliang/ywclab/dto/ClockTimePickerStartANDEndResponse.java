package com.jerryliang.ywclab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClockTimePickerStartANDEndResponse {

    private Integer clockTimePickerId;
    private String startTime;
    private String endTime;
    private Integer spentTime;
    private Integer finishedStatus;
    private Integer memberNo;

    @JsonProperty("isCurrentDate")
    private boolean isCurrentDate;
}
