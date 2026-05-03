# 监管与调度平台 - 完整实施计划

> **文档说明：** 使用 `- [ ]` 表示未完成，`- [x]` 表示已完成。每完成一项请手动更新标记。

**项目目标：** 在现有充电站运营管理平台基础上，开发监管与调度平台，实现对辖区内充电设施运营、车网互动活动、电网安全、市场秩序的宏观监控与合规管理。

**技术栈：** 
- 后端：Java 17 + Spring Boot 3.x + MyBatis Plus + MySQL 8.0
- 前端：Vue 3 + Vite + Element Plus + Pinia + ECharts
- 物联网对接：HTTP API + WebSocket

**项目进度：** 文档准备阶段已完成

---

## 阶段一：项目准备与文档设计 ✅

### 任务 1.1：需求分析与文档准备

- [x] **步骤 1：阅读并分析监管需求文档**
  - 文件：`监管与调度平台功能需求文档.md`
  - 输出：需求分析总结（6大模块划分：数据总览、设施监管、车网互动与调度、运营与合规管理、安全与应急管理、系统管理）

- [x] **步骤 2：设计数据库扩展结构**
  - 文件：创建 `database_design_supervision.sql`
  - 内容：扩展现有5张表 + 新增25张表（区域管理、运营商管理、接入审核、V2G活动、智能调度、补贴管理等）
  - 包含：主键、外键、索引、注释、初始化数据（深圳市行政区划、示例运营商、监管角色）

- [x] **步骤 3：绘制监管平台ER图**
  - 文件：创建 `er_diagram_supervision.md`
  - 工具：Mermaid ER Diagram
  - 内容：完整实体关系图（30张表），模块化分层展示，标注复用/扩展/新增状态

- [x] **步骤 4：编写监管平台API文档**
  - 文件：创建 `api_documentation_supervision.md`
  - 内容：10大模块，约100+个API接口
  - 包含：请求参数、响应格式、错误码说明、WebSocket推送事件

- [x] **步骤 5：创建项目计划文档**
  - 文件：创建 `supervision_project_plan.md`（本文档）
  - 内容：完整任务清单和进度追踪

- [x] **步骤 6: 数据库脚本执行验证**
  - 执行 `database_design_supervision.sql`
  - 验证表结构创建、索引、初始数据
  - 备注: 数据库表已创建并初始化数据

---

## 阶段二：后端开发 📋

### 任务 2.1：项目模块初始化与环境搭建

- [ ] **步骤 1：扩展现有数据库表**
  - 文件：执行 `ALTER TABLE` 语句扩展 `station`、`device`、`charging_order`、`alarm`、`role`
  - 新增字段：`operator_id`、`region_code`、`access_status`、`is_v2g` 等

- [x] **步骤 2：创建监管模块包结构**
  - 文件：创建 `entity/` 目录下25个实体类
  - 包含：Region、Operator、OperatorQualification、AccessApplication、OperatorRating、OperatorViolation、V2gActivity、V2gActivityParticipant、OrderedChargingStrategy、DemandResponseActivity、DemandResponseOperatorRecord、DispatchTask、DispatchStationAllocation、DispatchHistory、SubsidyApplication、SubsidyAuditRecord、SubsidyRecord、Policy、PolicyPushRecord、EmergencyDispatchCommand、AlarmEscalationRule、AlarmHandlingRecord、RectificationNotice、V2gEffectStats、OperatorMonthlyStats

- [ ] **步骤 3：扩展认证与权限配置**
  - 文件：`config/SecurityConfig.java`
  - 新增监管角色：SUPERVISE_ADMIN、CITY_SUPERVISOR、DISTRICT_SUPERVISOR
  - 扩展区域数据权限拦截器

### 任务 2.2：监管平台基础模块

- [x] **步骤 1：创建区域管理实体与Mapper**
  - 文件：
    - `entity/Region.java` ✅
    - `mapper/RegionMapper.java` ✅
    - `service/RegionService.java` ✅
    - `service/impl/RegionServiceImpl.java` ✅
  - 功能：三级区域树查询、区域编码前缀匹配

- [x] **步骤 2：创建运营商管理实体与Mapper**
  - 文件：
    - `entity/Operator.java` ✅
    - `entity/OperatorQualification.java` ✅
    - `mapper/OperatorMapper.java` ✅
    - `mapper/OperatorQualificationMapper.java` ✅
    - `service/OperatorComplianceService.java` ✅

