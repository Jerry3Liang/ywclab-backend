package com.jerryliang.ywclab.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roleNo")
    private Integer roleNo;

    @Column(name = "roleType")
    private String roleType;

    @ManyToMany
    @JoinTable(
            name = "memberRole",
            joinColumns = @JoinColumn(name = "memberNo"),
            inverseJoinColumns = @JoinColumn(name = "roleNo")
    )
    private Set<Member> members = new HashSet<>();
}
