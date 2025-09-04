package com.jerryliang.ywclab.service;

import com.jerryliang.ywclab.dto.OPsAnalyzeDTO;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface OPsService {

    List<Double> findAllDataByColumnIndex(Sheet sheet, Integer columnIndex, String keyWord);

    List<List<Double>> findOPsDataAndMilliSec(String minPointSelected, List<Double> rawDataList, List<Double> rawMilliSecList);

    ResponseEntity<byte[]> exportOPsXlsx(Map<String, OPsAnalyzeDTO> cWaveTableDataDownloadRequestMapSet);
}
