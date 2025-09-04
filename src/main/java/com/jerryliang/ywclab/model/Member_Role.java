package com.jerryliang.ywclab.model;

import jakarta.persistence.*;

@Entity
@Table(name = "memberRole")
public class Member_Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberRoleNo")
    private Integer memberRoleNo;

    @ManyToOne
    @JoinColumn(name = "roleNo", insertable = false, updatable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "memberNo", insertable = false, updatable = false)
    private Member member;
}
