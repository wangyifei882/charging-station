# 电动汽车充电桩运营管理平台 - 后端API文档

## 文档说明
- **技术栈**: Java 17 + Spring Boot 3.x + MyBatis Plus
- **接口规范**: RESTful API
- **数据格式**: JSON
- **认证方式**: JWT Token
- **基础路径**: `/api/v1`
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

## 1. 认证模块 `/auth`

### 1.1 用户登录
- **接口**: `POST /auth/login`
- **说明**: 用户登录获取Token
- **请求体**:
```json
{
  "username": "admin",
  "password": "encrypted_password"
}
```
- **响应**:
```json
{
  "code": 200,
  "data": {
    "token": "jwt_token_string",
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "realName": "张三",
      "roleName": "运维岗",
      "avatar": "url"
    }
  }
}
```

### 1.2 用户登出
- **接口**: `POST /auth/logout`
- **说明**: 退出登录
- **请求头**: `Authorization: Bearer {token}`

### 1.3 获取菜单权限
- **接口**: `GET /auth/menus`
- **说明**: 获取当前用户菜单权限
- **响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "parentId": 0,
      "menuName": "场站总览",
      "menuIcon": "dashboard",
      "menuPath": "/dashboard"
    }
  ]
}
```

---

## 2. 场站总览模块 `/dashboard`

### 2.1 获取场站总览数据
- **接口**: `GET /dashboard/overview`
- **说明**: 获取场站核心数据看板
- **响应**:
```json
{
  "code": 200,
  "data": {
    "deviceStats": {
      "total": 50,
      "online": 45,
      "offline": 3,
      "fault": 2
    },
    "realtimeData": {
      "idleCount": 20,
      "chargingCount": 25,
      "todayEnergy": 1250.50,
      "todayRevenue": 3680.00,
      "currentLoad": 450.20
    },
    "peakHours": {
      "peakPeriod": "10:00-12:00",
      "currentPriceType": "峰时段",
      "recommendedDevices": ["AC-001", "DC-003"]
    },
    "recentAlarms": [
      {
        "id": 1,
        "alarmNo": "ALM202404170001",
        "alarmLevel": 3,
        "alarmContent": "设备DC-001温度过高",
        "createTime": "2024-04-17 10:30:00"
      }
    ]
  }
}
```

### 2.2 获取24小时负荷趋势
- **接口**: `GET /dashboard/load-trend`
- **参数**: `?hours=24`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "timestamps": ["2024-04-16 11:00", "2024-04-16 12:00"],
    "loads": [350.5, 420.3, 480.1]
  }
}
```

---

## 3. 设备管理模块 `/devices`

### 3.1 设备列表查询
- **接口**: `GET /devices`
- **参数**:
  - `page`: 页码（默认1）
  - `size`: 每页条数（默认10）
  - `deviceCode`: 设备编号（可选）
  - `typeId`: 设备类型（可选）
  - `status`: 设备状态（可选）
  - `areaCode`: 区域编码（可选）
- **响应**: 分页设备列表

### 3.2 获取设备详情
- **接口**: `GET /devices/{id}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "deviceCode": "DC-001",
    "deviceName": "1号直流快充桩",
    "typeName": "直流充电桩",
    "areaCode": "A区",
    "position": "A-01",
    "manufacturer": "特来电",
    "model": "TLD-DC-120",
    "powerRating": 120.00,
    "commissionDate": "2023-01-15",
    "nextMaintenanceDate": "2024-07-15",
    "status": 1,
    "realTimePower": 85.5,
    "realTimeVoltage": 380.0,
    "realTimeCurrent": 225.0,
    "totalEnergy": 15680.50
  }
}
```

### 3.3 创建设备
- **接口**: `POST /devices`
- **权限**: 运维岗、超级管理员
- **请求体**:
```json
{
  "deviceCode": "DC-051",
  "deviceName": "51号直流快充桩",
  "typeId": 2,
  "areaCode": "B区",
  "position": "B-01",
  "manufacturer": "特来电",
  "model": "TLD-DC-120",
  "powerRating": 120.00,
  "commissionDate": "2024-04-17"
}
```

