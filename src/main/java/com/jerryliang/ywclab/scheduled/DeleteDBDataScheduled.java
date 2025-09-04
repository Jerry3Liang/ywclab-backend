package com.jerryliang.ywclab.scheduled;

import com.jerryliang.ywclab.repository.ActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class DeleteDBDataScheduled {

    @Autowired
    private ActionLogRepository actionLogRepository;

    @Scheduled(cron = "0 0 0 5 * ?")
    public void cleanUpOldRecords() {
        // 計算刪除條件的日期，例如 60 天前
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -60); // 設定為 90 天前
        Date cutoffDate = calendar.getTime();

        // 執行刪除
        try {
            actionLogRepository.deleteByTimeLessThanEqual3Month(cutoffDate);
            System.out.println("成功刪除 60 天前的檔案記錄。");
        } catch (Exception e) {
            System.out.println("刪除檔案記錄失敗：" + e.getMessage());
        }
    }
}
