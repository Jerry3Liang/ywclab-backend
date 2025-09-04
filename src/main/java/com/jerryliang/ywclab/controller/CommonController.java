package com.jerryliang.ywclab.controller;

import com.jerryliang.ywclab.annotation.ActionLogs;
import com.jerryliang.ywclab.dto.CheckCWaveFolderNameRequest;
import com.jerryliang.ywclab.dto.CheckFolderNameRequest;
import com.jerryliang.ywclab.utils.CommonMethods;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/common")
public class CommonController {

    //檔案資料夾的 URL
    @Value("${file.updateFileName.path}")
    private String finalNewFileNamePath;

    @ActionLogs(action = "檢查資料夾是否存在")
    @PostMapping("/checkFolderName")
    public ResponseEntity<Boolean> checkFolderName(@RequestBody CheckFolderNameRequest checkFolderNameRequest) {

        //各實驗檔名規則
        String OPsFileNamePattern = "^[A-Za-z()+=&~ \\d\\u4e00-\\u9fa5]+_[^_-]+-.*$";
        String OCTTotalLayerFileNamePattern = "^[A-Za-z()+=&~ \\d\\u4e00-\\u9fa5]+_\\d+[a-zA-Z]-.*$";
        String OCTFourLayerFileNamePattern = "^[A-Za-z()+=&~ \\d\\u4e00-\\u9fa5]+_\\d+[a-zA-Z]{2}-.*$";

        String expFileName = checkFolderNameRequest.getFileName();

        String folderPath = "";

        if(expFileName.matches(OCTFourLayerFileNamePattern)) {
            folderPath = finalNewFileNamePath + "/OCT Four Layer/";
        } else if(expFileName.matches(OCTTotalLayerFileNamePattern)) {
            folderPath = finalNewFileNamePath + "/OCT Total Layer/";
        } else if(expFileName.matches(OPsFileNamePattern)) {
            folderPath = finalNewFileNamePath + "/OPs/";
        }

        return CommonMethods.checkFolderName(folderPath, checkFolderNameRequest.getInputCaseName());
    }

    @ActionLogs(action = "檢查 C Wave 的資料夾是否存在")
    @PostMapping("/checkCWaveFolderName")
    public ResponseEntity<Boolean> checkCWaveFolderName(@RequestBody CheckCWaveFolderNameRequest checkCWaveFolderNameRequest) {

        String folderPath = finalNewFileNamePath + "/C Wave/";

        return CommonMethods.checkFolderName(folderPath, checkCWaveFolderNameRequest.getInputCaseName());
    }
}
