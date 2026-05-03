# 电动汽车充电桩运营管理平台 - 物联网平台对接文档

## 1. 对接概述

### 1.1 对接目标
本运营管理平台需要从学校物联网平台获取充电桩设备的实时运行数据，包括：
- 设备状态（在线/离线/故障）
- 实时功率、电压、电流
- 充电量统计
- 温度等环境参数

### 1.2 对接架构

```
┌─────────────────────┐                    ┌──────────────────────┐
│  运营管理平台       │                    │  学校物联网平台      │
│                     │                    │                      │
│  ┌───────────────┐  │   HTTP/MQTT        │  ┌────────────────┐  │
│  │ 数据采集服务  │◄─┼───────────────────┼─►│  设备数据接口  │  │
│  └───────────────┘  │                    │  └────────────────┘  │
│         │           │                    │                      │
│         ▼           │                    │  ┌────────────────┐  │
│  ┌───────────────┐  │                    │  │  设备控制接口  │  │
│  │  本地数据库   │  │                    │  └────────────────┘  │
│  └───────────────┘  │                    │                      │
│                     │                    │  ┌────────────────┐  │
│  ┌───────────────┐  │                    │  │  设备状态接口  │  │
│  │  WebSocket    │◄─┼────────────────────┼──┤  (推送)        │  │
│  └───────────────┘  │                    │  └────────────────┘  │
└─────────────────────┘                    └──────────────────────┘
```

### 1.3 数据同步方式

| 同步方式 | 说明 | 刷新频率 | 适用场景 |
|---------|------|---------|---------|
| 定时轮询 | 主动调用物联网平台API | 10秒/次 | 设备状态、实时参数 |
| WebSocket推送 | 物联网平台主动推送 | 实时 | 状态变化、告警事件 |
| 批量拉取 | 定时拉取历史数据 | 5分钟/次 | 运行曲线、统计数据 |

---

## 2. 认证鉴权

### 2.1 获取访问令牌
- **接口**: `POST {IOT_BASE_URL}/auth/token`
- **请求头**:
```
Content-Type: application/json
```
- **请求体**:
```json
{
  "appId": "charging_station_app",
  "appSecret": "your_app_secret",
  "grantType": "client_credentials"
}
```
- **响应**:
```json
{
  "code": 200,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200,
    "tokenType": "Bearer"
  }
}
```

### 2.2 Token刷新
- **接口**: `POST {IOT_BASE_URL}/auth/refresh`
- **请求体**:
```json
{
  "refreshToken": "refresh_token_string"
}
```

### 2.3 鉴权方式
所有API请求需在请求头中携带Token：
```
Authorization: Bearer {accessToken}
X-App-Id: charging_station_app
```

---

## 3. 设备数据接口

### 3.1 获取设备列表
- **接口**: `GET {IOT_BASE_URL}/api/devices`
- **参数**:
  - `page`: 页码
  - `size`: 每页条数
  - `deviceType`: 设备类型（charger/v2g/storage/photovoltaic）
  - `status`: 设备状态（online/offline/fault）
- **响应**:
```json
{
  "code": 200,
  "data": {
    "total": 50,
    "devices": [
      {
        "iotDeviceId": "IOT-DC-001",
        "deviceName": "1号直流充电桩",
        "deviceType": "charger",
        "subType": "dc",
        "status": "online",
        "location": {
          "area": "A区",
          "position": "A-01"
        },
        "attributes": {
          "manufacturer": "特来电",
          "model": "TLD-DC-120",
          "powerRating": 120,
          "firmwareVersion": "v2.0.1"
        },
        "onlineTime": "2024-04-17T08:00:00Z",
        "lastReportTime": "2024-04-17T10:30:00Z"
      }
    ]
  }
}
```

### 3.2 获取设备实时数据
- **接口**: `GET {IOT_BASE_URL}/api/devices/{iotDeviceId}/telemetry/latest`
- **说明**: 获取设备最新遥测数据（10秒刷新）
- **响应**:
```json
{
  "code": 200,
  "data": {
    "iotDeviceId": "IOT-DC-001",
    "timestamp": "2024-04-17T10:30:15Z",
    "telemetry": {
      "status": "charging",
      "power": 85.5,
      "voltage": 380.2,
      "current": 224.8,
      "energy": 12.5,
      "temperature": 42.5,
      "soc": 65.5,
      "gunStatus": "connected",
      "chargeStatus": "charging",
      "errorCode": null,
      "totalEnergy": 15680.50
    },
    "quality": "good"
  }
}
```

