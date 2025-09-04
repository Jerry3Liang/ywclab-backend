package com.jerryliang.ywclab.service;

import com.jerryliang.ywclab.dto.*;

import java.util.List;

public interface ClockTimePickerService {

    Integer insertStartTime(ClockTimePickerStartRequest clockTimePickerStartRequest);

    ClockTimePickerStartANDEndResponse getTimePickerInfoByClockTimePickerId(Integer clockTimePickerId);

    List<AllClockTimePickerStartAndEndResponse> getAllTimeInfo();

    List<ClockTimePickerStartANDEndResponse> getTimeInfoByMemberNo(Integer memberNo);

    ClockTimePickerStartANDEndResponse getTheLastTimeInfoByMemberNo(Integer memberNo);

    void insertEndTime(Integer clockTimePickerId, ClockTimePickerEndRequest clockTimePickerEndRequest);

    Integer reInsertStartTimeAndEndTime(ReClockTimePickerRequest reClockTimePickerRequest);
}
