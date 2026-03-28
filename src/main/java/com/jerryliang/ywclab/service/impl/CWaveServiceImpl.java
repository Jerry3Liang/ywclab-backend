package com.jerryliang.ywclab.service.impl;

import com.jerryliang.ywclab.dto.CWaveDataDto;
import com.jerryliang.ywclab.dto.CWaveTableToDownloadEntity;
import com.jerryliang.ywclab.model.CWaveTableEntity;
import com.jerryliang.ywclab.service.CWaveService;
import com.jerryliang.ywclab.utils.DatetimeConverter;
import com.jerryliang.ywclab.utils.ReNameUtil;
import com.jerryliang.ywclab.utils.XlsxUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class CWaveServiceImpl implements CWaveService {

    @Value("${YWCLab.data.excelDownload.path}")
    String EXCEL_PATH;

    @Override
    public List<CWaveTableEntity> newFindFilterData1(Sheet sheet) {
        List<CWaveTableEntity> CWaveTableDataList1 = new ArrayList<>();

        Integer valueStartIndex = findRowIndexByKeyWord(sheet);

        if(valueStartIndex != null){
            int index = valueStartIndex;

            CWaveTableEntity CWaveTableDataR1 = new CWaveTableEntity();
            for(int i = index + 1; i < index + 4; i++){
                CWaveTableDataR1.setWhichEye("RE");
                CWaveDataDto cWaveDataDto = new CWaveDataDto();
                for(int j = 10; j < 13; j++ ){
                    Object rData1 = findDataByColumnIndex(sheet, i, j, index);
                    if(j == 10) {
                        cWaveDataDto.setWaveName((String) rData1);
                    } else if(j == 11) {
                        cWaveDataDto.setValue((double) rData1);
                    } else {
                        cWaveDataDto.setMilliSeconds((double) rData1);
                    }
                }

                if(cWaveDataDto.getWaveName().contains("a")) {
                    CWaveTableDataR1.setAWave(cWaveDataDto);
                } else if(cWaveDataDto.getWaveName().contains("b")) {
                    CWaveTableDataR1.setBWave(cWaveDataDto);
                } else {
                    CWaveTableDataR1.setCWave(cWaveDataDto);
                }
            }

            CWaveTableDataList1.add(CWaveTableDataR1);

            CWaveTableEntity CWaveTableDataL1 = new CWaveTableEntity();
            for(int i = index + 4; i < index + 7; i++){
                CWaveTableDataL1.setWhichEye("LE");
                CWaveDataDto cWaveDataDto = new CWaveDataDto();
                for(int j = 10; j < 13; j++ ){
                    Object rData1 = findDataByColumnIndex(sheet, i, j, index);
                    if(j == 10) {
                        cWaveDataDto.setWaveName((String) rData1);
                    } else if(j == 11) {
                        cWaveDataDto.setValue((double) rData1);
                    } else {
                        cWaveDataDto.setMilliSeconds((double) rData1);
                    }
                }

                if(cWaveDataDto.getWaveName().contains("a")) {
                    CWaveTableDataL1.setAWave(cWaveDataDto);
                } else if(cWaveDataDto.getWaveName().contains("b")) {
                    CWaveTableDataL1.setBWave(cWaveDataDto);
                } else {
                    CWaveTableDataL1.setCWave(cWaveDataDto);
                }
            }

            CWaveTableDataList1.add(CWaveTableDataL1);
        } else {
            throw new IllegalArgumentException("找不到對應的值");
        }

        return CWaveTableDataList1;
    }

    @Override
    public List<CWaveTableEntity> newFindFilterData2(Sheet sheet) {
        List<CWaveTableEntity> CWaveTableDataList2 = new ArrayList<>();

        Integer valueStartIndex = findRowIndexByKeyWord(sheet);

        if(valueStartIndex != null){
            int index = valueStartIndex;

            CWaveTableEntity CWaveTableDataR2 = new CWaveTableEntity();
            for(int i = index + 7; i < index + 10; i++){
                CWaveTableDataR2.setWhichEye("RE");
                CWaveDataDto cWaveDataDto = new CWaveDataDto();
                for(int j = 10; j < 13; j++ ){
                    Object rData1 = findDataByColumnIndex(sheet, i, j, index);
                    if(j == 10) {
                        cWaveDataDto.setWaveName((String) rData1);
                    } else if(j == 11) {
                        cWaveDataDto.setValue((double) rData1);
                    } else {
                        cWaveDataDto.setMilliSeconds((double) rData1);
                    }
                }

                if(cWaveDataDto.getWaveName().contains("a")) {
                    CWaveTableDataR2.setAWave(cWaveDataDto);
                } else if(cWaveDataDto.getWaveName().contains("b")) {
                    CWaveTableDataR2.setBWave(cWaveDataDto);
                } else {
                    CWaveTableDataR2.setCWave(cWaveDataDto);
                }
            }

            CWaveTableDataList2.add(CWaveTableDataR2);

            CWaveTableEntity CWaveTableDataL2 = new CWaveTableEntity();
            for(int i = index + 10; i < index + 13; i++){
                CWaveTableDataL2.setWhichEye("LE");
                CWaveDataDto cWaveDataDto = new CWaveDataDto();
                for(int j = 10; j < 13; j++ ){
                    Object rData1 = findDataByColumnIndex(sheet, i, j, index);
                    if(j == 10) {
                        cWaveDataDto.setWaveName((String) rData1);
                    } else if(j == 11) {
                        cWaveDataDto.setValue((double) rData1);
                    } else {
                        cWaveDataDto.setMilliSeconds((double) rData1);
                    }
                }

                if(cWaveDataDto.getWaveName().contains("a")) {
                    CWaveTableDataL2.setAWave(cWaveDataDto);
                } else if(cWaveDataDto.getWaveName().contains("b")) {
                    CWaveTableDataL2.setBWave(cWaveDataDto);
                } else {
                    CWaveTableDataL2.setCWave(cWaveDataDto);
                }
            }

            CWaveTableDataList2.add(CWaveTableDataL2);
        } else {
            throw new IllegalArgumentException("找不到對應的值");
        }

        return CWaveTableDataList2;
    }

    @Override
    public List<Object> oldFindFilterData1(Sheet sheet) {

        List<Object> CWaveDataList1 = new ArrayList<>();

        Object rData1_i25_j11 = null;
        Object rData1_i26_j11 = null;
        int index_i25_j11 = -1;
        int index_i26_j11 = -1;

        Object lData1_i28_j11 = null;
        Object lData1_i29_j11 = null;
        int index_i28_j11 = -1;
        int index_i29_j11 = -1;

        Integer valueStartIndex = findRowIndexByKeyWord(sheet);
        if(valueStartIndex != null){
            int index = valueStartIndex;
            for(int i = index + 1; i < index + 4; i++){
                for(int j = 10; j < 13; j++ ){
                    Object rData1 = findDataByColumnIndex(sheet, i, j, index);

                    if (i == index + 1 && j == 11) {
                        rData1_i25_j11 = rData1;
                        index_i25_j11 = CWaveDataList1.size();
                    } else if (i == index + 2 && j == 11) {
                        rData1_i26_j11 = rData1;
                        index_i26_j11 = CWaveDataList1.size();
                    }

                    CWaveDataList1.add(rData1);
                }
            }

            isData1Correct(CWaveDataList1, rData1_i25_j11, rData1_i26_j11, index_i25_j11, index_i26_j11);

            for(int i = index + 4; i < index + 7; i++){
                for(int j = 10; j < 13; j++ ){
                    Object lData1 = findDataByColumnIndex(sheet, i, j, index);

                    if (i == index + 4 && j == 11) {
                        lData1_i28_j11 = lData1;
                        index_i28_j11 = CWaveDataList1.size();
                    } else if (i == index + 5 && j == 11) {
                        lData1_i29_j11 = lData1;
                        index_i29_j11 = CWaveDataList1.size();
                    }

                    CWaveDataList1.add(lData1);
                }
            }

            isData1Correct(CWaveDataList1, lData1_i28_j11, lData1_i29_j11, index_i28_j11, index_i29_j11);
        } else {
            throw new IllegalArgumentException("找不到對應的值");
        }

        return CWaveDataList1;
    }

    @Override
    public List<Object> oldFindFilterData2(Sheet sheet) {

        List<Object> CWaveDataList2 = new ArrayList<>();

        Object rData2_i31_j11 = null;
        Object rData2_i32_j11 = null;
        int index_i31_j11 = -1;
        int index_i32_j11 = -1;

        Object lData2_i34_j11 = null;
        Object lData2_i35_j11 = null;
        int index_i34_j11 = -1;
        int index_i35_j11 = -1;

        Integer valueStartIndex = findRowIndexByKeyWord(sheet);
        if(valueStartIndex != null){
            int index = valueStartIndex;
            for(int i = index + 7; i < index + 10; i++){
                for(int j = 10; j < 13; j++ ){
                    Object rData2 = findDataByColumnIndex(sheet, i, j, index);

                    if (i == index + 7 && j == 11) {
                        rData2_i31_j11 = rData2;
                        index_i31_j11 = CWaveDataList2.size();
                    } else if (i == index + 8 && j == 11) {
                        rData2_i32_j11 = rData2;
                        index_i32_j11 = CWaveDataList2.size();
                    }

                    CWaveDataList2.add(rData2);
                }
            }

            isData2Correct(CWaveDataList2, rData2_i31_j11, rData2_i32_j11, index_i31_j11, index_i32_j11);

            for(int i = index + 10; i < index + 13; i++){
                for(int j = 10; j < 13; j++ ){
                    Object lData2 = findDataByColumnIndex(sheet, i, j, index);

                    if (i == index + 10 && j == 11) {
                        lData2_i34_j11 = lData2;
                        index_i34_j11 = CWaveDataList2.size();
                    } else if (i == index + 11 && j == 11) {
                        lData2_i35_j11 = lData2;
                        index_i35_j11 = CWaveDataList2.size();
                    }

                    CWaveDataList2.add(lData2);
                }
            }

            isData2Correct(CWaveDataList2, lData2_i34_j11, lData2_i35_j11, index_i34_j11, index_i35_j11);
        } else {
            throw new IllegalArgumentException("找不到對應的值");
        }

        return CWaveDataList2;
    }

    @Override
    public List<Double> findAllDataByColumnIndex(Sheet sheet, Integer columnIndex, Integer startRowIndex) {

        List<Double> dataList = new ArrayList<>();
        for (int rowIndex = startRowIndex; rowIndex <= 3071; rowIndex++) {
            readExcelCWaveRawData(sheet, columnIndex, dataList, rowIndex);
        }

        return dataList;
    }

    @Override
    public List<Double> findABWaveDataByColumnIndex(Sheet sheet, Integer columnIndex, Integer startRowIndex) {

        List<Double> dataList = new ArrayList<>();

        for (int rowIndex = startRowIndex; rowIndex <= 171; rowIndex++) {
            readExcelCWaveRawData(sheet, columnIndex, dataList, rowIndex);
        }

        return dataList;
    }

    @Override
    public Double findDataByRowIndex(Sheet sheet, Integer rowIndex) {

        Row row = sheet.getRow(rowIndex);
        Cell valueCell = row.getCell(11);
        return getNumericCellValue(valueCell);
    }

    @Override
    public Double findLuxDataByRowIndex(Sheet sheet, Integer rowIndex) {

        Row row = sheet.getRow(rowIndex);
        Cell valueCell = row.getCell(2);
        return getNumericCellValue(valueCell);
    }

    @Override
    public String findExpDateByRowIndex(Sheet sheet, Integer rowIndex) {

        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return null; // 如果該行不存在，返回 null
        }

        Cell valueCell = row.getCell(7);
        if (valueCell == null) {
            return null; // 如果該單元格不存在，返回 null
        }

        // 檢查是否為日期類型
        if (valueCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(valueCell)) {
            Date date = valueCell.getDateCellValue();
            // 使用 SimpleDateFormat 將日期格式化為指定格式
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            return formatter.format(date);
        } else {
            // 如果不是日期類型，嘗試轉為字串
            return getStringCellValue(valueCell);
        }
    }

    @Override
    public Integer findRowIndexByCellValue(Sheet sheet, String keyWord, Integer columnIndex) {

        for (Row row : sheet) {
            if (row == null) continue;
            Cell cell = row.getCell(columnIndex);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                String cellValue = cell.getStringCellValue();
                if (cellValue.contains(keyWord)) {
                    return row.getRowNum(); // 返回匹配關鍵字的 row index
                }
            }
        }

        return null;
    }

    @Override
    public ResponseEntity<byte[]> exportCWaveXlsx(Map<String, List<CWaveTableEntity>> cWaveTableDataDownloadRequestMapSet, Map<String, String> expDateMapSet, Map<String, Double> luxDataMapSet) {

        //產生當前時間 yyyy年MM月dd日
        String generateTime = DatetimeConverter.getSYSTime(4);

        //設定Excel表頭
        List<List<String>> header = new ArrayList<>();
        //欄位名稱
        header.add(Arrays.asList("Mouse NO.", "cd.s/m", "Date", "Eye (R, a wave)", "µV", "ms", "Eye (R, b wave)", "µV", "ms", "Eye (R, c wave)", "µV",
                "ms", "Eye (L, a wave)", "µV", "ms", "Eye (L, b wave)", "µV", "ms", "Eye (L, c wave)", "µV", "ms"));

        try {
            //產出Excel檔案
//            XlsxUtil.createCWaveXlsxFile(EXCEL_PATH, header, cWaveTableDataDownloadRequestMapSet, expDateMapSet, luxDataMapSet, "Times New Roman");
            XlsxUtil.createProductionCWaveXlsxFile(EXCEL_PATH, cWaveTableDataDownloadRequestMapSet);

            //轉換為Byte
            return XlsxUtil.parseXlsxFileToByte("C Wave_", generateTime);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public String fileReName(Map<String, String> cWaveNewAndOldFileNameMapSet, String folderPath, String inputCaseName) {

        //設定編號對照表 Excel 表頭
        List<List<String>> header = new ArrayList<>();
        //欄位名稱
        header.add(Arrays.asList("原老鼠編號", "分組後老鼠編號"));

        try {
            File directory = new File(folderPath);

            if(!directory.exists() || !directory.isDirectory()){
                throw new IllegalArgumentException("不存在的資料夾路徑： " + folderPath);
            }

            File[] files = directory.listFiles();

            //建立資料夾路徑
            String nameComparisonPath = folderPath + "/File Name Comparison/" + inputCaseName;

            //取得資料夾 URL
            Path nameComparisonDir = Paths.get(nameComparisonPath);

            //確保資料夾存在
            if(!Files.exists(nameComparisonDir)){
                Files.createDirectories(nameComparisonDir);
            }

            //產出對照表 Excel 檔案
            XlsxUtil.createFileNameMapXlsxFile(nameComparisonPath, header, cWaveNewAndOldFileNameMapSet, "Times New Roman");

            if (files != null) {
                for(File file : files){
                    ReNameUtil.fileRename(
                            cWaveNewAndOldFileNameMapSet,
                            folderPath,
                            inputCaseName,
                            file,
                            "C Wave",
                            0
                    );
                }
            }

            return "檔案名稱修改完成";
        } catch (Exception e) {
            e.printStackTrace();
            return "檔案名稱修改時發生錯誤" + e.getMessage();
        }
    }

    private static Object findDataByColumnIndex(Sheet sheet, Integer rowIndex, Integer columnIndex, Integer valueStartIndex){

        Object value = null;
        for (int i = 10;  i < columnIndex + 1; i++) {
            Row row = sheet.getRow(rowIndex);
            Cell valueCell;

            if(row != null) {
                if(columnIndex == 10){
                    if (rowIndex >= valueStartIndex + 1 && rowIndex <= valueStartIndex + 6) { // 25 to 30 inclusive
                        valueCell = row.getCell(columnIndex);
                        value = valueCell + "1";
                        return value;
                    } else if (rowIndex >= valueStartIndex + 7 && rowIndex <= valueStartIndex + 12) { // 31 to 36 inclusive
                        valueCell = row.getCell(columnIndex);
                        value = valueCell + "2";
                        return value;
                    }

                    valueCell = row.getCell(columnIndex);
                    value = getNumericCellValue(valueCell);
                    return value;
                }

                valueCell = row.getCell(columnIndex);
                value = getNumericCellValue(valueCell);
            }
        }

        return value;
    }

    private void readExcelCWaveRawData(Sheet sheet, Integer columnIndex, List<Double> dataList, int rowIndex) {

        Row row = sheet.getRow(rowIndex);
        if(row != null){
            if(columnIndex == 7){
                Cell cell = row.getCell(columnIndex);
                double cellValue = getNumericCellValue(cell) / 1000;
                dataList.add(cellValue);
                return;
            } else if (columnIndex == 11) {
                Cell cell = row.getCell(columnIndex);
                double cellValue = getNumericCellValue(cell) / 1000;
                dataList.add(cellValue);
                return;
            } else if (columnIndex == 15) {
                Cell cell = row.getCell(columnIndex);
                double cellValue = getNumericCellValue(cell) / 1000;
                dataList.add(cellValue);
                return;
            } else if (columnIndex == 19) {
                Cell cell = row.getCell(columnIndex);
                double cellValue = getNumericCellValue(cell) / 1000;
                dataList.add(cellValue);
                return;
            }

            Cell cell = row.getCell(columnIndex);
            double cellValue = getNumericCellValue(cell);
            dataList.add(cellValue);
        }
    }

    private static Integer findRowIndexByKeyWord(Sheet sheet) {

        for (Row row : sheet) {
            Cell cell = row.getCell(11);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                String cellValue = cell.getStringCellValue();
                if ("uV".equals(cellValue)) {
                    return row.getRowNum(); //返回匹配關鍵字的 row index
                }
            }
        }

        return null;
    }

    private void isData1Correct(
            List<Object> CWaveDataList1,
            Object rData1_i25_j11,
            Object rData1_i26_j11,
            int index_i25_j11,
            int index_i26_j11
    ) {

        if (rData1_i25_j11 != null && rData1_i26_j11 != null) {
            try {
                double value25 = Double.parseDouble(rData1_i25_j11.toString());
                double value26 = Double.parseDouble(rData1_i26_j11.toString());

                if (value25 > 0 && value26 < 0) {
                    // Swap values
                    CWaveDataList1.set(index_i25_j11, rData1_i26_j11);
                    CWaveDataList1.set(index_i26_j11, rData1_i25_j11);
                }
            } catch (NumberFormatException e) {
                // Handle the case where rData1 cannot be parsed to a double
                e.printStackTrace();
            }
        }
    }

    private void isData2Correct(
            List<Object> CWaveDataList2,
            Object rData2_i31_j11,
            Object rData2_i32_j11,
            int index_i31_j11,
            int index_i32_j11
    ) {

        if (rData2_i31_j11 != null && rData2_i32_j11 != null) {
            try {
                double value31 = Double.parseDouble(rData2_i31_j11.toString());
                double value32 = Double.parseDouble(rData2_i32_j11.toString());

                if (value31 > 0 && value32 < 0) {
                    // Swap values
                    CWaveDataList2.set(index_i31_j11, rData2_i32_j11);
                    CWaveDataList2.set(index_i32_j11, rData2_i31_j11);

                }
            } catch (NumberFormatException e) {
                // Handle the case where rData1 cannot be parsed to a double
                e.printStackTrace();
            }
        }
    }

    private static double getNumericCellValue(Cell cell) {

        if (cell != null) {
            if (cell.getCellType() == CellType.NUMERIC) {

                return cell.getNumericCellValue();
            }
        }

        return Double.NaN; // Return NaN for non-numeric cells or errors
    }

    private static String getStringCellValue(Cell cell) {

        if (cell != null) {
            if (cell.getCellType() == CellType.STRING) {

                return cell.getStringCellValue();
            }
        }

        return null; // Return Null for non-string cells or errors
    }
}
