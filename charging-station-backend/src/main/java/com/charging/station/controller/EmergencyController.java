package com.charging.station.controller;

import com.charging.station.common.Result;
import com.charging.station.dto.EmergencyCommandRequest;
import com.charging.station.entity.EmergencyDispatchCommand;
import com.charging.station.service.EmergencyService;
import com.charging.station.service.AlarmService;
import com.charging.station.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/supervision/emergency")
public class EmergencyController {

    @Autowired
    private EmergencyService emergencyService;

    @Autowired
    private AlarmService alarmService;

    @GetMapping("/commands")
    public Result<IPage<com.charging.station.dto.EmergencyCommandDTO>> getCommandList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer commandType) {
        return Result.success(emergencyService.pageByCondition(page, size, status, commandType));
    }

    @PostMapping("/commands")
    public Result<Void> createCommand(
            @RequestBody EmergencyCommandRequest request,
            @RequestHeader(value = "userId", required = false, defaultValue = "1") Long issuerId) {
        // 处理多站点情况
        String targetStationIds = request.getTargetStationIds();
        if (targetStationIds != null && !targetStationIds.isEmpty()) {
            String[] stationIds = targetStationIds.split(",");
            for (String stationIdStr : stationIds) {
                try {
                    Long stationId = Long.parseLong(stationIdStr.trim());
                    EmergencyDispatchCommand command = new EmergencyDispatchCommand();
                    command.setCommandType(request.getCommandType());
                    command.setTargetOperatorId(request.getTargetOperatorId());
                    command.setTargetStationId(stationId);
                    command.setCommandContent(request.getCommandContent());
                    command.setIssuerId(issuerId);
                    emergencyService.createEmergencyCommand(command, issuerId);
                } catch (NumberFormatException e) {
                    // 忽略无效的站点ID
                }
            }
        } else if (request.getTargetStationId() != null) {
            // 单站点情况
            EmergencyDispatchCommand command = new EmergencyDispatchCommand();
            command.setCommandType(request.getCommandType());
            command.setTargetOperatorId(request.getTargetOperatorId());
            command.setTargetStationId(request.getTargetStationId());
            command.setCommandContent(request.getCommandContent());
            command.setIssuerId(issuerId);
            emergencyService.createEmergencyCommand(command, issuerId);
        }
        return Result.success();
    }

    @GetMapping("/commands/{id}")
    public Result<EmergencyDispatchCommand> getCommandDetail(@PathVariable Long id) {
        return Result.success(emergencyService.getById(id));
    }

    @PutMapping("/commands/{id}/cancel")
    public Result<Void> cancelCommand(@PathVariable Long id, @RequestBody Map<String, String> body) {
        emergencyService.cancelCommand(id, body.get("cancelReason"));
        return Result.success();
    }
}
