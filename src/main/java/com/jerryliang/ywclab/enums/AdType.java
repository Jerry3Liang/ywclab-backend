package com.jerryliang.ywclab.enums;

import lombok.Getter;

@Getter
public enum AdType {

    TRUE("是"),
    NONE("否");

    final String desc;

    AdType(String desc){
        this.desc = desc;
    }

}
