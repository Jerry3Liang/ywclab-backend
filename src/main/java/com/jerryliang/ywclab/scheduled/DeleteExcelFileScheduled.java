package com.jerryliang.ywclab.scheduled;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
public class DeleteExcelFileScheduled {

    // 指定資料夾路徑
    private static final String TARGET_FOLDER = "/Users/liangchejui/Desktop/DeleteTest";

    // 回收桶資料夾路徑
    private static final String RECYCLE_BIN_FOLDER = "/Users/liangchejui/.Trash";

    @Scheduled(cron = "0 0 19 1 * ?") // 每小時執行
    public void cleanUpExcelFiles() {
        File sourceFolder = new File(TARGET_FOLDER);
        File recycleBinFolder = new File(RECYCLE_BIN_FOLDER);

        // 確保回收桶資料夾存在
        if (!recycleBinFolder.exists() && !recycleBinFolder.mkdirs()) {
            System.out.println("無法建立回收桶資料夾：" + RECYCLE_BIN_FOLDER);
            return;
        }

        if (!sourceFolder.exists()) {
            System.out.println("目標資料夾不存在：" + TARGET_FOLDER);
            return;
        }

        // 過濾 Excel 檔案
        File[] files = sourceFolder.listFiles((dir, name) -> name.endsWith(".xlsx") || name.endsWith(".xls"));
        if (files == null || files.length == 0) {
            System.out.println("資料夾中沒有 Excel 檔案需要移動!");
            return;
        }

        // 移動檔案到回收桶
        for (File file : files) {
            try {
                Path targetPath = new File(recycleBinFolder, file.getName()).toPath();
                Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("已移動檔案到回收桶：" + file.getName());
            } catch (IOException e) {
                System.out.println("移動檔案失敗：" + file.getName() + "，錯誤：" + e.getMessage());
            }
        }
    }
}
