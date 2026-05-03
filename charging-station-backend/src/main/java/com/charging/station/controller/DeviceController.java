package com.charging.station.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.station.common.Result;
import com.charging.station.entity.Device;
import com.charging.station.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @GetMapping
    public Result<Page<Device>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String deviceCode,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long typeId) {
        return Result.success(deviceService.getDevicePage(page, size, deviceCode, status, typeId));
    }

    @GetMapping("/{id}")
    public Result<Device> getById(@PathVariable Long id) {
        return Result.success(deviceService.getDeviceById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Device device) {
        deviceService.addDevice(device);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Device device) {
        deviceService.updateDevice(id, device);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return Result.success();
    }

    @GetMapping("/stats")
    public Result<Object> stats() {
        return Result.success(deviceService.getDeviceStats());
    }

    @GetMapping("/all")
    public Result<List<Device>> all() {
        return Result.success(deviceService.getAllDevices());
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        deviceService.updateDeviceStatus(id, status);
        return Result.success();
    }

    @PostMapping("/{id}/control")
    public Result<Void> control(@PathVariable Long id, @RequestBody java.util.Map<String, Object> params) {
        String action = (String) params.get("action");
        switch (action) {
            case "start" -> deviceService.updateDeviceStatus(id, 1);
            case "stop" -> deviceService.updateDeviceStatus(id, 0);
            default -> { return Result.error(400, "未知操作"); }
        }
        return Result.success();
    }
}