### 3.4 更新设备信息
- **接口**: `PUT /devices/{id}`
- **权限**: 运维岗、超级管理员
- **请求体**: 设备信息（同创建）

### 3.5 删除设备
- **接口**: `DELETE /devices/{id}`
- **权限**: 超级管理员

### 3.6 获取设备实时参数
- **接口**: `GET /devices/{id}/realtime`
- **刷新频率**: 10秒
- **响应**:
```json
{
  "code": 200,
  "data": {
    "power": 85.5,
    "voltage": 380.0,
    "current": 225.0,
    "temperature": 42.5,
    "status": 1,
    "currentOrder": {
      "orderNo": "ORD202404170001",
      "userId": 1001,
      "startTime": "2024-04-17 10:00:00"
    }
  }
}
```

### 3.7 获取设备24小时运行曲线
- **接口**: `GET /devices/{id}/history`
- **参数**: `?hours=24`
- **响应**: 时间序列数据

### 3.8 远程控制设备
- **接口**: `POST /devices/{id}/control`
- **权限**: 运维岗、超级管理员
- **请求体**:
```json
{
  "action": "start",
  "params": {
    "power": 100,
    "duration": 60
  },
  "confirm": true
}
```
- **action枚举**: `start`(启动充电), `stop`(停止充电), `adjust_power`(调节功率), `v2g_discharge`(V2G放电), `energy_storage_start`(储能启动), `energy_storage_stop`(储能停止)

### 3.9 设备维保记录查询
- **接口**: `GET /devices/{id}/maintenances`
- **参数**: `?page=1&size=10`

### 3.10 新增维保记录
- **接口**: `POST /devices/{id}/maintenances`
- **请求体**:
```json
{
  "maintenanceType": 1,
  "maintenanceDate": "2024-04-17",
  "maintenanceContent": "定期检查、清洁、测试",
  "maintenanceResult": "正常",
  "nextMaintenanceDate": "2024-10-17",
  "cost": 500.00,
  "remark": "例行维护"
}
```

### 3.11 设备OTA升级
- **接口**: `POST /devices/{id}/ota-upgrade`
- **请求体**:
```json
{
  "newVersion": "v2.1.0",
  "upgradeType": 1
}
```

### 3.12 OTA升级记录查询
- **接口**: `GET /devices/{id}/ota-records`

### 3.13 场站负荷状态
- **接口**: `GET /devices/load-status`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "totalLoad": 450.20,
    "ratedCapacity": 800.00,
    "loadRate": 56.28,
    "warnThreshold": 90.00,
    "isWarning": false
  }
}
```

### 3.14 导出设备列表
- **接口**: `GET /devices/export`
- **响应**: Excel文件流

---

## 4. 充电运营模块 `/charging`

### 4.1 充电订单列表
- **接口**: `GET /charging/orders`
- **参数**:
  - `page`, `size`
  - `orderNo`: 订单号
  - `userId`: 用户ID
  - `deviceId`: 设备ID
  - `status`: 订单状态
  - `paymentStatus`: 支付状态
  - `startTime`, `endTime`: 时间范围
- **响应**: 分页订单列表

### 4.2 获取订单详情
- **接口**: `GET /charging/orders/{id}`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "orderNo": "ORD202404170001",
    "userCode": "CU100001",
    "userName": "李四",
    "plateNumber": "京A12345",
    "deviceCode": "DC-001",
    "deviceName": "1号直流快充桩",
    "orderType": 1,
    "startTime": "2024-04-17 10:00:00",
    "endTime": "2024-04-17 11:30:00",
    "totalEnergy": 45.5,
    "totalDuration": 90,
    "electricityFee": 50.05,
    "serviceFee": 13.65,
    "totalFee": 63.70,
    "discountFee": 3.18,
    "actualFee": 60.52,
    "paymentStatus": 1,
    "paymentMethod": 1,
    "status": 1
  }
}
```

