package com.charging.station.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.entity.OperatorQualification;
import com.charging.station.dto.AuditRequest;
import com.charging.station.mapper.OperatorQualificationMapper;
import com.charging.station.service.OperatorQualificationService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class OperatorQualificationServiceImpl extends ServiceImpl<OperatorQualificationMapper, OperatorQualification> implements OperatorQualificationService {

    @Override
    public IPage<OperatorQualification> pageByCondition(int page, int size, Integer auditStatus, Integer qualificationType) {
        Page<OperatorQualification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OperatorQualification> wrapper = new LambdaQueryWrapper<>();
        if (auditStatus != null) {
            wrapper.eq(OperatorQualification::getAuditStatus, auditStatus);
        }
        if (qualificationType != null) {
            wrapper.eq(OperatorQualification::getQualificationType, qualificationType);
        }
        wrapper.orderByDesc(OperatorQualification::getCreateTime);
        return page(pageParam, wrapper);
    }

    @Override
    public void audit(Long id, AuditRequest request, Long auditorId) {
        OperatorQualification q = getById(id);
        if (q != null) {
            q.setAuditStatus(request.getAuditResult());
            q.setAuditOpinion(request.getAuditOpinion());
            q.setAuditorId(auditorId);
            q.setAuditTime(LocalDateTime.now());
            updateById(q);
        }
    }
}
