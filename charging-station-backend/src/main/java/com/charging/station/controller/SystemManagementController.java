package com.charging.station.controller;

import com.charging.station.common.Result;
import com.charging.station.entity.Region;
import com.charging.station.service.RegionService;
import com.charging.station.vo.RegionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/supervision/system")
public class SystemManagementController {

    @Autowired
    private RegionService regionService;

    @GetMapping("/regions")
    public Result<List<RegionVO>> getRegionTree() {
        return Result.success(regionService.getRegionTree());
    }

    @GetMapping("/regions/children")
    public Result<List<Region>> getRegionChildren(@RequestParam(required = false) String parentCode) {
        return Result.success(regionService.getChildrenByCode(parentCode));
    }
}
