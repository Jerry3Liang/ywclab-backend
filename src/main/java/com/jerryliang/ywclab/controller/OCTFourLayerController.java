package com.jerryliang.ywclab.controller;

import com.jerryliang.ywclab.Exception.IncompleteDataException;
import com.jerryliang.ywclab.Exception.InvalidFileNameException;
import com.jerryliang.ywclab.annotation.ActionLogs;
import com.jerryliang.ywclab.dto.DownloadOCTFourLayerDataRequest;
import com.jerryliang.ywclab.dto.OCTFourResponse;
import com.jerryliang.ywclab.model.OCTFourLayerEntity;
import com.jerryliang.ywclab.service.OCTFourLayerService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/fourLayer")
public class OCTFourLayerController {

    //搭配 Lombok 的 @RequiredArgsConstructor 實作 Constructor Injection，不使用 @Autowired
    private final OCTFourLayerService octFourLayerService;

    InputStream inputFile;

    @ActionLogs(action = "獲取 OCT Four Layer 最終計算 Data")
    @PostMapping("/excelDataToTable")
    public ResponseEntity<?> getOCTFourLayerFinalData(@RequestBody MultipartFile[] files) throws IllegalArgumentException {
        if(files == null){
            return ResponseEntity.notFound().build();
        }

        List<OCTFourResponse> OCTFourFinalDataList = new ArrayList<>();
        List<String> errors = new ArrayList<>(); //用來收集錯誤訊息的 List

        for (MultipartFile file : files) {
            try{
                String fileName = file.getOriginalFilename();
                String fileNamePattern = "^[A-Za-z()+=&~ \\d\\u4e00-\\u9fa5]+_\\d+[a-zA-Z]{2}-.*$";

                if (fileName == null) {
                    throw new AssertionError();
                }

                if (!fileName.matches(fileNamePattern)) {
                    throw new InvalidFileNameException(fileName + "： 不符合命名規則！");
                }

                OCTFourResponse oCTFourResponse = new OCTFourResponse();

                inputFile = file.getInputStream();
                int endSubstringNum = fileName.indexOf("-");
                String needFileName = fileName.substring(0, endSubstringNum);
                oCTFourResponse.setGroupName(needFileName);
                Workbook workbook = WorkbookFactory.create(inputFile);
                Sheet sheet = workbook.getSheetAt(0);
                List<OCTFourLayerEntity> dataList = octFourLayerService.getOCTFourFinalData(sheet, fileName);
                List<Double> averageDataList = new ArrayList<>();
                double averageTotalData = dataList.get(4).getAverage();
                averageDataList.add(averageTotalData);

                for(int i = 1; i < dataList.size() - 1; i++){
                    double averageData = dataList.get(i).getAverage();
                    averageDataList.add(averageData);
                }

                oCTFourResponse.setOCTFourLayerDataList(averageDataList);
                OCTFourFinalDataList.add(oCTFourResponse);
                workbook.close();
                inputFile.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (IncompleteDataException e) {
                // 捕捉 IncompleteDataException
                errors.add(e.getMessage());
            }catch (InvalidFileNameException e){
                errors.add(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (!errors.isEmpty()) {
            // 如果有錯誤，將錯誤訊息返回
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.ok(OCTFourFinalDataList);
    }

    @ActionLogs(action = "下載 OCT Four Layer data")
    @PostMapping("/downloadOCTFourLayerData")
    public ResponseEntity<byte[]> downloadFourLayerTableData(@RequestBody DownloadOCTFourLayerDataRequest downloadOCTFourLayerDataRequest){
        if(downloadOCTFourLayerDataRequest.getOctFourLayerDataMapSet() == null){
            return ResponseEntity.noContent().build();
        }

        return octFourLayerService.exportOctFourLayerXlsx(downloadOCTFourLayerDataRequest.getOctFourLayerDataMapSet());
    }
}
