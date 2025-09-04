package com.jerryliang.ywclab.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jerryliang.ywclab.Exception.InvalidFileNameException;
import com.jerryliang.ywclab.annotation.ActionLogs;
import com.jerryliang.ywclab.dto.ReNameExpFileNameExceptCWaveRequest;
import com.jerryliang.ywclab.service.UpdateFileNameService;
import com.jerryliang.ywclab.utils.ReNameUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/updateFileName")
public class UpdateFileNameController {

    @Autowired
    private UpdateFileNameService updateFileNameService;

    //最後修改好檔名的 URL
    @Value("${file.updateFileName.path}")
    private String finalNewFileNamePath;

    @ActionLogs(action = "根據 Mapping Excel 修改對應的檔名")
    @PostMapping(value = "/reFileNameExceptCWave", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateFileNameByMappingExcel(@ModelAttribute ReNameExpFileNameExceptCWaveRequest reNameExpFileNameExceptCWaveRequest) throws IllegalArgumentException {

        String responseStr  = ""; //改完檔名回傳的訊息

        //各實驗檔名規則
        String OPsFileNamePattern = "^[A-Za-z()+=&~ \\d\\u4e00-\\u9fa5]+_[^_-]+-.*$";
        String OCTTotalLayerFileNamePattern = "^[A-Za-z()+=&~ \\d\\u4e00-\\u9fa5]+_\\d+[a-zA-Z]-.*$";
        String OCTFourLayerFileNamePattern = "^[A-Za-z()+=&~ \\d\\u4e00-\\u9fa5]+_\\d+[a-zA-Z]{2}-.*$";

        MultipartFile[] files = reNameExpFileNameExceptCWaveRequest.getFiles();
        MultipartFile mapExcelFile = reNameExpFileNameExceptCWaveRequest.getMapExcelFile();
        String inputCaseName = reNameExpFileNameExceptCWaveRequest.getInputCaseName();

        if(files == null){
            return ResponseEntity.notFound().build();
        }

        List<String> errors = new ArrayList<>(); //用來收集錯誤訊息的 List

        for (MultipartFile file : files) {
            try{
                String fileName = file.getOriginalFilename();

                if (fileName == null) {
                    throw new AssertionError();
                }

                //取得資料夾 URL
                Path targetDir = Paths.get(finalNewFileNamePath);

                //確保資料夾存在
                if(!Files.exists(targetDir)){
                    Files.createDirectories(targetDir);
                }

                if (fileName.matches(OCTFourLayerFileNamePattern) || fileName.matches(OCTTotalLayerFileNamePattern) || fileName.matches(OPsFileNamePattern)) {
                    //複製檔案至資料夾
                    Path targetPath = targetDir.resolve(fileName);
                    Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    throw new InvalidFileNameException(fileName + " 不符合命名規則！");
                }
            } catch (InvalidFileNameException e) {
                errors.add(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        try {
            //獲得資料夾裡每個檔案
            File directory = new File(finalNewFileNamePath);
            if(!directory.exists() || !directory.isDirectory()){
                throw new IllegalArgumentException("不存在的資料夾路徑： " + finalNewFileNamePath);
            }

            File[] copyFiles = directory.listFiles();

            if (copyFiles != null) {
                for(File file : copyFiles){
                    String fileName = file.getName();

                    //將前端傳進來的 JSON String 轉回 Map
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> catchFileNameMap = mapper.readValue(reNameExpFileNameExceptCWaveRequest.getFileNameMap(), new TypeReference<>() {
                    });

                    if(mapExcelFile == null) {
                        if(fileName.matches(OCTFourLayerFileNamePattern)) {
                            responseStr = ReNameUtil.fileRename(
                                    catchFileNameMap,
                                    finalNewFileNamePath,
                                    inputCaseName,
                                    file,
                                    "OCT Four Layer",
                                    2
                            );
                        } else if(fileName.matches(OCTTotalLayerFileNamePattern)) {
                            responseStr = ReNameUtil.fileRename(
                                    catchFileNameMap,
                                    finalNewFileNamePath,
                                    inputCaseName,
                                    file,
                                    "OCT Total Layer",
                                    1
                            );
                        } else if(fileName.matches(OPsFileNamePattern)) {
                            responseStr = ReNameUtil.fileRename(
                                    catchFileNameMap,
                                    finalNewFileNamePath,
                                    inputCaseName,
                                    file,
                                    "OPs",
                                    0
                            );
                        }
                    } else {
                        //讀取舊編號與新編號的 Excel 檔
                        InputStream inputFile = mapExcelFile.getInputStream();
                        Workbook workbook = WorkbookFactory.create(inputFile);
                        Sheet sheet = workbook.getSheetAt(0);
                        Map<String, String> oldAndNewFileNameMapSet = updateFileNameService.getOldAndNewFileNameMap(sheet);
                        if(fileName.matches(OCTFourLayerFileNamePattern)) {
                            responseStr = ReNameUtil.fileRename(
                                    oldAndNewFileNameMapSet,
                                    finalNewFileNamePath,
                                    inputCaseName,
                                    file,
                                    "OCT Four Layer",
                                    2
                            );
                        } else if(fileName.matches(OCTTotalLayerFileNamePattern)) {
                            responseStr = ReNameUtil.fileRename(
                                    oldAndNewFileNameMapSet,
                                    finalNewFileNamePath,
                                    inputCaseName,
                                    file,
                                    "OCT Total Layer",
                                    1
                            );
                        } else if(fileName.matches(OPsFileNamePattern)) {
                            responseStr = ReNameUtil.fileRename(
                                    oldAndNewFileNameMapSet,
                                    finalNewFileNamePath,
                                    inputCaseName,
                                    file,
                                    "OPs",
                                    0
                            );
                        }

                        workbook.close();
                        inputFile.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (!errors.isEmpty()) {
            // 如果有錯誤，將錯誤訊息返回
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok(responseStr);
    }

}
