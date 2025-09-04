package com.jerryliang.ywclab.repository;

import com.jerryliang.ywclab.model.ActionLog;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM ActionLog AL WHERE AL.time <= :cutoffDate")
    void deleteByTimeLessThanEqual3Month(Date cutoffDate);
}
