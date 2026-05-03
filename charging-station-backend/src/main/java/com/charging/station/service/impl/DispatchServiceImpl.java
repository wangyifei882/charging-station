package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.entity.DispatchStationAllocation;
import com.charging.station.entity.DispatchTask;
import com.charging.station.entity.Station;
import com.charging.station.mapper.DispatchTaskMapper;
import com.charging.station.service.DispatchService;
import com.charging.station.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class DispatchServiceImpl extends ServiceImpl<DispatchTaskMapper, DispatchTask> implements DispatchService {

    @Autowired
    private StationService stationService;

    @Override
    public Map<String, Object> getOverview(String taskNo) {
        Map<String, Object> result = new HashMap<>();
        DispatchTask task = getOne(new LambdaQueryWrapper<DispatchTask>()
                .eq(DispatchTask::getTaskNo, taskNo));
        if (task == null) {
            throw new RuntimeException("调度任务不存在");
        }
        result.put("totalChargePower", task.getTotalChargePower());
        result.put("totalDischargePower", task.getTotalDischargePower());
        result.put("actualChargePower", task.getActualChargePower());
        result.put("actualDischargePower", task.getActualDischargePower());
        result.put("executionRate", task.getExecutionRate());
        result.put("availableStationCount", task.getAvailableStationCount());
        result.put("overloadStationCount", task.getOverloadStationCount());
        result.put("gridConstraintStatus", task.getGridConstraintStatus());
        result.put("dispatchMode", task.getDispatchMode());
        return result;
    }

    @Override
    public Map<String, Object> getRegionPowerAllocation(String taskNo) {
        Map<String, Object> result = new HashMap<>();
        DispatchTask task = getOne(new LambdaQueryWrapper<DispatchTask>()
                .eq(DispatchTask::getTaskNo, taskNo));
        if (task == null) {
            throw new RuntimeException("调度任务不存在");
        }

        List<Map<String, Object>> regionAllocations = new ArrayList<>();
        List<Station> stations = stationService.list(new LambdaQueryWrapper<Station>()
                .eq(Station::getRegionCode, task.getRegionCode()));
        
        Map<String, List<Station>> regionStationMap = new HashMap<>();
        for (Station station : stations) {
            String regionCode = station.getRegionCode();
            regionStationMap.computeIfAbsent(regionCode, k -> new ArrayList<>()).add(station);
        }

        for (Map.Entry<String, List<Station>> entry : regionStationMap.entrySet()) {
            String regionCode = entry.getKey();
            List<Station> regionStations = entry.getValue();
            Map<String, Object> regionData = new HashMap<>();
            regionData.put("regionCode", regionCode);
            regionData.put("regionName", getRegionName(regionCode));
            regionData.put("stationCount", regionStations.size());
            regionData.put("allocatedPower", calculateAllocatedPower(regionStations));
            regionData.put("usedPower", calculateUsedPower(regionStations));
            regionAllocations.add(regionData);
        }

        result.put("taskNo", taskNo);
        result.put("allocations", regionAllocations);
        return result;
    }

    @Override
    public Map<String, Object> getPowerTrend(String taskNo, Integer hours) {
        Map<String, Object> result = new HashMap<>();
        DispatchTask task = getOne(new LambdaQueryWrapper<DispatchTask>()
                .eq(DispatchTask::getTaskNo, taskNo));
        if (task == null) {
            throw new RuntimeException("调度任务不存在");
        }

        List<String> timestamps = new ArrayList<>();
        List<Double> chargeTrend = new ArrayList<>();
        List<Double> dischargeTrend = new ArrayList<>();
        List<Double> gridPowerTrend = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < (hours != null ? hours : 24); i++) {
            String time = String.format("%02d:00", (cal.get(Calendar.HOUR_OF_DAY) - (hours - i)) % 24);
            timestamps.add(time);
            
            double baseCharge = task.getTotalChargePower().doubleValue() / hours;
            double baseDischarge = task.getTotalDischargePower().doubleValue() / hours;
            
            chargeTrend.add(baseCharge + Math.sin(i * Math.PI / 6) * baseCharge * 0.3);
            dischargeTrend.add(baseDischarge + Math.cos(i * Math.PI / 8) * baseDischarge * 0.4);
            gridPowerTrend.add(chargeTrend.get(i) - dischargeTrend.get(i));
        }

        result.put("taskNo", taskNo);
        result.put("timestamps", timestamps);
        result.put("chargeTrend", chargeTrend);
        result.put("dischargeTrend", dischargeTrend);
        result.put("gridPowerTrend", gridPowerTrend);
        return result;
    }

    public Map<String, Object> getStationPowerDetail(String taskNo, Long stationId) {
        Map<String, Object> result = new HashMap<>();
        DispatchTask task = getOne(new LambdaQueryWrapper<DispatchTask>()
                .eq(DispatchTask::getTaskNo, taskNo));
        if (task == null) {
            throw new RuntimeException("调度任务不存在");
        }

        Station station = stationService.getById(stationId);
        if (station == null) {
            throw new RuntimeException("站点不存在");
        }

        result.put("taskNo", taskNo);
        result.put("stationId", stationId);
        result.put("stationName", station.getName());
        result.put("allocatedChargePower", new BigDecimal("150.5"));
        result.put("allocatedDischargePower", new BigDecimal("80.0"));
        result.put("actualChargePower", new BigDecimal("145.3"));
        result.put("actualDischargePower", new BigDecimal("75.8"));
        result.put("loadRate", new BigDecimal("96.5"));
        result.put("status", "正常");
        return result;
    }

    private String getRegionName(String regionCode) {
        if (regionCode == null) return "未知区域";
        Map<String, String> regionNames = new HashMap<>();
        regionNames.put("440300", "深圳市");
        regionNames.put("440303", "罗湖区");
        regionNames.put("440304", "福田区");
        regionNames.put("440305", "南山区");
        regionNames.put("440306", "宝安区");
        regionNames.put("440307", "龙岗区");
        regionNames.put("440308", "盐田区");
        return regionNames.getOrDefault(regionCode, regionCode);
    }

    private BigDecimal calculateAllocatedPower(List<Station> stations) {
        return BigDecimal.valueOf(stations.size() * 200.0);
    }

    private BigDecimal calculateUsedPower(List<Station> stations) {
        return BigDecimal.valueOf(stations.size() * 180.0);
    }

    @Override
    public void adjustStationPower(String taskNo, Long stationId, BigDecimal newChargePower, BigDecimal newDischargePower) {
        DispatchTask task = getOne(new LambdaQueryWrapper<DispatchTask>()
                .eq(DispatchTask::getTaskNo, taskNo));
        if (task == null) {
            throw new RuntimeException("调度任务不存在");
        }

        Station station = stationService.getById(stationId);
        if (station == null) {
            throw new RuntimeException("站点不存在");
        }

        task.setActualChargePower(task.getActualChargePower()
                .subtract(new BigDecimal("150.5"))
                .add(newChargePower));
        task.setActualDischargePower(task.getActualDischargePower()
                .subtract(new BigDecimal("80.0"))
                .add(newDischargePower));
        updateById(task);
    }

    @Override
    public void issueDispatchCommand(String taskNo) {
        DispatchTask task = getOne(new LambdaQueryWrapper<DispatchTask>()
                .eq(DispatchTask::getTaskNo, taskNo));
        if (task == null) {
            throw new RuntimeException("调度任务不存在");
        }

        task.setStatus(1);
        updateById(task);
    }
}
