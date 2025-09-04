package com.jerryliang.ywclab.controller;

import com.jerryliang.ywclab.Exception.EvaluationLimitExceededException;
import com.jerryliang.ywclab.Exception.GarbledCharactersException;
import com.jerryliang.ywclab.Exception.InvalidFileNameException;
import com.jerryliang.ywclab.annotation.ActionLogs;
import com.jerryliang.ywclab.dto.*;
import com.jerryliang.ywclab.model.CWaveEntity;
import com.jerryliang.ywclab.service.CWaveService;
import com.jerryliang.ywclab.utils.CommonMethods;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@CrossOrigin
@RestController
@RequestMapping("/c_wave")
public class CWaveController {

    @Autowired
    private CWaveService cWaveService;

    //最後修改好檔名的 URL
    @Value("${file.updateFileName.path}")
    private String finalNewFileNamePath;

    @ActionLogs(action = "獲取 C Wave Table Data")
    @PostMapping("/excelDataToTable")
    public ResponseEntity<?> getCWaveTableAverageData(@RequestBody MultipartFile[] files) throws IllegalArgumentException {
        if(files == null){
            return ResponseEntity.notFound().build();
        }

        List<CWaveResponse> CWaveRawDataList = new ArrayList<>();
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

                CWaveResponse cWaveResponse = new CWaveResponse();
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
                cWaveResponse.setGroupName(needFileName);
                Sheet sheet = workbook.getSheetAt(0);

                if(sheet.getPhysicalNumberOfRows() == 0){
                    throw new GarbledCharactersException(fileName + " 內容可能為亂碼或空白，導致讀取不到內容！");
                } else {
                    // 設定字元範圍，判斷是否為亂碼
                    Pattern validPattern = Pattern.compile("[\\w\\s\\p{L}]+");
                    int rowCount = 0; // 計數器初始化為 0
                    for (Row row : sheet) {
                        // 只檢查前 6 行
                        if (rowCount >= 6) {
                            break;
                        }
                        for (Cell cell : row) {
                            if (cell.getCellType() == CellType.STRING) {
                                String cellValue = cell.getStringCellValue();
                                if (!validPattern.matcher(cellValue).matches()) {
                                    throw new GarbledCharactersException(fileName + " 內容可能為亂碼，導致讀取不到內容！ 疑似亂碼的內容: " + cellValue);
                                }
                            }
                        }

                        rowCount++; // 每次檢查完一行後增加計數器
                    }
                }

                int expDateStartIndex = cWaveService.findRowIndexByCellValue(sheet, "Date", 7);
                String expDate = cWaveService.findExpDateByRowIndex(sheet, expDateStartIndex + 2);
                cWaveResponse.setExpDate(expDate);

                int luxStartIndex = cWaveService.findRowIndexByCellValue(sheet, "cd.s/m", 2);
                Double lux = cWaveService.findLuxDataByRowIndex(sheet, luxStartIndex + 2);
                cWaveResponse.setLux(lux);
                List<Object> cWaveRawData1 = cWaveService.findFilterData1(sheet);
                cWaveResponse.setEyeDataOne(cWaveRawData1);
                List<Object> cWaveRawData2 = cWaveService.findFilterData2(sheet);
                cWaveResponse.setEyeDataTwo(cWaveRawData2);

                CWaveRawDataList.add(cWaveResponse);

                workbook.close();
                inputFile.close();

            } catch (InvalidFileNameException | GarbledCharactersException e) {
                errors.add(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (!errors.isEmpty()) {
            // 如果有錯誤，將錯誤訊息返回
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok(CWaveRawDataList);
    }

    @ActionLogs(action = "獲取 C Wave Raw Data")
    @PostMapping("/excelDataToLine")
    public ResponseEntity<?> getCWaveRawData(@RequestBody MultipartFile[] files) throws IllegalArgumentException {
        if(files == null){
            return ResponseEntity.notFound().build();
        }
        List<CWaveEntity> cWaveEntityList = new ArrayList<>();
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

                CWaveEntity cWaveEntity = new CWaveEntity();
                InputStream inputFile;
                Workbook workbook;

                inputFile = file.getInputStream();

                //轉換格式
                //先嘗試直接使用 Apache POI 讀取檔案
                try {
                    workbook = WorkbookFactory.create(inputFile);
                } catch (Exception e) {
                    try {
                        //若讀取失敗，則使用 Aspose 轉換為 .xlsx 格式
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
                cWaveEntity.setExpGroupName(needFileName);
                Sheet sheet = workbook.getSheetAt(0);

                int rightEyeData1StartIndex = cWaveService.findRowIndexByCellValue(sheet, "Chan 1", 7);
                List<Double> rightEyeData1 = cWaveService.findAllDataByColumnIndex(sheet, 7, rightEyeData1StartIndex + 2);
                cWaveEntity.setExpRightEyeRawData1(rightEyeData1);

                int rightEyeData2StartIndex = cWaveService.findRowIndexByCellValue(sheet, "Chan 3", 15);
                List<Double> rightEyeData2 = cWaveService.findAllDataByColumnIndex(sheet, 15, rightEyeData2StartIndex + 2);
                cWaveEntity.setExpRightEyeRawData2(rightEyeData2);

                int leftEyeData1StartIndex = cWaveService.findRowIndexByCellValue(sheet, "Chan 2", 11);
                List<Double> leftEyeData1 = cWaveService.findAllDataByColumnIndex(sheet, 11, leftEyeData1StartIndex + 2);
                cWaveEntity.setExpLeftEyeRawData1(leftEyeData1);

                int leftEyeData2StartIndex = cWaveService.findRowIndexByCellValue(sheet, "Chan 4", 19);
                List<Double> leftEyeData2 = cWaveService.findAllDataByColumnIndex(sheet, 19, leftEyeData2StartIndex + 2);
                cWaveEntity.setExpLeftEyeRawData2(leftEyeData2);

                int timeStartIndex = cWaveService.findRowIndexByCellValue(sheet, "Time (ms)", 6);
                List<Double> milliSecData = cWaveService.findAllDataByColumnIndex(sheet, 6, timeStartIndex + 1);
                cWaveEntity.setDataMilliSec(milliSecData);

                cWaveEntityList.add(cWaveEntity);

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

    @ActionLogs(action = "獲取 AB Wave Raw Data")
    @PostMapping("/excelABDataToLine")
    public ResponseEntity<?> getABWaveRawData(@RequestBody MultipartFile[] files) throws IllegalArgumentException {
        if(files == null){
            return ResponseEntity.notFound().build();
        }
        List<CWaveEntity> cWaveEntityList = new ArrayList<>();
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

                CWaveEntity cWaveEntity = new CWaveEntity();
                InputStream inputFile;
                Workbook workbook;

                inputFile = file.getInputStream();

                //轉換格式
                //先嘗試直接使用 Apache POI 讀取檔案
                try {
                    workbook = WorkbookFactory.create(inputFile);
                } catch (Exception e) {
                    try {
                        //若讀取失敗，則使用 Aspose 轉換為 .xlsx 格式
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
                cWaveEntity.setExpGroupName(needFileName);
                Sheet sheet = workbook.getSheetAt(0);

                int rightEyeData1StartIndex = cWaveService.findRowIndexByCellValue(sheet, "Chan 1", 7);
                List<Double> rightEyeData1 = cWaveService.findABWaveDataByColumnIndex(sheet, 7, rightEyeData1StartIndex + 2);
                cWaveEntity.setExpRightEyeRawData1(rightEyeData1);

                int rightEyeData2StartIndex = cWaveService.findRowIndexByCellValue(sheet, "Chan 3", 15);
                List<Double> rightEyeData2 = cWaveService.findABWaveDataByColumnIndex(sheet, 15, rightEyeData2StartIndex + 2);
                cWaveEntity.setExpRightEyeRawData2(rightEyeData2);

                int leftEyeData1StartIndex = cWaveService.findRowIndexByCellValue(sheet, "Chan 2", 11);
                List<Double> leftEyeData1 = cWaveService.findABWaveDataByColumnIndex(sheet, 11, leftEyeData1StartIndex + 2);
                cWaveEntity.setExpLeftEyeRawData1(leftEyeData1);

                int leftEyeData2StartIndex = cWaveService.findRowIndexByCellValue(sheet, "Chan 4", 19);
                List<Double> leftEyeData2 = cWaveService.findABWaveDataByColumnIndex(sheet, 19, leftEyeData2StartIndex + 2);
                cWaveEntity.setExpLeftEyeRawData2(leftEyeData2);

                int timeStartIndex = cWaveService.findRowIndexByCellValue(sheet, "Time (ms)", 6);
                List<Double> milliSecData = cWaveService.findABWaveDataByColumnIndex(sheet, 6, timeStartIndex + 1);
                cWaveEntity.setDataMilliSec(milliSecData);

                cWaveEntityList.add(cWaveEntity);

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

    @ActionLogs(action = "透過 C Wave Table Data 進行分組")
    @PostMapping("/groupingByTable")
    public ResponseEntity<?> groupingByCWaveTableAverageData(@RequestBody MultipartFile[] files) throws IllegalArgumentException {
        if(files == null){
            return ResponseEntity.notFound().build();
        }

        List<GroupByCWaveResponse> tableDataList = new ArrayList<>();
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

                GroupByCWaveResponse cWaveTableResponse = new GroupByCWaveResponse();
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
                cWaveTableResponse.setGroupName(needFileName);
                Sheet sheet = workbook.getSheetAt(0);

                if(sheet.getPhysicalNumberOfRows() == 0){
                    throw new GarbledCharactersException(fileName + " 內容可能為亂碼或空白，導致讀取不到內容！");
                } else {
                    // 設定字元範圍，判斷是否為亂碼
                    Pattern validPattern = Pattern.compile("[\\w\\s\\p{L}]+");
                    int rowCount = 0; // 計數器初始化為 0
                    for (Row row : sheet) {
                        // 只檢查前 6 行
                        if (rowCount >= 6) {
                            break;
                        }
                        for (Cell cell : row) {
                            if (cell.getCellType() == CellType.STRING) {
                                String cellValue = cell.getStringCellValue();
                                if (!validPattern.matcher(cellValue).matches()) {
                                    throw new GarbledCharactersException(fileName + " 內容可能為亂碼，導致讀取不到內容！ 疑似亂碼的內容: " + cellValue);
                                }
                            }
                        }

                        rowCount++; //每次檢查完一行後增加計數器
                    }
                }

                int valueStartIndex = cWaveService.findRowIndexByCellValue(sheet, "uV", 11);

                double rightEyeAWave1 = cWaveService.findDataByRowIndex(sheet, valueStartIndex + 1);
                double rightEyeAWave2 = cWaveService.findDataByRowIndex(sheet,valueStartIndex + 7);
                double rightEyeBWave1 = cWaveService.findDataByRowIndex(sheet, valueStartIndex + 2);
                double rightEyeBWave2 = cWaveService.findDataByRowIndex(sheet, valueStartIndex + 8);

                //檢查並交換 A波和 B波的值
                if (rightEyeAWave1 >= 0 && rightEyeBWave1 <= 0) {
                    //如果 A波為正數且 B波為負數，交換它們
                    double temp = rightEyeAWave1;
                    rightEyeAWave1 = rightEyeBWave1;
                    rightEyeBWave1 = temp;
                }
                if (rightEyeAWave2 >= 0 && rightEyeBWave2 <= 0) {
                    //如果 A波為正數且 B波為負數，交換它們
                    double temp = rightEyeAWave2;
                    rightEyeAWave2 = rightEyeBWave2;
                    rightEyeBWave2 = temp;
                }

                cWaveTableResponse.setRightEyeAverageAWave((rightEyeAWave1 + rightEyeAWave2) / 2);
                cWaveTableResponse.setRightEyeAverageBWave((rightEyeBWave1 + rightEyeBWave2) / 2);

                double rightEyeCWave1 = cWaveService.findDataByRowIndex(sheet, valueStartIndex + 3);
                double rightEyeCWave2 = cWaveService.findDataByRowIndex(sheet, valueStartIndex + 9);
                cWaveTableResponse.setRightEyeAverageCWave((rightEyeCWave1 + rightEyeCWave2) / 2);

                double leftEyeAWave1 = cWaveService.findDataByRowIndex(sheet,valueStartIndex + 4);
                double leftEyeAWave2 = cWaveService.findDataByRowIndex(sheet,valueStartIndex + 10);
                double leftEyeBWave1 = cWaveService.findDataByRowIndex(sheet, valueStartIndex + 5);
                double leftEyeBWave2 = cWaveService.findDataByRowIndex(sheet, valueStartIndex + 11);

                // 檢查並交換 A波和 B波的值
                if (leftEyeAWave1 >= 0 && leftEyeBWave1 <= 0) {
                    // 如果 A波為正數且 B波為負數，交換它們
                    double temp = leftEyeAWave1;
                    leftEyeAWave1 = leftEyeBWave1;
                    leftEyeBWave1 = temp;
                }
                if (leftEyeAWave2 >= 0 && leftEyeBWave2 <= 0) {
                    // 如果 A波為正數且 B波為負數，交換它們
                    double temp = leftEyeAWave2;
                    leftEyeAWave2 = leftEyeBWave2;
                    leftEyeBWave2 = temp;
                }

                cWaveTableResponse.setLeftEyeAverageAWave((leftEyeAWave1 + leftEyeAWave2) / 2);
                cWaveTableResponse.setLeftEyeAverageBWave((leftEyeBWave1 + leftEyeBWave2) / 2);

                double leftEyeCWave1 = cWaveService.findDataByRowIndex(sheet, valueStartIndex + 6);
                double leftEyeCWave2 = cWaveService.findDataByRowIndex(sheet, valueStartIndex + 12);
                cWaveTableResponse.setLeftEyeAverageCWave((leftEyeCWave1 + leftEyeCWave2) / 2);

                tableDataList.add(cWaveTableResponse);

                //取得資料夾 URL
                Path targetDir = Paths.get(finalNewFileNamePath);

                //確保資料夾存在
                if(!Files.exists(targetDir)){
                    Files.createDirectories(targetDir);
                }

                //複製檔案至資料夾
                Path targetPath = targetDir.resolve(fileName);
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                workbook.close();
                inputFile.close();

            } catch (InvalidFileNameException | GarbledCharactersException e) {
                errors.add(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (!errors.isEmpty()) {
            //如果有錯誤，將錯誤訊息返回
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok(tableDataList);
    }

    @ActionLogs(action = "下載 C Wave table data")
    @PostMapping("/downloadCWaveTableData")
    public ResponseEntity<byte[]> downloadCWaveTableData(@RequestBody DownloadCWaveTableDataRequest downloadCWaveTableDataRequest){
        if(downloadCWaveTableDataRequest.getCWaveTableDataMapSet() == null){
            return ResponseEntity.noContent().build();
        }

        return cWaveService.exportCWaveXlsx(
                downloadCWaveTableDataRequest.getCWaveTableDataMapSet(),
                downloadCWaveTableDataRequest.getExpDateMapSet(),
                downloadCWaveTableDataRequest.getLuxDataMapSet()
        );
    }

    @ActionLogs(action = "將 C Wave 檔案原名稱改為以分好組別名稱")
    @PostMapping("/reNameCWaveFile")
    public ResponseEntity<String> cWaveFileReName(@RequestBody ReNameCWaveDataFileRequest reNameCWaveDataFileRequest){
        if(reNameCWaveDataFileRequest.getCWaveNewAndOldFileNameMapSet() == null){
            return ResponseEntity.noContent().build();
        }

        String responseStr = cWaveService
                .fileReName(
                        reNameCWaveDataFileRequest.getCWaveNewAndOldFileNameMapSet(),
                        finalNewFileNamePath,
                        reNameCWaveDataFileRequest.getInputCaseName()
                );

        return ResponseEntity.ok(responseStr);
    }
}
