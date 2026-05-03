package com.charging.station.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.station.dto.UserDTO;
import com.charging.station.entity.User;
import com.charging.station.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;

    public Page<UserDTO> getUserPage(int page, int size, String username, Integer status) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> userPage = userMapper.selectPage(pageParam, wrapper);
        
        // 转换为 DTO
        List<UserDTO> dtoList = userPage.getRecords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        Page<UserDTO> dtoPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }
    
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setStatus(user.getStatus());
        dto.setLastLoginTime(user.getLastLoginTime());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        // 模拟角色名称（实际应该从 user_role 表关联查询）
        dto.setRoleName(getDefaultRoleName(user.getId()));
        return dto;
    }
    
    private String getDefaultRoleName(Long userId) {
        // 根据用户ID分配默认角色，实际应该从数据库查询
        if (userId == 1) return "系统管理员";
        if (userId % 3 == 0) return "查看人员";
        return "运营人员";
    }

    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public void addUser(User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        // 默认密码
        if (user.getPassword() == null) {
            user.setPassword("123456");
        }
        // 默认状态为启用
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        userMapper.insert(user);
    }

    public void updateUser(Long id, User user) {
        user.setId(id);
        user.setUpdateTime(LocalDateTime.now());
        // 不更新密码
        user.setPassword(null);
        userMapper.updateById(user);
    }

    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    public void updateUserStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
}
