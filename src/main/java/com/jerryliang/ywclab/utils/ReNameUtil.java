package com.jerryliang.ywclab.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReNameUtil {

    public static String fileRename(
            Map<String, String> newAndOldFileNameMapSet,
            String folderPath,
            String inputCaseName,
            File copyFile,
            String subFolderName,
            int nameCutOffset
    ) {

        try {
            List<String> keys = new ArrayList<>(newAndOldFileNameMapSet.keySet());

            // 建立資料夾路徑
            String caseNamePath = folderPath + "/" + subFolderName + "/" + inputCaseName;
            String ruleOutPath = caseNamePath + "/Rule out";

            // 取得資料夾 URL
            Path caseNameDir = Paths.get(caseNamePath);
            Path ruleOutDir = Paths.get(ruleOutPath);

            // 確保資料夾存在
            if (!Files.exists(caseNameDir)) {
                Files.createDirectories(caseNameDir);
            }

            if (!Files.exists(ruleOutDir)) {
                Files.createDirectories(ruleOutDir);
            }

            if (isValidExcelFile(copyFile)) {
                String fileOriginalName = copyFile.getName();
                String fileNeedName = fileOriginalName.substring(0, fileOriginalName.indexOf("-") - nameCutOffset);
                String oldName = "";
                for (String key : keys) {
                    oldName = key;
                    if (fileNeedName.equals(oldName)) {
                        String newName = newAndOldFileNameMapSet.get(oldName) + fileOriginalName.substring(fileOriginalName.indexOf("-") - nameCutOffset);

                        // 建立新檔案的完整路徑
                        Path sourcePath = copyFile.toPath(); // 原檔案路徑
                        Path targetPath = Paths.get(caseNameDir.toString(), newName); // 改名後檔案路徑
                        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        break;
                    }
                }

                if (!fileNeedName.equals(oldName)) {
                    // 建立剔除檔案的完整路徑
                    Path sourcePath = copyFile.toPath(); // 原檔案路徑
                    Path targetPath = Paths.get(ruleOutDir.toString(), fileOriginalName);
                    Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            return "檔案名稱修改完成";
        } catch (Exception e) {
            e.printStackTrace();

            return "檔案名稱修改時發生錯誤: " + e.getMessage();
        }
    }

    private static boolean isValidExcelFile(File file) {

        if(!(file.getName().matches("^[A-Za-z()+=&~ \\d\\u4e00-\\u9fa5]+_[^_-]+-.*$"))) {

            return false;
        }

        // 檢查檔案名稱是否以 "._" 或 "~$" 開頭
        return !file.getName().startsWith("._") && !file.getName().startsWith("~$") && !file.getName().startsWith(".");
    }
}