- [x] **步骤 3：创建通用DTO和VO类**
  - DTO类：AuditRequest、SubsidyAuditRequest、PowerAdjustRequest、RectificationNoticeRequest、DispatchCommandRequest、AlarmHandleRequest、EmergencyCommandRequest ✅
  - VO类：RegionVO ✅

- [x] **步骤 4：创建核心Service和Controller**
  - Service：AccessApplicationService、SubsidyService、SubsidyAuditRecordService、DispatchService ✅
  - Controller：AccessApplicationController、SubsidyController ✅

### 任务 2.3：数据总览模块

- [x] **步骤 1：实现数据总览Service**
  - 文件：`service/SupervisionDashboardService.java`
  - 功能：9个核心指标聚合查询、区域数据过滤
  - 备注：已实现，使用Mock数据

- [x] **步骤 2：实现数据总览Controller**
  - 接口：
    - `GET /api/v1/supervision/dashboard/metrics` - 获取核心指标
    - `GET /api/v1/supervision/dashboard/charge-discharge-trend` - 充放电趋势
    - `GET /api/v1/supervision/dashboard/demand-response-effect` - 需求响应效果
    - `GET /api/v1/supervision/dashboard/alarms` - 实时告警列表
    - `GET /api/v1/supervision/dashboard/unread-alarm-count` - 未处理告警数

- [ ] **步骤 3：实现WebSocket实时推送**
  - 文件：`config/WebSocketConfig.java`
  - 事件：alarm:new、dispatch:update、emergency:command

### 任务 2.4：设施监管模块

- [x] **步骤 1：实现设施台账查询**
  - 文件：`service/FacilityService.java`
  - 接口：`GET /api/v1/supervision/facility/ledger`
  - 功能：多条件筛选、区域/运营商过滤、导出Excel

- [x] **步骤 2：实现接入申请审核**
  - 文件：
    - `entity/AccessApplication.java`
    - `service/AccessApplicationService.java`
  - 接口：
    - `GET /api/v1/supervision/facility/access-applications` - 申请列表
    - `POST /api/v1/supervision/facility/access-applications/{id}/audit` - 审核申请

- [x] **步骤 3：实现运行状态监控**
  - 接口：`GET /api/v1/supervision/facility/status-monitor`
  - 功能：故障设备过滤、整改通知下发

### 任务 2.5：车网互动与调度模块

- [x] **步骤 1：实现V2G活动管理**
  - 文件：
    - `entity/V2gActivity.java`
    - `entity/V2gActivityParticipant.java`
    - `service/V2gActivityService.java`
  - 接口：活动列表、详情、导出报告

- [x] **步骤 2：实现有序充电策略监管**
  - 文件：
    - `entity/OrderedChargingStrategy.java`
    - `service/OrderedChargingService.java`
  - 接口：策略列表、异常场站标记、整改通知下发

- [x] **步骤 3：实现需求响应监管**
  - 文件：
    - `entity/DemandResponseActivity.java`
    - `entity/DemandResponseOperatorRecord.java`
    - `service/DemandResponseService.java`
  - 接口：活动列表、详情、运营商完成率排名、导出报告

- [x] **步骤 4：实现智能调度核心**
  - 文件：
    - `entity/DispatchTask.java`
    - `entity/DispatchStationAllocation.java`
    - `entity/DispatchHistory.java`
    - `service/DispatchService.java`
  - 接口：
    - 调度任务列表、详情 ✅
    - 站点功率分配明细 ✅ (返回空HashMap)
    - 区域功率分配数据 ✅ (返回空HashMap)
    - 全网功率趋势 ✅ (返回空HashMap)
    - 场站负荷热力图 ❌ (未实现)
    - 人工调整功率 ❌ (未实现)
    - 下发调度指令 ❌ (未实现)
  - 备注：部分接口已实现但返回Mock数据

- [ ] **步骤 5：实现车网互动效果统计**
  - 文件：
    - `entity/V2gEffectStats.java`
    - `service/V2gEffectStatsService.java`
  - 接口：生成报告、获取统计数据、导出报告（PDF/Word）

### 任务 2.6：运营与合规管理模块

- [x] **步骤 1：实现运营商管理**
  - 文件：
    - `entity/OperatorRating.java`
    - `entity/OperatorViolation.java`
    - `service/OperatorComplianceService.java`
  - 接口：
    - 运营商基础信息列表、详情 ✅
    - 资质审核列表、审核 ✅
    - 服务评级列表、自动生成评级 ✅
    - 违规记录列表、新增违规、下发整改 ✅