### 3.3 获取设备历史数据
- **接口**: `GET {IOT_BASE_URL}/api/devices/{iotDeviceId}/telemetry/history`
- **参数**:
  - `startTime`: 开始时间（ISO 8601格式）
  - `endTime`: 结束时间
  - `interval`: 采样间隔（秒）
  - `metrics`: 指标列表（逗号分隔）
- **请求示例**:
```
GET /api/devices/IOT-DC-001/telemetry/history?startTime=2024-04-17T00:00:00Z&endTime=2024-04-17T23:59:59Z&interval=60&metrics=power,voltage,current,temperature
```
- **响应**:
```json
{
  "code": 200,
  "data": {
    "iotDeviceId": "IOT-DC-001",
    "metrics": ["power", "voltage", "current", "temperature"],
    "timeRange": {
      "start": "2024-04-17T00:00:00Z",
      "end": "2024-04-17T23:59:59Z"
    },
    "points": [
      {
        "timestamp": "2024-04-17T00:00:00Z",
        "values": {
          "power": 0,
          "voltage": 380,
          "current": 0,
          "temperature": 25.5
        }
      },
      {
        "timestamp": "2024-04-17T00:01:00Z",
        "values": {
          "power": 85.5,
          "voltage": 380.2,
          "current": 224.8,
          "temperature": 26.0
        }
      }
    ]
  }
}
```

### 3.4 批量获取设备数据
- **接口**: `POST {IOT_BASE_URL}/api/devices/telemetry/batch`
- **说明**: 一次性获取多个设备的最新数据（用于场站总览）
- **请求体**:
```json
{
  "deviceIds": ["IOT-DC-001", "IOT-DC-002", "IOT-AC-001"],
  "metrics": ["status", "power", "voltage", "current", "temperature"]
}
```
- **响应**:
```json
{
  "code": 200,
  "data": [
    {
      "iotDeviceId": "IOT-DC-001",
      "timestamp": "2024-04-17T10:30:15Z",
      "telemetry": {
        "status": "charging",
        "power": 85.5,
        "voltage": 380.2,
        "current": 224.8,
        "temperature": 42.5
      }
    },
    {
      "iotDeviceId": "IOT-DC-002",
      "timestamp": "2024-04-17T10:30:15Z",
      "telemetry": {
        "status": "idle",
        "power": 0,
        "voltage": 380.0,
        "current": 0,
        "temperature": 28.5
      }
    }
  ]
}
```

