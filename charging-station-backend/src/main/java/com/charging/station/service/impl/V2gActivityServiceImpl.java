package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.dto.V2gActivityDTO;
import com.charging.station.entity.V2gActivity;
import com.charging.station.mapper.V2gActivityMapper;
import com.charging.station.service.V2gActivityService;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class V2gActivityServiceImpl extends ServiceImpl<V2gActivityMapper, V2gActivity> implements V2gActivityService {

    @Override
    public IPage<V2gActivityDTO> pageByCondition(int page, int size, Integer status, String regionCode) {
        Page<V2gActivity> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<V2gActivity> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(V2gActivity::getStatus, status);
        }
        if (regionCode != null) {
            wrapper.eq(V2gActivity::getRegionCode, regionCode);
        }
        wrapper.orderByDesc(V2gActivity::getStartTime);
        IPage<V2gActivity> entityPage = page(pageParam, wrapper);

        List<V2gActivityDTO> dtoList = entityPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        Page<V2gActivityDTO> dtoPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    private V2gActivityDTO convertToDTO(V2gActivity entity) {
        V2gActivityDTO dto = new V2gActivityDTO();
        dto.setId(entity.getId());
        dto.setActivityNo(entity.getActivityNo());
        dto.setActivityName(entity.getActivityName());
        // 根据活动名称判断类型
        if (entity.getActivityName() != null && entity.getActivityName().contains("有序充电")) {
            dto.setActivityType("ordered");
        } else {
            dto.setActivityType("v2g");
        }
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setRegionCode(entity.getRegionCode());
        dto.setOrganizer(entity.getOrganizer());
        dto.setTargetDischargeEnergy(entity.getTargetDischargeEnergy());
        dto.setTargetPower(entity.getTargetDischargeEnergy());
        dto.setActualDischargeEnergy(entity.getActualDischargeEnergy());
        dto.setParticipatingStationCount(entity.getParticipatingStationCount());
        dto.setParticipatingVehicleCount(entity.getParticipatingVehicleCount());
        dto.setParticipantCount(entity.getParticipatingVehicleCount());
        dto.setAvgDischargePrice(entity.getAvgDischargePrice());
        dto.setStatus(entity.getStatus());
        dto.setRemark(entity.getRemark());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    @Override
    public Map<String, Object> getActivityDetail(Long id) {
        Map<String, Object> result = new HashMap<>();
        V2gActivity activity = getById(id);
        result.put("activityInfo", convertToDTO(activity));
        return result;
    }
}