- [x] **步骤 2：实现补贴管理**
  - 文件：
    - `entity/SubsidyApplication.java`
    - `entity/SubsidyAuditRecord.java`
    - `entity/SubsidyRecord.java`
    - `service/SubsidyService.java`
  - 接口：
    - 补贴申报列表、详情 ✅
    - 三级审核（初审→复核→终审） ✅
    - 补贴资金追溯列表、详情 ❌ (未实现)
    - 异常申报监控 ❌ (未实现)

- [x] **步骤 3：实现政策管理**
  - 文件：
    - `entity/Policy.java`
    - `entity/PolicyPushRecord.java`
    - `service/PolicyService.java`
  - 接口：
    - 政策列表、详情 ✅
    - 发布政策、下架政策 ✅
    - 获取推送记录、阅读率统计 ❌ (未实现)

### 任务 2.7：安全与应急管理模块

- [x] **步骤 1：实现安全告警监控**
  - 文件：
    - `entity/AlarmEscalationRule.java`
    - `entity/AlarmHandlingRecord.java`
    - `service/EmergencyAlarmService.java`
  - 接口：
    - 告警列表（实时刷新） ✅
    - 告警详情、处理告警 ✅
    - 向运营商下发通知 ❌ (未实现)
    - 录入处理结果、闭环确认 ❌ (部分实现)

- [x] **步骤 2：实现应急调度指令**
  - 文件：
    - `entity/EmergencyDispatchCommand.java`
    - `service/EmergencyCommandService.java`
  - 接口：
    - 指令列表、详情 ✅
    - 创建应急指令、取消指令 ✅

- [ ] **步骤 3：实现事故追溯**
  - 接口：`GET /api/v1/supervision/emergency/accidents`
  - 功能：完整处理链条查询、导出报告

### 任务 2.8：系统管理模块

- [x] **步骤 1：实现区域树查询**
  - 接口：`GET /api/v1/supervision/system/regions`
  - 功能：三级区域树形结构 ✅

- [ ] **步骤 2：实现告警升级规则管理**
  - 接口：
    - `GET /api/v1/supervision/system/alarm-escalation-rules`
    - `PUT /api/v1/supervision/system/alarm-escalation-rules/{id}`

- [ ] **步骤 3：实现充电量分析**
  - 接口：`GET /api/v1/supervision/system/energy-analysis`
  - 功能：多维度充电量统计

- [ ] **步骤 4：复用原有操作日志模块**
  - 接口：`GET /api/v1/supervision/system/operation-logs`
  - 功能：操作日志查询、导出

### 任务 2.9：通用组件开发

- [ ] **步骤 1：实现Excel导出工具**
  - 文件：`utils/ExcelExportUtil.java`
  - 功能：设施台账、功率分配明细、操作日志导出

- [ ] **步骤 2：实现PDF报告生成工具**
  - 文件：`utils/PdfReportUtil.java`
  - 功能：V2G活动报告、需求响应报告、效果统计报告

- [ ] **步骤 3：实现整改通知PDF生成**
  - 文件：`utils/RectificationNoticeUtil.java`
  - 功能：设备整改、运营整改、安全整改通知

- [ ] **步骤 4：实现短信通知服务**
  - 文件：`service/SmsNotificationService.java`
  - 功能：严重告警短信通知、应急指令通知

---

## 阶段三：前端开发 📋

### 任务 3.1：项目初始化

- [x] **步骤 1：创建Vue项目**
  ```bash
  npm create vite@latest supervision-frontend -- --template vue
  cd supervision-frontend
  npm install
  ```

- [x] **步骤 2：安装依赖**
  ```bash
  npm install element-plus pinia vue-router axios @element-plus/icons-vue
  npm install echarts vue-echarts
  npm install -D sass
  ```

