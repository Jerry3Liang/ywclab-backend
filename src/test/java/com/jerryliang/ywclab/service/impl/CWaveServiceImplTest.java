package com.jerryliang.ywclab.service.impl;

import com.jerryliang.ywclab.service.CWaveService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.util.List;

@SpringBootTest
class CWaveServiceImplTest {

    @Autowired
    private CWaveService cWaveService;

    @Test
    public void testFindFilterData1() {
        String filePath = "/Users/liangchejui/Desktop/程式語言相關/YWC 動物實驗軟體/C-Wave 測試用檔案/Day0/BL_03-C_Wave Day0.xls";
        try(FileInputStream file = new FileInputStream(filePath)){
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);
            List<Object> Data1 = cWaveService.oldFindFilterData1(sheet);
            List<Object> Data2 = cWaveService.oldFindFilterData2(sheet);

            System.out.println(Data1);
            System.out.println(Data2);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}