### 3.5 获取设备状态
- **接口**: `GET {IOT_BASE_URL}/api/devices/{iotDeviceId}/status`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "iotDeviceId": "IOT-DC-001",
    "connectionStatus": "online",
    "runningStatus": "charging",
    "lastHeartbeat": "2024-04-17T10:30:15Z",
    "signalStrength": 85,
    "uptime": 86400
  }
}
```

---

## 4. 设备控制接口

### 4.1 启动充电
- **接口**: `POST {IOT_BASE_URL}/api/devices/{iotDeviceId}/commands/start-charge`
- **请求体**:
```json
{
  "params": {
    "targetSoc": 90,
    "maxPower": 100,
    "userId": "user_001"
  },
  "timeout": 30
}
```
- **响应**:
```json
{
  "code": 200,
  "data": {
    "commandId": "cmd_123456",
    "status": "sent",
    "message": "命令已发送"
  }
}
```

### 4.2 停止充电
- **接口**: `POST {IOT_BASE_URL}/api/devices/{iotDeviceId}/commands/stop-charge`
- **请求体**:
```json
{
  "reason": "用户主动停止",
  "timeout": 30
}
```

### 4.3 调节功率
- **接口**: `POST {IOT_BASE_URL}/api/devices/{iotDeviceId}/commands/adjust-power`
- **请求体**:
```json
{
  "params": {
    "targetPower": 80
  },
  "timeout": 30
}
```

### 4.4 V2G放电控制
- **接口**: `POST {IOT_BASE_URL}/api/devices/{iotDeviceId}/commands/v2g-discharge`
- **请求体**:
```json
{
  "params": {
    "dischargePower": 50,
    "minSoc": 20,
    "duration": 120
  },
  "timeout": 30
}
```

### 4.5 储能设备控制
- **接口**: `POST {IOT_BASE_URL}/api/devices/{iotDeviceId}/commands/storage-control`
- **请求体**:
```json
{
  "params": {
    "action": "start",
    "mode": "charge",
    "targetPower": 100
  },
  "timeout": 30
}
```

### 4.6 光伏设备控制
- **接口**: `POST {IOT_BASE_URL}/api/devices/{iotDeviceId}/commands/photovoltaic-control`
- **请求体**:
```json
{
  "params": {
    "action": "start"
  },
  "timeout": 30
}
```

### 4.7 查询命令执行状态
- **接口**: `GET {IOT_BASE_URL}/api/commands/{commandId}/status`
- **响应**:
```json
{
  "code": 200,
  "data": {
    "commandId": "cmd_123456",
    "status": "success",
    "result": "充电已启动",
    "execTime": "2024-04-17T10:30:20Z",
    "responseTime": "2024-04-17T10:30:18Z"
  }
}
```

---

## 5. 实时推送接口（WebSocket）

### 5.1 连接地址
```
wss://iot-platform.school.edu.cn/ws/telemetry?token={accessToken}
```

### 5.2 订阅设备数据
连接成功后，发送订阅消息：
```json
{
  "action": "subscribe",
  "deviceIds": ["IOT-DC-001", "IOT-DC-002"],
  "metrics": ["power", "voltage", "current", "status"]
}
```

### 5.3 接收推送数据
```json
{
  "type": "telemetry_update",
  "data": {
    "iotDeviceId": "IOT-DC-001",
    "timestamp": "2024-04-17T10:30:15Z",
    "telemetry": {
      "power": 85.5,
      "voltage": 380.2,
      "current": 224.8,
      "status": "charging"
    }
  }
}
```

### 5.4 接收告警推送
```json
{
  "type": "alarm_event",
  "data": {
    "iotDeviceId": "IOT-DC-001",
    "alarmType": "over_temperature",
    "alarmLevel": 2,
    "alarmValue": 75.5,
    "threshold": 70.0,
    "timestamp": "2024-04-17T10:30:15Z"
  }
}
```

### 5.5 取消订阅
```json
{
  "action": "unsubscribe",
  "deviceIds": ["IOT-DC-001"]
}
```

### 5.6 心跳保活
每30秒发送一次心跳：
```json
{
  "action": "ping"
}
```
服务器响应：
```json
{
  "action": "pong"
}
```

---

## 6. 告警事件接口

### 6.1 订阅告警事件
- **接口**: `POST {IOT_BASE_URL}/api/devices/{iotDeviceId}/alarms/subscribe`
- **请求体**:
```json
{
  "alarmTypes": ["over_temperature", "over_voltage", "over_current", "device_fault"],
  "notifyUrl": "https://charging-station.school.edu.cn/api/iot/alarm-callback"
}
```

### 6.2 告警回调接收
物联网平台通过HTTP POST推送告警到运营管理平台：
- **地址**: `{your_callback_url}/api/iot/alarm-callback`
- **请求体**:
```json
{
  "iotDeviceId": "IOT-DC-001",
  "alarmType": "over_temperature",
  "alarmLevel": 2,
  "alarmContent": "设备温度过高",
  "alarmValue": 75.5,
  "threshold": 70.0,
  "timestamp": "2024-04-17T10:30:15Z"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "received"
}
```

### 6.3 告警确认
- **接口**: `POST {IOT_BASE_URL}/api/alarms/{alarmId}/acknowledge`
- **请求体**:
```json
{
  "acknowledgeBy": "admin",
  "remark": "已确认，正在处理"
}
```

---

## 7. 设备管理接口

### 7.1 设备注册
- **接口**: `POST {IOT_BASE_URL}/api/devices/register`
- **说明**: 新设备安装后注册到物联网平台
- **请求体**:
```json
{
  "deviceName": "52号直流充电桩",
  "deviceType": "charger",
  "subType": "dc",
  "attributes": {
    "manufacturer": "特来电",
    "model": "TLD-DC-120",
    "serialNumber": "TLD202404170001",
    "firmwareVersion": "v2.0.1",
    "powerRating": 120
  },
  "location": {
    "area": "B区",
    "position": "B-02"
  }
}
```

### 7.2 设备OTA升级
- **接口**: `POST {IOT_BASE_URL}/api/devices/{iotDeviceId}/ota/upgrade`
- **请求体**:
```json
{
  "firmwareVersion": "v2.1.0",
  "firmwareUrl": "https://firmware.example.com/v2.1.0.bin",
  "upgradeTime": "immediate"
}
```

### 7.3 设备OTA回滚
- **接口**: `POST {IOT_BASE_URL}/api/devices/{iotDeviceId}/ota/rollback`

### 7.4 设备重启
- **接口**: `POST {IOT_BASE_URL}/api/devices/{iotDeviceId}/commands/reboot`

---

## 8. 本地数据同步实现

### 8.1 同步服务架构

```java
// 定时同步任务调度
@Component
public class IotDataSyncScheduler {
    
