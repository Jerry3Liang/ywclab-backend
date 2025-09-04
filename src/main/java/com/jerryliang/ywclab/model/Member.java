package com.jerryliang.ywclab.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Data
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberNo")
    private Integer memberNo;

    @Column(name = "email")
    private String email;

    @Column(name = "memberNickName")
    private String memberNickName;

    @Column(name = "password")
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "createdTime")
    private Date createdTime; //創建帳號時間

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "lastLoginTime")
    private Date lastLoginTime; // 最後登入日

    @Column(name = "openFunction")
    private Integer openFunction;

    @ManyToMany
    @JoinTable(
            name = "memberRole",
            joinColumns = @JoinColumn(name = "roleNo"),
            inverseJoinColumns = @JoinColumn(name = "memberNo")
    )
    private Set<Role> Roles = new HashSet<>();

    @OneToMany(mappedBy = "memberNo", cascade = CascadeType.ALL)
    private List<ClockTimePicker> clockTimePicker = new ArrayList<>();

}
