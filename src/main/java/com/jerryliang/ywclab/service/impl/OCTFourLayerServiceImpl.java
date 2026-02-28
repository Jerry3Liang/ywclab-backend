package com.jerryliang.ywclab.service.impl;

import com.jerryliang.ywclab.Exception.IncompleteDataException;
import com.jerryliang.ywclab.model.OCTFourLayerEntity;
import com.jerryliang.ywclab.service.OCTFourLayerService;
import com.jerryliang.ywclab.utils.DatetimeConverter;
import com.jerryliang.ywclab.utils.XlsxUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class OCTFourLayerServiceImpl implements OCTFourLayerService {

    @Value("${YWCLab.data.excelDownload.path}")
    String EXCEL_PATH;

    @Override
    public List<OCTFourLayerEntity> getOCTFourFinalData(Sheet sheet, String fileName) throws IncompleteDataException {

        List<OCTFourLayerEntity> columnStatisticsList = new ArrayList<>();

        int lastRowNum = sheet.getLastRowNum();
        int lastColNum = sheet.getRow(0).getPhysicalNumberOfCells();

        //判斷 Excel 檔案的資料是否完整
        if(lastColNum < 5){
            throw new IncompleteDataException(fileName + "： 資料不完整！ 總共只有 " + lastColNum + "個 column，少了 " + (5 - lastColNum) + "個 column。");
        }

        for (int colIndex = 0; colIndex < lastColNum; colIndex++) {

            OCTFourLayerEntity columnStatistics = new OCTFourLayerEntity();
            columnStatistics.setColumnIndex(colIndex + 1);

            for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    Cell cell = row.getCell(colIndex);

                    if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                        columnStatistics.addToSum(cell.getNumericCellValue());
                        columnStatistics.incrementCount();
                    }
                }
            }

            columnStatistics.calculateAverage();
            columnStatisticsList.add(columnStatistics);
        }

        return columnStatisticsList;
    }

    @Override
    public ResponseEntity<byte[]> exportOctFourLayerXlsx(Map<String, List<Object>> octFourLayerDataDownloadRequestMapSet) {
        //產生當前時間 yyyy年MM月dd日
        String generateTime = DatetimeConverter.getSYSTime(4);

        //設定Excel表頭
        List<List<String>> header = new ArrayList<>();
        //欄位名稱
        header.add(Arrays.asList("Mouse NO.", "Total", "NFL-INL", "OPL-ONL", "IS-OS-RPE"));

        try {
            //產出Excel檔案
//            XlsxUtil.createOCTFourLayerXlsxFile(EXCEL_PATH, header, octFourLayerDataDownloadRequestMapSet, "Times New Roman");
            XlsxUtil.createProductionOCTFourLayerXlsxFile(EXCEL_PATH, octFourLayerDataDownloadRequestMapSet);

            //轉換為Byte
            return XlsxUtil.parseXlsxFileToByte("OCT 4 Layer_", generateTime);
        }
        catch (Exception e) {
            return null;
        }
    }
}