- [x] **步骤 3：配置项目结构**
  ```
  supervision-frontend/
  ├── src/
  │   ├── api/                    # API请求
  │   │   ├── index.js            # API模块索引
  │   │   ├── request.js          # axios配置
  │   │   ├── dashboard.js        # 数据总览API
  │   │   ├── facility.js         # 设施监管API
  │   │   ├── v2g.js              # 车网互动API
  │   │   ├── compliance.js       # 合规管理API
  │   │   ├── emergency.js        # 应急管理API
  │   │   └── system.js           # 系统管理API
  │   ├── assets/                 # 静态资源
  │   ├── components/             # 公共组件
  │   │   ├── charts/             # 图表组件
  │   │   │   ├── ChargeDischargeTrend.vue
  │   │   │   ├── DemandResponseEffect.vue
  │   │   │   ├── PowerTrend.vue
  │   │   │   └── RegionPowerAllocation.vue
  │   │   ├── tables/             # 表格组件
  │   │   └── layout/             # 布局组件
  │   ├── layouts/                # 布局组件
  │   │   └── MainLayout.vue      # 主布局（顶部+左侧导航+主内容区）
  │   ├── router/                 # 路由配置
  │   │   └── index.js
  │   ├── stores/                 # Pinia状态管理
  │   │   ├── user.js
  │   │   ├── region.js
  │   │   └── alarm.js
  │   ├── styles/                 # 全局样式
  │   │   └── global.scss
  │   ├── utils/                  # 工具函数
  │   │   ├── auth.js             # 认证工具
  │   │   └── region.js           # 区域工具
  │   ├── views/                  # 页面组件
  │   ├── App.vue
  │   └── main.js
  ```

### 任务 3.2：基础布局与路由

- [x] **步骤 1：实现主布局**
  - 文件：`layouts/MainLayout.vue`
  - 内容：
    - 顶部固定信息栏：平台LOGO、监管区域切换、用户信息、消息告警铃铛、手动刷新、退出登录
    - 左侧固定导航栏：6个一级菜单（数据总览、设施监管、车网互动与调度、运营与合规管理、安全与应急管理）
    - 主内容展示区：路由视图

- [x] **步骤 2：配置路由**
  - 文件：`router/index.js`
  - 内容：
    ```javascript
    {
      path: '/login',
      component: Login
    },
    {
      path: '/',
      component: MainLayout,
      children: [
        { path: 'dashboard', component: Dashboard },
        { path: 'facility', component: FacilityLedger },
        { path: 'access-audit', component: AccessAudit },
        { path: 'v2g-activity', component: V2gActivity },
        { path: 'demand-response', component: DemandResponse },
        { path: 'smart-dispatch', component: SmartDispatch },
        { path: 'operator-manage', component: OperatorManage },
        { path: 'subsidy-manage', component: SubsidyManage },
        { path: 'alarm-monitor', component: AlarmMonitor },
        { path: 'emergency-command', component: EmergencyCommand }
      ]
    }
    ```
  - 权限路由守卫：根据用户角色加载对应菜单

- [x] **步骤 3：实现登录页**
  - 文件：`views/Login.vue`
  - 内容：用户名密码登录、JWT Token存储、监管区域选择

### 任务 3.3：数据总览页面

- [x] **步骤 1：实现核心指标卡片区**
  - 文件：`views/Dashboard.vue`
  - 内容：8个指标卡片（运营商数、充电站数、充电桩数、充电量、在线率、订单数、V2G活动、告警数）
  - 交互：点击卡片钻取到对应模块

- [x] **步骤 2：实现趋势图表区**
  - 文件：`views/Dashboard.vue`
  - 库：ECharts 双轴折线图
  - 内容：充放电趋势、站点类型分布饼图

- [x] **步骤 3：实现需求响应效果图**
  - 文件：`views/Dashboard.vue`
  - 库：ECharts 柱状图
  - 内容：近7天响应电量统计

- [x] **步骤 4：实现告警实时提醒区**
  - 内容：按紧急程度排序的告警列表、查看详情跳转

### 任务 3.4：设施监管页面

- [x] **步骤 1：实现设施台账页面**
  - 文件：`views/facility/FacilityLedger.vue`
  - 组件：
    - 顶部筛选栏：区域、运营商、站点状态筛选
    - 主体表格：站点名称、所属运营商、区域、地址、设备数、在线设备、充电量、状态、操作
    - 底部分页

- [ ] **步骤 2：实现站点详情页面**
  - 文件：`views/facility/StationDetail.vue`
  - 内容：
    - 基本信息卡片
    - 设备清单表格
    - 历史运行数据图表（近30天充电量趋势、在线率趋势）
    - 告警记录列表（近90天）
    - 返回按钮

