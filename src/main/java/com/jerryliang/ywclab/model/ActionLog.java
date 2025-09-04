package com.jerryliang.ywclab.model;

import com.jerryliang.ywclab.enums.LogStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "ACTION_LOG")
public class ActionLog {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //使用者動作
    @Column(name = "ACTION")
    private String action;

    @Column(name = "Method")
    private String method;

    @Column(name = "className")
    private String className;

    @Column(name = "RUN_TIME")
    private long runTime;

    @Column(name = "TIME")
    private Date time;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private LogStatus status;

    @Column(name = "MESSAGE", length = 1024)
    private String message;
}
