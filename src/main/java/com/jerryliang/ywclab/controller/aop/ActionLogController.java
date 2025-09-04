package com.jerryliang.ywclab.controller.aop;

import com.jerryliang.ywclab.annotation.ActionLogs;
import com.jerryliang.ywclab.enums.LogStatus;
import com.jerryliang.ywclab.model.ActionLog;
import com.jerryliang.ywclab.service.ActionLogService;
import com.jerryliang.ywclab.utils.JointPointUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Aspect
@Component
public class ActionLogController {

    @Autowired
    private ActionLogService actionLogService;

    @Around(value = "@annotation(com.jerryliang.ywclab.annotation.ActionLogs)")
    public Object actionLog(ProceedingJoinPoint joinPoint) throws Throwable {
        ActionLogs annotation = JointPointUtil.getActionLogAnnotation(joinPoint);

        //取得名稱
        String method = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String action = annotation.action();
        boolean errorOnly = annotation.errorOnly();

        //計時開始
        long runTimeStart = System.currentTimeMillis();

        //執行工作
        Object result;

        ActionLog logData = new ActionLog();
        logData.setAction(action);
        logData.setMethod(method);
        logData.setClassName(className);
        logData.setTime(new Date());
        logData.setStatus(LogStatus.SUCCESS);

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {

            logData.setStatus(LogStatus.FAILURE);
            logData.setMessage(e.getMessage());
            log.debug("Action Log {}", e);
            throw e;
        } finally {
            //計時結束
            long runTime = System.currentTimeMillis() - runTimeStart;
            log.debug("req runtime:{}", runTime);

            logData.setRunTime(runTime);

            //當 error 只為 true 時，且操作真的失敗時才存 LOG
            if(!errorOnly || logData.getStatus() == LogStatus.FAILURE){
                actionLogService.jobSave(logData);
            }
        }
    }
}
