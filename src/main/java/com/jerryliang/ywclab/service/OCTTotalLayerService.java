package com.jerryliang.ywclab.service;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface OCTTotalLayerService {

    List<Double> getOCTTotalFinalData(Sheet sheet);

    ResponseEntity<byte[]> exportOctTotalLayerXlsx(Map<String, List<Object>> octTotalLayerDataDownloadRequestMapSet);
}
