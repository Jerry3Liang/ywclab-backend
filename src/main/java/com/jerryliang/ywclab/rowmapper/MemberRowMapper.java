package com.jerryliang.ywclab.rowmapper;

import com.jerryliang.ywclab.dto.MemberRequest;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberRowMapper implements RowMapper<MemberRequest> {
    @Override
    public MemberRequest mapRow(ResultSet rs, int rowNum) throws SQLException {

        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setMemberNo(rs.getInt("memberNo"));
        memberRequest.setEmail(rs.getString("email"));
        memberRequest.setPassword(rs.getString("password"));
        memberRequest.setMemberNickName(rs.getString("memberNickName"));
        memberRequest.setCreatedTime(rs.getString("createdTime"));
        memberRequest.setLastLoginTime(rs.getString("lastLoginTime"));
        memberRequest.setOpenFunction(rs.getInt("openFunction"));

        return memberRequest;
    }
}