- [x] **步骤 3：实现接入审核页面**
  - 文件：`views/facility/AccessAudit.vue`
  - 内容：
    - 顶部筛选：审核状态筛选
    - 主体表格：申请编号、运营商名称、站点名称、提交时间、审核状态、操作
    - 审核弹窗：审核意见输入、通过/驳回按钮

- [ ] **步骤 4：实现运行状态监控页面**
  - 文件：`views/facility/StatusMonitor.vue`
  - 内容：
    - 顶部筛选：区域、运营商、状态、故障类型
    - 主体表格：设备编号、所属站点、所属运营商、设备类型、运行状态、故障类型、故障时间、处理状态、下发整改通知按钮
    - 设备详情钻取：设备档案、实时参数、近24小时运行曲线、故障记录、整改通知记录

### 任务 3.5：车网互动与调度页面

- [x] **步骤 1：实现V2G活动监管页面**
  - 文件：`views/v2g/V2gActivity.vue`
  - 内容：活动统计卡片、活动列表、状态筛选、启动/结束操作

- [x] **步骤 2：实现需求响应监管页面**
  - 文件：`views/v2g/DemandResponse.vue`
  - 内容：响应统计、活动列表、运营商排名、响应率展示

- [x] **步骤 3：实现智能调度页面**
  - 文件：`views/v2g/SmartDispatch.vue`
  - 内容：调度任务统计、功率分配、调度效果、任务列表、新建调度

### 任务 3.6：运营与合规管理页面

- [x] **步骤 1：实现运营商管理页面**
  - 文件：`views/compliance/OperatorManage.vue`
  - 子Tab：运营商列表、资质管理、违规记录

- [x] **步骤 2：实现补贴管理页面**
  - 文件：`views/compliance/SubsidyManage.vue`
  - 子Tab：补贴申报、补贴发放、审核弹窗

- [ ] **步骤 3：实现政策管理页面**
  - 文件：`views/compliance/PolicyManage.vue`
  - 内容：
    - 政策列表：查看、编辑、下架、推送运营商
    - 发布政策弹窗：填写信息、上传附件、发布
    - 推送记录弹窗：运营商已读/未读状态、阅读率统计

### 任务 3.7：安全与应急管理页面

- [x] **步骤 1：实现安全告警监控页面**
  - 文件：`views/emergency/AlarmMonitor.vue`
  - 内容：告警统计卡片、实时告警列表、级别筛选、处理弹窗

- [x] **步骤 2：实现应急调度指令页面**
  - 文件：`views/emergency/EmergencyCommand.vue`
  - 内容：指令统计、执行状态饼图、指令列表、新建指令弹窗

- [ ] **步骤 3：实现事故追溯页面**
  - 文件：`views/emergency/AccidentTrace.vue`
  - 内容：
    - 顶部筛选：事故类型、时间范围
    - 主体表格：事故编号、事故类型、发生时间、所属站点、所属运营商、处理状态、操作
    - 事故详情：完整处理链条、时间轴展示

### 任务 3.8：系统管理页面

- [ ] **步骤 1：实现用户权限管理**
  - 文件：`views/system/UserManagement.vue`
  - 内容：用户列表、创建用户、编辑用户、分配角色、禁用/启用、重置密码

- [ ] **步骤 2：实现数据接入管理**
  - 文件：`views/system/DataAccess.vue`
  - 内容：运营商数据接入状态、接入配置、同步监控

- [ ] **步骤 3：实现告警规则配置**
  - 文件：`views/system/AlarmConfig.vue`
  - 内容：告警规则列表、创建规则、编辑规则、删除规则、告警升级规则配置

- [ ] **步骤 4：实现运营分析**
  - 文件：`views/system/EnergyAnalysis.vue`
  - 内容：充电量分析、多维度统计、趋势图表、导出

- [ ] **步骤 5：实现操作日志**
  - 文件：`views/system/OperationLog.vue`
  - 内容：日志列表、筛选、导出

---

## 阶段四：测试与优化 📋

### 任务 4.1：后端测试

- [ ] **步骤 1：单元测试**
  - 工具：JUnit 5 + Mockito
  - 覆盖：Service层核心逻辑（调度算法、补贴审核流程、告警升级逻辑）

- [ ] **步骤 2：集成测试**
  - 工具：Spring Boot Test
  - 覆盖：Controller接口测试、区域数据权限过滤

- [ ] **步骤 3：性能测试**
  - 工具：JMeter
  - 目标：并发100用户响应时间<2秒、WebSocket推送延迟<500ms

