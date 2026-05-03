package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.entity.AlarmEscalationRule;
import com.charging.station.mapper.AlarmEscalationRuleMapper;
import com.charging.station.service.AlarmEscalationRuleService;
import org.springframework.stereotype.Service;

@Service
public class AlarmEscalationRuleServiceImpl extends ServiceImpl<AlarmEscalationRuleMapper, AlarmEscalationRule> implements AlarmEscalationRuleService {

    @Override
    public IPage<AlarmEscalationRule> pageByCondition(int page, int size, Integer status) {
        Page<AlarmEscalationRule> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AlarmEscalationRule> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(AlarmEscalationRule::getStatus, status);
        }
        wrapper.orderByDesc(AlarmEscalationRule::getCreateTime);
        
        return page(pageParam, wrapper);
    }
}
