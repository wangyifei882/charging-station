# 监管与调度平台 - 后端API文档

## 文档说明
- **技术栈**: Java 17 + Spring Boot 3.x + MyBatis Plus
- **接口规范**: RESTful API
- **数据格式**: JSON
- **认证方式**: JWT Token
- **基础路径**: `/api/v1/supervision`
- **响应格式**: 统一使用 `Result<T>` 包装

### 统一响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1698765432000
}
```

### 分页响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

---

## 1. 数据总览模块 `/dashboard`

### 1.1 获取数据总览核心指标
- **接口**: `GET /dashboard/metrics`
- **参数**: `?region_code=440305` (可选,监管区域编码)
- **响应**:
```json
{
  "code": 200,
  "data": {
    "facilityAccess": {
      "stationCount": 45,
      "deviceCount": 520,
      "v2gDeviceCount": 85,
      "v2gRatio": 16.35
    },
    "todayCharging": {
      "energy": 12500.50,
      "yesterdayRatio": 5.2
    },
    "todayV2gDischarge": {
      "energy": 850.30,
      "vehicleCount": 120
    },
    "demandResponse": {
      "peakShaveEnergy": 2500.00,
      "valleyFillEnergy": 1800.00
    },
    "operatorAccess": {
      "operatorCount": 8,
      "newThisMonth": 1
    },
    "deviceOnlineRate": {
      "rate": 96.50,
      "yesterdayRatio": 0.8
    },
    "faultAlarm": {
      "pendingCount": 15,
      "severeCount": 3
    },
    "subsidyGrant": {
      "yearTotal": 2500000.00
    },
    "dispatchExecution": {
      "executionRate": 92.50,
      "overloadStationCount": 2
    }
  }
}
```

### 1.2 获取充放电趋势数据
- **接口**: `GET /dashboard/charge-discharge-trend`
- **参数**:
  - `days`: 天数(7/30)
  - `region_code`: 区域编码(可选)
  - `aggregate`: 聚合维度(day/week/month)
- **响应**:
```json
{
  "code": 200,
  "data": {
    "timestamps": ["2025-01-08", "2025-01-09", "2025-01-10"],
    "chargeEnergy": [1200.50, 1350.20, 1280.00],
    "dischargeEnergy": [85.30, 92.10, 88.50]
  }
}
```

### 1.3 获取需求响应效果图数据
- **接口**: `GET /dashboard/demand-response-effect`
- **参数**:
  - `days`: 天数(7/30)
  - `region_code`: 区域编码(可选)
- **响应**:
```json
{
  "code": 200,
  "data": {
    "timestamps": ["2025-01-08", "2025-01-09"],
    "operators": [
      {"name": "南网电动", "color": "#FF6B6B", "data": [120.50, 135.20]},
      {"name": "特来电", "color": "#4ECDC4", "data": [95.30, 88.10]}
    ],
    "targetLine": [200.00, 200.00],
    "completionRates": [97.50, 102.30]
  }
}
```

### 1.4 获取实时告警列表
- **接口**: `GET /dashboard/alarms`
- **参数**:
  - `level`: 告警级别(可选,1-提示 2-警告 3-严重)
  - `limit`: 条数(默认20)
  - `region_code`: 区域编码(可选)
- **响应**: 实时告警列表,按紧急程度排序

### 1.5 获取未处理告警数量
- **接口**: `GET /dashboard/unread-alarm-count`
- **刷新频率**: 实时
- **响应**:
```json
{
  "code": 200,
  "data": {
    "totalCount": 15,
    "severeCount": 3,
    "warningCount": 8,
    "infoCount": 4
  }
}
```

---

## 2. 设施监管模块 `/facility`

### 2.1 设施台账列表
- **接口**: `GET /facility/ledger`
- **参数**:
  - `page`, `size`: 分页
  - `region_code`: 区域编码(支持级联)
  - `operator_id`: 运营商ID(可多选)
  - `access_status`: 接入状态(全部/已接入/断连/待审核)
  - `device_type`: 设施类型(全部/直流快充/交流慢充/V2G)
  - `keyword`: 关键词搜索(站点名称/地址/设备编号)
- **响应**:
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "stationName": "南山科技园充电站",
        "operatorName": "南网电动",
        "region": "深圳市南山区",
        "address": "科技路10号",
        "totalDevices": 20,
        "dcFastCount": 8,
        "acSlowCount": 6,
        "v2gCount": 6,
        "commissionDate": "2024-03-15",
        "accessStatus": 1,
        "lastReportTime": "2025-01-15 14:30:00"
      }
    ],
    "total": 45
  }
}
```

