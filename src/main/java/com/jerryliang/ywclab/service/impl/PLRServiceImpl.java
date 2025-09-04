package com.jerryliang.ywclab.service.impl;

import com.jerryliang.ywclab.service.PLRService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PLRServiceImpl implements PLRService {

    @Override
    public List<Double> findAllDataByColumnIndex(Sheet sheet, Integer columnIndex) {

        List<Double> dataList = new ArrayList<>();
        for (int rowIndex = 36; rowIndex <= 4840; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if(row != null){
                Cell cell = row.getCell(columnIndex);
                Double cellValue = getNumericCellValue(cell);
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
