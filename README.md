# 电动汽车充电桩运营管理平台

## 项目简介

本项目是一个完整的电动汽车充电桩运营管理平台，包含**单场站运营管理**和**监管与调度**两个前端系统，以及统一的后端服务。项目集设备管理、充电运营、收益核算、运营分析、故障告警、V2G调度、补贴管理、应急指挥等功能于一体，支持与物联网平台对接，实现充电桩的远程监控和智能调度。

## 项目结构

```
electric-vehicle/
├── charging-station-backend/       # 后端服务
├── charging-station-frontend-plus/ # 单场站运营管理前端
└── supervision-frontend/           # 监管与调度平台前端
```

## 技术栈

### 后端技术

- **后端框架**：Spring Boot 3.2.0
- **开发语言**：Java 17
- **数据库**：MySQL
- **ORM框架**：MyBatis Plus 3.5.5
- **认证授权**：Spring Security + JWT
- **消息队列**：MQTT（物联网数据对接）
- **实时通信**：WebSocket
- **工具库**：Hutool 5.8.25
- **构建工具**：Maven

### 前端技术（两个前端项目）

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4.0+ | 前端框架 |
| Vue Router | 4.2.5+ | 路由管理 |
| Pinia | 2.1.7+ | 状态管理（监管平台） |
| Element Plus | 2.4.4+ | UI组件库 |
| ECharts | 5.4.3+ | 图表可视化 |
| Axios | 1.6.0+ | HTTP客户端 |
| Vite | 5.0.0+ | 构建工具 |
| Sass | 1.69.0+ | CSS预处理器 |

## 系统架构

### 后端模块划分

```
charging-station-backend/
├── aspect/              # AOP切面（操作日志等）
├── common/              # 公共类（统一响应、错误码）
├── config/              # 配置类（JWT、MQTT、安全等）
├── controller/          # 控制器层
├── dto/                 # 数据传输对象
│   └── iot/            # 物联网相关DTO
├── entity/              # 数据库实体
├── exception/           # 全局异常处理
├── filter/              # JWT认证过滤器
├── mapper/              # MyBatis Plus Mapper
├── service/             # 业务逻辑层
│   └── impl/           # 服务实现类
├── vo/                  # 视图对象
├── websocket/           # WebSocket服务
└── ChargingStationApplication.java  # 启动类
```

### 前端项目介绍

#### 1. charging-station-frontend-plus（单场站运营管理平台）

面向单个充电站运营人员的管理平台，主要功能包括：

| 模块 | 说明 |
|------|------|
| 场站总览 | 实时监控场站运行状态、设备统计、负荷趋势 |
| 设备管理 | 设备台账、实时参数、维保记录、远程控制 |
| 充电运营 | 订单管理、用户管理、预约管理、实时充电监控 |
| 收益核算 | 营收统计、对账管理、费率配置 |
| 运营分析 | 设备效率分析、充电数据统计、用户行为分析、运营报表 |
| 故障申报 | 故障工单管理、在线咨询 |
| 告警管理 | 告警规则配置、告警记录处理 |
| 系统设置 | 用户管理、角色权限、操作日志、场站配置 |

#### 2. supervision-frontend（监管与调度平台）

面向政府监管部门或区域调度中心的管理平台，主要功能包括：

| 模块 | 说明 |
|------|------|
| 监管总览 | 区域场站整体运行态势、统计分析 |
| 设施监管 | 场站台账、状态监控、准入审核 |
| V2G调度 | 需求响应、智能调度、V2G活动管理 |
| 合规管理 | 运营商管理、政策管理、补贴管理 |
| 应急指挥 | 告警监控、应急命令、事故追溯 |
| 系统管理 | 用户管理、权限配置 |

### 后端功能模块

| 模块 | 说明 |
|------|------|
| 场站总览 | 实时监控场站运行状态、设备统计、负荷趋势 |
| 设备管理 | 设备台账、实时参数、维保记录、远程控制 |
| 充电运营 | 订单管理、用户管理、预约管理、实时充电监控 |
| 收益核算 | 营收统计、对账管理、费率配置 |
| 运营分析 | 设备效率分析、充电数据统计、用户行为分析、运营报表 |
| 故障申报 | 故障工单管理、在线咨询 |
| 告警管理 | 告警规则配置、告警记录处理 |
| V2G调度 | V2G活动管理、放电调度、效果统计 |
| 补贴管理 | 补贴申请、审核、发放 |
| 运营商管理 | 运营商资质、评分、违规记录 |
| 应急指挥 | 告警监控、应急命令、事故追溯 |
| 系统设置 | 用户管理、角色权限、操作日志、场站配置 |
| 物联网对接 | 设备映射、数据同步、MQTT订阅 |

## 快速开始

### 环境要求

- **后端**：JDK 17+、Maven 3.6+、MySQL 8.0+
- **前端**：Node.js 16+、npm 8+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE charging_station DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改配置文件 `charging-station-backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/charging_station?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

### 物联网平台配置（可选）

如需对接物联网平台，配置以下参数：
```yaml
iot:
  platform:
    base-url: http://iotapi.iot.yun.gdatacloud.com:32664
    app-id: your_app_id
    app-secret: your_app_secret

mqtt:
  broker:
    url: tcp://mqtt.yun.gdatacloud.com:31854
    client-id: your_client_id
  credentials:
    username: your_username
    password: your_password
```

### 启动后端服务

```bash
# 进入后端目录
cd charging-station-backend

