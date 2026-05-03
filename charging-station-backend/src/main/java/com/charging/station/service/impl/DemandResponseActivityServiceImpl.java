package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.dto.DemandResponseActivityDTO;
import com.charging.station.dto.OperatorParticipationRankDTO;
import com.charging.station.entity.DemandResponseActivity;
import com.charging.station.entity.DemandResponseOperatorRecord;
import com.charging.station.entity.Operator;
import com.charging.station.mapper.DemandResponseActivityMapper;
import com.charging.station.mapper.DemandResponseOperatorRecordMapper;
import com.charging.station.mapper.OperatorMapper;
import com.charging.station.service.DemandResponseActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DemandResponseActivityServiceImpl extends ServiceImpl<DemandResponseActivityMapper, DemandResponseActivity> implements DemandResponseActivityService {

    @Autowired
    private DemandResponseOperatorRecordMapper operatorRecordMapper;

    @Autowired
    private OperatorMapper operatorMapper;

    @Override
    public IPage<DemandResponseActivityDTO> pageByCondition(int page, int size, Integer responseType, Integer status) {
        Page<DemandResponseActivity> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<DemandResponseActivity> wrapper = new LambdaQueryWrapper<>();
        if (responseType != null) {
            wrapper.eq(DemandResponseActivity::getResponseType, responseType);
        }
        if (status != null) {
            wrapper.eq(DemandResponseActivity::getStatus, status);
        }
        wrapper.orderByDesc(DemandResponseActivity::getStartTime);
        IPage<DemandResponseActivity> entityPage = page(pageParam, wrapper);

        List<DemandResponseActivityDTO> dtoList = entityPage.getRecords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        Page<DemandResponseActivityDTO> dtoPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    private DemandResponseActivityDTO convertToDTO(DemandResponseActivity entity) {
        DemandResponseActivityDTO dto = new DemandResponseActivityDTO();
        dto.setId(entity.getId());
        dto.setActivityNo(entity.getActivityNo());
        dto.setActivityName(entity.getActivityName());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setResponseType(entity.getResponseType());
        dto.setTargetEnergy(entity.getTargetEnergy());
        dto.setTargetPower(entity.getTargetEnergy());
        dto.setActualEnergy(entity.getActualEnergy());
        dto.setActualPower(entity.getActualEnergy());
        dto.setCompletionRate(entity.getCompletionRate());
        dto.setResponseRate(entity.getCompletionRate());
        dto.setParticipatingOperatorCount(entity.getParticipatingOperatorCount());
        dto.setParticipantCount(entity.getParticipatingOperatorCount());
        dto.setStatus(entity.getStatus());
        dto.setRemark(entity.getRemark());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }

    @Override
    public Map<String, Object> getActivityDetail(Long id) {
        Map<String, Object> result = new HashMap<>();
        DemandResponseActivity activity = getById(id);
        result.put("activityInfo", convertToDTO(activity));
        return result;
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 本月响应次数
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long monthlyCount = lambdaQuery()
            .ge(DemandResponseActivity::getStartTime, monthStart)
            .le(DemandResponseActivity::getStartTime, now)
            .count();
        stats.put("monthlyResponseCount", monthlyCount);

        // 获取本月活动列表
        List<DemandResponseActivity> monthActivities = lambdaQuery()
            .ge(DemandResponseActivity::getStartTime, monthStart)
            .le(DemandResponseActivity::getStartTime, now)
            .list();

        // 本月响应总电量
        BigDecimal totalEnergy = monthActivities.stream()
            .map(DemandResponseActivity::getActualEnergy)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalResponseEnergy", totalEnergy.doubleValue());

        // 本月平均响应率
        BigDecimal avgRate = monthActivities.stream()
            .map(DemandResponseActivity::getCompletionRate)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        double avgResponseRate = monthActivities.isEmpty() ? 0 : avgRate.doubleValue() / monthActivities.size();
        stats.put("avgResponseRate", avgResponseRate);

        return stats;
    }

    @Override
    public List<OperatorParticipationRankDTO> getOperatorRanking() {
        // 获取所有运营商记录
        List<DemandResponseOperatorRecord> records = operatorRecordMapper.selectList(null);
        
        // 如果没有记录，返回空列表
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 按运营商分组统计
        Map<Long, List<DemandResponseOperatorRecord>> groupedByOperator = records.stream()
            .filter(r -> r.getOperatorId() != null)
            .collect(Collectors.groupingBy(DemandResponseOperatorRecord::getOperatorId));
        
        // 如果没有分组数据，返回空列表
        if (groupedByOperator.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取运营商名称
        List<Long> operatorIds = new ArrayList<>(groupedByOperator.keySet());
        Map<Long, String> operatorNameMap = new HashMap<>();
        if (!operatorIds.isEmpty()) {
            try {
                List<Operator> operators = operatorMapper.selectBatchIds(operatorIds);
                if (operators != null && !operators.isEmpty()) {
                    Map<Long, String> tempMap = operators.stream()
                        .filter(o -> o.getId() != null)
                        .collect(Collectors.toMap(Operator::getId, op -> 
                            op.getOperatorName() != null ? op.getOperatorName() : "未知运营商", 
                            (k1, k2) -> k1));
                    operatorNameMap.putAll(tempMap);
                }
            } catch (Exception e) {
                // 如果查询失败，使用默认名称
            }
            // 为没有名称的运营商设置默认名称
            for (Long id : operatorIds) {
                operatorNameMap.putIfAbsent(id, "运营商" + id);
            }
        }

        // 构建排名数据
        List<OperatorParticipationRankDTO> rankingList = new ArrayList<>();
        for (Map.Entry<Long, List<DemandResponseOperatorRecord>> entry : groupedByOperator.entrySet()) {
            Long operatorId = entry.getKey();
            List<DemandResponseOperatorRecord> operatorRecords = entry.getValue();

            OperatorParticipationRankDTO dto = new OperatorParticipationRankDTO();
            dto.setOperatorId(operatorId);
            dto.setOperatorName(operatorNameMap.getOrDefault(operatorId, "运营商" + operatorId));
            dto.setResponseCount(operatorRecords.size());
            
            BigDecimal totalEnergy = operatorRecords.stream()
                .map(DemandResponseOperatorRecord::getActualEnergy)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setTotalEnergy(totalEnergy);
            dto.setTotalPower(totalEnergy);

            BigDecimal avgRate = operatorRecords.stream()
                .map(DemandResponseOperatorRecord::getCompletionRate)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avgResponseRate = operatorRecords.isEmpty() ? BigDecimal.ZERO : 
                avgRate.divide(new BigDecimal(operatorRecords.size()), 2, RoundingMode.HALF_UP);
            dto.setResponseRate(avgResponseRate);
            dto.setAvgResponseRate(avgResponseRate);

            // 奖励金额 = 响应电量 * 0.5元/kWh
            BigDecimal reward = totalEnergy.multiply(new BigDecimal("0.5"));
            dto.setRewardAmount(reward);

            rankingList.add(dto);
        }

        // 按响应电量排序（降序）
        rankingList.sort((a, b) -> b.getTotalEnergy().compareTo(a.getTotalEnergy()));
        
        // 重新设置排名
        for (int i = 0; i < rankingList.size(); i++) {
            rankingList.get(i).setRank(i + 1);
        }

        return rankingList;
    }
}
