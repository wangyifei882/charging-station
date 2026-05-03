package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.dto.EmergencyCommandDTO;
import com.charging.station.entity.EmergencyDispatchCommand;

public interface EmergencyService extends IService<EmergencyDispatchCommand> {
    IPage<EmergencyCommandDTO> pageByCondition(int page, int size, Integer status, Integer commandType);
    void createEmergencyCommand(EmergencyDispatchCommand command, Long issuerId);
    void cancelCommand(Long id, String cancelReason);
}
