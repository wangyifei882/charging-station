package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.entity.OperatorViolation;

public interface OperatorViolationService extends IService<OperatorViolation> {
    IPage<OperatorViolation> pageByCondition(int page, int size, Long operatorId, Integer violationType, Integer rectificationStatus);
}
