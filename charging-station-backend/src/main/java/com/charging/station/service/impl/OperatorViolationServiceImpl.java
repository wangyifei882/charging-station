package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.entity.OperatorViolation;
import com.charging.station.mapper.OperatorViolationMapper;
import com.charging.station.service.OperatorViolationService;
import org.springframework.stereotype.Service;

@Service
public class OperatorViolationServiceImpl extends ServiceImpl<OperatorViolationMapper, OperatorViolation> implements OperatorViolationService {

    @Override
    public IPage<OperatorViolation> pageByCondition(int page, int size, Long operatorId, Integer violationType, Integer rectificationStatus) {
        Page<OperatorViolation> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OperatorViolation> wrapper = new LambdaQueryWrapper<>();
        if (operatorId != null) {
            wrapper.eq(OperatorViolation::getOperatorId, operatorId);
        }
        if (violationType != null) {
            wrapper.eq(OperatorViolation::getViolationType, violationType);
        }
        if (rectificationStatus != null) {
            wrapper.eq(OperatorViolation::getRectificationStatus, rectificationStatus);
        }
        wrapper.orderByDesc(OperatorViolation::getCreateTime);
        return page(pageParam, wrapper);
    }
}
