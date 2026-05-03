package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.dto.V2gActivityDTO;
import com.charging.station.entity.V2gActivity;
import java.util.Map;

public interface V2gActivityService extends IService<V2gActivity> {
    IPage<V2gActivityDTO> pageByCondition(int page, int size, Integer status, String regionCode);
    Map<String, Object> getActivityDetail(Long id);
}