### 2.2 获取站点详情
- **接口**: `GET /facility/stations/{id}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "stationInfo": {
      "id": 1,
      "name": "南山科技园充电站",
      "address": "科技路10号",
      "operatorName": "南网电动",
      "commissionDate": "2024-03-15",
      "contactPerson": "李四",
      "contactPhone": "13800138001"
    },
    "deviceList": [
      {
        "deviceCode": "DC-001",
        "typeName": "直流快充桩",
        "powerRating": 120.00,
        "status": 1,
        "lastCommTime": "2025-01-15 14:30:00"
      }
    ],
    "energyTrend": {
      "timestamps": ["2024-12-16", "2024-12-17"],
      "energyData": [1200.50, 1350.20]
    },
    "onlineRateTrend": {
      "timestamps": ["2024-12-16", "2024-12-17"],
      "rateData": [96.50, 97.20]
    },
    "alarmList": [
      {
        "alarmNo": "ALM202501150001",
        "alarmLevel": 3,
        "alarmContent": "设备DC-001温度过高",
        "createTime": "2025-01-15 10:30:00"
      }
    ]
  }
}
```

### 2.3 导出设施台账
- **接口**: `GET /facility/ledger/export`
- **参数**: 同列表查询参数
- **响应**: Excel文件流

### 2.4 接入申请列表
- **接口**: `GET /facility/access-applications`
- **参数**:
  - `page`, `size`: 分页
  - `audit_status`: 审核状态(全部/待审核/已通过/已驳回)
  - `time_range`: 申请时间(7天/本月/自定义)
  - `keyword`: 关键词搜索
- **响应**: 接入申请分页列表

### 2.5 获取接入申请详情
- **接口**: `GET /facility/access-applications/{id}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "applicationNo": "APP202501150001",
    "operatorName": "南网电动",
    "stationName": "南山科技园充电站",
    "stationAddress": "科技路10号",
    "coordinateLat": 22.542,
    "coordinateLng": 114.057,
    "contactPerson": "李四",
    "contactPhone": "13800138001",
    "deviceList": [
      {"model": "TLD-DC-120", "power": 120.00, "type": "直流快充", "serialNo": "SN001"}
    ],
    "qualificationFiles": [
      {"name": "营业执照", "url": "https://xxx.com/license.pdf"},
      {"name": "电力接入许可", "url": "https://xxx.com/power.pdf"}
    ],
    "sitePhotos": [
      {"name": "站点环境", "url": "https://xxx.com/site.jpg"}
    ]
  }
}
```

### 2.6 审核接入申请
- **接口**: `POST /facility/access-applications/{id}/audit`
- **权限**: 监管人员
- **请求体**:
```json
{
  "auditResult": 1,
  "auditOpinion": "资质齐全,审核通过"
}
```
- `auditResult`: 1-通过 2-驳回(驳回时auditOpinion必填)

### 2.7 运行状态监控列表
- **接口**: `GET /facility/status-monitor`
- **参数**:
  - `page`, `size`: 分页
  - `region_code`: 区域筛选
  - `operator_id`: 运营商筛选
  - `status`: 状态(全部/正常/故障/离线)
  - `fault_type`: 故障类型筛选
