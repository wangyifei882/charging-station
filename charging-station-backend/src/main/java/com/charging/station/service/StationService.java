package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.entity.Station;

public interface StationService extends IService<Station> {
    IPage<Station> getStationPage(Page<Station> page, Long operatorId, String regionCode, Integer status);
}
