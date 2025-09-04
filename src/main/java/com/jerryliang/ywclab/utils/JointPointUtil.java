package com.jerryliang.ywclab.utils;

import com.jerryliang.ywclab.annotation.ActionLogs;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

public class JointPointUtil {
    public static ActionLogs getActionLogAnnotation(ProceedingJoinPoint joinPoint){

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        return method.getAnnotation(ActionLogs.class);
    }
}
