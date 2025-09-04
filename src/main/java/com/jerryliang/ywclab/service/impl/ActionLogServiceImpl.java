package com.jerryliang.ywclab.service.impl;

import com.jerryliang.ywclab.repository.ActionLogRepository;
import com.jerryliang.ywclab.model.ActionLog;
import com.jerryliang.ywclab.service.ActionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActionLogServiceImpl implements ActionLogService {

    @Autowired
    private ActionLogRepository actionLogRepository;

    @Override
    public void jobSave(ActionLog logData) {
        if(logData == null){
            return;
        }

        actionLogRepository.save(logData);
    }
}
