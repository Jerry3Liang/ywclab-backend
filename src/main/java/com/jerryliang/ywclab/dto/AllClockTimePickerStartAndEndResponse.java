package com.jerryliang.ywclab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AllClockTimePickerStartAndEndResponse {

    private Integer clockTimePickerId;
    private String startTime;
    private String endTime;
    private Integer spentTime;
    private Integer finishedStatus;
    private String memberNickName;

    @JsonProperty("isCurrentDate")
    private boolean isCurrentDate;
}