- **响应**:
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "deviceCode": "DC-001",
        "stationName": "南山科技园充电站",
        "operatorName": "南网电动",
        "deviceType": "直流快充",
        "status": 2,
        "faultType": "通讯中断",
        "faultTime": "2025-01-15 10:00:00",
        "handlingStatus": 0
      }
    ],
    "total": 15
  }
}
```

### 2.8 获取设备详情(监控)
- **接口**: `GET /facility/devices/{deviceCode}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "deviceArchive": {
      "deviceCode": "DC-001",
      "model": "TLD-DC-120",
      "manufacturer": "特来电",
      "commissionDate": "2024-03-15",
      "warrantyExpiry": "2026-03-15"
    },
    "realTimeParams": {
      "voltage": 380.0,
      "current": 225.0,
      "power": 85.5
    },
    "runningCurve24h": {
      "timestamps": ["10:00", "11:00"],
      "voltage": [380.0, 379.5],
      "current": [225.0, 220.0],
      "power": [85.5, 83.2]
    },
    "faultHistory": [
      {"faultType": "通讯中断", "occurTime": "2025-01-15", "resolveTime": "2025-01-15"}
    ],
    "rectificationNotices": [
      {"noticeNo": "RN202501150001", "status": 1}
    ]
  }
}
```

### 2.9 下发整改通知
- **接口**: `POST /facility/devices/{deviceCode}/rectification-notice`
- **请求体**:
```json
{
  "noticeType": 1,
  "issueReason": "设备通讯中断超过2小时",
  "rectificationRequirement": "立即排查通讯模块",
  "deadline": "2025-01-20"
}
```
- **响应**: 生成PDF通知,返回下载URL

---

## 3. 车网互动与调度模块 `/v2g`

### 3.1 V2G活动列表
- **接口**: `GET /v2g/activities`
- **参数**:
  - `page`, `size`: 分页
  - `time_range`: 时间范围(7天/本月/自定义)
  - `region_code`: 区域筛选
  - `status`: 活动状态(全部/进行中/已结束/已取消)
- **响应**: V2G活动分页列表

### 3.2 获取V2G活动详情
- **接口**: `GET /v2g/activities/{id}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "activityInfo": {
      "activityNo": "V2G202501150001",
      "activityName": "南山区晚高峰V2G放电活动",
      "startTime": "2025-01-15 19:00:00",
      "endTime": "2025-01-15 22:00:00",
      "region": "深圳市南山区",
      "organizer": "南网电网"
    },
    "executionData": {
      "dischargePowerCurve": {
        "timestamps": ["19:00", "20:00"],
        "target": [500.00, 500.00],
        "actual": [485.50, 510.20]
      },
      "operatorRanking": [
        {"operatorName": "南网电动", "dischargeEnergy": 250.50, "rank": 1}
      ],
      "energyDistribution": {
        "byStation": [{"stationName": "南山科技园", "energy": 120.50}]
      }
    },
    "anomalies": [
      {"type": "放电功率异常波动", "time": "2025-01-15 20:30:00", "status": 1}
    ],
    "effectEvaluation": {
      "peakShaveContribution": 250.50,
      "userRevenueTotal": 1250.00
    }
  }
}
```

### 3.3 导出V2G活动报告
- **接口**: `GET /v2g/activities/{id}/export`
- **响应**: PDF报告文件流

### 3.4 有序充电策略列表
- **接口**: `GET /v2g/ordered-charging`
- **参数**:
  - `page`, `size`: 分页
  - `region_code`: 区域筛选
  - `operator_id`: 运营商筛选
  - `status`: 策略状态(全部/执行中/已暂停/已结束)
- **响应**: 有序充电策略分页列表

### 3.5 标记有序充电异常
- **接口**: `POST /v2g/ordered-charging/{id}/mark-anomaly`
- **请求体**:
```json
{
  "anomalyReason": "场站超容量充电"
}
```

### 3.6 获取异常场站列表
- **接口**: `GET /v2g/ordered-charging/anomaly-stations`
- **参数**: 同列表查询
- **响应**: 异常场站列表,支持下发整改通知

### 3.7 需求响应活动列表
- **接口**: `GET /v2g/demand-response`
- **参数**:
  - `page`, `size`: 分页
  - `response_type`: 响应类型(全部/削峰响应/填谷响应)
  - `time_range`: 时间范围
  - `status`: 响应状态(全部/进行中/已结束)
- **响应**: 需求响应活动分页列表

### 3.8 获取需求响应活动详情
- **接口**: `GET /v2g/demand-response/{id}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "activityInfo": {
      "activityNo": "DR202501150001",
      "activityName": "1月削峰需求响应",
      "startTime": "2025-01-15 14:00:00",
      "endTime": "2025-01-15 16:00:00",
      "responseType": 1,
      "targetEnergy": 5.00,
      "actualEnergy": 4.85,
      "completionRate": 97.00
    },
    "responseCurve": {
      "timestamps": ["14:00", "15:00"],
      "targetPower": [2500.00, 2500.00],
      "actualPower": [2400.00, 2500.00]
    },
    "operatorRanking": [
      {
        "operatorName": "南网电动",
        "responseEnergy": 1.50,
        "completionRate": 98.50,
        "rank": 1,
        "isQualified": true
      },
      {
        "operatorName": "星星充电",
        "responseEnergy": 0.80,
        "completionRate": 75.00,
        "rank": 3,
        "isQualified": false
      }
    ]
  }
}
```

### 3.9 导出需求响应报告
- **接口**: `GET /v2g/demand-response/{id}/export`
- **响应**: 含各运营商完成明细的PDF报告

---

## 4. 智能调度模块 `/dispatch`

### 4.1 获取调度总览指标
- **接口**: `GET /dispatch/overview`
- **参数**: `?task_no=xxx&region_code=xxx`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "totalChargePower": 2500.00,
    "totalDischargePower": 800.00,
    "actualChargePower": 2350.00,
    "actualDischargePower": 750.00,
    "executionRate": 94.00,
    "availableStationCount": 40,
    "overloadStationCount": 2,
    "gridConstraintStatus": 1,
    "dispatchMode": 1
  }
}
```