# 编译项目
mvn clean install

# 启动项目
mvn spring-boot:run
```

后端服务启动后，访问：`http://localhost:8080`

### 启动单场站运营管理前端

```bash
# 进入前端目录
cd charging-station-frontend-plus

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

前端启动后，访问：`http://localhost:5173`（具体端口以控制台输出为准）

### 启动监管与调度平台前端

```bash
# 进入前端目录
cd supervision-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

前端启动后，访问：`http://localhost:5173`（具体端口以控制台输出为准）

## API文档

详细的API文档请参考：
- [API文档](./API_documentation.md) - 基础运营平台API
- [监管API文档](./api_documentation_supervision.md) - 监管平台API
- [物联网集成文档](./IOT_integration.md) - 物联网平台对接说明
- [ER图](./ER_diagram.md) - 数据库实体关系图

### 主要API端点

| 模块 | 基础路径 |
|------|----------|
| 认证 | `/auth` |
| 场站总览 | `/dashboard` |
| 设备管理 | `/devices` |
| 充电订单 | `/charging/orders` |
| 收益核算 | `/finance` |
| 运营分析 | `/analytics` |
| 故障工单 | `/faults/tickets` |
| 告警管理 | `/alarms` |
| 系统设置 | `/system` |
| V2G调度 | `/v2g` |
| 补贴管理 | `/subsidy` |
| 物联网对接 | `/iot` |

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1698765432000
}
```

## 数据库设计

项目采用规范化设计，主要表包括：

- `station` - 场站信息
- `device` - 设备信息
- `user` - 系统用户
- `charging_order` - 充电订单
- `charging_session` - 充电会话
- `rate_config` - 费率配置
- `alarm` - 告警记录
- `fault_ticket` - 故障工单
- `v2g_activity` - V2G活动
- `subsidy_application` - 补贴申请

详细的ER图请参考 [ER_diagram.md](./ER_diagram.md)

## 核心功能

### 单场站运营管理平台功能

#### 1. 设备监控与远程控制
- 实时监控设备状态、功率、电压、电流、温度等参数
- 支持远程启动/停止充电、调节功率、V2G放电等操作
- 设备维保记录管理、OTA升级

#### 2. 充电订单管理
- 完整的充电订单生命周期管理
- 实时充电监控（10秒刷新）
- 异常退费、发票申请
- 预约管理

#### 3. 收益核算
- 实时营收统计
- 订单对账管理
- 费率配置与调整

#### 4. 运营分析
- 设备效率分析
- 充电数据统计
- 用户行为分析
- 运营报表生成

#### 5. 告警与故障处理
- 灵活的告警规则配置
- 多级别告警通知（弹窗、声音、短信）
- 故障工单流程管理
- 在线咨询支持

#### 6. 系统管理
- 用户管理与权限配置
- 操作日志记录
- 场站基础信息配置

### 监管与调度平台功能

#### 1. 区域监管总览
- 多场站运行态势实时监控
- 区域负荷统计分析
- 关键指标数据大屏展示

#### 2. 设施监管
- 场站台账管理
- 设备状态集中监控
- 场站准入审核
- 运营合规性检查

#### 3. V2G智能调度
- 需求响应活动管理
- 智能放电调度
- 调度效果评估
- V2G活动全流程追踪

#### 4. 合规管理
- 运营商资质管理
- 运营商评级体系
- 政策发布与推送
- 补贴申请审核与发放

#### 5. 应急指挥
- 区域告警集中监控
- 应急命令下发
- 事故追溯与分析
- 告警规则配置

#### 6. 物联网平台对接
- MQTT实时数据订阅
- 设备数据映射与同步
- 支持第三方物联网平台集成

## 测试

项目提供了API测试脚本：
- [api_test.ps1](./api_test.ps1) - 基础API测试
- [api_test_full.ps1](./api_test_full.ps1) - 完整API测试

## 项目文档

### 需求文档
- [单场站充电站运营管理平台需求说明文档.md](./单场站充电站运营管理平台需求说明文档.md)
- [监管与调度平台功能需求文档.md](./监管与调度平台功能需求文档.md)

### API文档
- [API_documentation.md](./API_documentation.md) - 单场站运营平台API文档
- [api_documentation_supervision.md](./api_documentation_supervision.md) - 监管平台API文档

### 数据库设计
- [ER_diagram.md](./ER_diagram.md) - 单场站数据库ER图
- [er_diagram_supervision.md](./er_diagram_supervision.md) - 监管平台数据库ER图
- [database_design.sql](./database_design.sql) - 数据库建表脚本
- [database_design_supervision.sql](./database_design_supervision.sql) - 监管平台数据库建表脚本

### 其他文档
- [IOT_integration.md](./IOT_integration.md) - 物联网集成文档
- [project_plan.md](./project_plan.md) - 项目计划
- [supervision_project_plan.md](./supervision_project_plan.md) - 监管平台项目计划

### 测试脚本
- [api_test.ps1](./api_test.ps1) - 基础API测试脚本
- [api_test_full.ps1](./api_test_full.ps1) - 完整API测试脚本
- [init_test_data.sql](./init_test_data.sql) - 测试数据初始化脚本
- [insert_supervision_test_data.sql](./insert_supervision_test_data.sql) - 监管平台测试数据脚本

## 许可证

本项目仅供学习和参考使用。

## 联系方式

如有问题或建议，请联系项目维护者。
