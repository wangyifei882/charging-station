package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.entity.OrderedChargingStrategy;
import com.charging.station.mapper.OrderedChargingStrategyMapper;
import com.charging.station.service.OrderedChargingStrategyService;
import org.springframework.stereotype.Service;

@Service
public class OrderedChargingStrategyServiceImpl extends ServiceImpl<OrderedChargingStrategyMapper, OrderedChargingStrategy> implements OrderedChargingStrategyService {

    @Override
    public IPage<OrderedChargingStrategy> pageByCondition(int page, int size, String regionCode, Long operatorId, Integer status) {
        Page<OrderedChargingStrategy> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OrderedChargingStrategy> wrapper = new LambdaQueryWrapper<>();
        if (regionCode != null) {
            wrapper.eq(OrderedChargingStrategy::getRegionCode, regionCode);
        }
        if (operatorId != null) {
            wrapper.eq(OrderedChargingStrategy::getOperatorId, operatorId);
        }
        if (status != null) {
            wrapper.eq(OrderedChargingStrategy::getStatus, status);
        }
        wrapper.orderByDesc(OrderedChargingStrategy::getCreateTime);
        return page(pageParam, wrapper);
    }
}
