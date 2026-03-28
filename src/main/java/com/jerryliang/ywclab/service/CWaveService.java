package com.jerryliang.ywclab.service;

import com.jerryliang.ywclab.dto.CWaveTableToDownloadEntity;
import com.jerryliang.ywclab.model.CWaveTableEntity;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CWaveService {


    List<CWaveTableEntity> newFindFilterData1(Sheet sheet);
    List<CWaveTableEntity> newFindFilterData2(Sheet sheet);
    List<Object> oldFindFilterData1(Sheet sheet);

    List<Object> oldFindFilterData2(Sheet sheet);

    List<Double> findAllDataByColumnIndex(Sheet sheet, Integer columnIndex, Integer startRowIndex);

    List<Double> findABWaveDataByColumnIndex(Sheet sheet, Integer columnIndex, Integer startRowIndex);

    Double findDataByRowIndex(Sheet sheet, Integer rowIndex);

    Double findLuxDataByRowIndex(Sheet sheet, Integer rowIndex);

    String findExpDateByRowIndex(Sheet sheet, Integer rowIndex);

    Integer findRowIndexByCellValue(Sheet sheet, String keyWord, Integer columnIndex);

    ResponseEntity<byte[]> exportCWaveXlsx(Map<String, List<CWaveTableEntity>> cWaveTableDataDownloadRequestMapSet, Map<String, String> expDateMapSet, Map<String, Double> luxDataMapSet);

    String fileReName(Map<String, String> cWaveNewAndOldFileNameMapSet, String folderPath, String inputCaseName);
}
