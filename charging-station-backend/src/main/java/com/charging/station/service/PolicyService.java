package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.entity.Policy;

public interface PolicyService extends IService<Policy> {
    IPage<Policy> pageByCondition(int page, int size, Integer status, String keyword);
    void publishPolicy(Policy policy, Long publisherId);
    void offlinePolicy(Long id);
}
