package com.jerryliang.ywclab.dao;

import com.jerryliang.ywclab.dto.MemberRequest;
import com.jerryliang.ywclab.model.Member;
import com.jerryliang.ywclab.model.Role;

import java.util.List;

public interface MemberDao {

    MemberRequest getMemberByEmail(String email);

    Integer createMember(MemberRequest memberRequest);

    List<Role> getRolesByMemberNo(Integer memberNo);

    void addRoleForMemberNo(Integer memberNo, Role role);
}
