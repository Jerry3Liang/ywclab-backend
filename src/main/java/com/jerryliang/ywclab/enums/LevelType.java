package com.jerryliang.ywclab.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum LevelType {

    SUPER("最高權限帳號"),
    NORMAL_USER("一般使用者");

    private final String desc;

    LevelType(String desc){
        this.desc = desc;
    }

    public static List<LevelType> getLevelList(LevelType levelType) {

        if(null == levelType){
            return null;
        }

        List<LevelType> result = new ArrayList<>();
        result.add(levelType);
        return switch (levelType) {
            case SUPER -> {
                result.add(SUPER);
                yield result;
            }
            case NORMAL_USER -> {
                result.add(NORMAL_USER);
                yield result;
            }
        };
    }
}