### 4.3 异常退费
- **接口**: `POST /charging/orders/{id}/refund`
- **请求体**:
```json
{
  "refundAmount": 30.00,
  "refundReason": "充电异常终止"
}
```

### 4.4 发票申请
- **接口**: `POST /charging/orders/{id}/invoice`
- **请求体**:
```json
{
  "invoiceType": "personal",
  "invoiceTitle": "张三"
}
```

### 4.5 导出订单数据
- **接口**: `GET /charging/orders/export`

### 4.6 实时充电监控
- **接口**: `GET /charging/sessions/active`
- **刷新频率**: 10秒
- **响应**:
```json
{
  "code": 200,
  "data": [
    {
      "sessionId": 1,
      "orderNo": "ORD202404170002",
      "deviceCode": "DC-002",
      "userName": "王五",
      "currentPower": 95.5,
      "currentVoltage": 380.0,
      "currentCurrent": 251.3,
      "currentSoc": 65.5,
      "targetSoc": 90.0,
      "chargedEnergy": 30.2,
      "chargedDuration": 45,
      "currentCost": 42.50,
      "status": 1
    }
  ]
}
```

### 4.7 主动结束充电
- **接口**: `POST /charging/sessions/{sessionId}/stop`
- **请求体**:
```json
{
  "reason": "用户主动结束"
}
```

### 4.8 充电预约列表
- **接口**: `GET /charging/reservations`
- **参数**:
  - `page`, `size`
  - `status`: 预约状态
  - `startTime`, `endTime`: 时间范围
- **响应**: 分页预约列表

### 4.9 确认预约
- **接口**: `POST /charging/reservations/{id}/confirm`
- **权限**: 运维岗、运营岗

### 4.10 取消预约
- **接口**: `POST /charging/reservations/{id}/cancel`
- **请求体**:
```json
{
  "cancelReason": "用户申请取消"
}
```

### 4.11 充电用户列表
- **接口**: `GET /charging/users`
- **参数**:
  - `page`, `size`
  - `phone`: 手机号
  - `memberLevelId`: 会员等级

### 4.12 获取用户详情
- **接口**: `GET /charging/users/{id}`

### 4.13 查询用户消费记录
- **接口**: `GET /charging/users/{id}/consumptions`
- **参数**: `?page=1&size=10&startTime=2024-04-01&endTime=2024-04-30`

### 4.14 调整会员等级
- **接口**: `PUT /charging/users/{id}/member-level`
- **请求体**:
```json
{
  "memberLevelId": 3,
  "reason": "VIP客户升级"
}
```

---

## 5. 收益核算模块 `/finance`

### 5.1 实时营收统计
- **接口**: `GET /finance/revenue-stats`
- **参数**:
  - `timeDimension`: 统计维度（day/week/month/custom）
  - `startTime`, `endTime`: 自定义时间范围
- **响应**:
```json
{
  "code": 200,
  "data": {
    "totalRevenue": 52380.00,
    "totalEnergy": 18560.50,
    "totalOrders": 1250,
    "avgOrderAmount": 41.90,
    "trend": [
      {"date": "2024-04-10", "revenue": 3680.00, "energy": 1250.50},
      {"date": "2024-04-11", "revenue": 4120.00, "energy": 1380.20}
    ]
  }
}
```

### 5.2 订单对账列表
- **接口**: `GET /finance/reconciliations`
- **参数**:
  - `page`, `size`
  - `settlementDate`: 结算日期
  - `reconciliationStatus`: 对账状态

### 5.3 生成日对账报表
- **接口**: `POST /finance/reconciliations/generate-daily`
- **请求体**:
```json
{
  "settlementDate": "2024-04-17"
}
```

### 5.4 生成月对账报表
- **接口**: `POST /finance/reconciliations/generate-monthly`
- **请求体**:
```json
{
  "month": "2024-04"
}
```

### 5.5 标记对账状态
- **接口**: `PUT /finance/reconciliations/{id}/status`
- **请求体**:
```json
{
  "reconciliationStatus": 1,
  "remark": "已核对，无误"
}
```

### 5.6 导出对账报表
- **接口**: `GET /finance/reconciliations/{id}/export`

