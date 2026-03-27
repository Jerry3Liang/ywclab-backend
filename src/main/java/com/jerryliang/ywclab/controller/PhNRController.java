package com.jerryliang.ywclab.controller;

import com.jerryliang.ywclab.Exception.EvaluationLimitExceededException;
import com.jerryliang.ywclab.Exception.InvalidFileNameException;
import com.jerryliang.ywclab.annotation.ActionLogs;
import com.jerryliang.ywclab.dto.PhNRResponse;
import com.jerryliang.ywclab.model.PhNREntity;
import com.jerryliang.ywclab.service.PhNRService;
import com.jerryliang.ywclab.utils.CommonMethods;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/phnr")
public class PhNRController {

    @Autowired
    private PhNRService phNRService;

    @ActionLogs(action = "獲取 PhNR Table Data")
    @PostMapping("/excelDataToTable")
    public ResponseEntity<?> getPhNRTableAverageData(@RequestBody MultipartFile[] files) throws IllegalArgumentException {
        if(files == null){
            return ResponseEntity.notFound().build();
        }

        List<PhNRResponse> phNRRawDataList = new ArrayList<>();
        List<String> errors = new ArrayList<>(); //用來收集錯誤訊息的 List
        for (MultipartFile file : files) {
            try{
                String fileName = file.getOriginalFilename();
                String fileNamePattern = "^[A-Za-z()+=&~ \\d\\u4e00-\\u9fa5]+_[^_-]+-.*$";

                if (fileName == null) {
                    throw new AssertionError();
                }

                if (!fileName.matches(fileNamePattern)) {
                    throw new InvalidFileNameException(fileName + " 不符合命名規則！");
                }

                PhNRResponse phNRResponse = new PhNRResponse();
                InputStream inputFile;
                Workbook workbook;

                inputFile = file.getInputStream();

                //轉換格式
                //先嘗試直接使用 Apache POI 讀取檔案
                try {
                    workbook = WorkbookFactory.create(inputFile);
                } catch (Exception e) {
                    //若讀取失敗，則使用 Aspose 轉換為 .xlsx 格式
                    inputFile = CommonMethods.convertToXlsx(file);
                    workbook = WorkbookFactory.create(inputFile);
                }

                int endSubstringNum = fileName.indexOf("-");
                String needFileName = fileName.substring(0, endSubstringNum);
                phNRResponse.setGroupName(needFileName);
                Sheet sheet = workbook.getSheetAt(0);
                List<Object> phnrRightRawData = phNRService.findPhNRTableData(sheet);
                phNRResponse.setRightEyeData(phnrRightRawData);

                phNRRawDataList.add(phNRResponse);

                workbook.close();
                inputFile.close();
            } catch (InvalidFileNameException e) {
                errors.add(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (!errors.isEmpty()) {
            // 如果有錯誤，將錯誤訊息返回
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok(phNRRawDataList);
    }

    @ActionLogs(action = "獲取 PhNR Wave Raw Data")
    @PostMapping("/excelDataToLine")
    public ResponseEntity<?> getPhNRRawData(@RequestBody MultipartFile[] files) throws IllegalArgumentException {
        if(files == null){
            return ResponseEntity.notFound().build();
        }
        List<PhNREntity> phNREntityList = new ArrayList<>();
        List<String> errors = new ArrayList<>(); //用來收集錯誤訊息的 List
        for (MultipartFile file : files) {
            try{
                System.out.println(file.getContentType());
                String fileName = file.getOriginalFilename();
                String fileNamePattern = "^[A-Za-z()+=&~ \\d\\u4e00-\\u9fa5]+_[^_-]+-.*$";

                if (fileName == null) {
                    throw new AssertionError();
                }

                if (!fileName.matches(fileNamePattern)) {
                    throw new InvalidFileNameException(fileName + " 不符合命名規則！");
                }

                PhNREntity phNREntity = new PhNREntity();

                InputStream inputFile;
                Workbook workbook;

                inputFile = file.getInputStream();

                //轉換格式
                //先嘗試直接使用 Apache POI 讀取檔案
                try {
                    workbook = WorkbookFactory.create(inputFile);
                } catch (Exception e) {
                    //若讀取失敗，則使用 Aspose 轉換為 .xlsx 格式
                    try {
                        inputFile = CommonMethods.convertToXlsx(file);
                    } catch (EvaluationLimitExceededException ex) {
                        if (ex.getMessage().contains("files exceeding limitation")) {
                            errors.add(ex.getMessage());
                        } else {
                            throw new RuntimeException("Aspose 操作失敗：" + ex.getMessage());
                        }
                    }

                    workbook = WorkbookFactory.create(inputFile);
                }

                int endSubstringNum = fileName.indexOf("-");
                String needFileName = fileName.substring(0, endSubstringNum);
                phNREntity.setExpGroupName(needFileName);
                Sheet sheet = workbook.getSheetAt(0);
                List<Double> rightEyeData = phNRService.findAllDataByColumnIndex(sheet, 25);
                phNREntity.setExpRightEyeRawData(rightEyeData);
                List<Double> leftEyeData = phNRService.findAllDataByColumnIndex(sheet, 26);
                phNREntity.setExpLeftEyeRawData(leftEyeData);
                List<Double> milliSecData = phNRService.findAllDataByColumnIndex(sheet, 20);
                phNREntity.setDataMilliSec(milliSecData);
                phNREntityList.add(phNREntity);

                workbook.close();
                inputFile.close();
            } catch (InvalidFileNameException e) {
                errors.add(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (!errors.isEmpty()) {
            // 如果有錯誤，將錯誤訊息返回
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok(phNREntityList);
    }
}
