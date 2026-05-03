package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.dto.AccessApplicationDTO;
import com.charging.station.dto.AuditRequest;
import com.charging.station.entity.AccessApplication;
import com.charging.station.entity.Operator;
import com.charging.station.mapper.AccessApplicationMapper;
import com.charging.station.mapper.OperatorMapper;
import com.charging.station.service.AccessApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccessApplicationServiceImpl extends ServiceImpl<AccessApplicationMapper, AccessApplication> implements AccessApplicationService {

    @Autowired
    private OperatorMapper operatorMapper;

    @Override
    public IPage<AccessApplicationDTO> pageByCondition(int page, int size, Integer auditStatus, String keyword) {
        Page<AccessApplication> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AccessApplication> wrapper = new LambdaQueryWrapper<>();
        if (auditStatus != null) {
            wrapper.eq(AccessApplication::getAuditStatus, auditStatus);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(AccessApplication::getStationName, keyword)
                   .or()
                   .like(AccessApplication::getApplicationNo, keyword);
        }
        wrapper.orderByDesc(AccessApplication::getApplicationTime);
        IPage<AccessApplication> entityPage = page(pageParam, wrapper);

        List<Long> operatorIds = entityPage.getRecords().stream()
                .map(AccessApplication::getOperatorId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> operatorNameMap;
        if (!operatorIds.isEmpty()) {
            List<Operator> operators = operatorMapper.selectBatchIds(operatorIds);
            operatorNameMap = operators.stream()
                    .collect(Collectors.toMap(Operator::getId, Operator::getOperatorName, (k1, k2) -> k1));
        } else {
            operatorNameMap = java.util.Collections.emptyMap();
        }

        List<AccessApplicationDTO> dtoList = entityPage.getRecords().stream().map(entity -> {
            AccessApplicationDTO dto = new AccessApplicationDTO();
            dto.setId(entity.getId());
            dto.setApplicationNo(entity.getApplicationNo());
            dto.setOperatorId(entity.getOperatorId());
            dto.setOperatorName(operatorNameMap.getOrDefault(entity.getOperatorId(), "未知运营商"));
            dto.setStationId(entity.getStationId());
            dto.setStationName(entity.getStationName());
            dto.setStationAddress(entity.getStationAddress());
            dto.setDeviceSummary(entity.getDeviceSummary());
            dto.setTotalDevices(entity.getTotalDevices());
            dto.setDcFastCount(entity.getDcFastCount());
            dto.setAcSlowCount(entity.getAcSlowCount());
            dto.setV2gCount(entity.getV2gCount());
            dto.setContactPerson(entity.getContactPerson());
            dto.setContactPhone(entity.getContactPhone());
            dto.setApplicationTime(entity.getApplicationTime());
            dto.setSubmitTime(entity.getApplicationTime());
            dto.setAuditStatus(entity.getAuditStatus());
            dto.setStatus(String.valueOf(entity.getAuditStatus()));
            dto.setAuditOpinion(entity.getAuditOpinion());
            dto.setAuditorId(entity.getAuditorId());
            dto.setAuditTime(entity.getAuditTime());
            dto.setCreateTime(entity.getCreateTime());
            dto.setUpdateTime(entity.getUpdateTime());
            return dto;
        }).collect(Collectors.toList());

        Page<AccessApplicationDTO> dtoPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    public void audit(Long id, AuditRequest request, Long auditorId) {
        AccessApplication app = getById(id);
        if (app == null) {
            throw new RuntimeException("申请不存在");
        }
        if (app.getAuditStatus() != 0) {
            throw new RuntimeException("申请已审核");
        }
        app.setAuditStatus(request.getAuditResult());
        app.setAuditOpinion(request.getAuditOpinion());
        app.setAuditorId(auditorId);
        app.setAuditTime(LocalDateTime.now());
        updateById(app);
    }
}