### 4.2 获取区域功率分配数据
- **接口**: `GET /dispatch/region-power-allocation`
- **参数**: `?task_no=xxx`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "regions": [
      {"regionName": "南山区", "chargePower": 1200.00, "dischargePower": 400.00},
      {"regionName": "福田区", "chargePower": 800.00, "dischargePower": 300.00}
    ]
  }
}
```

### 4.3 获取全网功率趋势数据
- **接口**: `GET /dispatch/power-trend`
- **参数**: `?task_no=xxx&hours=24`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "timestamps": ["14:00", "15:00"],
    "targetPower": [2500.00, 2500.00],
    "actualPower": [2450.00, 2500.00]
  }
}
```

### 4.4 获取场站负荷热力图数据
- **接口**: `GET /dispatch/station-heatmap`
- **参数**: `?task_no=xxx`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "stations": [
      {"stationName": "南山科技园", "lat": 22.542, "lng": 114.057, "powerLoad": 85.5, "color": "green"},
      {"stationName": "福田中心站", "lat": 22.520, "lng": 114.050, "powerLoad": 98.5, "color": "red"}
    ]
  }
}
```

### 4.5 站点功率分配明细列表
- **接口**: `GET /dispatch/station-allocations`
- **参数**:
  - `page`, `size`: 分页
  - `task_no`: 调度任务筛选
  - `region_code`: 区域筛选
  - `operator_id`: 运营商筛选
  - `dispatch_type`: 调度类型(充电/放电/双向)
  - `execution_status`: 执行状态(全部/已执行/待执行/异常)
  - `keyword`: 关键词搜索
- **响应**:
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "stationName": "南山科技园充电站",
        "operatorName": "南网电动",
        "region": "深圳市南山区粤海街道",
        "maxChargePower": 150.00,
        "maxDischargePower": 50.00,
        "totalPowerLimit": 200.00,
        "actualChargePower": 145.50,
        "actualDischargePower": 48.00,
        "executionDeviation": 3.20,
        "executionStatus": 1
      }
    ],
    "total": 40
  }
}
```

### 4.6 导出功率分配明细
- **接口**: `GET /dispatch/station-allocations/export`
- **参数**: 同列表查询
- **响应**: Excel文件流

### 4.7 获取调度任务详情
- **接口**: `GET /dispatch/tasks/{taskNo}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "taskInfo": {
      "taskNo": "DT202501150001",
      "dispatchMode": 1,
      "dispatchType": 3,
      "startTime": "2025-01-15 14:00:00",
      "endTime": "2025-01-15 18:00:00",
      "regionCode": "440305",
      "totalChargePower": 2500.00,
      "totalDischargePower": 800.00,
      "executionRate": 94.00
    },
    "stationInfo": {
      "stationName": "南山科技园充电站",
      "transformerCapacity": 800.00,
      "transformerLimit": 720.00
    },
    "currentDispatch": {
      "chargePowerLimit": 150.00,
      "dischargePowerLimit": 50.00,
      "effectiveStartTime": "2025-01-15 14:00:00",
      "effectiveEndTime": "2025-01-15 18:00:00"
    },
    "powerComparison": {
      "timestamps": ["14:00", "15:00"],
      "targetPower": [150.00, 150.00],
      "actualPower": [145.50, 148.00]
    },
    "dispatchHistory": [
      {"dispatchTime": "2025-01-14 14:00:00", "chargeLimit": 150.00, "result": "正常"}
    ],
    "anomalyInfo": {
      "hasAnomaly": false,
      "anomalyReason": ""
    }
  }
}
```

### 4.8 人工调整功率
- **接口**: `PUT /dispatch/station-allocations/{id}/adjust-power`
- **权限**: 监管人员
- **请求体**:
```json
{
  "maxChargePower": 140.00,
  "maxDischargePower": 45.00,
  "adjustReason": "台区负荷越限,降低功率上限"
}
```

### 4.9 下发调度指令
- **接口**: `POST /dispatch/tasks/{taskNo}/issue-command`
- **请求体**:
```json
{
  "targetStationIds": [1, 2, 3],
  "commandType": "adjust_power",
  "commandContent": "请将充电功率上限调整为140kW"
}
```

---

## 5. 车网互动效果统计 `/v2g/stats`

### 5.1 生成效果报告
- **接口**: `POST /v2g/stats/generate`
- **请求体**:
```json
{
  "statsType": 1,
  "period": "2025-01"
}
```
- `statsType`: 1-月度 2-季度 3-年度

### 5.2 获取效果统计数据
- **接口**: `GET /v2g/stats/{period}`
- **参数**:
  - `stats_type`: 统计类型
