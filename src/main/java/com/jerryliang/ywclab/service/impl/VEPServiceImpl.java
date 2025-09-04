package com.jerryliang.ywclab.service.impl;

import com.jerryliang.ywclab.service.VEPService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VEPServiceImpl implements VEPService {
    @Override
    public List<Object> findVEPTableData(Sheet sheet) {

        List<Object> VEPDataList = new ArrayList<>();
        double scale = Math.pow(10, 2);

        Object rP1 = findDataByColumnIndex(sheet, 12, 11);
        Object rN1 = findDataByColumnIndex(sheet, 13, 11);
        Object rP2 = findDataByColumnIndex(sheet, 14, 11);
        Object rN1ms = findDataByColumnIndex(sheet, 13, 12);
        Object rP2ms = findDataByColumnIndex(sheet, 14, 12);

        VEPDataList.add(Math.ceil((Math.abs(Double.parseDouble(rP1.toString())-Double.parseDouble(rN1.toString())) * scale)) / scale);
        VEPDataList.add(rN1ms);
        VEPDataList.add(rP2);
        VEPDataList.add(rP2ms);

        Object lP1 = findDataByColumnIndex(sheet, 15, 11);
        Object lN1 = findDataByColumnIndex(sheet, 16, 11);
        Object lP2 = findDataByColumnIndex(sheet, 17, 11);
        Object lN1ms = findDataByColumnIndex(sheet, 16, 12);
        Object lP2ms = findDataByColumnIndex(sheet, 17, 12);

        VEPDataList.add(Math.ceil((Math.abs(Double.parseDouble(lP1.toString())-Double.parseDouble(lN1.toString())) * scale)) / scale);
        VEPDataList.add(lN1ms);
        VEPDataList.add(lP2);
        VEPDataList.add(lP2ms);

        return VEPDataList;
    }

    private static Object findDataByColumnIndex(Sheet sheet, Integer rowIndex, Integer columnIndex){

        Object value = null;
        for (int i = 10;  i < columnIndex + 1; i++) {
            Row row = sheet.getRow(rowIndex);
            Cell valueCell;

            if(row != null) {
                if(columnIndex == 10){
                    switch (rowIndex) {
                        case 12, 13, 14 -> {
                            valueCell = row.getCell(columnIndex);
                            value = "Right" + valueCell;
                            return value;
                        }

                        case 15, 16, 17 -> {
                            valueCell = row.getCell(columnIndex);
                            value = "Left" + valueCell;
                            return value;
                        }
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

    @Override
    public List<Double> findAllDataByColumnIndex(Sheet sheet, Integer columnIndex) {

        List<Double> dataList = new ArrayList<>();
        for (int rowIndex = 3; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if(row != null){
                if(columnIndex == 25){
                    Cell cell = row.getCell(columnIndex);
                    double cellValue = getNumericCellValue(cell) / 1000;
                    dataList.add(cellValue);
                    continue;
                } else if (columnIndex == 26) {
                    Cell cell = row.getCell(columnIndex);
                    double cellValue = getNumericCellValue(cell) / 1000;
                    dataList.add(cellValue);
                    continue;
                }

                Cell cell = row.getCell(columnIndex);
                double cellValue = getNumericCellValue(cell);
                dataList.add(cellValue);
            }
        }

        return dataList;
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
