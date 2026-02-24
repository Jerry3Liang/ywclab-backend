package com.jerryliang.ywclab.utils;

import com.jerryliang.ywclab.dto.OPsAnalyzeDTO;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.IntStream;

public class XlsxUtil {

    //創建時間
    static String time = DatetimeConverter.getSYSTime(4);

    /**
     * 產製 C Wave xlsx 檔
     * @param path: 輸出的 Excel 路徑
     * @param header: 表頭陣列
     * @param cWaveTableDataDownloadRequestMapSet: 前端傳來的 Data 內容
     * @param expDateMapSet: 前端傳來的 ExpDate 內容
     * @param luxDataMapSet: 前端傳來的 LuxData 內容
     * @param fontName: 字型名稱
     */
    public static void createCWaveXlsxFile(
            String path,
            List<List<String>> header,
            Map<String, List<Object>> cWaveTableDataDownloadRequestMapSet,
            Map<String, String> expDateMapSet,
            Map<String, Double> luxDataMapSet,
            String fontName
    ) throws IOException {

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        SXSSFSheet sheet = workbook.createSheet("Result");

        Map<Integer, Double> columnWidthMultiplier = new HashMap<>();
        columnWidthMultiplier.put(2, 4.5); // 第 3 列用 4.5 倍

        createHeaderAndStyleForExcel(header, fontName, workbook, sheet, columnWidthMultiplier);

        CellStyle contentStyle = createContentAndStyleForExcel(fontName, workbook);

        //設定內容
        List<String> keys = new ArrayList<>(cWaveTableDataDownloadRequestMapSet.keySet());
        List<String> expDateKeys = new ArrayList<>(expDateMapSet.keySet());
        List<String> luxKeys = new ArrayList<>(luxDataMapSet.keySet());
        int num = 0;

        //將 data 寫入 Excel
        for (int i = 0; i < keys.size(); i++) {

            String key = keys.get(i);
            String processedKey = key;

            String expDateKey = expDateKeys.get(i);
            String luxDateKey = luxKeys.get(i);

            //處理鍵值對，避免無效操作
            if (key.contains("_")) {
                processedKey = key.substring(0, key.indexOf("_"));
            }

            List<Object> values = cWaveTableDataDownloadRequestMapSet.get(keys.get(i));
            String expDateValues = expDateMapSet.get(expDateKey);
            Double luxValues = luxDataMapSet.get(luxDateKey);

            // 建立第二列 (Row)
            Row row = sheet.createRow(i + 1 + num);
            createCell(row, 0, keys.get(i).substring(0, keys.get(i).indexOf("-")), contentStyle);
            createCell(row, 1, luxValues, contentStyle);
            createCell(row, 2, expDateValues, contentStyle);

            for(int j = 0; j < values.size(); j++){
                createCell(row, (j + 3), values.get(j), contentStyle);
            }

            //判斷是否需要 Merge
            boolean needMerge = true;

            //組別一變就增加兩個 Row
            if (i < keys.size() - 1) { //確保不超出範圍
                String keyNext = keys.get(i + 1);
                if (keyNext.contains("_")) {
                    keyNext = keyNext.substring(0, keyNext.indexOf("_"));
                }

                if (!processedKey.equals(keyNext)) {
                    sheet.addMergedRegion(new CellRangeAddress(i + num, (i + 1) + num, 0, 0));
                    sheet.addMergedRegion(new CellRangeAddress(i + num, (i + 1) + num, 1, 1));
                    sheet.addMergedRegion(new CellRangeAddress(i + num, (i + 1) + num, 2, 2));
                    //只有不同時才增加行索引
                    num += 2;
                    //組別變化時不需要 Merge
                    needMerge = false;
                }
            }

            //只 Merge 奇數
            if (i % 2 == 0) {
               continue;
            }

            if(needMerge){
                sheet.addMergedRegion(new CellRangeAddress(i + num, (i + 1) + num, 0, 0));
                sheet.addMergedRegion(new CellRangeAddress(i + num, (i + 1) + num, 1, 1));
                sheet.addMergedRegion(new CellRangeAddress(i + num, (i + 1) + num, 2, 2));
            }
        }

        //寫出檔案
        FileOutputStream fileOut = new FileOutputStream(path + "/C Wave_" + time + ".xlsx");
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    /**
     * 產製老鼠舊編號與新編號對照表
     * @param path: 輸出的 Excel 路徑
     * @param header: 表頭陣列
     * @param cWaveNewAndOldFileNameMapSet: 前端傳來的 Map 內容
     * @param fontName: 字型名稱
     */
    public static void createFileNameMapXlsxFile(
            String path,
            List<List<String>> header,
            Map<String, String> cWaveNewAndOldFileNameMapSet,
            String fontName
    ) throws IOException{

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        SXSSFSheet sheet = workbook.createSheet("新舊編號對照表");

        Map<Integer, Double> columnWidthMultiplier = new HashMap<>();
//        columnWidthMultiplier.put(2, 4.5); // 第 3 列用 4.5 倍

        createHeaderAndStyleForExcel(header, fontName, workbook, sheet, columnWidthMultiplier);

        CellStyle contentStyle = createContentAndStyleForExcel(fontName, workbook);

        //設定內容
        List<String> keys = new ArrayList<>(cWaveNewAndOldFileNameMapSet.keySet());

        //將 data 寫入 Excel
        for (int i = 0; i < keys.size(); i++) {

            String key = keys.get(i);
            String value = cWaveNewAndOldFileNameMapSet.get(keys.get(i));

            // 建立第二列 (Row)
            Row row = sheet.createRow(i + 1);
            createCell(row, 0, key, contentStyle);
            createCell(row, 1, value, contentStyle);
        }

        //寫出檔案
        FileOutputStream fileOut = new FileOutputStream(path + "/Old and New File Name Comparison_" + time + ".xlsx");
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    /**
     * 產製 OPs xlsx 檔
     * @param path: 輸出的 Excel 路徑
     * @param header: 表頭陣列
     * @param opsDataDownloadRequestMapSet: 前端傳來的 Data 內容
     * @param fontName: 字型名稱
     */
    public static void createOPsXlsxFile(
            String path,
            List<List<String>> header,
            Map<String, OPsAnalyzeDTO> opsDataDownloadRequestMapSet,
            String fontName
    ) throws IOException {

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        SXSSFSheet sheet = workbook.createSheet("Result");

        Map<Integer, Double> columnWidthMultiplier = new HashMap<>();
        columnWidthMultiplier.put(2, 1.8); // 第 3 列用 4.5 倍
        columnWidthMultiplier.put(6, 1.8); // 第 3 列用 4.5 倍

        createHeaderAndStyleForExcel(header, fontName, workbook, sheet, columnWidthMultiplier);

        CellStyle contentStyle = createContentAndStyleForExcel(fontName, workbook);

        //設定內容
        List<String> keys = new ArrayList<>(opsDataDownloadRequestMapSet.keySet());
        int num = 0;

        //將 data 寫入 Excel
        for (int i = 0; i < keys.size(); i++) {

//            String key = keys.get(i);
//            String processedKey = key;
//
//            //處理鍵值對，避免無效操作
//            if (key.contains("_")) {
//                processedKey = key.substring(0, key.indexOf("_"));
//            }

            OPsAnalyzeDTO values = opsDataDownloadRequestMapSet.get(keys.get(i));
            List<Double> leftEyeOPsData = values.getLeftEyeOPsData();
            List<Double> leftEyeOPsMilliSec = values.getLeftEyeOPsMilliSec();
            List<Double> rightEyeOPsData = values.getRightEyeOPsData();
            List<Double> rightEyeOPsMilliSec = values.getRightEyeOPsMilliSec();

            for(int j = 0; j < 5; j++){
                //建立第二列 (Row)
                Row row = sheet.createRow((i + 1) + j + num);
                createCell(row, 0, keys.get(i), contentStyle);
                createCell(row, 1, "OP" + (j + 1) + " (L)", contentStyle);
                createCell(row, 2, leftEyeOPsData.get(j), contentStyle);
                createCell(row, 3, leftEyeOPsMilliSec.get(j), contentStyle);
                createCell(row, 4, leftEyeOPsData.get(1) + leftEyeOPsData.get(2) + leftEyeOPsData.get(3), contentStyle);
                createCell(row, 5, "OP" + (j + 1) + " (R)", contentStyle);
                createCell(row, 6, rightEyeOPsData.get(j), contentStyle);
                createCell(row, 7, rightEyeOPsMilliSec.get(j), contentStyle);
                createCell(row, 8, rightEyeOPsData.get(1) + rightEyeOPsData.get(2) + rightEyeOPsData.get(3), contentStyle);
            }

            sheet.addMergedRegion(new CellRangeAddress((i + 1) + num, (i + 1) + (num + 4), 0, 0));
            sheet.addMergedRegion(new CellRangeAddress((i + 1) + num, (i + 1) + (num + 4), 4, 4));
            sheet.addMergedRegion(new CellRangeAddress((i + 1) + num, (i + 1) + (num + 4), 8, 8));
            num += 5;
        }

        //寫出檔案
        FileOutputStream fileOut = new FileOutputStream(path + "/OPs_" + time + ".xlsx");
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    /**
     * 產製 OCT Four Layer xlsx 檔
     * @param path: 輸出的 Excel 路徑
     * @param header: 表頭陣列
     * @param octFourLayerDataDownloadRequestMapSet: 前端傳來的 Data 內容
     * @param fontName: 字型名稱
     */
    public static void createOCTFourLayerXlsxFile(
            String path,
            List<List<String>> header,
            Map<String, List<Object>> octFourLayerDataDownloadRequestMapSet,
            String fontName
    ) throws IOException {

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        SXSSFSheet sheet = workbook.createSheet("Result");

        //new HashMap<>() 為空 Map，因為沒有特定 Column 要調整寬度
        createHeaderAndStyleForExcel(header, fontName, workbook, sheet, new HashMap<>());

        CellStyle contentStyle = createContentAndStyleForExcel(fontName, workbook);
        CellStyle averageStyle = createContentAverageStyleForExcel(fontName, workbook);

        //設定內容
        List<String> keys = new ArrayList<>(octFourLayerDataDownloadRequestMapSet.keySet());

        int num = 0;
        int groupMouseCount = 0;
        double[] sums = new double[4]; //用於存儲 sum 值，totalSum、NFL_INLSum、OPL_ONLSum、IS_OS_RPESum 總共 4個

        //將 data 寫入 Excel
        for (int i = 0; i < keys.size(); i++) {

            String key = keys.get(i);
            String processedKey = key;

            //處理鍵值對，避免無效操作
            if (key.contains("_")) {
                processedKey = key.substring(0, key.indexOf("_"));
            }

            List<Object> values = octFourLayerDataDownloadRequestMapSet.get(keys.get(i));

            //建立第二列 (Row)
            Row row = sheet.createRow(i + 1 + num);
            createCell(row, 0, keys.get(i), contentStyle);

            for(int j = 0; j < values.size(); j++){
                createCell(row, (j + 1), values.get(j), contentStyle);
            }

            if (i < keys.size() - 1) {
                String keyNext = keys.get(i + 1);

                if (keyNext.contains("_")) {
                    keyNext = keyNext.substring(0, keyNext.indexOf("_"));
                }

                if (!processedKey.equals(keyNext)) {
                    //計算每個 Column 的 sum 值
                    for (int j = 0; j < values.size(); j++) {
                        if (values.get(j) instanceof Number) {
                            sums[j] += ((Number) values.get(j)).doubleValue();
                        }
                    }

                    //將每個 Column 的最終 sum 值寫入 Excel
                    setAverageRow(sheet, averageStyle, i + 2 + num, sums, groupMouseCount + 1);

                    //換組別將 sum 及 groupMouseCount 歸 0
                    Arrays.fill(sums, 0.0);
                    groupMouseCount = 0;
                    //只有不同時才增加行索引
                    num += 3;
                    continue;
                }
            }

            //計算每個 Column 的 sum 值
            for (int j = 0; j < values.size(); j++) {
                if (values.get(j) instanceof Number) {
                    sums[j] += ((Number) values.get(j)).doubleValue();
                }
            }

            groupMouseCount++;
        }

        //將每個 Column 的最終 sum 值寫入 Excel
        setAverageRow(sheet, averageStyle, (keys.size() - 1) + 2 + num, sums, groupMouseCount);

        //寫出檔案
        FileOutputStream fileOut = new FileOutputStream(path + "/OCT 4 Layer_" + time + ".xlsx");
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

    /**
     * 產製 OCT Total Layer xlsx 檔
     * @param path: 輸出的 Excel 路徑
     * @param octTotalLayerDataDownloadRequestMapSet: 前端傳來的 Data 內容
     */
    public static void createProductionOCTTotalLayerXlsxFile(
            String path,
            Map<String, List<Object>> octTotalLayerDataDownloadRequestMapSet
    ) throws IOException {

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        SXSSFSheet sheet = workbook.createSheet("Result");

        sheet.trackAllColumnsForAutoSizing();

        //設定內容
        List<String> keys = new ArrayList<>(octTotalLayerDataDownloadRequestMapSet.keySet());

        List<String> groupList = keys.stream()
                .map(key -> key.substring(0, key.indexOf("_"))).distinct().toList();

        int num = 0;
        int groupListCount = 0;

        for(int i = 0; i < groupList.size(); i++) {
            int rowIndex = i + num + groupListCount;

            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }

            String processedKey = groupList.get(i);

            //組別那一 Row
            createStyledCell(workbook, row, 0, "組別：",
                    "微軟正黑體", (short) 12, new byte[] {(byte) 0, (byte) 0, (byte) 0}, new byte[] {(byte) 255, (byte) 255, (byte) 84},
                    HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
                    BorderStyle.NONE, BorderStyle.NONE, BorderStyle.NONE, BorderStyle.NONE,
                    true, false, false);
            createStyledCell(workbook, row, 1, groupList.get(i),
                    "Arial", (short) 12, new byte[] {(byte) 255, (byte) 0, (byte) 0}, new byte[] {(byte) 255, (byte) 255, (byte) 84},
                    HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
                    BorderStyle.NONE, BorderStyle.NONE, BorderStyle.NONE, BorderStyle.NONE,
                    true, false, false);

            //流水編號那一 Row
            createStyledCell(workbook, sheet.createRow(i + 1 + num + groupListCount), 0, "流水編號",
                    "微軟正黑體", (short) 12, new byte[] {(byte) 0, (byte) 0, (byte) 0}, new byte[] {(byte) 217, (byte) 217, (byte) 217},
                    HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
                    BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN,
                    true, false, false);

            int groupMouseCount = 1;

            for (String key : keys) {
                int row3Index = i + 1 + num + groupListCount;
                Row row3 = sheet.getRow(row3Index);
                String comparedKey = key.substring(0, key.indexOf("_"));
                if (comparedKey.equals(groupList.get(i))) {
                    createStyledCell(workbook, row3, groupMouseCount, key,
                            "Arial", (short) 12, new byte[]{(byte) 0, (byte) 0, (byte) 0}, new byte[]{(byte) 217, (byte) 217, (byte) 217},
                            HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
                            BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN,
                            true, false, false);

                    groupMouseCount++;
                }
            }

            //耳標+哪眼(自行填入) 那一 Row
            createStyledCell(workbook, sheet.createRow(i + 2 + num + groupListCount), 0, "耳標+哪眼(自行填入)",
                    "微軟正黑體", (short) 12, new byte[] {(byte) 0, (byte) 0, (byte) 0}, new byte[] {(byte) 255, (byte) 255, (byte) 255},
                    HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
                    BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN,
                    true, false, true);

            //-800~800 那一 Row
            int dataStartRowIndex = i + 3 + num + groupListCount;

            int[] depths = IntStream.rangeClosed(-8, 8)
                    .map(d -> d * 100)
                    .toArray();

            int groupColumnIndex;
            for (int d = 0; d < depths.length; d++) {
                Row dataRow = sheet.getRow(dataStartRowIndex + d);
                if (dataRow == null) {
                    dataRow = sheet.createRow(dataStartRowIndex + d);
                }

                //Column 0 -800~800
                createStyledCell(workbook, dataRow, 0, String.valueOf(depths[d]),
                        "Arial", (short) 12, new byte[] {(byte) 0, (byte) 0, (byte) 0}, new byte[] {(byte) 208, (byte) 208, (byte) 208},
                        HorizontalAlignment.CENTER, VerticalAlignment.CENTER,
                        BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN,
                        true, false, false);

                groupColumnIndex = 1;

                for (String key : keys) {
                    String groupKey = key.substring(0, key.indexOf("_"));

                    if (!groupKey.equals(groupList.get(i))) continue;

                    List<Object> values =
                            octTotalLayerDataDownloadRequestMapSet.get(key);

                    Object value = values.get(d);

                    createStyledCell(workbook, dataRow, groupColumnIndex, value,
                            "Arial", (short) 12, new byte[] {(byte) 0, (byte) 0, (byte) 0}, new byte[] {(byte) 255, (byte) 255, (byte) 255},
                            HorizontalAlignment.RIGHT, VerticalAlignment.CENTER,
                            BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN,
                            false, false, false);

                    groupColumnIndex++;
                }
            }

            if (i < groupList.size() - 1) {
                String keyNext = groupList.get(i + 1);
                if (!processedKey.equals(keyNext)) {
                    groupListCount = 0;
                    //只有不同時才增加行索引
                    num += 20;
                    continue;
                }
            }

            groupListCount++;
        }

        for (int i = 0; i < keys.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        int maxCharKeyCount = calculateColumnWidthFactor(keys);
        Map<Integer, Integer> columnWidthMultiplier = new HashMap<>();
        columnWidthMultiplier.put(0, 13); // 第 1 列用 3.3 倍
        for(int i = 1; i < keys.size(); i++) {
            columnWidthMultiplier.put(i, maxCharKeyCount);
        }

        adjustColumnWidthForExcel(sheet, columnWidthMultiplier);

        //寫出檔案
        FileOutputStream fileOut = new FileOutputStream(path + "/OCT Total Layer_" + time + ".xlsx");
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();
    }

//    /**
//     * 產製 OCT Total Layer xlsx 檔
//     * @param path: 輸出的 Excel 路徑
//     * @param header: 表頭陣列
//     * @param octTotalLayerDataDownloadRequestMapSet: 前端傳來的 Data 內容
//     * @param fontName: 字型名稱
//     */
//    public static void createOCTTotalLayerXlsxFile(
//            String path,
//            List<List<String>> header,
//            Map<String, List<Object>> octTotalLayerDataDownloadRequestMapSet,
//            String fontName
//    ) throws IOException {
//
//        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
//        SXSSFSheet sheet = workbook.createSheet("Result");
//
//        Map<Integer, Double> columnWidthMultiplier = new HashMap<>();
//        columnWidthMultiplier.put(9, 3.8); // 第 9 列用 3.3 倍
//        columnWidthMultiplier.put(10, 1.9); // 第 10 列用 3.3 倍
//        columnWidthMultiplier.put(11, 1.9); // 第 11 列用 3.3 倍
//        columnWidthMultiplier.put(12, 1.9); // 第 12 列用 3.3 倍
//        columnWidthMultiplier.put(13, 1.9); // 第 13 列用 3.3 倍
//        columnWidthMultiplier.put(14, 1.9); // 第 14 列用 3.3 倍
//        columnWidthMultiplier.put(15, 1.9); // 第 15 列用 3.3 倍
//        columnWidthMultiplier.put(16, 1.9); // 第 16 列用 3.3 倍
//        columnWidthMultiplier.put(17, 1.9); // 第 17 列用 3.3 倍
//        createHeaderAndStyleForExcel(header, fontName, workbook, sheet, columnWidthMultiplier);
//
//        CellStyle contentStyle = createContentAndStyleForExcel(fontName, workbook);
//        CellStyle averageStyle = createContentAverageStyleForExcel(fontName, workbook);
//
//        //設定內容
//        List<String> keys = new ArrayList<>(octTotalLayerDataDownloadRequestMapSet.keySet());
//
//        int num = 0;
//        int groupMouseCount = 0;
//        double[] sums = new double[17]; // 用於存儲 sum 值，-800~800 總共 17個
//
//        //將 data 寫入 Excel
//        for (int i = 0; i < keys.size(); i++) {
//
//            String key = keys.get(i);
//            String processedKey = key;
//
//            //處理鍵值對，避免無效操作
//            if (key.contains("_")) {
//                processedKey = key.substring(0, key.indexOf("_"));
//            }
//
//            List<Object> values = octTotalLayerDataDownloadRequestMapSet.get(keys.get(i));
//
//            //建立第二列 (Row)
//            Row row = sheet.createRow(i + 1 + num);
//            createCell(row, 0, keys.get(i), contentStyle);
//
//            for(int j = 0; j < values.size(); j++){
//                createCell(row, (j + 1), values.get(j), contentStyle);
//            }
//
//            if (i < keys.size() - 1) {
//                String keyNext = keys.get(i + 1);
//                if (keyNext.contains("_")) {
//                    keyNext = keyNext.substring(0, keyNext.indexOf("_"));
//                }
//
//                if (!processedKey.equals(keyNext)) {
//                    //計算每個 Column 的 sum 值
//                    for (int j = 0; j < values.size(); j++) {
//                        if (values.get(j) instanceof Number) {
//                            sums[j] += ((Number) values.get(j)).doubleValue();
//                        }
//                    }
//
//                    //將每個 Column 的最終 sum 值寫入 Excel
//                    setAverageRow(sheet, averageStyle, i + 2 + num, sums, groupMouseCount + 1);
//
//                    //換組別將 sum 及 groupMouseCount 歸 0
//                    Arrays.fill(sums, 0.0);
//                    groupMouseCount = 0;
//                    //只有不同時才增加行索引
//                    num += 3;
//                    continue;
//                }
//            }
//
//            //計算每個 Column 的 sum 值
//            for (int j = 0; j < values.size(); j++) {
//                if (values.get(j) instanceof Number) {
//                    sums[j] += ((Number) values.get(j)).doubleValue();
//                }
//            }
//
//            groupMouseCount++;
//        }
//
//        //將每個 Column 的最終 sum 值寫入 Excel
//        setAverageRow(sheet, averageStyle, (keys.size() - 1) + 2 + num, sums, groupMouseCount);
//
//        //寫出檔案
//        FileOutputStream fileOut = new FileOutputStream(path + "/OCT Total Layer_" + time + ".xlsx");
//        workbook.write(fileOut);
//        workbook.close();
//        fileOut.close();
//    }

    /**
     * 設置 Excel 儲存格的通用方法
     * @param row: 已創建好的 row
     * @param columnIndex: 欲寫入 Excel 的 Column 索引
     * @param value: 欲寫入 Excel 的值
     * @param style: 欲使用的樣式
     */
    private static void createCell(Row row, int columnIndex, Object value, CellStyle style) {

        Cell cell = row.createCell(columnIndex);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }

        cell.setCellStyle(style);
    }

    /**
     * 計算平均值和設置 Excel 單元格
     * @param sheet: Excel 的內頁
     * @param averageStyle: 平均值欄位樣式
     * @param rowIndex: 欲寫入 Excel 的 row 索引
     * @param sums: 需計算 sum 值的陣列
     * @param count: 計算一個組別有幾隻老鼠
     */
    private static void setAverageRow(
            SXSSFSheet sheet,
            CellStyle averageStyle,
            int rowIndex,
            double[] sums,
            int count
    ) {

        Row row = sheet.createRow(rowIndex);
        Cell cell = row.createCell(0);
        cell.setCellValue("Average");
        cell.setCellStyle(averageStyle);

        for (int i = 0; i < sums.length; i++) {
            cell = row.createCell(i + 1);
            cell.setCellValue(sums[i] / count);
            cell.setCellStyle(averageStyle);
        }
    }

    private static Cell createStyledCell(
            Workbook workbook,
            Row row,
            int columnIndex,
            Object value,
            String fontName,
            short fontSize,
            byte[] rgbFont,
            byte[] rgbBackground,
            HorizontalAlignment horizontalAlignment,
            VerticalAlignment verticalAlignment,
            BorderStyle borderStyleTop,
            BorderStyle borderStyleBottom,
            BorderStyle borderStyleLeft,
            BorderStyle borderStyleRight,
            boolean bold,
            boolean italic,
            boolean wrapText) {

        //Excel 自動計算 cell 高度
        row.setHeight((short) -1);
        Cell cell = row.createCell(columnIndex);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }

        XSSFFont groupFont = (XSSFFont) workbook.createFont();
        groupFont.setFontName(fontName);
        groupFont.setFontHeightInPoints(fontSize);
        groupFont.setColor(new XSSFColor(rgbFont, null));
        groupFont.setBold(bold);
        groupFont.setItalic(italic);

        CellStyle style = workbook.createCellStyle();
        //設定儲存格自動換行
        style.setWrapText(wrapText);
        //設定儲存格上、下、左、右邊框
        style.setBorderTop(borderStyleTop);
        style.setBorderBottom(borderStyleBottom);
        style.setBorderLeft(borderStyleLeft);
        style.setBorderRight(borderStyleRight);
        //設定文字水平方向
        style.setAlignment(horizontalAlignment);
        //設定文字垂直方向
        style.setVerticalAlignment(verticalAlignment);
        style.setFont(groupFont);

        //背景顏色
        ((XSSFCellStyle) style).setFillForegroundColor(new XSSFColor(rgbBackground, new DefaultIndexedColorMap()));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cell.setCellStyle(style);

        return cell;
    }

    /**
     * 所有 Excel 表頭部分的通用樣式
     * @param header: 表頭內容陣列
     * @param fontName: 字型名稱
     * @param workbook: .xlsx 的 Excel
     * @param sheet: Excel 的內頁
     * @param columnWidthMultiplier: 動態設定列寬的索引與倍數
     */
    private static void createHeaderAndStyleForExcel(
            List<List<String>> header,
            String fontName,
            SXSSFWorkbook workbook,
            SXSSFSheet sheet,
            Map<Integer, Double> columnWidthMultiplier //動態設定列寬的索引與倍數
    ){
        //設定自適應列寬
        sheet.trackAllColumnsForAutoSizing();

        byte[] rgb = new byte[] {
                (byte) 255,
                (byte) 255,
                (byte) 255 };

        Font font = workbook.createFont();
        font.setFontName(fontName);

        XSSFFont headerFont = (XSSFFont) workbook.createFont();
        headerFont.setColor(new XSSFColor(rgb, null));
        headerFont.setFontName(fontName);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setBold(true);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderTop(BorderStyle.MEDIUM);
        headerStyle.setBorderRight(BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFont(headerFont);

        //背景顏色
        headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle defaultStyle = workbook.createCellStyle();
        defaultStyle.setFont(font);

        //設定表頭
        for (int i = 0; i < header.size(); i++) {
            Row row = sheet.createRow(i);

            for (int j = 0; j < header.get(i).size(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(headerStyle);
                cell.setCellValue(header.get(i).get(j)==null ? "" : header.get(i).get(j));
                //設定自適應列寬
                sheet.autoSizeColumn(j);

                //檢查是否有特定列需要使用倍數調整寬度
                if (columnWidthMultiplier.containsKey(j)) {
                    double multiplier = columnWidthMultiplier.get(j);
                    sheet.setColumnWidth(j, (int) (sheet.getColumnWidth(j) * multiplier));
                } else {
                    sheet.setColumnWidth(j, (sheet.getColumnWidth(j) * 16 / 10));
                }
            }
        }
    }

    /**
     * 所有 Excel 表頭部分的通用樣式
     * @param sheet: Excel 的內頁
     * @param columnWidthMultiplier: 動態設定列寬的索引與倍數
     */
    private static void adjustColumnWidthForExcel(
            SXSSFSheet sheet,
            Map<Integer, Integer> columnWidthMultiplier //動態設定列寬的索引與倍數
    ){

        List<Integer> keys = new ArrayList<>(columnWidthMultiplier.keySet());
        //設定自適應列寬
        sheet.trackAllColumnsForAutoSizing();

        // 其餘資料欄
        for (Integer key : keys) {
            sheet.setColumnWidth(key, columnWidthMultiplier.get(key) * 256);
        }

        //檢查是否有特定列需要使用倍數調整寬度
//        if (columnWidthMultiplier.containsKey(j)) {
//            double multiplier = columnWidthMultiplier.get(j);
//            sheet.setColumnWidth(j, (int) (sheet.getColumnWidth(j) * multiplier));
//        } else {
//            sheet.setColumnWidth(j, (sheet.getColumnWidth(j) * 16 / 10));
//        }
    }

    /**
     * 所有 Excel 內容部分的通用樣式
     * @param fontName: 字型名稱
     * @param workbook: .xlsx 的 Excel
     * @return : CellStyle
     */
    private static CellStyle createContentAndStyleForExcel(String fontName, SXSSFWorkbook workbook){

        CellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font contentFont = workbook.createFont();
        contentFont.setFontName(fontName);
        contentFont.setFontHeightInPoints((short) 12);
        contentStyle.setFont(contentFont);

        return contentStyle;
    }

    /**
     * 平均值欄位樣式
     * @param fontName: 字型名稱
     * @param workbook: .xlsx 的 Excel
     * @return : CellStyle
     */
    private static CellStyle createContentAverageStyleForExcel(String fontName, SXSSFWorkbook workbook){

        CellStyle averageStyle = workbook.createCellStyle();
        averageStyle.setAlignment(HorizontalAlignment.CENTER);
        averageStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        averageStyle.setBorderTop(BorderStyle.THIN);

        Font contentFont = workbook.createFont();
        contentFont.setFontName(fontName);
        contentFont.setFontHeightInPoints((short) 12);
        averageStyle.setFont(contentFont);

        return averageStyle;
    }

    /**
     * 把 xlsx file 轉換為 byte 放入 responseEntity
     * @param expType: 實驗種類
     * @param generateTime: 當前時間
     * @return : ResponseEntity<byte[]>
     */
    public static ResponseEntity<byte[]> parseXlsxFileToByte(
            String expType,
            String generateTime
    ) throws IOException {
        //轉為絕對路徑防範Path Traversal
        String excelPath = "/Users/liangchejui/Desktop/DeleteTest/";//Mac pro used
//        String excelPath = "C:\\YWC Lab Excel DownLoad";//windows used
        String ipList = expType + DatetimeConverter.getSYSTime(4) + ".xlsx";
        String excelPathIpList = excelPath + ipList;

        String normalizedPath = FilenameUtils.normalize(excelPathIpList);
        File file = new File(normalizedPath);

        //轉換byte
        byte[] xlsxFile = Files.readAllBytes(file.toPath());

        //設定HttpHeader
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        headers.setContentDispositionFormData("attachment", "_" + URLEncoder.encode(generateTime) + ".xlsx");
//        ContentDisposition contentDisposition = ContentDisposition
//                .attachment()
//                .filename(excelName)
//                .build();
//        headers.setContentDisposition(contentDisposition);
        headers.setCacheControl("no-cache,must-revalidate");

        return new ResponseEntity<>(xlsxFile, headers, HttpStatus.OK);
    }

    public static int calculateColumnWidthFactor(List<String> keys) {

        int maxLength = keys.stream()
                .filter(Objects::nonNull)
                .mapToInt(String::length)
                .max()
                .orElse(0);

        return (int) Math.ceil(maxLength * 1.5);
    }
}
