package com.jerryliang.ywclab.dao;

import com.jerryliang.ywclab.dto.*;

import java.util.List;

public interface ClockTimePickerDao {

    Integer insertStartTime(ClockTimePickerStartRequest clockTimePickerStartRequest);

    ClockTimePickerStartANDEndResponse getStartInfoByClockTimePickerId(Integer clockTimePickerId);

    List<AllClockTimePickerStartAndEndResponse> getAllTimeInfo();

    List<ClockTimePickerStartANDEndResponse> getTimeInfoByMemberNo(Integer memberNo);

    ClockTimePickerStartANDEndResponse getTheLastTimeInfoByMemberNo(Integer memberNo);

    void insertEndTime(Integer clockTimePickerId, ClockTimePickerEndRequest clockTimePickerEndRequest);

    Integer reInsertStartTimeAndEndTime(ReClockTimePickerRequest reClockTimePickerRequest);

}
