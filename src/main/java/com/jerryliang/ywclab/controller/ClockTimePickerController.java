package com.jerryliang.ywclab.controller;

import com.jerryliang.ywclab.annotation.ActionLogs;
import com.jerryliang.ywclab.dto.*;
import com.jerryliang.ywclab.service.ClockTimePickerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/clock")
public class ClockTimePickerController {
    //搭配 Lombok 的 @RequiredArgsConstructor 實作 Constructor Injection，不使用 @Autowired
    private final ClockTimePickerService clockTimePickerService;

    @ActionLogs(action = "簽到")
    @PostMapping("/insertStartTime")
    public ResponseEntity<?> insertStartTime(@RequestBody ClockTimePickerStartRequest clockTimePickerStartRequest){
        Integer clockTimePickerId = clockTimePickerService.insertStartTime(clockTimePickerStartRequest);
        ClockTimePickerStartANDEndResponse response = clockTimePickerService.getTimePickerInfoByClockTimePickerId(clockTimePickerId);

        return ResponseEntity.created(URI.create("http://localhost:8080/clock" + clockTimePickerId)).body(response);
    }

    @ActionLogs(action = "獲取全部成員所有簽到退資訊")
    @GetMapping("/getAllTimePickerInfo")
    public ResponseEntity<?> getAllTimePickerInfo(){
        List<AllClockTimePickerStartAndEndResponse> allTimePickerInfo = clockTimePickerService.getAllTimeInfo();

        return ResponseEntity.ok(allTimePickerInfo);
    }

    @ActionLogs(action = "獲取當天個人最新簽到退資訊")
    @GetMapping("/getTheLastClockTimePickerInfo/{memberNo}")
    public ResponseEntity<?> getTheLastTimePickerInfoByMemberNo(@PathVariable(name = "memberNo") Integer memberNo){
        ClockTimePickerStartANDEndResponse theLastTimePickerInfo = clockTimePickerService.getTheLastTimeInfoByMemberNo(memberNo);

        return ResponseEntity.ok(theLastTimePickerInfo);
    }

    @ActionLogs(action = "獲取當天所有簽到退資訊")
    @GetMapping("/getClockTimePickerInfo/{memberNo}")
    public ResponseEntity<?> getCurrentClockTimePickerInfoByMemberNo(@PathVariable(name = "memberNo") Integer memberNo){

        List<ClockTimePickerStartANDEndResponse> forgetTimePicker = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
        List<ClockTimePickerStartANDEndResponse> clockTimePickerStartANDEndResponse = clockTimePickerService.getTimeInfoByMemberNo(memberNo);
        for (ClockTimePickerStartANDEndResponse timePickerStartResponse : clockTimePickerStartANDEndResponse) {
            if (timePickerStartResponse.getFinishedStatus() != 1) {
                int startTimeDate = Integer.parseInt(timePickerStartResponse.getStartTime().substring(8, 10));
                if(startTimeDate == currentDate){
                    forgetTimePicker.add(timePickerStartResponse);
                }
            }
        }

        return ResponseEntity.ok(forgetTimePicker);
    }

    @ActionLogs(action = "補簽到退")
    @PostMapping("/reTimePicker")
    public ResponseEntity<?> reTimePicker(@RequestBody ReClockTimePickerRequest reClockTimePickerRequest){
        Integer clockTimePickerId = clockTimePickerService.reInsertStartTimeAndEndTime(reClockTimePickerRequest);
        ClockTimePickerStartANDEndResponse response = clockTimePickerService.getTimePickerInfoByClockTimePickerId(clockTimePickerId);

        return ResponseEntity.created(URI.create("http://localhost:8080/clock" + clockTimePickerId)).body(response);
    }

    @ActionLogs(action = "簽退 or 補簽退")
    @PutMapping("/insertEndTime/{clockTimePickerId}")
    public ResponseEntity<ClockTimePickerStartANDEndResponse> insertEndTime(@PathVariable Integer clockTimePickerId, @RequestBody ClockTimePickerEndRequest clockTimePickerEndRequest){
        ClockTimePickerStartANDEndResponse oldClockTimePickerStartANDEnd = clockTimePickerService.getTimePickerInfoByClockTimePickerId(clockTimePickerId);

        if(oldClockTimePickerStartANDEnd == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        //簽退
        clockTimePickerService.insertEndTime(clockTimePickerId, clockTimePickerEndRequest);

        ClockTimePickerStartANDEndResponse newClockTimePickerStartANDEnd = clockTimePickerService.getTimePickerInfoByClockTimePickerId(clockTimePickerId);

        return ResponseEntity.status(HttpStatus.OK).body(newClockTimePickerStartANDEnd);
    }

    @ActionLogs(action = "獲取個人所有簽到退資訊")
    @GetMapping("/getPersonalTimePickerInfo/{memberNo}")
    public ResponseEntity<?> getPersonalTimePickerInfoByMemberNo(@PathVariable(name = "memberNo") Integer memberNo){
        List<ClockTimePickerStartANDEndResponse> clockTimePickerStartANDEndResponse = clockTimePickerService.getTimeInfoByMemberNo(memberNo);

        List<ClockTimePickerStartANDEndResponse> filterClockTimePickerStartANDEndResponse = new ArrayList<>();
        for(ClockTimePickerStartANDEndResponse updateData : clockTimePickerStartANDEndResponse){
            Calendar calendar = Calendar.getInstance();
            int currentDate = calendar.get(Calendar.DAY_OF_MONTH);
            int startTimeDate = Integer.parseInt(updateData.getStartTime().substring(8, 10));
            if(startTimeDate == currentDate){
                updateData.setCurrentDate(true);
            }
            filterClockTimePickerStartANDEndResponse.add(updateData);
        }

        return ResponseEntity.ok(filterClockTimePickerStartANDEndResponse);
    }

    @ActionLogs(action = "根據 clockTimePickerId 獲取簽到退資訊")
    @GetMapping("/getClockTimePickerInfoByClockTimePickerId/{clockTimePickerId}")
    public ResponseEntity<ClockTimePickerStartANDEndResponse> getTimePickerInfoByClockTimePickerId(@PathVariable(name = "clockTimePickerId") Integer clockTimePickerId){
        ClockTimePickerStartANDEndResponse clockTimePickerStartANDEnd = clockTimePickerService.getTimePickerInfoByClockTimePickerId(clockTimePickerId);

        return ResponseEntity.ok(clockTimePickerStartANDEnd);
    }
}
