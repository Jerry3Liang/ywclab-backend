package com.jerryliang.ywclab.rowmapper;

import com.jerryliang.ywclab.model.Role;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RoleRowMapper implements RowMapper<Role> {

    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        Role role = new Role();
        role.setRoleNo(rs.getInt("roleNo"));
        role.setRoleType(rs.getString("roleType"));

        return role;
    }
}
