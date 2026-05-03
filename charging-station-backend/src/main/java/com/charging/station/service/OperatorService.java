package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.entity.Operator;
import java.util.List;

public interface OperatorService extends IService<Operator> {
    IPage<Operator> pageByCondition(int page, int size, String region, Integer qualificationStatus, String keyword);
    List<Operator> listAllEnabled();
}
