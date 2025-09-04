package com.jerryliang.ywclab.rowmapper;

import com.jerryliang.ywclab.dto.ClockTimePickerStartANDEndResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClockTimePickerStartResponseRowMapper implements RowMapper<ClockTimePickerStartANDEndResponse> {

    @Override
    public ClockTimePickerStartANDEndResponse mapRow(ResultSet rs, int rowNum) throws SQLException {

        ClockTimePickerStartANDEndResponse startResponse = new ClockTimePickerStartANDEndResponse();
        startResponse.setClockTimePickerId(rs.getInt("clockTimePickerId"));
        startResponse.setStartTime(rs.getString("startTime"));
        startResponse.setEndTime(rs.getString("endTime"));
        startResponse.setSpentTime(rs.getInt("spentTime"));
        startResponse.setFinishedStatus(rs.getInt("finishedStatus"));
        startResponse.setMemberNo(rs.getInt("memberNo"));

        return startResponse;
    }
}
