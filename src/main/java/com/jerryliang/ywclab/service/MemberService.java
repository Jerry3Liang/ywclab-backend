package com.jerryliang.ywclab.service;

import com.jerryliang.ywclab.dto.MemberRequest;
import com.jerryliang.ywclab.model.Role;

import java.util.List;

public interface MemberService {

    MemberRequest getMemberByEmail(String email);

    Integer createMember(MemberRequest memberRequest);

    List<Role> getRolesByMemberNo(Integer memberNo);

    void addRoleForMemberNo(Integer memberNo, Role role);
}
