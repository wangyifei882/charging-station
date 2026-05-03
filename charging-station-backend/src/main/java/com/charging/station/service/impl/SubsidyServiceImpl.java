package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.dto.SubsidyApplicationDTO;
import com.charging.station.entity.SubsidyApplication;
import com.charging.station.entity.SubsidyAuditRecord;
import com.charging.station.entity.Operator;
import com.charging.station.dto.SubsidyAuditRequest;
import com.charging.station.mapper.SubsidyApplicationMapper;
import com.charging.station.mapper.OperatorMapper;
import com.charging.station.service.SubsidyService;
import com.charging.station.service.SubsidyAuditRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubsidyServiceImpl extends ServiceImpl<SubsidyApplicationMapper, SubsidyApplication> implements SubsidyService {

    @Autowired
    private SubsidyAuditRecordService auditRecordService;

    @Autowired
    private OperatorMapper operatorMapper;

    @Override
    public IPage<SubsidyApplicationDTO> pageByCondition(int page, int size, Integer auditStatus, Integer currentAuditStage, Long operatorId) {
        Page<SubsidyApplication> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SubsidyApplication> wrapper = new LambdaQueryWrapper<>();
        if (auditStatus != null) {
            wrapper.eq(SubsidyApplication::getAuditStatus, auditStatus);
        }
        if (currentAuditStage != null) {
            wrapper.eq(SubsidyApplication::getCurrentAuditStage, currentAuditStage);
        }
        if (operatorId != null) {
            wrapper.eq(SubsidyApplication::getOperatorId, operatorId);
        }
        wrapper.orderByDesc(SubsidyApplication::getCreateTime);
        IPage<SubsidyApplication> entityPage = page(pageParam, wrapper);

        // 获取运营商ID列表
        List<Long> operatorIds = entityPage.getRecords().stream()
            .map(SubsidyApplication::getOperatorId)
            .filter(id -> id != null)
            .distinct()
            .collect(Collectors.toList());

        // 查询运营商名称
        Map<Long, String> operatorNameMap = new java.util.HashMap<>();
        if (!operatorIds.isEmpty()) {
            List<Operator> operators = operatorMapper.selectBatchIds(operatorIds);
            operatorNameMap = operators.stream()
                .collect(Collectors.toMap(
                    Operator::getId,
                    op -> op.getOperatorName() != null ? op.getOperatorName() : "未知运营商",
                    (k1, k2) -> k1));
        }

        // 转换为DTO
        final Map<Long, String> finalOperatorNameMap = operatorNameMap;
        List<SubsidyApplicationDTO> dtoList = entityPage.getRecords().stream()
            .map(app -> convertToDTO(app, finalOperatorNameMap))
            .collect(Collectors.toList());

        Page<SubsidyApplicationDTO> dtoPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    private SubsidyApplicationDTO convertToDTO(SubsidyApplication app, Map<Long, String> operatorNameMap) {
        SubsidyApplicationDTO dto = new SubsidyApplicationDTO();
        dto.setId(app.getId());
        dto.setApplicationNo(app.getApplicationNo());
        dto.setOperatorId(app.getOperatorId());
        dto.setOperatorName(operatorNameMap.getOrDefault(app.getOperatorId(), "未知运营商"));
        dto.setSubsidyType(app.getSubsidyType());
        dto.setTotalAmount(app.getTotalAmount());
        dto.setApplyAmount(app.getTotalAmount());
        dto.setCurrentAuditStage(app.getCurrentAuditStage());
        dto.setAuditStatus(app.getAuditStatus());
        dto.setStatus(app.getStatus());
        dto.setCreateTime(app.getCreateTime());
        dto.setUpdateTime(app.getUpdateTime());
        return dto;
    }

    @Override
    @Transactional
    public void audit(Long id, SubsidyAuditRequest request, Long auditorId, String auditorName) {
        SubsidyApplication app = getById(id);
        if (app == null) {
            throw new RuntimeException("申报不存在");
        }
        if (!app.getCurrentAuditStage().equals(request.getAuditStage())) {
            throw new RuntimeException("审核环节不匹配");
        }

        SubsidyAuditRecord record = new SubsidyAuditRecord();
        record.setApplicationId(id);
        record.setAuditStage(request.getAuditStage());
        record.setAuditResult(request.getAuditResult());
        record.setAuditOpinion(request.getAuditOpinion());
        record.setAuditorId(auditorId);
        record.setAuditorName(auditorName);
        record.setAuditTime(LocalDateTime.now());
        auditRecordService.save(record);

        if (request.getAuditResult() == 2) {
            app.setAuditStatus(app.getCurrentAuditStage() * 2);
        } else {
            if (request.getAuditStage() == 3) {
                app.setAuditStatus(5);
                app.setStatus(0);
            } else {
                app.setAuditStatus(request.getAuditStage() * 2 - 1);
                app.setCurrentAuditStage(request.getAuditStage() + 1);
            }
        }
        updateById(app);
    }
}
