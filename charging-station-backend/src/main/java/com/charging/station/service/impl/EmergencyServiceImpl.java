package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.dto.EmergencyCommandDTO;
import com.charging.station.entity.EmergencyDispatchCommand;
import com.charging.station.entity.Station;
import com.charging.station.mapper.EmergencyDispatchCommandMapper;
import com.charging.station.mapper.StationMapper;
import com.charging.station.service.EmergencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmergencyServiceImpl extends ServiceImpl<EmergencyDispatchCommandMapper, EmergencyDispatchCommand> implements EmergencyService {

    @Autowired
    private StationMapper stationMapper;

    @Override
    public IPage<EmergencyCommandDTO> pageByCondition(int page, int size, Integer status, Integer commandType) {
        Page<EmergencyDispatchCommand> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<EmergencyDispatchCommand> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(EmergencyDispatchCommand::getStatus, status);
        }
        if (commandType != null) {
            wrapper.eq(EmergencyDispatchCommand::getCommandType, commandType);
        }
        wrapper.orderByDesc(EmergencyDispatchCommand::getIssueTime);
        IPage<EmergencyDispatchCommand> entityPage = page(pageParam, wrapper);

        // 获取站点ID列表
        List<Long> stationIds = entityPage.getRecords().stream()
            .map(EmergencyDispatchCommand::getTargetStationId)
            .filter(id -> id != null)
            .distinct()
            .collect(Collectors.toList());

        // 查询站点名称
        Map<Long, String> stationNameMap = new java.util.HashMap<>();
        if (!stationIds.isEmpty()) {
            List<Station> stations = stationMapper.selectBatchIds(stationIds);
            stationNameMap = stations.stream()
                .collect(Collectors.toMap(
                    Station::getId, 
                    s -> s.getName() != null ? s.getName() : "未知站点",
                    (k1, k2) -> k1));
        }

        // 转换为DTO
        final Map<Long, String> finalStationNameMap = stationNameMap;
        List<EmergencyCommandDTO> dtoList = entityPage.getRecords().stream()
            .map(cmd -> convertToDTO(cmd, finalStationNameMap))
            .collect(Collectors.toList());

        Page<EmergencyCommandDTO> dtoPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    private EmergencyCommandDTO convertToDTO(EmergencyDispatchCommand cmd, Map<Long, String> stationNameMap) {
        EmergencyCommandDTO dto = new EmergencyCommandDTO();
        dto.setId(cmd.getId());
        dto.setCommandNo(cmd.getCommandNo());
        dto.setCommandType(cmd.getCommandType());
        dto.setTargetOperatorId(cmd.getTargetOperatorId());
        dto.setTargetStationId(cmd.getTargetStationId());
        dto.setTargetStationName(stationNameMap.getOrDefault(cmd.getTargetStationId(), "未知站点"));
        dto.setCommandContent(cmd.getCommandContent());
        dto.setStartTime(cmd.getStartTime());
        dto.setEndTime(cmd.getEndTime());
        dto.setIssuerId(cmd.getIssuerId());
        dto.setIssueTime(cmd.getIssueTime());
        dto.setStatus(cmd.getStatus());
        dto.setCreateTime(cmd.getCreateTime());
        dto.setUpdateTime(cmd.getUpdateTime());
        return dto;
    }

    @Override
    public void createEmergencyCommand(EmergencyDispatchCommand command, Long issuerId) {
        command.setIssuerId(issuerId);
        command.setIssueTime(LocalDateTime.now());
        command.setStatus(1); // 1=执行中
        // 生成指令编号: CMD + yyyyMMdd + 4位序号
        String dateStr = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
        String commandNo = "CMD" + dateStr + String.format("%04d", (int)(Math.random() * 9000) + 1000);
        command.setCommandNo(commandNo);
        save(command);
    }

    @Override
    public void cancelCommand(Long id, String cancelReason) {
        EmergencyDispatchCommand command = getById(id);
        if (command != null) {
            command.setStatus(3);
            command.setExecutionResult("取消原因: " + cancelReason);
            updateById(command);
        }
    }
}
