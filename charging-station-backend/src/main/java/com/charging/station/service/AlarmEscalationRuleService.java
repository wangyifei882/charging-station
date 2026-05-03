package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.entity.AlarmEscalationRule;

public interface AlarmEscalationRuleService extends IService<AlarmEscalationRule> {
    IPage<AlarmEscalationRule> pageByCondition(int page, int size, Integer status);
}
