package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.entity.OrderedChargingStrategy;

public interface OrderedChargingStrategyService extends IService<OrderedChargingStrategy> {
    IPage<OrderedChargingStrategy> pageByCondition(int page, int size, String regionCode, Long operatorId, Integer status);
}
