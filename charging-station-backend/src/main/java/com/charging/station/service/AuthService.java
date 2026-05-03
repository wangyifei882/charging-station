package com.charging.station.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.station.common.ErrorCode;
import com.charging.station.entity.User;
import com.charging.station.entity.UserRole;
import com.charging.station.entity.Role;
import com.charging.station.mapper.UserMapper;
import com.charging.station.mapper.UserRoleMapper;
import com.charging.station.mapper.RoleMapper;
import com.charging.station.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> login(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException(ErrorCode.LOGIN_FAILED.getMessage());
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException(ErrorCode.ACCOUNT_DISABLED.getMessage());
        }
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        UserRole userRole = userRoleMapper.selectOne(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, user.getId()));
        String role = "";
        if (userRole != null) {
            Role r = roleMapper.selectById(userRole.getRoleId());
            role = r != null ? r.getRoleName() : "";
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRealName(), role);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("roleName", role);
        userInfo.put("avatar", user.getAvatarUrl());
        result.put("userInfo", userInfo);
        return result;
    }
}