    @Scheduled(fixedRate = 10000) // 10秒同步一次
    public void syncDeviceData() {
        // 1. 获取需要同步的设备列表
        List<IotDeviceMapping> devices = mappingService.getEnabledDevices();
        
        // 2. 批量获取物联网平台数据
        List<TelemetryData> telemetryData = iotClient.batchGetTelemetry(devices);
        
        // 3. 更新本地设备状态
        for (TelemetryData data : telemetryData) {
            deviceService.updateRealtimeStatus(data);
            
            // 4. 保存历史数据
            deviceDataHistoryService.saveHistory(data);
            
            // 5. 检查告警规则
            alarmRuleService.checkAndTriggerAlarm(data);
        }
        
        // 6. 更新同步状态
        mappingService.updateSyncStatus(devices);
    }
    
    @Scheduled(cron = "0 */5 * * * ?") // 每5分钟同步历史数据
    public void syncHistoryData() {
        // 拉取最近5分钟的详细数据
    }
}
```

### 8.2 物联网客户端实现

```java
@Service
public class IotPlatformClient {
    
    private String iotBaseUrl;
    private String accessToken;
    
    // 批量获取设备遥测数据
    public List<TelemetryData> batchGetTelemetry(List<IotDeviceMapping> mappings) {
        List<String> deviceIds = mappings.stream()
            .map(IotDeviceMapping::getIotDeviceCode)
            .collect(Collectors.toList());
            
        BatchTelemetryRequest request = new BatchTelemetryRequest();
        request.setDeviceIds(deviceIds);
        request.setMetrics(Arrays.asList("power", "voltage", "current", "temperature"));
        
        ResponseEntity<Result> response = restTemplate.postForEntity(
            iotBaseUrl + "/api/devices/telemetry/batch",
            request,
            Result.class
        );
        
        return convertToTelemetryData(response.getBody());
    }
    
    // 发送设备控制命令
    public CommandResult sendCommand(String iotDeviceId, String command, Map<String, Object> params) {
        CommandRequest request = new CommandRequest();
        request.setParams(params);
        request.setTimeout(30);
        
        ResponseEntity<Result> response = restTemplate.postForEntity(
            iotBaseUrl + "/api/devices/" + iotDeviceId + "/commands/" + command,
            request,
            Result.class
        );
        
        return convertToCommandResult(response.getBody());
    }
}
```

### 8.3 WebSocket连接管理

```java
@Component
public class IotWebSocketClient implements WebSocketHandler {
    
    private WebSocketSession session;
    private ScheduledExecutorService heartbeatExecutor;
    
    // 连接物联网平台
    public void connect() {
        // 建立WebSocket连接
        // 订阅设备数据
        // 启动心跳保活
    }
    
    // 接收推送数据
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        IotPushMessage pushMessage = parseMessage(message.getPayload());
        
        switch (pushMessage.getType()) {
            case "telemetry_update":
                handleTelemetryUpdate(pushMessage);
                break;
            case "alarm_event":
                handleAlarmEvent(pushMessage);
                break;
        }
    }
    
    // 心跳保活
    private void startHeartbeat() {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage("{\"action\":\"ping\"}"));
            }
        }, 0, 30, TimeUnit.SECONDS);
    }
}
```

### 8.4 数据映射转换

```java
@Service
public class TelemetryDataConverter {
    
    // 物联网平台数据 -> 本地设备数据
    public DeviceStatus convertToLocalStatus(TelemetryData iotData) {
        DeviceStatus localStatus = new DeviceStatus();
        
        // 状态映射
        localStatus.setStatus(mapDeviceStatus(iotData.getTelemetry().getStatus()));
        localStatus.setRealTimePower(iotData.getTelemetry().getPower());
        localStatus.setRealTimeVoltage(iotData.getTelemetry().getVoltage());
        localStatus.setRealTimeCurrent(iotData.getTelemetry().getCurrent());
        localStatus.setTemperature(iotData.getTelemetry().getTemperature());
        
        return localStatus;
    }
    
