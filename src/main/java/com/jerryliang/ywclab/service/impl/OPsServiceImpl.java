package com.jerryliang.ywclab.service.impl;

import com.jerryliang.ywclab.dto.OPsAnalyzeDTO;
import com.jerryliang.ywclab.model.OPsAnalyzeEntity;
import com.jerryliang.ywclab.service.OPsService;
import com.jerryliang.ywclab.utils.DatetimeConverter;
import com.jerryliang.ywclab.utils.XlsxUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.jerryliang.ywclab.utils.CommonMethods.zeroIfAtLeastNZero;

@Component
public class OPsServiceImpl implements OPsService {

    @Value("${YWCLab.data.excelDownload.path}")
    String EXCEL_PATH;

    @Override
    public List<Double> findAllDataByColumnIndex(Sheet sheet, Integer columnIndex, String keyWord) {

        List<Double> dataList = new ArrayList<>();
        Integer valueStartIndex = findRowIndexByKeyWord(sheet, keyWord, columnIndex);
        if(valueStartIndex != null){
            for (int rowIndex = valueStartIndex + 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if(row != null){
                    if(columnIndex == 49){
                        Cell cell = row.getCell(columnIndex);
                        double cellValue = getNumericCellValue(cell) / 1000;
                        dataList.add(cellValue);
                        continue;
                    } else if (columnIndex == 53) {
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
        } else {
            throw new IllegalArgumentException("找不到對應的值");
        }

        return dataList;
    }

    @Override
    public List<List<Double>> findOPsDataAndMilliSec(String minPointSelected, List<Double> rawDataList, List<Double> rawMilliSecList) {

        List<List<Double>> OPsAndMilliSecList = new ArrayList<>();
        double scale = Math.pow(10, 2);

        double[] OPsMaxAndMinStarted;

        if (minPointSelected.equals("ROP3") || minPointSelected.equals("LOP3")) {
            OPsMaxAndMinStarted = findOPsMaxAndMinOP3Started(rawDataList, rawMilliSecList);
        } else {
            OPsMaxAndMinStarted = findOPsMaxAndMinOP4Started(rawDataList, rawMilliSecList);
        }

        List<Double> OPsDataList = new ArrayList<>();
        List<Double> OPsMilliSecList = new ArrayList<>();

        //計算 OP1~OP5 的值
        for (int i = 0; i < 5; i++) {
            double OP = Math.ceil(Math.abs(OPsMaxAndMinStarted[i * 3] - OPsMaxAndMinStarted[i * 3 + 1]) * scale) / scale;
            OPsDataList.add(OP);
            OPsMilliSecList.add(OPsMaxAndMinStarted[i * 3 + 2]);
        }

        OPsAndMilliSecList.add(OPsDataList);
        OPsAndMilliSecList.add(OPsMilliSecList);

        return OPsAndMilliSecList;
    }

    @Override
    public ResponseEntity<byte[]> exportOPsXlsx(Map<String, OPsAnalyzeDTO> opsDataDownloadRequestMapSet) {
        //產生當前時間 yyyy年MM月dd日
        String generateTime = DatetimeConverter.getSYSTime(4);

//        //設定Excel表頭
//        List<List<String>> header = new ArrayList<>();
//        //欄位名稱
//        header.add(Arrays.asList("Mouse NO.", "Eye (L, OPX)", "µV", "ms", "OP2+OP3+OP4 (L)", "Eye (R, OPX)", "µV", "ms", "OP2+OP3+OP4 (R)"));

        try {
            //產出Excel檔案
//            XlsxUtil.createOPsXlsxFile(EXCEL_PATH, header, opsDataDownloadRequestMapSet, "Times New Roman");
            XlsxUtil.createProductionOPsXlsxFile(EXCEL_PATH, opsDataDownloadRequestMapSet);

            //轉換為Byte
            return XlsxUtil.parseXlsxFileToByte("OPs_", generateTime);
        } catch (Exception e) {

            return null;
        }
    }

    private static Integer findRowIndexByKeyWord(Sheet sheet, String keyWord, Integer columnIndex) {

        for (Row row : sheet) {
            Cell cell = row.getCell(columnIndex);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                String cellValue = cell.getStringCellValue();
                if (keyWord.equals(cellValue)) {
                    return row.getRowNum(); //返回匹配關鍵字的 row index
                }
            }
        }

        return null;
    }

    private static double[] findOPsMaxAndMinOP4Started(List<Double> rawDataList, List<Double> rawMilliSecList){
        //找到 raw data 的最小值 (也就是 OP4 波谷)
        double OP4MinValue = findAllDataMin(rawDataList);

        //找到下一個最大值及其對應的毫秒數 (也就是 OP4 波鋒)，先獲得 OP4 Min 的 index，再去一一比較大小
        int OP4MinValueRowIndex = findValueRowIndex(rawDataList, OP4MinValue);
        ArrayList<OPsAnalyzeEntity> OP4MinToOP4MaxList = goBackCompareLargeAndStore(OP4MinValue, OP4MinValueRowIndex, rawDataList, rawMilliSecList);
        double OP4MaxValue = OP4MinToOP4MaxList.get(OP4MinToOP4MaxList.size() - 1).getValue();
        double OP4MaxValueSeconds = OP4MinToOP4MaxList.get(OP4MinToOP4MaxList.size() - 1).getSeconds();

        double OP5MinValue;
        double OP5MaxValue;
        double OP5MaxValueSeconds;

        //找到下一個最小值 (也就是 OP5 波谷)，先獲得 OP4 Max 的 index，再去一一比較大小
        int OP4MaxValueRowIndex = findValueRowIndex(rawDataList, OP4MaxValue);
        ArrayList<Double> OP4MaxToOP5MinList = goBackCompareSmallAndStore(OP4MaxValue, OP4MaxValueRowIndex, rawDataList);
        OP5MinValue = OP4MaxToOP5MinList.get(OP4MaxToOP5MinList.size()-1);

        if(OP4MaxValue == 0.0) {
            OP5MinValue = 0.0;
            OP5MaxValue = 0.0;
            OP5MaxValueSeconds = 0.0;
        } else {
            //如果 OP5MinValue = 0.0 代表 OP4 波谷在波形很後段，無法找到 OP5
            if(OP5MinValue == 0.0) {
                OP5MaxValue = 0.0;
                OP5MaxValueSeconds = 0.0;
            } else {
                //找到下一個最大值及其對應的毫秒數 (也就是 OP5 波鋒)，先獲得 OP5 Min 的 index，再去一一比較大小
                int OP5MinValueRowIndex = findValueRowIndex(rawDataList, OP5MinValue);
                ArrayList<OPsAnalyzeEntity> OP5MinToOP5MaxList = goBackCompareLargeAndStore(OP5MinValue, OP5MinValueRowIndex, rawDataList, rawMilliSecList);

                OP5MaxValue = OP5MinToOP5MaxList.get(OP5MinToOP5MaxList.size() - 1).getValue();
                OP5MaxValueSeconds = OP5MinToOP5MaxList.get(OP5MinToOP5MaxList.size() - 1).getSeconds();
            }
        }

        //往回找到前一個最大值及其對應的毫秒數 (也就是 OP3 波鋒)，再去一一比較大小
        ArrayList<OPsAnalyzeEntity> OP4MinToOP3MaxList = goForwardCompareLargeAndStore(OP4MinValue ,OP4MinValueRowIndex, rawDataList, rawMilliSecList);
        double OP3MaxValue = OP4MinToOP3MaxList.get(OP4MinToOP3MaxList.size() - 1).getValue();
        double OP3MaxValueSeconds = OP4MinToOP3MaxList.get(OP4MinToOP3MaxList.size() - 1).getSeconds();

        //往回找到前一個最小值 (也就是 OP3 波谷)，先獲得 OP3 Max 的 index，再去一一比較大小
        int OP3MaxValueRowIndex = findValueRowIndex(rawDataList, OP3MaxValue);
        ArrayList<Double> OP3MaxToMinList = goForwardCompareSmallAndStore(OP3MaxValue, OP3MaxValueRowIndex, rawDataList);
        double OP3MinValue = OP3MaxToMinList.get(OP3MaxToMinList.size() - 1);

        //往回找到前一個最大值及其對應的毫秒數 (也就是 OP2 波鋒)，先獲得 OP3 Min 的 index，再去一一比較大小
        int OP3MinValueRowIndex = findValueRowIndex(rawDataList, OP3MinValue);
        ArrayList<OPsAnalyzeEntity> OP3MinToOP2MaxList = goForwardCompareLargeAndStore(OP3MinValue ,OP3MinValueRowIndex, rawDataList, rawMilliSecList);
        double OP2MaxValue = OP3MinToOP2MaxList.get(OP3MinToOP2MaxList.size() - 1).getValue();
        double OP2MaxValueSeconds = OP3MinToOP2MaxList.get(OP3MinToOP2MaxList.size() - 1).getSeconds();
        //往回找到前一個最小值 (也就是 OP2 波谷)，先獲得 OP2 Max 的 index，再去一一比較大小
        int OP2MaxValueRowIndex = findValueRowIndex(rawDataList, OP2MaxValue);
        ArrayList<Double> OP2MaxToMinList = goForwardCompareSmallAndStore(OP2MaxValue, OP2MaxValueRowIndex, rawDataList);
        double OP2MinValue = OP2MaxToMinList.get(OP2MaxToMinList.size() - 1);

        //往回找到前一個最大值及其對應的毫秒數 (也就是 OP1 波鋒)，先獲得 OP2 Min 的 index，再去一一比較大小
        int OP2MinValueRowIndex = findValueRowIndex(rawDataList, OP2MinValue);
        ArrayList<OPsAnalyzeEntity> OP2MinToOP1MaxList = goForwardCompareLargeAndStore(OP2MinValue ,OP2MinValueRowIndex, rawDataList, rawMilliSecList);
        double OP1MaxValue = OP2MinToOP1MaxList.get(OP2MinToOP1MaxList.size() - 1).getValue();
        double OP1MaxValueSeconds = OP2MinToOP1MaxList.get(OP2MinToOP1MaxList.size() - 1).getSeconds();
        //往回找到前一個最小值 (也就是 OP1 波谷)，先獲得 OP1 Max 的 index，再去一一比較大小
        int OP1MaxValueRowIndex = findValueRowIndex(rawDataList, OP1MaxValue);
        ArrayList<Double> OP1MaxToMinList = goForwardCompareSmallAndStore(OP1MaxValue, OP1MaxValueRowIndex, rawDataList);
        double OP1MinValue = OP1MaxToMinList.get(OP1MaxToMinList.size() - 1);

        double[] finalOPValuesAndSeconds = new double[]{OP1MaxValue, OP1MinValue, OP1MaxValueSeconds, OP2MaxValue, OP2MinValue, OP2MaxValueSeconds,
                OP3MaxValue, OP3MinValue, OP3MaxValueSeconds, OP4MaxValue, OP4MinValue, OP4MaxValueSeconds,
                OP5MaxValue, OP5MinValue, OP5MaxValueSeconds};

        //檢查 finalOPValuesAndSeconds 陣列有大於 3個 0.0，就將全部值變成 0.0
        zeroIfAtLeastNZero(finalOPValuesAndSeconds, 3);

        return finalOPValuesAndSeconds;
    }

    private static double[] findOPsMaxAndMinOP3Started(List<Double> rawDataList, List<Double> rawMilliSecList){
        //找到 raw data 的最小值 (也就是 OP3 波谷)
        double OP3MinValue = findAllDataMin(rawDataList);

        //找到下一個最大值及其對應的毫秒數 (也就是 OP3 波鋒)，先獲得 OP3 Min 的 index，再去一一比較大小
        int OP3MinValueRowIndex = findValueRowIndex(rawDataList, OP3MinValue);
        ArrayList<OPsAnalyzeEntity> OP3MinToOP3MaxList = goBackCompareLargeAndStore(OP3MinValue, OP3MinValueRowIndex, rawDataList, rawMilliSecList);
        double OP3MaxValue = OP3MinToOP3MaxList.get(OP3MinToOP3MaxList.size() - 1).getValue();
        double OP3MaxValueSeconds = OP3MinToOP3MaxList.get(OP3MinToOP3MaxList.size() - 1).getSeconds();

        double OP4MinValue;
        double OP4MaxValue;
        double OP4MaxValueSeconds;
        double OP5MinValue;
        double OP5MaxValue;
        double OP5MaxValueSeconds;

        if(OP3MaxValue == 0.0) {
            OP4MinValue = 0.0;
            OP4MaxValue = 0.0;
            OP4MaxValueSeconds = 0.0;
            OP5MinValue = 0.0;
            OP5MaxValue = 0.0;
            OP5MaxValueSeconds = 0.0;
        } else {
            //找到下一個最小值 (也就是 OP4 波谷)，先獲得 OP3 Max 的 index，再去一一比較大小
            int OP3MaxValueRowIndex = findValueRowIndex(rawDataList, OP3MaxValue);
            ArrayList<Double> OP3MaxToOP4MinList = goBackCompareSmallAndStore(OP3MaxValue, OP3MaxValueRowIndex, rawDataList);
            OP4MinValue = OP3MaxToOP4MinList.get(OP3MaxToOP4MinList.size()-1);

            //如果 OP4MinValue = 0.0 代表 OP3 波谷在波形很後段，無法找到 OP4 及 OP5
            if(OP4MinValue == 0.0) {
                OP4MaxValue = 0.0;
                OP4MaxValueSeconds = 0.0;
                OP5MinValue = 0.0;
                OP5MaxValue = 0.0;
                OP5MaxValueSeconds = 0.0;
            } else {
                //找到下一個最大值及其對應的毫秒數 (也就是 OP4 波鋒)，先獲得 OP4 Min 的 index，再去一一比較大小
                int OP4MinValueRowIndex = findValueRowIndex(rawDataList, OP4MinValue);
                ArrayList<OPsAnalyzeEntity> OP4MinToOP4MaxList = goBackCompareLargeAndStore(OP4MinValue, OP4MinValueRowIndex, rawDataList, rawMilliSecList);
                OP4MaxValue = OP4MinToOP4MaxList.get(OP4MinToOP4MaxList.size() - 1).getValue();
                OP4MaxValueSeconds = OP4MinToOP4MaxList.get(OP4MinToOP4MaxList.size() - 1).getSeconds();

                //找到下一個最小值 (也就是 OP5 波谷)，先獲得 OP4 Max 的 index，再去一一比較大小
                int OP4MaxValueRowIndex = findValueRowIndex(rawDataList, OP4MaxValue);
                ArrayList<Double> OP4MaxToOP5MinList = goBackCompareSmallAndStore(OP4MaxValue, OP4MaxValueRowIndex, rawDataList);
                OP5MinValue = OP4MaxToOP5MinList.get(OP4MaxToOP5MinList.size()-1);

                //找到下一個最大值及其對應的毫秒數 (也就是 OP5 波鋒)，先獲得 OP5 Min 的 index，再去一一比較大小
                int OP5MinValueRowIndex = findValueRowIndex(rawDataList, OP5MinValue);
                ArrayList<OPsAnalyzeEntity> OP5MinToOP5MaxList = goBackCompareLargeAndStore(OP5MinValue, OP5MinValueRowIndex, rawDataList, rawMilliSecList);
                OP5MaxValue = OP5MinToOP5MaxList.get(OP5MinToOP5MaxList.size() - 1).getValue();
                OP5MaxValueSeconds = OP5MinToOP5MaxList.get(OP5MinToOP5MaxList.size() - 1).getSeconds();
            }
        }

        //往回找到前一個最大值及其對應的毫秒數 (也就是 OP2 波鋒)，再去一一比較大小
        ArrayList<OPsAnalyzeEntity> OP3MinToOP2MaxList = goForwardCompareLargeAndStore(OP3MinValue ,OP3MinValueRowIndex, rawDataList, rawMilliSecList);
        double OP2MaxValue = OP3MinToOP2MaxList.get(OP3MinToOP2MaxList.size() - 1).getValue();
        double OP2MaxValueSeconds = OP3MinToOP2MaxList.get(OP3MinToOP2MaxList.size() - 1).getSeconds();

        double OP2MinValue;
        double OP1MinValue;
        double OP1MaxValue;
        double OP1MaxValueSeconds;

        if(OP2MaxValue == 0.0) {
            OP2MinValue = 0.0;
            OP1MinValue = 0.0;
            OP1MaxValue = 0.0;
            OP1MaxValueSeconds = 0.0;
        } else {
            //往回找到前一個最小值 (也就是 OP2 波谷)，先獲得 OP2 Max 的 index，再去一一比較大小
            int OP2MaxValueRowIndex = findValueRowIndex(rawDataList, OP2MaxValue);
            ArrayList<Double> OP2MaxToMinList = goForwardCompareSmallAndStore(OP2MaxValue, OP2MaxValueRowIndex, rawDataList);
            OP2MinValue = OP2MaxToMinList.get(OP2MaxToMinList.size() - 1);

            if(OP2MinValue == 0.0) {
                OP1MinValue = 0.0;
                OP1MaxValue = 0.0;
                OP1MaxValueSeconds = 0.0;
            } else {
                //往回找到前一個最大值及其對應的毫秒數 (也就是 OP1 波鋒)，先獲得 OP2 Min 的 index，再去一一比較大小
                int OP2MinValueRowIndex = findValueRowIndex(rawDataList, OP2MinValue);
                ArrayList<OPsAnalyzeEntity> OP2MinToOP1MaxList = goForwardCompareLargeAndStore(OP2MinValue ,OP2MinValueRowIndex, rawDataList, rawMilliSecList);
                OP1MaxValue = OP2MinToOP1MaxList.get(OP2MinToOP1MaxList.size() - 1).getValue();
                OP1MaxValueSeconds = OP2MinToOP1MaxList.get(OP2MinToOP1MaxList.size() - 1).getSeconds();

                //往回找到前一個最小值 (也就是 OP1 波谷)，先獲得 OP1 Max 的 index，再去一一比較大小
                int OP1MaxValueRowIndex = findValueRowIndex(rawDataList, OP1MaxValue);
                ArrayList<Double> OP1MaxToMinList = goForwardCompareSmallAndStore(OP1MaxValue, OP1MaxValueRowIndex, rawDataList);
                OP1MinValue = OP1MaxToMinList.get(OP1MaxToMinList.size() - 1);
            }
        }

        double[] finalOPValuesAndSeconds = new double[]{OP1MaxValue, OP1MinValue, OP1MaxValueSeconds, OP2MaxValue, OP2MinValue, OP2MaxValueSeconds,
                OP3MaxValue, OP3MinValue, OP3MaxValueSeconds, OP4MaxValue, OP4MinValue, OP4MaxValueSeconds,
                OP5MaxValue, OP5MinValue, OP5MaxValueSeconds};

        //檢查 finalOPValuesAndSeconds 陣列有大於 3個 0.0，就將全部值變成 0.0
        zeroIfAtLeastNZero(finalOPValuesAndSeconds, 3);

        return finalOPValuesAndSeconds;
    }

    private static double findAllDataMin(List<Double> rawDataList){

        double minWaveValue = Double.MAX_VALUE;

        for (double value : rawDataList) {
            if (value < minWaveValue) {
                minWaveValue = value;
            }
        }

        return minWaveValue;
    }

    private static int findValueRowIndex(List<Double> rawDataList, double targetedValue){

        double currentValue = targetedValue;
        int maxRowIndex = 0;

        for(int i = 0; i < rawDataList.size(); i++){
            double value = rawDataList.get(i);
            if (value == currentValue) {
                currentValue = value;
                maxRowIndex = i;
            }
        }

        return maxRowIndex;
    }

    private static ArrayList<OPsAnalyzeEntity> goBackCompareLargeAndStore(double startValue, int startValueIndex, List<Double> rawDataList, List<Double> rawMilliSecList){

        ArrayList<OPsAnalyzeEntity> resultArray = new ArrayList<>();
        double timeForMaxValue = 0.0;
        OPsAnalyzeEntity currentValue = new OPsAnalyzeEntity(startValue, timeForMaxValue);

        for (int i = startValueIndex; i >= 0; i++) {
            if(i + 1 >= rawDataList.size()) {
                OPsAnalyzeEntity noValue = new OPsAnalyzeEntity(0.0, 0.0);
                resultArray.add(noValue);

                return resultArray;
            }

            double value = rawDataList.get(i + 1);
            if(!Double.valueOf(value).isNaN()){
                double time = rawMilliSecList.get(i + 1);
                OPsAnalyzeEntity secondValueWithTime = new OPsAnalyzeEntity(value, time);
                if(currentValue.getValue() < secondValueWithTime.getValue()){
                    resultArray.add(secondValueWithTime);
                } else {
                    break;
                }
            }

            //從新設定 currentValue 的值及其對應的秒數
            if (i - 1 >= 0) {
                double preValue = rawDataList.get(i +1);
                double milliSecond = rawMilliSecList.get(i + 1);
                if(!Double.valueOf(preValue).isNaN()){
                    currentValue.setValue(preValue);
                    currentValue.setSeconds(milliSecond);
                }
            }
        }

        return resultArray;
    }

    private static ArrayList<Double> goBackCompareSmallAndStore(double startValue, int startValueIndex, List<Double> rawDataList){

        ArrayList<Double> resultArray = new ArrayList<>();
        double currentValue = startValue;

        for (int i = startValueIndex; i >= 0; i++) {
            if(i + 1 >= rawDataList.size()) {
                resultArray.add(0.0);

                return resultArray;
            }

            double value = rawDataList.get(i + 1);
            if(!Double.valueOf(value).isNaN()){
                if(currentValue > value){
                    resultArray.add(value);
                } else {
                    break;
                }
            }

            //從新設定 currentValue 的值
            if (i - 1 >= 0) {
                double preValue = rawDataList.get(i +1);
                if(!Double.valueOf(preValue).isNaN()){
                    currentValue = preValue;
                }
            }
        }

        return resultArray;
    }

    private static ArrayList<OPsAnalyzeEntity> goForwardCompareLargeAndStore(double startValue, int startValueIndex, List<Double> rawDataList, List<Double> rawMilliSecList){

        ArrayList<OPsAnalyzeEntity> resultArray = new ArrayList<>();
        double timeForMaxValue = 0.0;
        OPsAnalyzeEntity currentValue = new OPsAnalyzeEntity(startValue, timeForMaxValue);

        for (int i = startValueIndex; i >= 0; i--) {
            if(i - 1 < 0) {
                OPsAnalyzeEntity noValue = new OPsAnalyzeEntity(0.0, 0.0);
                resultArray.add(noValue);

                return resultArray;
            }

            double value = rawDataList.get(i - 1);
            if(!Double.valueOf(value).isNaN()){
                double time = rawMilliSecList.get(i - 1);
                OPsAnalyzeEntity secondValueWithTime = new OPsAnalyzeEntity(value, time);
                if(currentValue.getValue() < secondValueWithTime.getValue()){
                    resultArray.add(secondValueWithTime);
                } else {
                    break;
                }
            }

            //從新設定 currentValue 的值及其對應的秒數
            double preValue = rawDataList.get(i - 1);
            double milliSecond = rawMilliSecList.get(i - 1);
            if(!Double.valueOf(preValue).isNaN()){
                currentValue.setValue(preValue);
                currentValue.setSeconds(milliSecond);
            }
        }

        return resultArray;
    }

    private static ArrayList<Double> goForwardCompareSmallAndStore(double startValue, int startValueIndex, List<Double> rawDataList){

        ArrayList<Double> resultArray = new ArrayList<>();
        double currentValue = startValue;

        for (int i = startValueIndex; i >= 0; i--) {
            if(i - 1 < 0) {
                resultArray.add(0.0);

                return resultArray;
            }

            double value = rawDataList.get(i - 1);
            if(!Double.valueOf(value).isNaN()){
                if(currentValue > value){
                    resultArray.add(value);
                } else {
                    break;
                }
            }

            //從新設定 currentValue 的值
            double preValue = rawDataList.get(i - 1);
            if(!Double.valueOf(preValue).isNaN()){
                currentValue = preValue;
            }
        }

        return resultArray;
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
