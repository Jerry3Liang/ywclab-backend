package com.jerryliang.ywclab.controller;

import com.jerryliang.ywclab.Exception.EvaluationLimitExceededException;
import com.jerryliang.ywclab.Exception.InvalidFileNameException;
import com.jerryliang.ywclab.annotation.ActionLogs;
import com.jerryliang.ywclab.dto.VEPResponse;
import com.jerryliang.ywclab.model.VEPEntity;
import com.jerryliang.ywclab.service.VEPService;
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
@RequestMapping("/vep")
public class VEPController {

    @Autowired
    private VEPService vepService;

    @ActionLogs(action = "獲取 VEP Table Data")
    @PostMapping("/excelDataToTable")
    public ResponseEntity<?> getVEPTableAverageData(@RequestBody MultipartFile[] files) throws IllegalArgumentException {
        if(files == null){
            return ResponseEntity.notFound().build();
        }

        List<VEPResponse> VEPRawDataList = new ArrayList<>();
        List<String> errors = new ArrayList<>(); //用來收集錯誤訊息的 List
        for (MultipartFile file : files) {
            try{
                String fileName = file.getOriginalFilename();
                String fileNamePattern = "^[A-Za-z()+=&~ \\d\\u4e00-\\u9fa5]+_\\d+-.*$";

                if (fileName == null) {
                    throw new AssertionError();
                }

                if (!fileName.matches(fileNamePattern)) {
                    throw new InvalidFileNameException(fileName + " 不符合命名規則！");
                }

                VEPResponse vepResponse = new VEPResponse();
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
                vepResponse.setGroupName(needFileName);
                Sheet sheet = workbook.getSheetAt(0);
                List<Object> vepRightRawData = vepService.findVEPTableData(sheet);
                vepResponse.setRightEyeData(vepRightRawData);

                VEPRawDataList.add(vepResponse);

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

        return ResponseEntity.ok(VEPRawDataList);
    }

    @ActionLogs(action = "獲取 VEP Wave Raw Data")
    @PostMapping("/excelDataToLine")
    public ResponseEntity<?> getVEPRawData(@RequestBody MultipartFile[] files) throws IllegalArgumentException {
        if(files == null){
            return ResponseEntity.notFound().build();
        }
        List<VEPEntity> cWaveEntityList = new ArrayList<>();
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

                VEPEntity vEPEntity = new VEPEntity();

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
                vEPEntity.setExpGroupName(needFileName);
                Sheet sheet = workbook.getSheetAt(0);
                List<Double> rightEyeData = vepService.findAllDataByColumnIndex(sheet, 25);
                vEPEntity.setExpRightEyeRawData(rightEyeData);
                List<Double> leftEyeData = vepService.findAllDataByColumnIndex(sheet, 26);
                vEPEntity.setExpLeftEyeRawData(leftEyeData);
                List<Double> milliSecData = vepService.findAllDataByColumnIndex(sheet, 20);
                vEPEntity.setDataMilliSec(milliSecData);
                cWaveEntityList.add(vEPEntity);

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

        return ResponseEntity.ok(cWaveEntityList);
    }
}
