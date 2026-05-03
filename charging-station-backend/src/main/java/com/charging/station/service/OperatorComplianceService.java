package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.entity.Operator;
import com.charging.station.entity.OperatorRating;
import com.charging.station.entity.OperatorViolation;

public interface OperatorComplianceService extends IService<Operator> {
    IPage<Operator> pageByCondition(int page, int size, Integer qualificationStatus, String keyword);
    IPage<OperatorRating> getRatingPage(int page, int size, Long operatorId, String period, String grade);
    IPage<OperatorViolation> getViolationPage(int page, int size, Long operatorId, Integer violationType, Integer rectificationStatus);
    void generateRating(Long operatorId, String period, Integer ratingType);
}
