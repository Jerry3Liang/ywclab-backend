package com.jerryliang.ywclab.dao;

import com.jerryliang.ywclab.model.Role;

public interface RoleDao {

    Role getRoleByName(String roleName);
}
