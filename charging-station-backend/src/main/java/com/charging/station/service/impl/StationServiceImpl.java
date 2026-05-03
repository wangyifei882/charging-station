package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.entity.Station;
import com.charging.station.mapper.StationMapper;
import com.charging.station.service.StationService;
import org.springframework.stereotype.Service;

@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService {

    @Override
    public IPage<Station> getStationPage(Page<Station> page, Long operatorId, String regionCode, Integer status) {
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<>();
        if (operatorId != null) {
            wrapper.eq(Station::getOperatorId, operatorId);
        }
        if (regionCode != null) {
            wrapper.eq(Station::getRegionCode, regionCode);
        }
        if (status != null) {
            wrapper.eq(Station::getStatus, status);
        }
        wrapper.orderByDesc(Station::getCreateTime);
        return page(page, wrapper);
    }
}
