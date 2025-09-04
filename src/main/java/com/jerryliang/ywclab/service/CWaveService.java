package com.jerryliang.ywclab.service;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CWaveService {

    List<Object> findFilterData1(Sheet sheet);

    List<Object> findFilterData2(Sheet sheet);

    List<Double> findAllDataByColumnIndex(Sheet sheet, Integer columnIndex, Integer startRowIndex);

    List<Double> findABWaveDataByColumnIndex(Sheet sheet, Integer columnIndex, Integer startRowIndex);

    Double findDataByRowIndex(Sheet sheet, Integer rowIndex);

    Double findLuxDataByRowIndex(Sheet sheet, Integer rowIndex);

    String findExpDateByRowIndex(Sheet sheet, Integer rowIndex);

    Integer findRowIndexByCellValue(Sheet sheet, String keyWord, Integer columnIndex);

    ResponseEntity<byte[]> exportCWaveXlsx(Map<String, List<Object>> cWaveTableDataDownloadRequestMapSet, Map<String, String> expDateMapSet, Map<String, Double> luxDataMapSet);

    String fileReName(Map<String, String> cWaveNewAndOldFileNameMapSet, String folderPath, String inputCaseName);
}
