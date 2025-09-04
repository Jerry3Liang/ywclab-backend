package com.jerryliang.ywclab.enums;

import lombok.Getter;

@Getter
public enum LogStatus {

    SUCCESS("成功"),
    FAILURE("失敗");

    private final String desc;

    LogStatus(String desc){
        this.desc = desc;
    }
}
