package com.charging.station.controller;

import com.charging.station.common.Result;
import com.charging.station.dto.SubsidyAuditRequest;
import com.charging.station.entity.SubsidyApplication;
import com.charging.station.service.SubsidyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

@RestController
@RequestMapping("/api/v1/supervision/subsidy")
public class SubsidyController {

    @Autowired
    private SubsidyService subsidyService;

    @GetMapping("/applications")
    public Result<IPage<com.charging.station.dto.SubsidyApplicationDTO>> listApplications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) Integer currentAuditStage,
            @RequestParam(required = false) Long operatorId) {
        return Result.success(subsidyService.pageByCondition(page, size, auditStatus, currentAuditStage, operatorId));
    }

    @GetMapping("/applications/{id}")
    public Result<SubsidyApplication> getApplication(@PathVariable Long id) {
        return Result.success(subsidyService.getById(id));
    }

    @PostMapping("/applications/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestBody SubsidyAuditRequest request,
                              @RequestHeader(value = "userId", required = false, defaultValue = "1") Long auditorId,
                              @RequestHeader(value = "userName", required = false, defaultValue = "admin") String auditorName) {
        subsidyService.audit(id, request, auditorId, auditorName);
        return Result.success();
    }
}
