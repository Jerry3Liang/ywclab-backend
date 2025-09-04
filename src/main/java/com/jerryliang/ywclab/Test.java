package com.jerryliang.ywclab;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Test {
    public static void main(String[] args) {
        String filePath = "/Users/liangchejui/Desktop/聯邦網通/超商代收/test1.xlsx";

        List<String> excelColumnLists = Test.readExcelAndGetColumnNames(filePath);

        List<Map<String, String>> excelMappingData = Test.readExcelAndGetMappingDate(filePath);

        List<String> jsonStringLists = new ArrayList<>();

        String jsonString = "{"
                + "\"1\":{\"name\":\"學號\",\"requiredPaymentDefaultItem\":1,\"type\":\"PAYMENT\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"A01\"},"
                + "\"2\":{\"name\":\"姓名\",\"requiredPaymentDefaultItem\":2,\"type\":\"PAYMENT\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"\"},"
                + "\"6\":{\"name\":\"班級\",\"requiredPaymentDefaultItem\":6,\"type\":\"PAYMENT\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"J101\"},"
                + "\"13\":{\"name\":\"暑輔費\",\"requiredPaymentDefaultItem\":13,\"type\":\"BASIC\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"2040\"},"
                + "\"14\":{\"name\":\"育樂營\",\"requiredPaymentDefaultItem\":14,\"type\":\"BASIC\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"400\"},"
                + "\"15\":{\"name\":\"團膳費\",\"requiredPaymentDefaultItem\":15,\"type\":\"BASIC\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"850\"},"
                + "\"16\":{\"name\":\"專車\",\"requiredPaymentDefaultItem\":16,\"type\":\"BASIC\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"0\"},"
                + "\"7\":{\"name\":\"座號\",\"requiredPaymentDefaultItem\":7,\"type\":\"PAYMENT\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"30\"}"
                + "}";

        String jsonString1 = "{"
                + "\"1\":{\"name\":\"學號\",\"requiredPaymentDefaultItem\":1,\"type\":\"PAYMENT\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"A02\"},"
                + "\"6\":{\"name\":\"班級\",\"requiredPaymentDefaultItem\":6,\"type\":\"PAYMENT\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"J101\"},"
                + "\"13\":{\"name\":\"暑輔費\",\"requiredPaymentDefaultItem\":13,\"type\":\"BASIC\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"2040\"},"
                + "\"14\":{\"name\":\"育樂營\",\"requiredPaymentDefaultItem\":14,\"type\":\"BASIC\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"400\"},"
                + "\"15\":{\"name\":\"團膳費\",\"requiredPaymentDefaultItem\":15,\"type\":\"BASIC\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"850\"},"
                + "\"16\":{\"name\":\"專車\",\"requiredPaymentDefaultItem\":16,\"type\":\"BASIC\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"0\"},"
                + "\"7\":{\"name\":\"座號\",\"requiredPaymentDefaultItem\":7,\"type\":\"PAYMENT\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"30\"}"
                + "}";

        String jsonString2 = "{"
                + "\"1\":{\"name\":\"學號\",\"requiredPaymentDefaultItem\":1,\"type\":\"PAYMENT\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"A03\"},"
                + "\"2\":{\"name\":\"姓名\",\"requiredPaymentDefaultItem\":2,\"type\":\"PAYMENT\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"\"},"
                + "\"3\":{\"name\":\"身分證\",\"requiredPaymentDefaultItem\":3,\"type\":\"PAYMENT\",\"encrypt\":true,\"format\":\"THREE_BLOCK\",\"value\":\"H123452266\"},"
                + "\"13\":{\"name\":\"暑輔費\",\"requiredPaymentDefaultItem\":13,\"type\":\"BASIC\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"2040\"},"
                + "\"14\":{\"name\":\"育樂營\",\"requiredPaymentDefaultItem\":14,\"type\":\"BASIC\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"400\"},"
                + "\"15\":{\"name\":\"團膳費\",\"requiredPaymentDefaultItem\":15,\"type\":\"BASIC\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"850\"},"
                + "\"16\":{\"name\":\"專車\",\"requiredPaymentDefaultItem\":16,\"type\":\"BASIC\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"0\"},"
                + "\"7\":{\"name\":\"座號\",\"requiredPaymentDefaultItem\":7,\"type\":\"PAYMENT\",\"encrypt\":false,\"format\":\"THREE_BLOCK\",\"value\":\"30\"}"
                + "}";
        jsonStringLists.add(jsonString);
        jsonStringLists.add(jsonString1);
        jsonStringLists.add(jsonString2);

        for(int i =1; i < excelColumnLists.size(); i++){
            Map<String, String> dataMap = new HashMap<>();
            for (Map<String, String> row : excelMappingData) {
                dataMap.put(row.get("學號"), row.get(excelColumnLists.get(i)));
            }
            System.out.println(dataMap);

            for(int j = 0; j < jsonStringLists.size(); j++){
                JSONObject jsonObject = new JSONObject(jsonStringLists.get(j));
                // 取得 key "1" 的 JSON 物件
                JSONObject studentIdObject = jsonObject.getJSONObject("1");
                // 取得 "value" 欄位的值
                String studentIdValue = studentIdObject.getString("value");

                // 取得 key 並按數字順序排序
                List<String> sortedKeys = new ArrayList<>(jsonObject.keySet());
                sortedKeys.sort(Comparator.comparingInt(Integer::parseInt));

                Map<String, JSONObject> itemDetailMap = new HashMap<>();

                // 依序輸出 key 並存入 itemDetailMap
                for (String key : sortedKeys) {
                    itemDetailMap.put(key, jsonObject.getJSONObject(key));
                }

                //檢查是否為已有 item
                for(int k = 0; k < itemDetailMap.size(); k++){
                    String key = sortedKeys.get(k);
                    if(itemDetailMap.get(key).getString("name").equals(excelColumnLists.get(i))){
                        jsonObject.getJSONObject(key).put("value", dataMap.get(studentIdValue));
                        String newJasonString = jsonObject.toString();
                        //將 newJasonString 直接存入 DB

                        break;
                    }
                }

                //先從 DB 找 req_payment_default_item 表的 sort 欄位的 (值 + 1) 作為要存入 itemDetailMap 的 key
                String newKey = "5";

                //新存入的 jsonString 直接先用原本 key = 1 的 value
                itemDetailMap.put(newKey, studentIdObject);

                //修改 value
                System.out.println(itemDetailMap.get(newKey));

                //如果 key 為 3，


            }
        }







        JSONObject jsonObject = new JSONObject(jsonString);

        // 取得 key "1" 的 JSON 物件
        JSONObject studentIdObject = jsonObject.getJSONObject("1");
        // 取得 "value" 欄位的值
        String studentIdValue = studentIdObject.getString("value");

        System.out.println("學號的值: " + studentIdValue);

        // 取得 key 並按數字順序排序
        List<String> sortedKeys = new ArrayList<>(jsonObject.keySet());
        sortedKeys.sort(Comparator.comparingInt(Integer::parseInt));

        Map<String, JSONObject> itemDetailMap = new HashMap<>();

        // 依序輸出 key
        for (String key : sortedKeys) {
            JSONObject value = jsonObject.getJSONObject(key);
            System.out.println(value);
            itemDetailMap.put(key, jsonObject.getJSONObject(key));
            String valueName = value.getString("name");
            System.out.println(valueName);
        }

        List<String> itemDetailMapKeys = new ArrayList<>(itemDetailMap.keySet());

        System.out.println("1 : " + jsonObject);

        jsonObject.getJSONObject("2").put("value", "陳柏文");

        System.out.println("2 : " + jsonObject);

        System.out.println("舊的 itemDetail : " + jsonString);
        String newJasonString = jsonObject.toString();
        System.out.println("新的 itemDetail : " + newJasonString);

    }

    public static List<Map<String, String>> readExcelAndGetMappingDate(String filePath) {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // 讀取第一個工作表
            Iterator<Row> rowIterator = sheet.iterator();

            // 讀取標題列（第一行）
            if (!rowIterator.hasNext()) return dataList; // 若 Excel 為空，直接返回
            Row headerRow = rowIterator.next();

            // 儲存標題對應的欄位名稱
            List<String> columnNames = new ArrayList<>();
            for (Cell cell : headerRow) {
                columnNames.add(cell.getStringCellValue().trim()); // 取得標題列名稱
            }

            // 讀取剩餘行數據
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, String> rowData = new HashMap<>();

                for (int i = 0; i < columnNames.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData.put(columnNames.get(i), cell.toString().trim()); // 存入 Map
                }

                dataList.add(rowData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    public static List<String> readExcelAndGetColumnNames(String filePath) {
        List<String> columnNames = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // 讀取第一個工作表
            Iterator<Row> rowIterator = sheet.iterator();

            // 讀取標題列（第一行）
            if (!rowIterator.hasNext()) return columnNames; // 若 Excel 為空，直接返回
            Row headerRow = rowIterator.next();

            // 儲存標題對應的欄位名稱
            for (Cell cell : headerRow) {
                columnNames.add(cell.getStringCellValue().trim()); // 取得標題列名稱
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return columnNames;
    }
}