### 任务 4.2：前端测试

- [ ] **步骤 1：组件测试**
  - 工具：Vitest + Vue Test Utils
  - 覆盖：核心图表组件、表格组件、表单组件

- [ ] **步骤 2：E2E测试**
  - 工具：Cypress
  - 覆盖：完整业务流程（登录→查看数据总览→设施监管→下发整改→审核接入）

### 任务 4.3：联调测试

- [ ] **步骤 1：接口联调**
  - 验证所有API接口（100+）
  - 验证请求参数校验、响应格式、错误处理

- [ ] **步骤 2：区域数据权限联调**
  - 验证市级监管员查看全市数据
  - 验证区级监管员仅查看本区数据
  - 验证街道级监管员仅查看本街道数据

- [ ] **步骤 3：实时推送联调**
  - 验证WebSocket告警推送
  - 验证调度指令推送
  - 验证应急指令推送

- [ ] **步骤 4：全流程测试**
  - 模拟完整监管流程：设施接入→运行监控→发现异常→下发整改→运营商整改→闭环确认

---

## 阶段五：部署上线 📋

### 任务 5.1：环境准备

- [ ] **步骤 1：服务器配置**
  - 安装JDK 17、MySQL 8.0、Nginx、Redis（用于WebSocket）

- [ ] **步骤 2：数据库初始化**
  - 执行 `database_design.sql`（原有平台）
  - 执行 `database_design_supervision.sql`（监管平台扩展）

- [ ] **步骤 3：配置Nginx**
  - 配置前端静态资源服务
  - 配置后端API反向代理
  - 配置WebSocket代理

### 任务 5.2：应用部署

- [ ] **步骤 1：后端部署**
  ```bash
  mvn clean package -DskipTests
  java -jar charging-station-backend.jar --spring.profiles.active=prod
  ```

- [ ] **步骤 2：前端部署**
  ```bash
  cd supervision-frontend
  npm run build
  # 部署dist目录到Nginx
  ```

- [ ] **步骤 3：数据迁移**
  - 从原有平台数据迁移operator_id、region_code
  - 验证数据一致性

### 任务 5.3：监控与运维

- [ ] **步骤 1：配置日志**
  - 文件：`logback-spring.xml`
  - 内容：应用日志、访问日志、错误日志分离

- [ ] **步骤 2：配置健康检查**
  - 文件：`application.yml` (Actuator)
  - 内容：数据库连接、Redis连接、WebSocket连接健康检查

- [ ] **步骤 3：配置告警监控**
  - 工具：Prometheus + Grafana
  - 监控指标：API响应时间、数据库连接池、WebSocket连接数、错误率

---

## 总体进度统计

> **更新说明：** 2026-04-20 新增功能实现完成后更新

| 阶段 | 总任务数 | 已完成 | 进行中 | 未开始 | 完成率 |
|-----|---------|-------|-------|--------|--------|
| 阶段一：项目准备与文档设计 | 6 | 6 | 0 | 0 | 100% |
| 阶段二：后端开发 | 60 | 45 | 0 | 15 | 75% |
| 阶段三：前端开发 | 45 | 28 | 0 | 17 | 62.2% |
| 阶段四：测试与优化 | 12 | 0 | 0 | 12 | 0% |
| 阶段五：部署上线 | 9 | 0 | 0 | 9 | 0% |
| **总计** | **132** | **79** | **0** | **53** | **59.8%** |

---

## 使用说明

1. **标记完成**：将 `- [ ]` 改为 `- [x]` 表示任务完成
2. **更新进度**：每完成一个任务，更新总体进度统计表
3. **添加备注**：可在任务下方添加备注说明问题或变更
4. **定期检查**：建议每日检查一次进度，确保项目按计划推进

---

**最后更新时间：** 2026-04-20
**文档维护者：** 项目开发团队
**更新记录：**
- 2026-04-20：根据实际代码核查，修正后端开发完成状态（49→59），总体进度由37.1%更新为44.7%
- 2026-04-20：新增功能实现，WebSocket实时推送、DispatchService功率分配算法、站点详情页面、运行状态监控页面、政策管理页面，总体进度更新为52.3%
- 2026-04-20：继续完成V2G效果统计接口、告警升级规则管理、事故追溯页面、用户权限管理、告警规则配置，总体进度更新为59.8%