- **响应**:
```json
{
  "code": 200,
  "data": {
    "coreMetrics": {
      "totalPeakShaveEnergy": 15.50,
      "totalValleyFillEnergy": 12.30,
      "totalRenewableEnergy": 8.50,
      "totalUserRevenue": 125000.00
    },
    "v2gTrend": {
      "timestamps": ["2024-02", "2024-03"],
      "dischargeEnergy": [850.30, 920.50]
    },
    "demandResponseTrend": {
      "timestamps": ["2024-02", "2024-03"],
      "participationCount": [5, 8]
    },
    "operatorContribution": [
      {"operatorName": "南网电动", "responseEnergy": 5.50, "dischargeEnergy": 850.30, "rank": 1}
    ],
    "regionHeatmap": [
      {"regionName": "南山区", "v2gDischargeEnergy": 350.50},
      {"regionName": "福田区", "v2gDischargeEnergy": 280.30}
    ]
  }
}
```

### 5.3 导出效果报告
- **接口**: `GET /v2g/stats/{period}/export`
- **参数**: `?format=pdf` (pdf/word)
- **响应**: 报告文件流

---

## 6. 运营与合规管理模块 `/compliance`

### 6.1 运营商基础信息列表
- **接口**: `GET /compliance/operators`
- **参数**:
  - `page`, `size`: 分页
  - `region`: 注册区域
  - `qualification_status`: 资质状态
  - `keyword`: 关键词搜索
- **响应**: 运营商分页列表

### 6.2 获取运营商详情
- **接口**: `GET /compliance/operators/{id}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "basicInfo": {
      "id": 1,
      "operatorCode": "OPR001",
      "operatorName": "南网电动",
      "creditCode": "91440300MA5XXXXX1X",
      "legalPerson": "张三",
      "contactPerson": "李四",
      "contactPhone": "13800138001",
      "registrationRegion": "深圳市南山区",
      "address": "深圳市南山区科技园路1号",
      "businessScope": "充电桩运营、V2G服务",
      "accessStationCount": 15,
      "qualificationStatus": 1,
      "qualificationValidDate": "2026-12-31",
      "ratingGrade": "A",
      "ratingScore": 92.50,
      "violationCount": 0
    },
    "stationList": [
      {"stationName": "南山科技园充电站", "status": 1}
    ],
    "ratingTrend": {
      "periods": ["2024-10", "2024-11"],
      "scores": [90.00, 92.50]
    },
    "violationRecords": [],
    "subsidyRecords": [
      {"period": "2024-Q4", "amount": 50000.00, "status": 1}
    ]
  }
}
```

### 6.3 资质审核列表
- **接口**: `GET /compliance/qualifications`
- **参数**:
  - `page`, `size`: 分页
  - `audit_status`: 审核状态
  - `qualification_type`: 资质类型
- **响应**: 资质审核分页列表

### 6.4 审核资质
- **接口**: `POST /compliance/qualifications/{id}/audit`
- **请求体**:
```json
{
  "auditResult": 1,
  "auditOpinion": "资质有效,审核通过",
  "validPeriod": "2026-12-31"
}
```

### 6.5 服务评级列表
- **接口**: `GET /compliance/ratings`
- **参数**:
  - `page`, `size`: 分页
  - `operator_id`: 运营商筛选
  - `rating_period`: 评级周期
  - `grade`: 等级筛选(A/B/C/D)
- **响应**: 评级记录分页列表

### 6.6 生成服务评级
- **接口**: `POST /compliance/ratings/generate`
- **请求体**:
```json
{
  "operatorId": 1,
  "ratingPeriod": "2025-01",
  "ratingType": 1
}
```
- 自动计算评分, D级自动生成约谈通知

### 6.7 违规记录列表
- **接口**: `GET /compliance/violations`
- **参数**:
  - `page`, `size`: 分页
  - `operator_id`: 运营商筛选
  - `violation_type`: 违规类型
  - `rectification_status`: 整改状态
- **响应**: 违规记录分页列表

### 6.8 新增违规记录
- **接口**: `POST /compliance/violations`
- **请求体**:
```json
{
  "operatorId": 1,
  "violationType": 1,
  "violationContent": "场站超容量充电",
  "penaltyType": 1,
  "rectificationDeadline": "2025-01-25"
}
```

### 6.9 下发整改通知(违规)
- **接口**: `POST /compliance/violations/{id}/rectification`
- **请求体**:
```json
{
  "rectificationRequirement": "立即调整充电策略,确保不超过变压器容量"
}
```

---

## 7. 补贴管理模块 `/subsidy`

### 7.1 补贴申报列表
- **接口**: `GET /subsidy/applications`
- **参数**:
  - `page`, `size`: 分页
  - `operator_id`: 运营商筛选
  - `audit_status`: 审核状态
  - `current_audit_stage`: 当前审核环节
  - `subsidy_type`: 补贴类型
