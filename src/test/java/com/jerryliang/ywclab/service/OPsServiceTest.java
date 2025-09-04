package com.jerryliang.ywclab.service;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.util.List;

@SpringBootTest
public class OPsServiceTest {

    @Autowired
    private OPsService oPsService;

    @Test
    public void testFindAllDataByColumnIndex(){
        String filePath = "/Users/liangchejui/Desktop/程式語言相關/YWC 動物實驗軟體/OPs 測試用檔案/test4/A_01-OPs-day28.xls";
        try(FileInputStream file = new FileInputStream(filePath)){
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);
            List<Double> timeList =  oPsService.findAllDataByColumnIndex(sheet, 40, "Time (ms)");
            List<Double> dataList = oPsService.findAllDataByColumnIndex(sheet, 53, "Chan 4");
            String minPointSelected = "LOP3";

            List<List<Double>> OPsDataAndMilliSecList = oPsService.findOPsDataAndMilliSec(minPointSelected, dataList, timeList);
            for(List<Double> a : OPsDataAndMilliSecList){
                System.out.println(a);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}