package com.charging.station.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.station.common.Result;
import com.charging.station.entity.OperationLog;
import com.charging.station.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operation-logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    public Result<Page<OperationLog>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module) {
        return Result.success(operationLogService.getLogPage(page, size, username, module));
    }

    @PostMapping
    public Result<Void> add(@RequestBody OperationLog log) {
        operationLogService.addLog(log);
        return Result.success();
    }
}
