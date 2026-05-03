package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.entity.Policy;
import com.charging.station.mapper.PolicyMapper;
import com.charging.station.service.PolicyService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PolicyServiceImpl extends ServiceImpl<PolicyMapper, Policy> implements PolicyService {

    @Override
    public IPage<Policy> pageByCondition(int page, int size, Integer status, String keyword) {
        Page<Policy> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Policy> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Policy::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Policy::getTitle, keyword);
        }
        wrapper.orderByDesc(Policy::getPublishTime);
        return page(pageParam, wrapper);
    }

    @Override
    public void publishPolicy(Policy policy, Long publisherId) {
        policy.setPublisherId(publisherId);
        policy.setPublishTime(LocalDateTime.now());
        policy.setStatus(1);
        save(policy);
    }

    @Override
    public void offlinePolicy(Long id) {
        Policy policy = getById(id);
        if (policy != null) {
            policy.setStatus(2);
            updateById(policy);
        }
    }
}