### 5.7 费率配置列表
- **接口**: `GET /finance/rate-configs`
- **参数**:
  - `page`, `size`
  - `rateType`: 费率类型（峰/平/谷）

### 5.8 创建费率配置
- **接口**: `POST /finance/rate-configs`
- **请求体**:
```json
{
  "rateType": 1,
  "startTime": "10:00:00",
  "endTime": "12:00:00",
  "electricityPrice": 1.2000,
  "servicePrice": 0.4000,
  "effectiveDate": "2024-04-17"
}
```

### 5.9 更新费率配置
- **接口**: `PUT /finance/rate-configs/{id}`

### 5.10 获取V2G放电单价
- **接口**: `GET /finance/rate-configs/v2g-price`

### 5.11 费率调整记录查询
- **接口**: `GET /finance/rate-configs/{id}/logs`

---

## 6. 运营分析模块 `/analytics`

### 6.1 设备效率分析
- **接口**: `GET /analytics/device-efficiency`
- **参数**:
  - `startTime`, `endTime`: 时间范围
  - `deviceTypeId`: 设备类型
- **响应**:
```json
{
  "code": 200,
  "data": {
    "utilizationRate": 65.5,
    "faultRate": 2.1,
    "avgChargeDuration": 45.2,
    "devices": [
      {
        "deviceCode": "DC-001",
        "utilizationRate": 78.5,
        "chargeCount": 125,
        "avgDuration": 50.2,
        "faultCount": 1,
        "isInefficient": false
      }
    ]
  }
}
```

### 6.2 充电数据统计
- **接口**: `GET /analytics/charging-stats`
- **参数**:
  - `dimension`: 统计维度（hour/day/week/month）
  - `startTime`, `endTime`: 时间范围
- **响应**: 时段/日期充电量、订单数统计

