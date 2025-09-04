package com.jerryliang.ywclab.service.impl;

import com.jerryliang.ywclab.dao.MemberDao;
import com.jerryliang.ywclab.dto.MemberRequest;
import com.jerryliang.ywclab.model.Role;
import com.jerryliang.ywclab.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    @Override
    public MemberRequest getMemberByEmail(String email) {
        return memberDao.getMemberByEmail(email);
    }

    @Override
    public Integer createMember(MemberRequest memberRequest) {
        return memberDao.createMember(memberRequest);
    }

    @Override
    public List<Role> getRolesByMemberNo(Integer memberNo) {
        return memberDao.getRolesByMemberNo(memberNo);
    }

    @Override
    public void addRoleForMemberNo(Integer memberNo, Role role) {
        memberDao.addRoleForMemberNo(memberNo, role);
    }
}
