package com.charging.station.controller;

import com.charging.station.common.Result;
import com.charging.station.entity.Alarm;
import com.charging.station.entity.AlarmEscalationRule;
import com.charging.station.service.AlarmEscalationRuleService;
import com.charging.station.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/supervision/emergency/alarms")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private AlarmEscalationRuleService alarmEscalationRuleService;

    @GetMapping
    public Result<IPage<Alarm>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer alarmLevel,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String regionCode,
            @RequestParam(required = false) Long operatorId) {
        return Result.success(alarmService.pageByCondition(page, size, alarmLevel, status, regionCode, operatorId));
    }

    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(@RequestParam(required = false) Integer level) {
        return Result.success(alarmService.getUnreadAlarmCount(level));
    }

    @GetMapping("/escalation-rules")
    public Result<IPage<AlarmEscalationRule>> getEscalationRules(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.success(alarmEscalationRuleService.pageByCondition(page, size, status));
    }

    @GetMapping("/escalation-rules/{id}")
    public Result<AlarmEscalationRule> getEscalationRule(@PathVariable Long id) {
        return Result.success(alarmEscalationRuleService.getById(id));
    }

    @PostMapping("/escalation-rules")
    public Result<Void> createEscalationRule(@RequestBody AlarmEscalationRule rule) {
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        alarmEscalationRuleService.save(rule);
        return Result.success();
    }

    @PutMapping("/escalation-rules/{id}")
    public Result<Void> updateEscalationRule(@PathVariable Long id, @RequestBody AlarmEscalationRule rule) {
        rule.setId(id);
        rule.setUpdateTime(LocalDateTime.now());
        alarmEscalationRuleService.updateById(rule);
        return Result.success();
    }

    @DeleteMapping("/escalation-rules/{id}")
    public Result<Void> deleteEscalationRule(@PathVariable Long id) {
        alarmEscalationRuleService.removeById(id);
        return Result.success();
    }
}
