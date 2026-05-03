package com.charging.station.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.charging.station.entity.SubsidyAuditRecord;
import com.charging.station.mapper.SubsidyAuditRecordMapper;
import com.charging.station.service.SubsidyAuditRecordService;
import org.springframework.stereotype.Service;

@Service
public class SubsidyAuditRecordServiceImpl extends ServiceImpl<SubsidyAuditRecordMapper, SubsidyAuditRecord> implements SubsidyAuditRecordService {
}
