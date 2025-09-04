package com.jerryliang.ywclab.service;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public interface PLRService {
    List<Double> findAllDataByColumnIndex(Sheet sheet, Integer columnIndex);
}
