package com.charging.station.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.station.common.Result;
import com.charging.station.dto.FaultTicketDTO;
import com.charging.station.entity.FaultTicket;
import com.charging.station.service.FaultTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/faults/tickets")
public class FaultTicketController {
    @Autowired
    private FaultTicketService faultTicketService;

    @GetMapping
    public Result<Page<FaultTicketDTO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long deviceId) {
        return Result.success(faultTicketService.getTicketPage(page, size, status, deviceId));
    }

    @GetMapping("/{id}")
    public Result<FaultTicket> getById(@PathVariable Long id) {
        return Result.success(faultTicketService.getTicketById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody FaultTicket ticket) {
        faultTicketService.createTicket(ticket);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        faultTicketService.updateTicketStatus(id, params.get("status"));
        return Result.success();
    }

    @PutMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id, @RequestBody Map<String, String> params) {
        faultTicketService.completeTicket(id, params.get("solution"), params.get("attachmentUrls"));
        return Result.success();
    }
}
