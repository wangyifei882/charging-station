package com.charging.station.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.station.common.Result;
import com.charging.station.entity.Station;
import com.charging.station.entity.Region;
import com.charging.station.entity.Operator;
import com.charging.station.entity.Device;
import com.charging.station.service.StationService;
import com.charging.station.service.RegionService;
import com.charging.station.service.OperatorService;
import com.charging.station.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/supervision/facility")
public class FacilityController {

    @Autowired
    private StationService stationService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private DeviceService deviceService;

    @GetMapping("/ledger")
    public Result<IPage<Map<String, Object>>> getStationLedger(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(required = false) String regionCode,
            @RequestParam(required = false) Integer status) {

        Page<Station> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Station> wrapper = new LambdaQueryWrapper<>();
        if (operatorId != null) {
            wrapper.eq(Station::getOperatorId, operatorId);
        }
        if (regionCode != null) {
            wrapper.eq(Station::getRegionCode, regionCode);
        }
        if (status != null) {
            wrapper.eq(Station::getStatus, status);
        }
        wrapper.orderByDesc(Station::getCreateTime);
        IPage<Station> stationPage = stationService.page(pageParam, wrapper);

        // 转换为前端需要的格式
        List<Map<String, Object>> records = stationPage.getRecords().stream().map(station -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", station.getId());
            map.put("stationName", station.getName());
            map.put("address", station.getAddress());
            map.put("status", station.getStatus());
            map.put("regionCode", station.getRegionCode());

            // 关联查询运营商名称
            if (station.getOperatorId() != null) {
                Operator operator = operatorService.getById(station.getOperatorId());
                if (operator != null) {
                    map.put("operatorName", operator.getOperatorName());
                }
            }

            // 关联查询区域名称
            if (station.getRegionCode() != null) {
                Region region = regionService.lambdaQuery().eq(Region::getRegionCode, station.getRegionCode()).one();
                if (region != null) {
                    map.put("regionName", region.getRegionName());
                }
            }

            // 统计设备数量
            List<Device> devices = deviceService.lambdaQuery().eq(Device::getStationId, station.getId()).list();
            map.put("totalDevices", devices.size());
            map.put("onlineDevices", (int) devices.stream().filter(d -> d.getStatus() == 1).count());

            // 今日充电量（mock）
            map.put("todayCharge", String.valueOf((int)(Math.random() * 1500 + 200)));

            return map;
        }).collect(Collectors.toList());

        // 构建返回的Page
        Page<Map<String, Object>> resultPage = new Page<>(page, pageSize, stationPage.getTotal());
        resultPage.setRecords(records);
        return Result.success(resultPage);
    }

    @GetMapping("/stations/{id}")
    public Result<Map<String, Object>> getStationDetail(@PathVariable Long id) {
        Station station = stationService.getById(id);
        if (station == null) {
            return Result.error(400, "站点不存在");
        }
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("id", station.getId());
        data.put("name", station.getName());
        data.put("address", station.getAddress());
        data.put("status", station.getStatus());
        return Result.success(data);
    }

    @GetMapping("/regions-by-operator")
    public Result<List<Map<String, Object>>> getRegionsByOperator(@RequestParam(required = false) Long operatorId) {
        List<Region> regions;
        if (operatorId != null) {
            // 获取该运营商下所有站点实际使用的区域编码（街道级别）
            List<Station> stations = stationService.lambdaQuery().eq(Station::getOperatorId, operatorId).list();
            List<String> regionCodes = stations.stream()
                .map(Station::getRegionCode)
                .filter(code -> code != null && !code.isEmpty())
                .distinct()
                .collect(Collectors.toList());
            if (regionCodes.isEmpty()) {
                return Result.success(List.of());
            }
            // 查询实际的区域（街道级别，不限制regionLevel）
            regions = regionService.lambdaQuery().in(Region::getRegionCode, regionCodes).list();
        } else {
            // 未选择运营商时，返回所有区域
            regions = regionService.list();
        }
        
        List<Map<String, Object>> result = regions.stream().map(region -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("code", region.getRegionCode());
            map.put("name", region.getRegionName());
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }
}
