package com.charging.station.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.charging.station.dto.AuditRequest;
import com.charging.station.dto.SubsidyApplicationDTO;
import com.charging.station.entity.SubsidyApplication;
import com.charging.station.dto.SubsidyAuditRequest;

public interface SubsidyService extends IService<SubsidyApplication> {
    IPage<SubsidyApplicationDTO> pageByCondition(int page, int size, Integer auditStatus, Integer currentAuditStage, Long operatorId);
    void audit(Long id, SubsidyAuditRequest request, Long auditorId, String auditorName);
}