- **响应**: 补贴申报分页列表

### 7.2 获取补贴申报详情
- **接口**: `GET /subsidy/applications/{id}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "applicationNo": "SUB202501150001",
    "operatorName": "南网电动",
    "subsidyType": 1,
    "subsidyPeriod": "2025-Q1",
    "totalAmount": 500000.00,
    "supportingData": {"stationCount": 15, "deviceCount": 200, "totalEnergy": 125000},
    "currentAuditStage": 1,
    "auditStatus": 0,
    "auditHistory": []
  }
}
```

### 7.3 审核补贴申报
- **接口**: `POST /subsidy/applications/{id}/audit`
- **请求体**:
```json
{
  "auditStage": 1,
  "auditResult": 1,
  "auditOpinion": "数据核实无误,初审通过"
}
```
- 自动流转下一审核环节

### 7.4 补贴资金追溯列表
- **接口**: `GET /subsidy/records`
- **参数**:
  - `page`, `size`: 分页
  - `operator_id`: 运营商筛选
  - `grant_time`: 发放时间
  - `grant_batch_no`: 批次号
- **响应**: 补贴发放记录分页列表

### 7.5 获取补贴发放详情
- **接口**: `GET /subsidy/records/{id}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "applicationNo": "SUB202501150001",
    "operatorName": "南网电动",
    "amount": 500000.00,
    "grantTime": "2025-01-20 10:00:00",
    "grantBatchNo": "GB20250120001",
    "auditHistory": [
      {"stage": "初审", "result": "通过", "auditor": "张主任", "time": "2025-01-16"},
      {"stage": "复核", "result": "通过", "auditor": "李处长", "time": "2025-01-18"},
      {"stage": "终审", "result": "通过", "auditor": "王局长", "time": "2025-01-19"}
    ]
  }
}
```

### 7.6 异常申报监控
- **接口**: `GET /subsidy/abnormal-applications`
- **响应**: 重复申报、数据异常等监控列表

---

## 8. 政策管理模块 `/policy`

### 8.1 政策列表
- **接口**: `GET /policy/list`
- **参数**:
  - `page`, `size`: 分页
  - `status`: 状态(全部/草稿/已发布/已下架)
  - `keyword`: 关键词搜索
- **响应**: 政策分页列表

### 8.2 获取政策详情
- **接口**: `GET /policy/{id}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "policyNo": "POL202501150001",
    "title": "关于2025年充电设施建设补贴的通知",
    "content": "为鼓励充电设施建设...",
    "attachments": [
      {"name": "政策附件.pdf", "url": "https://xxx.com/policy.pdf"}
    ],
    "publishTime": "2025-01-15 10:00:00",
    "publisherName": "张主任",
    "status": 1
  }
}
```

### 8.3 发布政策
- **接口**: `POST /policy/publish`
- **请求体**:
```json
{
  "title": "关于2025年充电设施建设补贴的通知",
  "content": "为鼓励充电设施建设...",
  "attachmentUrls": ["https://xxx.com/policy.pdf"],
  "targetOperatorIds": [1, 2, 3]
}
```

### 8.4 下架政策
- **接口**: `PUT /policy/{id}/offline`

