package com.charging.station.controller;

import com.charging.station.common.Result;
import com.charging.station.entity.*;
import com.charging.station.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

@RestController
@RequestMapping("/api/v1/supervision/compliance")
public class OperatorComplianceController {

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private OperatorQualificationService operatorQualificationService;

    @Autowired
    private OperatorRatingService operatorRatingService;

    @Autowired
    private OperatorViolationService operatorViolationService;

    @GetMapping("/operators")
    public Result<IPage<Operator>> getOperatorList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Integer qualificationStatus,
            @RequestParam(required = false) String keyword) {
        return Result.success(operatorService.pageByCondition(page, size, region, qualificationStatus, keyword));
    }

    @GetMapping("/operators/{id}")
    public Result<Operator> getOperatorDetail(@PathVariable Long id) {
        return Result.success(operatorService.getById(id));
    }

    @GetMapping("/qualifications")
    public Result<IPage<OperatorQualification>> getQualificationList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) Integer qualificationType) {
        return Result.success(operatorQualificationService.pageByCondition(page, size, auditStatus, qualificationType));
    }

    @PostMapping("/qualifications/{id}/audit")
    public Result<Void> auditQualification(
            @PathVariable Long id,
            @RequestBody com.charging.station.dto.AuditRequest request,
            @RequestHeader(value = "userId", required = false, defaultValue = "1") Long auditorId) {
        operatorQualificationService.audit(id, request, auditorId);
        return Result.success();
    }

    @GetMapping("/ratings")
    public Result<IPage<OperatorRating>> getRatingList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String grade) {
        return Result.success(operatorRatingService.pageByCondition(page, size, operatorId, period, grade));
    }

    @GetMapping("/violations")
    public Result<IPage<OperatorViolation>> getViolationList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(required = false) Integer violationType,
            @RequestParam(required = false) Integer rectificationStatus) {
        return Result.success(operatorViolationService.pageByCondition(page, size, operatorId, violationType, rectificationStatus));
    }

    @PostMapping("/violations")
    public Result<Void> addViolation(@RequestBody OperatorViolation violation) {
        operatorViolationService.save(violation);
        return Result.success();
    }
}
