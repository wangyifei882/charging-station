package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.entity.Operator;
import com.charging.station.mapper.OperatorMapper;
import com.charging.station.service.OperatorService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OperatorServiceImpl extends ServiceImpl<OperatorMapper, Operator> implements OperatorService {

    @Override
    public IPage<Operator> pageByCondition(int page, int size, String region, Integer qualificationStatus, String keyword) {
        Page<Operator> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Operator> wrapper = new LambdaQueryWrapper<>();
        if (region != null) {
            wrapper.eq(Operator::getRegistrationRegion, region);
        }
        if (qualificationStatus != null) {
            wrapper.eq(Operator::getQualificationStatus, qualificationStatus);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Operator::getOperatorName, keyword)
                   .or()
                   .like(Operator::getOperatorCode, keyword);
        }
        wrapper.orderByDesc(Operator::getCreateTime);
        return page(pageParam, wrapper);
    }

    @Override
    public List<Operator> listAllEnabled() {
        return list(new LambdaQueryWrapper<Operator>().eq(Operator::getStatus, 1));
    }
}