    // 设备状态映射
    private Integer mapDeviceStatus(String iotStatus) {
        switch (iotStatus) {
            case "online":
            case "idle":
                return 1; // 在线
            case "charging":
            case "discharging":
                return 1; // 在线
            case "offline":
                return 0; // 离线
            case "fault":
                return 2; // 故障
            default:
                return 0;
        }
    }
}
```

---

## 9. 错误处理与容错

### 9.1 网络异常处理
- 连接失败时，每30秒重试一次，最多重试3次
- 重试失败后，标记同步状态为"同步失败"
- 触发告警通知运维人员

### 9.2 数据异常处理
- 数据校验失败时，记录异常日志
- 忽略异常数据，使用上一次有效数据
- 连续5次数据校验失败，触发设备状态检查

### 9.3 限流控制
- 物联网平台API调用频率限制：100次/分钟
- 使用令牌桶算法控制请求频率
- 超限请求进入队列等待

### 9.4 降级策略
- 物联网平台不可用时，使用本地缓存数据
- 本地缓存数据超时时间：5分钟
- 降级模式下，前端显示数据延迟提示

---

## 10. 对接配置说明

### 10.1 application.yml配置

```yaml
# 物联网平台对接配置
iot:
  platform:
    base-url: https://iot-platform.school.edu.cn
    app-id: charging_station_app
    app-secret: your_app_secret
    # 数据同步配置
    sync:
      interval: 10  # 同步间隔（秒）
      batch-size: 50  # 批量同步数量
      history-interval: 300  # 历史数据同步间隔（秒）
    # WebSocket配置
    websocket:
      enabled: true
      heartbeat-interval: 30  # 心跳间隔（秒）
      reconnect-interval: 10  # 重连间隔（秒）
      max-reconnect-attempts: 5  # 最大重连次数
    # HTTP客户端配置
    http:
      connect-timeout: 5000  # 连接超时（毫秒）
      read-timeout: 10000  # 读取超时（毫秒）
      max-connections: 100  # 最大连接数
      rate-limit: 100  # 每分钟最大请求数
```

### 10.2 环境变量

| 环境变量 | 说明 | 示例值 |
|---------|------|--------|
| IOT_PLATFORM_BASE_URL | 物联网平台地址 | https://iot-platform.school.edu.cn |
| IOT_PLATFORM_APP_ID | 应用ID | charging_station_app |
| IOT_PLATFORM_APP_SECRET | 应用密钥 | *** |

---

## 11. 测试与验证

### 11.1 接口测试清单

| 测试项 | 测试方法 | 预期结果 |
|-------|---------|---------|
| 认证鉴权 | 调用Token接口 | 成功获取Token |
| 设备列表 | 获取设备列表 | 返回设备清单 |
| 实时数据 | 获取设备遥测 | 返回最新数据 |
| 历史数据 | 获取历史数据 | 返回时间序列数据 |
| 设备控制 | 发送启动命令 | 命令执行成功 |
| WebSocket | 建立连接订阅 | 收到推送数据 |
| 告警回调 | 触发告警事件 | 收到回调通知 |

### 11.2 性能指标

| 指标 | 要求 |
|-----|------|
| 数据同步延迟 | < 2秒 |
| 命令响应时间 | < 5秒 |
| WebSocket重连时间 | < 10秒 |
| 并发设备数 | >= 100台 |
| 数据同步成功率 | >= 99.9% |

---

## 12. 常见问题

### Q1: Token过期如何处理？
A: 系统自动监控Token有效期，在过期前5分钟自动刷新Token。如果刷新失败，重新获取Token。

### Q2: 设备离线如何检测？
A: 通过WebSocket心跳检测和定时轮询设备状态两种方式综合判断。连续3次心跳无响应判定为离线。

### Q3: 数据同步失败如何恢复？
A: 系统记录失败的设备列表，采用指数退避策略重试（10s, 20s, 40s, 80s）。同时提供手动同步接口。

### Q4: 物联网平台地址变更怎么办？
A: 在application.yml中更新`iot.platform.base-url`配置，重启服务即可。

### Q5: 如何调试物联网接口？
A: 提供Postman集合和测试脚本，可在测试环境验证所有接口。日志级别调至DEBUG可查看完整请求响应。
