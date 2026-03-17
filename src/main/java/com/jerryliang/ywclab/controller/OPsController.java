package com.jerryliang.ywclab.controller;

import com.jerryliang.ywclab.Exception.EvaluationLimitExceededException;
import com.jerryliang.ywclab.Exception.InvalidFileNameException;
import com.jerryliang.ywclab.annotation.ActionLogs;
import com.jerryliang.ywclab.dto.DownloadOPsDataRequest;
import com.jerryliang.ywclab.dto.OPsAnalyzeRequest;
import com.jerryliang.ywclab.dto.OPsAnalyzeResponse;
import com.jerryliang.ywclab.model.OPsEntity;
import com.jerryliang.ywclab.service.OPsService;
import com.jerryliang.ywclab.utils.CommonMethods;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/ops")
public class OPsController {

    @Autowired
    private OPsService oPsService;

    @ActionLogs(action = "獲取 OPs Raw Data")
    @PostMapping("/excelDataToLine")
    public ResponseEntity<?> getOPsRawData(@RequestBody MultipartFile[] files) throws IllegalArgumentException {
        if(files == null){
            return ResponseEntity.notFound().build();
        }
        List<OPsEntity> oPsEntityList = new ArrayList<>();
        List<String> errors = new ArrayList<>(); //用來收集錯誤訊息的 List
        for (MultipartFile file : files) {
            try{
//                System.out.println(file.getContentType());
                String fileName = file.getOriginalFilename();
                String fileNamePattern = "^[A-Za-z()+=&~ \\d\\u4e00-\\u9fa5]+_[^_-]+-.*$";

                if (fileName == null) {
                    throw new AssertionError();
                }

                if (!fileName.matches(fileNamePattern)) {
                    throw new InvalidFileNameException(fileName + " 不符合命名規則！");
                }

                OPsEntity oPsEntity = new OPsEntity();
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
                oPsEntity.setExpGroupName(needFileName);
                Sheet sheet = workbook.getSheetAt(0);
                List<Double> rightEyeData = oPsService.findAllDataByColumnIndex(sheet, 49, "Chan 3");
                oPsEntity.setExpRightEyeRawData(rightEyeData);
                List<Double> leftEyeData = oPsService.findAllDataByColumnIndex(sheet, 53, "Chan 4");
                oPsEntity.setExpLeftEyeRawData(leftEyeData);
                List<Double> milliSecData = oPsService.findAllDataByColumnIndex(sheet, 40, "Step 3");
                oPsEntity.setDataMilliSec(milliSecData);
                oPsEntityList.add(oPsEntity);

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

        return ResponseEntity.ok(oPsEntityList);
    }

    @ActionLogs(action = "執行所選最低點的方法")
    @PostMapping("/analyzeOPs")
    public ResponseEntity<?> analyzeOPs(@RequestBody List<OPsAnalyzeRequest> oPsAnalyzeRequestList){
        if(oPsAnalyzeRequestList.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        List<OPsAnalyzeResponse> responseList = new ArrayList<>();
        for (OPsAnalyzeRequest oPsAnalyzeRequest : oPsAnalyzeRequestList) {
            OPsAnalyzeResponse oPsAnalyzeResponse = new OPsAnalyzeResponse();
            oPsAnalyzeResponse.setExpGroupName(oPsAnalyzeRequest.getExpGroupName());

            List<List<Double>> rightOPsAndMilliSec = oPsService.findOPsDataAndMilliSec
                    (
                            oPsAnalyzeRequest.getRightMinPointSelected(),
                            oPsAnalyzeRequest.getRightEyeRawData(),
                            oPsAnalyzeRequest.getMilliSec()
                    );
            oPsAnalyzeResponse.setRightEyeOPsData(rightOPsAndMilliSec.get(0));
            oPsAnalyzeResponse.setRightEyeOPsMilliSec(rightOPsAndMilliSec.get(1));
            if(rightOPsAndMilliSec.get(0).size() == 5) {
                double rightEyeOPSSum = rightOPsAndMilliSec.get(0).stream().mapToDouble(Double::doubleValue).sum();
                BigDecimal rightRounded = BigDecimal.valueOf(rightEyeOPSSum)
                        .setScale(2, RoundingMode.HALF_UP);

                oPsAnalyzeResponse.setRightEyeOPsTotal(rightRounded.doubleValue());
                oPsAnalyzeResponse.setRightEyeOPsTotalMilliSec(0.0);
            }

            List<List<Double>> leftOPsAndMilliSec = oPsService.findOPsDataAndMilliSec
                    (
                            oPsAnalyzeRequest.getLeftMinPointSelected(),
                            oPsAnalyzeRequest.getLeftEyeRawData(),
                            oPsAnalyzeRequest.getMilliSec()
                    );
            oPsAnalyzeResponse.setLeftEyeOPsData(leftOPsAndMilliSec.get(0));
            oPsAnalyzeResponse.setLeftEyeOPsMilliSec(leftOPsAndMilliSec.get(1));
            if(leftOPsAndMilliSec.get(0).size() == 5) {
                double leftEyeOPSSum = leftOPsAndMilliSec.get(0).stream().mapToDouble(Double::doubleValue).sum();
                BigDecimal leftRounded = BigDecimal.valueOf(leftEyeOPSSum)
                        .setScale(2, RoundingMode.HALF_UP);

                oPsAnalyzeResponse.setLeftEyeOPsTotal(leftRounded.doubleValue());
                oPsAnalyzeResponse.setLeftEyeOPsTotalMilliSec(0.0);
            }

            responseList.add(oPsAnalyzeResponse);
        }

        return ResponseEntity.ok(responseList);
    }

    @ActionLogs(action = "下載 OPs table data")
    @PostMapping("/downloadOPsData")
    public ResponseEntity<byte[]> downloadOPsData(@RequestBody DownloadOPsDataRequest downloadOPsDataRequest){
        if(downloadOPsDataRequest.getOpsDataMapSet() == null){
            return ResponseEntity.noContent().build();
        }

//        System.out.println("DownloadOPsDataRequest : " + downloadOPsDataRequest);
        return oPsService.exportOPsXlsx(downloadOPsDataRequest.getOpsDataMapSet());
    }
}
