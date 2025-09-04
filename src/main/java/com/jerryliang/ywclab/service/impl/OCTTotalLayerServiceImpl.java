package com.jerryliang.ywclab.service.impl;

import com.jerryliang.ywclab.service.OCTTotalLayerService;
import com.jerryliang.ywclab.utils.DatetimeConverter;
import com.jerryliang.ywclab.utils.XlsxUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OCTTotalLayerServiceImpl implements OCTTotalLayerService {

    @Value("${YWCLab.data.excelDownload.path}")
    String EXCEL_PATH;

    @Override
    public List<Double> getOCTTotalFinalData(Sheet sheet) {

        List<Double> dataList = new ArrayList<>();

        double[] minAndPosition = findAllDataMinAndPosition(sheet, 1, 0);
        List<double[]> doubleList = allDataMinusMinPosition(sheet, 1, 0, minAndPosition[1]);

        Map<Double, Double> map = new HashMap<>();
        for (double[] doubles : doubleList) {
            map.put(doubles[0], doubles[1]);
        }

        double thickness1 = findClosest(-800.0, sheet, 1, 0, minAndPosition[1]);
        double thickness2 = findClosest(-700.0, sheet, 1, 0, minAndPosition[1]);
        double thickness3 = findClosest(-600.0, sheet, 1, 0, minAndPosition[1]);
        double thickness4 = findClosest(-500.0, sheet, 1, 0, minAndPosition[1]);
        double thickness5 = findClosest(-400.0, sheet, 1, 0, minAndPosition[1]);
        double thickness6 = findClosest(-300.0, sheet, 1, 0, minAndPosition[1]);
        double thickness7 = findClosest(-200.0, sheet, 1, 0, minAndPosition[1]);
        double thickness8 = findClosest(-100.0, sheet, 1, 0, minAndPosition[1]);
        double thickness9 = findClosest(0.0, sheet, 1, 0, minAndPosition[1]);
        double thickness10 = findClosest(100.0, sheet, 1, 0, minAndPosition[1]);
        double thickness11 = findClosest(200.0, sheet, 1, 0, minAndPosition[1]);
        double thickness12 = findClosest(300.0, sheet, 1, 0, minAndPosition[1]);
        double thickness13 = findClosest(400.0, sheet, 1, 0, minAndPosition[1]);
        double thickness14 = findClosest(500.0, sheet, 1, 0, minAndPosition[1]);
        double thickness15 = findClosest(600.0, sheet, 1, 0, minAndPosition[1]);
        double thickness16 = findClosest(700.0, sheet, 1, 0, minAndPosition[1]);
        double thickness17 = findClosest(800.0, sheet, 1, 0, minAndPosition[1]);

        double scale = Math.pow(10, 2);
        dataList.add(Math.ceil(map.get(thickness1) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness2) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness3) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness4) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness5) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness6) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness7) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness8) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness9) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness10) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness11) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness12) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness13) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness14) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness15) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness16) * scale) / scale);
        dataList.add(Math.ceil(map.get(thickness17) * scale) / scale);

        return dataList;
    }

    @Override
    public ResponseEntity<byte[]> exportOctTotalLayerXlsx(Map<String, List<Object>> octTotalLayerDataDownloadRequestMapSet) {

        //產生當前時間 yyyy年MM月dd日
        String generateTime = DatetimeConverter.getSYSTime(4);

        //設定Excel表頭
        List<List<String>> header = new ArrayList<>();
        //欄位名稱
        header.add(Arrays.asList("Mouse NO.", "-800", "-700", "-600", "-500", "-400", "-300", "-200", "-100", "0",
                                 "100", "200", "300", "400", "500", "600", "700", "800"));

        try {
            //產出Excel檔案
            XlsxUtil.createOCTTotalLayerXlsxFile(EXCEL_PATH, header, octTotalLayerDataDownloadRequestMapSet, "Times New Roman");

            //轉換為Byte
            return XlsxUtil.parseXlsxFileToByte("OCT Total Layer_", generateTime);
        }
        catch (Exception e) {
            return null;
        }
    }

    private double[] findAllDataMinAndPosition(Sheet sheet, Integer columnIndex, Integer correspondingPositionColumnIndex){

        double minThicknessValue = Double.MAX_VALUE;
        double minThicknessValueCorrespondingPosition = 0.0;

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if(row != null){
                Cell cell = row.getCell(columnIndex);
                double cellValue = getNumericCellValue(cell);
                Cell correspondingPosition = row.getCell(correspondingPositionColumnIndex);
                double minPosition = getNumericCellValue(correspondingPosition);

                if (cellValue < minThicknessValue) {
                    minThicknessValue = cellValue;
                    minThicknessValueCorrespondingPosition = minPosition;
                }
            }
        }

        return new double[]{minThicknessValue, minThicknessValueCorrespondingPosition};
    }

    private static List<double[]> allDataMinusMinPosition(Sheet sheet, int columnIndex, int correspondingPositionColumnIndex, double minPosition){

        double value;
        double thicknessValueCorrespondingPosition;
        List<double[]> allDataAndPositionList = new ArrayList<>();

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if(row != null){
                Cell cell = row.getCell(columnIndex);
                value = getNumericCellValue(cell);
                Cell correspondingPosition = row.getCell(correspondingPositionColumnIndex);
                double position = getNumericCellValue(correspondingPosition);
                thicknessValueCorrespondingPosition = position- minPosition;
                double[] cellValueAndPosition= {thicknessValueCorrespondingPosition, value};
                allDataAndPositionList.add(cellValueAndPosition);
            }
        }

        return allDataAndPositionList;
    }

    private static double findClosest(double thickness, Sheet sheet, int columnIndex, int correspondingPositionColumnIndex, double minPosition){

        List<double[]> allDataAndPositionList = allDataMinusMinPosition(sheet, columnIndex, correspondingPositionColumnIndex, minPosition);
        double closest = allDataAndPositionList.get(0)[0];
        for (double[] possibleValues : allDataAndPositionList) {
            double minDiff = Math.abs(thickness - closest);
            for (int j = 0; j < possibleValues.length; j++) {
                double number = possibleValues[0];
                double diff = Math.abs(thickness - number);
                if (diff < minDiff) {
                    closest = number;
                    break;
                }
            }
        }

        return closest;
    }

    private static double getNumericCellValue(Cell cell) {

        if (cell != null) {
            if (cell.getCellType() == CellType.NUMERIC) {

                return cell.getNumericCellValue();
            }
        }

        return Double.NaN; // Return NaN for non-numeric cells or errors
    }
}
