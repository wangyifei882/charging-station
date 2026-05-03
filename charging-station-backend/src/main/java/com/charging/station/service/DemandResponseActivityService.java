package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.dto.DemandResponseActivityDTO;
import com.charging.station.dto.OperatorParticipationRankDTO;
import com.charging.station.entity.DemandResponseActivity;
import java.util.List;
import java.util.Map;

public interface DemandResponseActivityService extends IService<DemandResponseActivity> {
    IPage<DemandResponseActivityDTO> pageByCondition(int page, int size, Integer responseType, Integer status);
    Map<String, Object> getActivityDetail(Long id);
    Map<String, Object> getStats();
    List<OperatorParticipationRankDTO> getOperatorRanking();
}