### 8.5 获取推送记录
- **接口**: `GET /policy/{id}/push-records`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "policyTitle": "关于2025年充电设施建设补贴的通知",
    "pushRecords": [
      {"operatorName": "南网电动", "pushTime": "2025-01-15 10:05:00", "readStatus": 1, "readTime": "2025-01-15 10:30:00"},
      {"operatorName": "特来电", "pushTime": "2025-01-15 10:05:00", "readStatus": 0}
    ],
    "readRate": 66.67
  }
}
```

---

## 9. 安全与应急管理模块 `/emergency`

### 9.1 安全告警列表
- **接口**: `GET /emergency/alarms`
- **参数**:
  - `page`, `size`: 分页
  - `alarm_level`: 告警级别(严重/警告/提示)
  - `status`: 处理状态(待处理/处理中/已闭环)
  - `region_code`: 区域筛选
  - `operator_id`: 运营商筛选
  - `start_time`, `end_time`: 时间范围
- **响应**: 告警列表(实时刷新)

### 9.2 获取告警详情
- **接口**: `GET /emergency/alarms/{id}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "alarmInfo": {
      "alarmNo": "ALM202501150001",
      "alarmLevel": 3,
      "alarmType": 4,
      "alarmContent": "设备DC-001温度过高,当前温度85℃",
      "occurTime": "2025-01-15 10:30:00",
      "deviceParams": {
        "voltage": 380.0,
        "current": 225.0,
        "power": 85.5,
        "temperature": 85.0
      }
    },
    "locationInfo": {
      "stationName": "南山科技园充电站",
      "stationAddress": "科技路10号",
      "deviceCode": "DC-001",
      "installPosition": "A区-01号"
    },
    "handlingRecords": [
      {
        "handlingType": 1,
        "content": "已向运营商下发告警通知",
        "notifyMethod": "短信+站内信",
        "notifyTime": "2025-01-15 10:32:00",
        "handlerName": "系统自动"
      }
    ]
  }
}
```

### 9.3 处理告警
- **接口**: `POST /emergency/alarms/{id}/handle`
- **请求体**:
```json
{
  "handlingType": 2,
  "newStatus": 1,
  "handlingContent": "已通知运营商,等待处理"
}
```

### 9.4 向运营商下发告警通知
- **接口**: `POST /emergency/alarms/{id}/notify-operator`
- **响应**: 自动生成短信+站内信通知

### 9.5 录入运营商处理结果
- **接口**: `POST /emergency/alarms/{id}/record-operator-result`
- **请求体**:
```json
{
  "handlingMeasures": "已更换温控模块",
  "handlingTime": "2025-01-15 14:00:00",
  "handlerName": "李工程师"
}
```

### 9.6 闭环确认
- **接口**: `POST /emergency/alarms/{id}/close`
- **请求体**:
```json
{
  "closeOpinion": "已确认整改完成,告警闭环"
}
```

### 9.7 应急调度指令列表
- **接口**: `GET /emergency/commands`
- **参数**:
  - `page`, `size`: 分页
  - `status`: 指令状态(待确认/执行中/已完成/已取消)
  - `command_type`: 指令类型
  - `start_time`, `end_time`: 时间范围
- **响应**: 指令列表

### 9.8 创建应急调度指令
- **接口**: `POST /emergency/commands`
- **权限**: 监管人员
- **请求体**:
```json
{
  "commandType": 1,
  "targetOperatorId": 1,
  "targetStationId": null,
  "commandContent": "因电网紧急削峰需求,请立即将所有场站充电功率降低30%",
  "startTime": "2025-01-15 16:00:00",
  "endTime": "2025-01-15 18:00:00"
}
```

### 9.9 获取应急指令详情
- **接口**: `GET /emergency/commands/{id}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "commandNo": "CMD202501150001",
    "commandType": 1,
    "targetOperatorName": "南网电动",
    "commandContent": "因电网紧急削峰需求...",
    "startTime": "2025-01-15 16:00:00",
    "endTime": "2025-01-15 18:00:00",
    "issuerName": "张主任",
    "issueTime": "2025-01-15 15:55:00",
    "status": 1,
    "executionResult": "已执行,平均功率降低28%"
  }
}
```

### 9.10 取消应急指令
- **接口**: `PUT /emergency/commands/{id}/cancel`
- **请求体**:
```json
{
  "cancelReason": "电网负荷已恢复正常"
}
```

### 9.11 事故追溯列表
- **接口**: `GET /emergency/accidents`
- **参数**:
  - `page`, `size`: 分页
  - `accident_type`: 事故类型
  - `start_time`, `end_time`: 时间范围
- **响应**: 事故追溯列表,包含完整处理链条

---

## 10. 系统管理模块 `/system`

### 10.1 获取区域树
- **接口**: `GET /system/regions`
- **响应**:
```json
{
  "code": 200,
  "data": [
    {
      "regionCode": "440300",
      "regionName": "深圳市",
      "regionLevel": 1,
      "children": [
        {
          "regionCode": "440305",
          "regionName": "南山区",
          "regionLevel": 2,
          "children": [
            {"regionCode": "44030501", "regionName": "南头街道", "regionLevel": 3}
          ]
        }
      ]
    }
  ]
}
```

### 10.2 获取充电量分析数据
- **接口**: `GET /system/energy-analysis`
- **参数**:
  - `time_range`: 时间范围
  - `region_code`: 区域筛选
  - `dimension`: 聚合维度(day/week/month)
- **响应**: 充电量趋势数据

### 10.3 告警规则配置列表
- **接口**: `GET /system/alarm-rules`
- **响应**: 告警规则列表(复用原有模块)

### 10.4 获取告警升级规则
- **接口**: `GET /system/alarm-escalation-rules`
- **响应**:
```json
{
  "code": 200,
  "data": [
    {"alarmLevel": 3, "timeoutMinutes": 10, "escalationTargetRole": "CITY_SUPERVISOR", "enabled": true},
    {"alarmLevel": 2, "timeoutMinutes": 30, "escalationTargetRole": "DISTRICT_SUPERVISOR", "enabled": true}
  ]
}
```

### 10.5 更新告警升级规则
- **接口**: `PUT /system/alarm-escalation-rules/{id}`
- **请求体**:
```json
{
  "timeoutMinutes": 15,
  "escalationTargetRole": "CITY_SUPERVISOR",
  "notifyMethod": "1,2,3"
}
```

### 10.6 操作日志列表
- **接口**: `GET /system/operation-logs`
- **参数**:
  - `page`, `size`: 分页
  - `username`: 操作用户
  - `module`: 操作模块
  - `start_time`, `end_time`: 时间范围
- **响应**: 分页操作日志(复用原有模块)

### 10.7 导出操作日志
- **接口**: `GET /system/operation-logs/export`

### 10.8 修改密码
- **接口**: `PUT /system/password`
- **请求体**:
```json
{
  "oldPassword": "old_encrypted_password",
  "newPassword": "new_encrypted_password"
}
```

### 10.9 获取当前用户信息
- **接口**: `GET /system/user-info`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "userId": 2,
    "username": "supervise_admin",
    "realName": "监管管理员",
    "roleName": "超级监管员",
    "phone": "13800138010",
    "email": "supervise@charging.com",
    "managedRegion": "深圳市"
  }
}
```

