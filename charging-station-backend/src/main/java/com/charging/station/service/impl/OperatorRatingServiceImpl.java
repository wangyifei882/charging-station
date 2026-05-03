package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.entity.OperatorRating;
import com.charging.station.mapper.OperatorRatingMapper;
import com.charging.station.service.OperatorRatingService;
import org.springframework.stereotype.Service;

@Service
public class OperatorRatingServiceImpl extends ServiceImpl<OperatorRatingMapper, OperatorRating> implements OperatorRatingService {

    @Override
    public IPage<OperatorRating> pageByCondition(int page, int size, Long operatorId, String period, String grade) {
        Page<OperatorRating> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OperatorRating> wrapper = new LambdaQueryWrapper<>();
        if (operatorId != null) wrapper.eq(OperatorRating::getOperatorId, operatorId);
        if (period != null) wrapper.eq(OperatorRating::getRatingPeriod, period);
        if (grade != null) wrapper.eq(OperatorRating::getGrade, grade);
        wrapper.orderByDesc(OperatorRating::getCreateTime);
        return page(pageParam, wrapper);
    }
}
