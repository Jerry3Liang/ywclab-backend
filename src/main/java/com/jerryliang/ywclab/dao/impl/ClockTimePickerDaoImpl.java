package com.jerryliang.ywclab.dao.impl;

import com.jerryliang.ywclab.dao.ClockTimePickerDao;
import com.jerryliang.ywclab.dto.*;
import com.jerryliang.ywclab.rowmapper.AllClockTimePickerStartAndEndResponseRowMapper;
import com.jerryliang.ywclab.rowmapper.ClockTimePickerStartResponseRowMapper;
import com.jerryliang.ywclab.utils.DatetimeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ClockTimePickerDaoImpl implements ClockTimePickerDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Integer insertStartTime(ClockTimePickerStartRequest clockTimePickerStartRequest) {
        String sql = "INSERT INTO timePicker (startTime, finishedStatus, memberNo) " +
                     "VALUES (:startTime, :finishedStatus, :memberNo)";

        Map<String, Object> map = new HashMap<>();
        map.put("startTime", clockTimePickerStartRequest.getStartTime());
        map.put("finishedStatus", clockTimePickerStartRequest.getFinishedStatus());
        map.put("memberNo", clockTimePickerStartRequest.getMemberNo());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public ClockTimePickerStartANDEndResponse getStartInfoByClockTimePickerId(Integer clockTimePickerId) {
        String sql = "SELECT clockTimePickerId, startTime, endTime, spentTime, finishedStatus, memberNo FROM timePicker " +
                     "WHERE clockTimePickerId = :clockTimePickerId";

        Map<String, Object> map = new HashMap<>();
        map.put("clockTimePickerId", clockTimePickerId);

        List<ClockTimePickerStartANDEndResponse> clockTimePickerStartANDEndResponseList = namedParameterJdbcTemplate.query(sql, map, new ClockTimePickerStartResponseRowMapper());

        if(!clockTimePickerStartANDEndResponseList.isEmpty()){
            return clockTimePickerStartANDEndResponseList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<AllClockTimePickerStartAndEndResponse> getAllTimeInfo() {
        String sql = "SELECT clockTimePickerId, startTime, endTime, spentTime, finishedStatus, memberNickName FROM timePicker t " +
                     "LEFT JOIN member m ON t.memberNo = m.memberNo ORDER BY startTime";

        List<AllClockTimePickerStartAndEndResponse> clockTimePickerStartANDEndResponseList = namedParameterJdbcTemplate.query(sql, new AllClockTimePickerStartAndEndResponseRowMapper());

        return clockTimePickerStartANDEndResponseList;
    }

    @Override
    public List<ClockTimePickerStartANDEndResponse> getTimeInfoByMemberNo(Integer memberNo) {
        String sql = "SELECT clockTimePickerId, startTime, endTime, spentTime, finishedStatus, memberNo FROM timePicker " +
                "WHERE memberNo = :memberNo ORDER BY startTime";

        Map<String, Object> map = new HashMap<>();
        map.put("memberNo", memberNo);

        List<ClockTimePickerStartANDEndResponse> clockTimePickerStartANDEndResponseList = namedParameterJdbcTemplate.query(sql, map, new ClockTimePickerStartResponseRowMapper());

        return clockTimePickerStartANDEndResponseList;
    }

    @Override
    public ClockTimePickerStartANDEndResponse getTheLastTimeInfoByMemberNo(Integer memberNo) {
        String sql = "SELECT TOP(1) clockTimePickerId, startTime, endTime, spentTime, finishedStatus, memberNo FROM timePicker " +
                "WHERE memberNo = :memberNo ORDER BY startTime DESC";

        Map<String, Object> map = new HashMap<>();
        map.put("memberNo", memberNo);

        List<ClockTimePickerStartANDEndResponse> clockTimePickerStartANDEndResponseList = namedParameterJdbcTemplate.query(sql, map, new ClockTimePickerStartResponseRowMapper());

        if(!clockTimePickerStartANDEndResponseList.isEmpty()){
            return clockTimePickerStartANDEndResponseList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void insertEndTime(Integer clockTimePickerId, ClockTimePickerEndRequest clockTimePickerEndRequest) {
        String sql = "UPDATE timePicker SET endTime = :endTime, spentTime = :spentTime, finishedStatus = :finishedStatus " +
                     "WHERE clockTimePickerId = :clockTimePickerId";

        Map<String, Object> map = new HashMap<>();
        map.put("clockTimePickerId", clockTimePickerId);
        map.put("finishedStatus", clockTimePickerEndRequest.getFinishedStatus());

        map.put("endTime", clockTimePickerEndRequest.getEndTime());
        Date startTime = DatetimeConverter.parse(clockTimePickerEndRequest.getStartTime(), "yyyy-MM-dd HH:mm:ss");
        Date endTime = DatetimeConverter.parse(clockTimePickerEndRequest.getEndTime(), "yyyy-MM-dd HH:mm:ss");
        long startMillis = startTime.getTime();
        long endMillis = endTime.getTime();

        //獲取簽到退時間差 (分鐘)，如果大於 60 交給前端轉換成小時
        Integer spentTime = (int) (((endMillis - startMillis) / 1000) / 60);
        map.put("spentTime", spentTime);

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public Integer reInsertStartTimeAndEndTime(ReClockTimePickerRequest reClockTimePickerRequest) {
        String sql = "INSERT INTO timePicker (startTime, endTime, spentTime, finishedStatus, memberNo) " +
                "VALUES (:startTime, :endTime, :spentTime, :finishedStatus, :memberNo)";

        Map<String, Object> map = new HashMap<>();
        map.put("startTime", reClockTimePickerRequest.getStartTime());
        map.put("endTime", reClockTimePickerRequest.getEndTime());

        Date startTime = DatetimeConverter.parse(reClockTimePickerRequest.getStartTime(), "yyyy-MM-dd HH:mm:ss");
        Date endTime = DatetimeConverter.parse(reClockTimePickerRequest.getEndTime(), "yyyy-MM-dd HH:mm:ss");
        long startMillis = startTime.getTime();
        long endMillis = endTime.getTime();

        //獲取簽到退時間差 (分鐘)，如果大於 60 交給前端轉換成小時
        Integer spentTime = (int) (((endMillis - startMillis) / 1000) / 60);
        map.put("spentTime", spentTime);
        map.put("finishedStatus", reClockTimePickerRequest.getFinishedStatus());
        map.put("memberNo", reClockTimePickerRequest.getMemberNo());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
        return keyHolder.getKey().intValue();
    }
}
