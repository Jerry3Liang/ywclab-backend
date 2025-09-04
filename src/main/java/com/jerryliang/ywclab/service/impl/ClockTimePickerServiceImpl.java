package com.jerryliang.ywclab.service.impl;

import com.jerryliang.ywclab.dao.ClockTimePickerDao;
import com.jerryliang.ywclab.dto.*;
import com.jerryliang.ywclab.service.ClockTimePickerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClockTimePickerServiceImpl implements ClockTimePickerService {

    @Autowired
    private ClockTimePickerDao clockTimePickerDao;

    @Override
    public Integer insertStartTime(ClockTimePickerStartRequest clockTimePickerStartRequest) {
        return clockTimePickerDao.insertStartTime(clockTimePickerStartRequest);
    }

    @Override
    public ClockTimePickerStartANDEndResponse getTimePickerInfoByClockTimePickerId(Integer clockTimePickerId) {
        return clockTimePickerDao.getStartInfoByClockTimePickerId(clockTimePickerId);
    }

    @Override
    public List<AllClockTimePickerStartAndEndResponse> getAllTimeInfo() {
        return clockTimePickerDao.getAllTimeInfo();
    }

    @Override
    public List<ClockTimePickerStartANDEndResponse> getTimeInfoByMemberNo(Integer memberNo) {
        return clockTimePickerDao.getTimeInfoByMemberNo(memberNo);
    }

    @Override
    public ClockTimePickerStartANDEndResponse getTheLastTimeInfoByMemberNo(Integer memberNo) {
        return clockTimePickerDao.getTheLastTimeInfoByMemberNo(memberNo);
    }

    @Override
    public void insertEndTime(Integer clockTimePickerId, ClockTimePickerEndRequest clockTimePickerEndRequest) {
        clockTimePickerDao.insertEndTime(clockTimePickerId, clockTimePickerEndRequest);
    }

    @Override
    public Integer reInsertStartTimeAndEndTime(ReClockTimePickerRequest reClockTimePickerRequest) {
        return clockTimePickerDao.reInsertStartTimeAndEndTime(reClockTimePickerRequest);
    }
}
