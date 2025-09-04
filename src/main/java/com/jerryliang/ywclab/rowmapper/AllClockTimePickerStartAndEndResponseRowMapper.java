package com.jerryliang.ywclab.rowmapper;

import com.jerryliang.ywclab.dto.AllClockTimePickerStartAndEndResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AllClockTimePickerStartAndEndResponseRowMapper implements RowMapper<AllClockTimePickerStartAndEndResponse> {

    @Override
    public AllClockTimePickerStartAndEndResponse mapRow(ResultSet rs, int rowNum) throws SQLException {

        AllClockTimePickerStartAndEndResponse allClockTimePickerStartAndEndResponse = new AllClockTimePickerStartAndEndResponse();
        allClockTimePickerStartAndEndResponse.setClockTimePickerId(rs.getInt("clockTimePickerId"));
        allClockTimePickerStartAndEndResponse.setStartTime(rs.getString("startTime"));
        allClockTimePickerStartAndEndResponse.setEndTime(rs.getString("endTime"));
        allClockTimePickerStartAndEndResponse.setSpentTime(rs.getInt("spentTime"));
        allClockTimePickerStartAndEndResponse.setFinishedStatus(rs.getInt("finishedStatus"));
        allClockTimePickerStartAndEndResponse.setMemberNickName(rs.getString("memberNickName"));

        return allClockTimePickerStartAndEndResponse;
    }
}
