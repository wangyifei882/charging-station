package com.charging.station.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.station.entity.OperationLog;
import com.charging.station.mapper.OperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class OperationLogService {
    
    @Autowired
    private OperationLogMapper operationLogMapper;

    public Page<OperationLog> getLogPage(int page, int size, String username, String module) {
        Page<OperationLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            wrapper.like(OperationLog::getUsername, username);
        }
        if (StringUtils.hasText(module)) {
            wrapper.eq(OperationLog::getModule, module);
        }
        
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return operationLogMapper.selectPage(pageParam, wrapper);
    }

    public void addLog(OperationLog log) {
        log.setCreateTime(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}
