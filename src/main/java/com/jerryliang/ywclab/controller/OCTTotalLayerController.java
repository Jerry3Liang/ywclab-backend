package com.jerryliang.ywclab.controller;

import com.jerryliang.ywclab.Exception.InvalidFileNameException;
import com.jerryliang.ywclab.annotation.ActionLogs;
import com.jerryliang.ywclab.dto.DownloadOCTTotalLayerDataRequest;
import com.jerryliang.ywclab.dto.OCTTotalResponse;
import com.jerryliang.ywclab.service.OCTTotalLayerService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/totalLayer")
public class OCTTotalLayerController {

    @Autowired
    private OCTTotalLayerService octTotalLayerService;

    @ActionLogs(action = "獲取 OCT Total Layer 最終計算 Data")
    @PostMapping("/excelDataToTable")
    public ResponseEntity<?> getOCTTotalLayerFinalData(@RequestBody MultipartFile[] files) throws IllegalArgumentException {
        if(files == null){
            return ResponseEntity.notFound().build();
        }
        List<OCTTotalResponse> OCTTotalFinalDataList = new ArrayList<>();
        List<String> errors = new ArrayList<>(); //用來收集錯誤訊息的 List
        for (MultipartFile file : files) {
            try{
                String fileName = file.getOriginalFilename();
                String fileNamePattern = "^[A-Za-z()+=&~ \\d\\u4e00-\\u9fa5]+_\\d+[a-zA-Z]-.*$";

                if (fileName == null) {
                    throw new AssertionError();
                }

                if (!fileName.matches(fileNamePattern)) {
                    throw new InvalidFileNameException(fileName + " 不符合命名規則！");
                }
                OCTTotalResponse oCTTotalResponse = new OCTTotalResponse();

                InputStream inputFile = file.getInputStream();
                int endSubstringNum = fileName.indexOf("-");
                String needFileName = fileName.substring(0, endSubstringNum);
                oCTTotalResponse.setGroupName(needFileName);
                Workbook workbook = WorkbookFactory.create(inputFile);
                Sheet sheet = workbook.getSheetAt(0);
                List<Double> dataList = octTotalLayerService.getOCTTotalFinalData(sheet);
                oCTTotalResponse.setOCTToTalLayerdataList(dataList);
                OCTTotalFinalDataList.add(oCTTotalResponse);
                workbook.close();
                inputFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidFileNameException e){
                errors.add(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (!errors.isEmpty()) {
            // 如果有錯誤，將錯誤訊息返回
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok(OCTTotalFinalDataList);
    }

    @ActionLogs(action = "下載 OCT Total Layer data")
    @PostMapping("/downloadOCTTotalLayerData")
    public ResponseEntity<byte[]> downloadFourLayerTableData(@RequestBody DownloadOCTTotalLayerDataRequest downloadOCTTotalLayerDataRequest){
        if(downloadOCTTotalLayerDataRequest.getOctTotalLayerDataMapSet() == null){
            return ResponseEntity.noContent().build();
        }

        return octTotalLayerService.exportOctTotalLayerXlsx(downloadOCTTotalLayerDataRequest.getOctTotalLayerDataMapSet());
    }
}
