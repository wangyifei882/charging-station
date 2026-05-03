package com.charging.station.controller;

import com.charging.station.common.Result;
import com.charging.station.dto.AccessApplicationDTO;
import com.charging.station.dto.AuditRequest;
import com.charging.station.entity.AccessApplication;
import com.charging.station.service.AccessApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

@RestController
@RequestMapping("/api/v1/supervision/facility/access-applications")
public class AccessApplicationController {

    @Autowired
    private AccessApplicationService accessApplicationService;

    @GetMapping
    public Result<IPage<AccessApplicationDTO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        Integer filterStatus = auditStatus != null ? auditStatus : status;
        return Result.success(accessApplicationService.pageByCondition(page, size, filterStatus, keyword));
    }

    @GetMapping("/{id}")
    public Result<AccessApplication> detail(@PathVariable Long id) {
        return Result.success(accessApplicationService.getById(id));
    }

    @PostMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestBody AuditRequest request,
                              @RequestHeader(value = "userId", required = false, defaultValue = "1") Long auditorId) {
        accessApplicationService.audit(id, request, auditorId);
        return Result.success();
    }
}
