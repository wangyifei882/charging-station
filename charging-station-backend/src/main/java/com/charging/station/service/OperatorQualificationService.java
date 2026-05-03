package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.entity.OperatorQualification;
import com.charging.station.dto.AuditRequest;

public interface OperatorQualificationService extends IService<OperatorQualification> {
    IPage<OperatorQualification> pageByCondition(int page, int size, Integer auditStatus, Integer qualificationType);
    void audit(Long id, AuditRequest request, Long auditorId);
}
