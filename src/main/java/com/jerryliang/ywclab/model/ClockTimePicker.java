package com.jerryliang.ywclab.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Entity
@Table(name = "timePicker")
public class ClockTimePicker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clockTimePickerId")
    private Integer clockTimePickerId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "startTime")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "endTime")
    private Date endTime;

    @Column(name = "spentTime")
    private Integer spentTime;

    @Column(name = "finishedStatus")
    private Integer finishedStatus;

    @ManyToOne
    @JoinColumn(name = "memberNo")
    private Member memberNo;
}
