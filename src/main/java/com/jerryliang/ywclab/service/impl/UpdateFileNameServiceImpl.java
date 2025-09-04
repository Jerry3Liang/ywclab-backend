package com.jerryliang.ywclab.service.impl;

import com.jerryliang.ywclab.service.UpdateFileNameService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UpdateFileNameServiceImpl implements UpdateFileNameService {

    @Override
    public Map<String, String> getOldAndNewFileNameMap(Sheet sheet) {

        Map<String, String> fileNameMap = new HashMap<>();

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if(row != null){
                Cell keyCell = row.getCell(0);
                Cell valueCell = row.getCell(1);
                String key = getStringCellValue(keyCell);
                String value = getStringCellValue(valueCell);
                fileNameMap.put(key, value);
            }
        }

        return fileNameMap;
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
