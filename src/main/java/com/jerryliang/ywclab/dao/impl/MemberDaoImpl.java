package com.jerryliang.ywclab.dao.impl;

import com.jerryliang.ywclab.dao.MemberDao;
import com.jerryliang.ywclab.dto.MemberRequest;
import com.jerryliang.ywclab.model.Member;
import com.jerryliang.ywclab.model.Role;
import com.jerryliang.ywclab.rowmapper.MemberRowMapper;
import com.jerryliang.ywclab.rowmapper.RoleRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MemberDaoImpl implements MemberDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public MemberRequest getMemberByEmail(String email) {
        String sql = "SELECT memberNo, email, memberNickName, password, createdTime, lastLoginTime, openFunction FROM member WHERE email = :email";

        Map<String, Object> map = new HashMap<>();
        map.put("email", email);

        List<MemberRequest> employeeList = namedParameterJdbcTemplate.query(sql, map, new MemberRowMapper());

        if(employeeList.size() > 0){
            return employeeList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Integer createMember(MemberRequest memberRequest) {
        String sql = "INSERT INTO member (email, password, memberNickName, createdTime, lastLoginTime, openFunction) " +
                     "VALUES (:email, :password, :memberNickName, :createdTime, :lastLoginTime, :openFunction)";

        Map<String, Object> map = new HashMap<>();
        map.put("email", memberRequest.getEmail());
        map.put("password", memberRequest.getPassword());
        map.put("memberNickName", memberRequest.getMemberNickName());
        map.put("createdTime", memberRequest.getCreatedTime());
        map.put("lastLoginTime", memberRequest.getLastLoginTime());
        map.put("openFunction", 0);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        return keyHolder.getKey().intValue();
    }

    @Override
    public List<Role> getRolesByMemberNo(Integer memberNo) {
        String sql = "SELECT r.roleNo, r.roleType FROM role r " +
                "JOIN memberRole mr ON r.roleNo = mr.roleNo " +
                "WHERE mr.memberNo = :memberNo";

        Map<String, Object> map = new HashMap<>();
        map.put("memberNo", memberNo);

        List<Role> roleList = namedParameterJdbcTemplate.query(sql, map, new RoleRowMapper());

        return roleList;
    }

    @Override
    public void addRoleForMemberNo(Integer memberNo, Role role) {
        String sql = "INSERT INTO memberRole (memberNo, roleNo) VALUES (:memberNo, :roleNo)";

        Map<String, Object> map = new HashMap<>();
        map.put("memberNo", memberNo);
        map.put("roleNo", role.getRoleNo());

        namedParameterJdbcTemplate.update(sql, map);
    }
}
