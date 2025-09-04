package com.jerryliang.ywclab.service;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;

public interface UpdateFileNameService {
    Map<String, String> getOldAndNewFileNameMap(Sheet sheet);
}
