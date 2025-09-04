package com.jerryliang.ywclab.service;

import com.jerryliang.ywclab.model.OCTFourLayerEntity;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface OCTFourLayerService {

    List<OCTFourLayerEntity> getOCTFourFinalData(Sheet sheet, String fileName) throws Exception;

    ResponseEntity<byte[]> exportOctFourLayerXlsx(Map<String, List<Object>> octFourLayerDataDownloadRequestMapSet);
}