---

## 附录A：数据字典

### A.1 区域层级枚举
| 值 | 说明 |
|---|---|
| 1 | 市级 |
| 2 | 区级 |
| 3 | 街道 |

### A.2 接入状态枚举
| 值 | 说明 |
|---|---|
| 0 | 待审核 |
| 1 | 已接入 |
| 2 | 断连 |
| 3 | 已驳回 |

### A.3 告警级别枚举
| 值 | 说明 | 通知方式 |
|---|---|---|
| 1 | 提示 | 站内信 |
| 2 | 警告 | 站内信+声音 |
| 3 | 严重 | 弹窗+声音+短信 |

### A.4 V2G活动状态枚举
| 值 | 说明 |
|---|---|
| 0 | 未开始 |
| 1 | 进行中 |
| 2 | 已结束 |
| 3 | 已取消 |

### A.5 调度模式枚举
| 值 | 说明 |
|---|---|
| 1 | 自动调度 |
| 2 | 人工干预 |

### A.6 调度执行状态枚举
| 值 | 说明 |
|---|---|
| 1 | 正常 |
| 2 | 超功率 |
| 3 | 未执行 |
| 4 | 离线 |

### A.7 补贴审核状态枚举
| 值 | 说明 |
|---|---|
| 0 | 待审核 |
| 1 | 初审通过 |
| 2 | 初审驳回 |
| 3 | 复核通过 |
| 4 | 复核驳回 |
| 5 | 终审通过 |
| 6 | 终审驳回 |

### A.8 应急指令类型枚举
| 值 | 说明 |
|---|---|
| 1 | 紧急削峰 |
| 2 | 限充 |
| 3 | 其他 |

### A.9 运营商评级等级枚举
| 值 | 分数范围 | 说明 |
|---|---|---|
| A | 90-100 | 优秀 |
| B | 75-89 | 良好 |
| C | 60-74 | 合格 |
| D | 0-59 | 不合格(自动约谈) |

---

## 附录B：错误码说明

| 错误码 | 说明 |
|---|---|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或Token失效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 5001 | 区域数据不存在 |
| 5002 | 运营商资质已过期 |
| 5003 | 接入申请已审核 |
| 5004 | 调度任务不存在 |
| 5005 | 功率调整超限 |
| 5006 | 应急指令已下发 |
| 5007 | 补贴申报已审核 |
| 5008 | 告警已闭环 |

---

## 附录C：WebSocket推送事件

### C.1 实时告警推送
- **事件**: `alarm:new`
- **数据格式**:
```json
{
  "type": "alarm:new",
  "data": {
    "alarmNo": "ALM202501150001",
    "alarmLevel": 3,
    "alarmContent": "设备DC-001温度过高",
    "stationName": "南山科技园充电站",
    "operatorName": "南网电动",
    "occurTime": "2025-01-15 10:30:00"
  }
}
```

### C.2 调度指令更新推送
- **事件**: `dispatch:update`
- **数据格式**:
```json
{
  "type": "dispatch:update",
  "data": {
    "taskNo": "DT202501150001",
    "executionRate": 94.00,
    "overloadStationCount": 2
  }
}
```

### C.3 应急指令推送
- **事件**: `emergency:command`
- **数据格式**:
```json
{
  "type": "emergency:command",
  "data": {
    "commandNo": "CMD202501150001",
    "commandType": 1,
    "commandContent": "立即将所有场站充电功率降低30%"
  }
}
```

---

> **最后更新**: 2026-04-18
> **文档版本**: v1.0
> **文档维护**: 项目开发团队
