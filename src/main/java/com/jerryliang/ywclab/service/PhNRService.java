package com.jerryliang.ywclab.service;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public interface PhNRService {

    List<Object> findPhNRTableData(Sheet sheet);

    List<Double> findAllDataByColumnIndex(Sheet sheet, Integer columnIndex);
}