### 6.3 用户行为分析
- **接口**: `GET /analytics/user-behavior`
- **参数**: `?startTime=2024-04-01&endTime=2024-04-30`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "peakHours": [10, 11, 14, 15, 19, 20],
    "chargerPreference": {
      "fastCharge": 75.5,
      "slowCharge": 24.5
    },
    "rechargeRate": 68.2,
    "memberDistribution": {
      "normal": 45.0,
      "silver": 30.0,
      "gold": 20.0,
      "platinum": 5.0
    }
  }
}
```

### 6.4 运营报表生成
- **接口**: `POST /analytics/report/generate`
- **请求体**:
```json
{
  "reportType": "daily",
  "date": "2024-04-17"
}
```
- **reportType枚举**: `daily`, `weekly`, `monthly`

### 6.5 导出运营报表
- **接口**: `GET /analytics/report/export`
- **参数**:
  - `reportType`: 报表类型
  - `format`: 导出格式（excel/pdf）
  - `date`: 报表日期

---

## 7. 故障申报模块 `/faults`

### 7.1 创建故障工单
- **接口**: `POST /faults/tickets`
- **请求体**:
```json
{
  "deviceId": 1,
  "faultType": 1,
  "faultDescription": "设备无法启动充电，屏幕显示E01错误代码",
  "faultImages": ["url1", "url2"],
  "expectedFinishTime": "2024-04-18 18:00:00"
}
```
- **响应**: 生成工单编号

### 7.2 故障工单列表
- **接口**: `GET /faults/tickets`
- **参数**:
  - `page`, `size`
  - `status`: 工单状态
  - `deviceId`: 设备ID
  - `reporterId`: 上报人ID
  - `startTime`, `endTime`: 时间范围

### 7.3 获取工单详情
- **接口**: `GET /faults/tickets/{id}`

### 7.4 更新工单状态
- **接口**: `PUT /faults/tickets/{id}/status`
- **请求体**:
```json
{
  "status": 1,
  "handlerId": 5,
  "handlerCompany": "特来电维保"
}
```

### 7.5 完成工单
- **接口**: `PUT /faults/tickets/{id}/complete`
- **请求体**:
```json
{
  "solution": "更换主控板，测试正常",
  "attachmentUrls": ["url1", "url2"],
  "remark": "已恢复正常使用"
}
```

### 7.6 在线咨询记录
- **接口**: `GET /faults/tickets/{id}/consult-records`

### 7.7 添加咨询记录
- **接口**: `POST /faults/tickets/{id}/consult-records`
- **请求体**:
```json
{
  "content": "设备E01错误如何处理？",
  "sender": "user",
  "timestamp": "2024-04-17 14:30:00"
}
```

---

## 8. 告警模块 `/alarms`

### 8.1 告警规则列表
- **接口**: `GET /alarms/rules`
- **响应**: 告警规则列表

### 8.2 创建告警规则
- **接口**: `POST /alarms/rules`
- **请求体**:
```json
{
  "ruleName": "设备过载告警",
  "alarmType": 2,
  "alarmLevel": 2,
  "triggerCondition": "load_rate > 90",
  "notifyMethod": "1,2,3",
  "notifyUsers": "1,2,3",
  "enabled": true
}
```

### 8.3 更新告警规则
- **接口**: `PUT /alarms/rules/{id}`

### 8.4 删除告警规则
- **接口**: `DELETE /alarms/rules/{id}`

### 8.5 告警记录列表
- **接口**: `GET /alarms/records`
- **参数**:
  - `page`, `size`
  - `alarmLevel`: 告警级别
  - `status`: 处理状态
  - `deviceId`: 设备ID
  - `startTime`, `endTime`: 时间范围

### 8.6 处理告警
- **接口**: `PUT /alarms/records/{id}/handle`
- **请求体**:
```json
{
  "status": 2,
  "handleRemark": "已处理，设备恢复正常"
}
```

### 8.7 忽略告警
- **接口**: `PUT /alarms/records/{id}/ignore`
- **请求体**:
```json
{
  "handleRemark": "误报，已确认"
}
```

### 8.8 获取未处理告警数
- **接口**: `GET /alarms/records/unread-count`
- **刷新频率**: 实时

---

## 9. 系统设置模块 `/system`

### 9.1 场站信息
- **接口**: `GET /system/station`
- **响应**: 场站基础信息

### 9.2 更新场站信息
- **接口**: `PUT /system/station`
- **请求体**:
```json
{
  "name": "XX充电站",
  "address": "XX市XX区XX路",
  "contactPhone": "13800138000",
  "ratedCapacity": 800.00,
  "businessHours": "00:00-24:00"
}
```

### 9.3 充电规则配置
- **接口**: `GET /system/station-configs`
- **响应**: 场站配置列表

### 9.4 更新充电规则
- **接口**: `PUT /system/station-configs`
- **请求体**:
```json
{
  "parkingFeeRule": "超过30分钟收取占位费",
  "parkingFeeRate": 10.00,
  "refundRule": "充电前5分钟可免费取消"
}
```

### 9.5 用户列表
- **接口**: `GET /system/users`
- **参数**:
  - `page`, `size`
  - `username`: 用户名
  - `roleId`: 角色ID

### 9.6 创建系统用户
- **接口**: `POST /system/users`
- **请求体**:
```json
{
  "username": "zhangsan",
  "password": "encrypted_password",
  "realName": "张三",
  "phone": "13800138000",
  "email": "zhangsan@example.com",
  "roleIds": [2, 3]
}
```

### 9.7 更新系统用户
- **接口**: `PUT /system/users/{id}`

### 9.8 重置密码
- **接口**: `PUT /system/users/{id}/password`
- **权限**: 超级管理员
- **请求体**:
```json
{
  "newPassword": "encrypted_password"
}
```

### 9.9 禁用/启用用户
- **接口**: `PUT /system/users/{id}/status`
- **请求体**:
```json
{
  "status": 0
}
```

### 9.10 角色列表
- **接口**: `GET /system/roles`
- **响应**: 角色列表

### 9.11 创建角色
- **接口**: `POST /system/roles`
- **请求体**:
```json
{
  "roleCode": "CUSTOM_ROLE",
  "roleName": "自定义角色",
  "description": "描述",
  "menuIds": [1, 2, 3, 4]
}
```

### 9.12 更新角色权限
- **接口**: `PUT /system/roles/{id}/menus`
- **请求体**:
```json
{
  "menuIds": [1, 2, 3, 4, 5]
}
```

### 9.13 操作日志列表
- **接口**: `GET /system/operation-logs`
- **参数**:
  - `page`, `size`
  - `username`: 操作用户
  - `module`: 操作模块
  - `startTime`, `endTime`: 时间范围
- **响应**: 分页操作日志

### 9.14 导出操作日志
- **接口**: `GET /system/operation-logs/export`

---

## 10. 物联网平台对接模块 `/iot`

### 10.1 设备映射列表
- **接口**: `GET /iot/mappings`
- **响应**: 本地设备与物联网平台设备映射关系

### 10.2 创建设备映射
- **接口**: `POST /iot/mappings`
- **请求体**:
```json
{
  "localDeviceId": 1,
  "iotDeviceCode": "IOT-DC-001",
  "apiEndpoint": "/api/devices/IOT-DC-001/data",
  "syncInterval": 10
}
```

### 10.3 更新设备映射
- **接口**: `PUT /iot/mappings/{id}`

### 10.4 删除设备映射
- **接口**: `DELETE /iot/mappings/{id}`

### 10.5 手动同步设备数据
- **接口**: `POST /iot/sync/{deviceId}`
- **说明**: 立即同步指定设备的物联网平台数据
- **响应**:
```json
{
  "code": 200,
  "data": {
    "success": true,
    "message": "同步成功",
    "syncTime": "2024-04-17 15:30:00"
  }
}
```

### 10.6 批量同步设备数据
- **接口**: `POST /iot/sync-batch`
- **说明**: 批量同步所有映射设备的数据

### 10.7 获取同步状态
- **接口**: `GET /iot/sync-status/{deviceId}`

---

## 11. 文件上传模块 `/files`

### 11.1 上传单文件
- **接口**: `POST /files/upload`
- **请求类型**: `multipart/form-data`
- **参数**: `file` (文件)
- **响应**:
```json
{
  "code": 200,
  "data": {
    "url": "https://xxx.com/uploads/2024/04/17/xxx.jpg",
    "filename": "original_name.jpg",
    "size": 102400
  }
}
```

### 11.2 批量上传文件
- **接口**: `POST /files/upload-batch`
- **参数**: `files` (文件数组)

### 11.3 删除文件
- **接口**: `DELETE /files/{fileId}`

---

## 附录A：数据字典

### A.1 设备状态枚举
| 值 | 说明 |
|---|---|
| 0 | 离线 |
| 1 | 在线 |
| 2 | 故障 |
| 3 | 维护中 |

### A.2 订单状态枚举
| 值 | 说明 |
|---|---|
| 0 | 充电中 |
| 1 | 已完成 |
| 2 | 异常终止 |
| 3 | 已取消 |

### A.3 支付状态枚举
| 值 | 说明 |
|---|---|
| 0 | 未支付 |
| 1 | 已支付 |
| 2 | 退款中 |
| 3 | 已退款 |

### A.4 告警级别枚举
| 值 | 说明 | 通知方式 |
|---|---|---|
| 1 | 一般 | 弹窗 |
| 2 | 重要 | 弹窗+声音 |
| 3 | 紧急 | 弹窗+声音+短信 |

### A.5 费率类型枚举
| 值 | 说明 |
|---|---|
| 1 | 峰时段 |
| 2 | 平时段 |
| 3 | 谷时段 |

### A.6 工单状态枚举
| 值 | 说明 |
|---|---|
| 0 | 待处理 |
| 1 | 处理中 |
| 2 | 已完成 |
| 3 | 已关闭 |
| 4 | 已驳回 |

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
| 1001 | 用户名或密码错误 |
| 1002 | 账号已被禁用 |
| 2001 | 设备不在线 |
| 2002 | 设备正在充电中 |
| 3001 | 订单已支付 |
| 3002 | 订单不可退款 |
| 4001 | 设备控制失败 |
