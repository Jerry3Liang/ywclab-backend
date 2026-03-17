package com.jerryliang.ywclab.utils;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

public class CommonMethods {

    public static InputStream convertToXlsx(MultipartFile file) throws Exception {

        Workbook workbook = new Workbook(file.getInputStream());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.save(outputStream, SaveFormat.XLSX);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public static ResponseEntity<Boolean> checkFolderName(String folderPath, String inputCaseName) {
        File folder = new File(folderPath);

        if(!folder.exists() || !folder.isDirectory()){
            throw new IllegalArgumentException("不存在的資料夾路徑： " + folderPath);
        }

        File[] files = folder.listFiles();

        if (files != null) {
            for(File checkingFile : files){
                String fileName = checkingFile.getName();
                if(inputCaseName.equals(fileName)){

                    return ResponseEntity.ok(true);
                }
            }
        }

        return ResponseEntity.ok(false);
    }

    public static void zeroIfAtLeastNZero(double[] arr, int n) {
        int count = 0;

        for (double v : arr) {
            if (v == 0.0 && ++count >= n) {
                java.util.Arrays.fill(arr, 0.0);
                return;
            }
        }
    }
}
