package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.entity.OperatorRating;

public interface OperatorRatingService extends IService<OperatorRating> {
    IPage<OperatorRating> pageByCondition(int page, int size, Long operatorId, String period, String grade);
}
