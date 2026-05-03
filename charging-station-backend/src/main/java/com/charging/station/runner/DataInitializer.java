package com.charging.station.runner;

import com.charging.station.entity.OperationLog;
import com.charging.station.mapper.OperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========== 开始初始化数据 ==========");
        
        // 检查是否需要初始化操作日志数据
        try {
            Long logCount = operationLogMapper.selectCount(null);
            if (logCount == null || logCount == 0) {
                initOperationLogs();
                System.out.println("✓ 操作日志数据初始化完成");
            } else {
                System.out.println("✓ 操作日志数据已存在，跳过初始化 (共 " + logCount + " 条)");
            }
        } catch (Exception e) {
            System.err.println("✗ 操作日志数据初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("========== 数据初始化结束 ==========");
    }
    
    private void initOperationLogs() {
        List<OperationLog> logs = Arrays.asList(
            createLog(1L, "admin", "系统", "登录", "管理员登录系统", "POST", "/api/auth/login", "192.168.1.100", 150, LocalDateTime.of(2025, 4, 23, 9, 30, 15)),
            createLog(1L, "admin", "设备管理", "重启设备", "重启 DC-001 设备", "POST", "/api/devices/1/restart", "192.168.1.100", 2300, LocalDateTime.of(2025, 4, 23, 10, 15, 22)),
            createLog(2L, "operator1", "订单管理", "订单退款", "退款处理 订单#20260423001", "POST", "/api/orders/1/refund", "192.168.1.101", 800, LocalDateTime.of(2025, 4, 23, 11, 20, 5)),
            createLog(1L, "admin", "用户管理", "新增用户", "新增用户 operator2", "POST", "/api/users", "192.168.1.100", 450, LocalDateTime.of(2025, 4, 23, 13, 45, 30)),
            createLog(2L, "operator1", "故障处理", "处理告警", "处理设备 AC-003 通信中断告警", "PUT", "/api/faults/tickets/3/complete", "192.168.1.101", 1200, LocalDateTime.of(2025, 4, 23, 14, 10, 18)),
            createLog(3L, "viewer1", "数据查看", "查看数据", "查看场站总览数据", "GET", "/api/dashboard/overview", "192.168.1.102", 320, LocalDateTime.of(2025, 4, 23, 15, 30, 45)),
            createLog(1L, "admin", "系统配置", "修改配置", "修改场站配置信息", "PUT", "/api/station/config", "192.168.1.100", 280, LocalDateTime.of(2025, 4, 23, 16, 20, 33)),
            createLog(2L, "operator1", "订单管理", "查看订单", "查看订单列表", "GET", "/api/orders", "192.168.1.101", 560, LocalDateTime.of(2025, 4, 22, 9, 15, 10)),
            createLog(1L, "admin", "设备管理", "新增设备", "新增设备 DC-005", "POST", "/api/devices", "192.168.1.100", 890, LocalDateTime.of(2025, 4, 22, 11, 30, 25)),
            createLog(4L, "operator2", "故障申报", "提交工单", "提交故障工单 FT20260422001", "POST", "/api/faults/tickets", "192.168.1.103", 670, LocalDateTime.of(2025, 4, 22, 14, 45, 50)),
            createLog(3L, "viewer1", "报表管理", "导出报表", "导出收益报表", "GET", "/api/reports/revenue/export", "192.168.1.102", 2400, LocalDateTime.of(2025, 4, 22, 16, 0, 15)),
            createLog(1L, "admin", "告警配置", "修改规则", "修改温度过高告警规则", "PUT", "/api/alarm/rules/1", "192.168.1.100", 340, LocalDateTime.of(2025, 4, 21, 10, 30, 0)),
            createLog(2L, "operator1", "设备控制", "启动充电", "远程启动 DC-002 充电", "POST", "/api/devices/2/start", "192.168.1.101", 1800, LocalDateTime.of(2025, 4, 21, 13, 20, 40)),
            createLog(1L, "admin", "用户管理", "修改用户", "修改用户 viewer1 信息", "PUT", "/api/users/3", "192.168.1.100", 290, LocalDateTime.of(2025, 4, 21, 15, 10, 22)),
            createLog(4L, "operator2", "报表查看", "查看报表", "查看运营分析报表", "GET", "/api/reports/analysis", "192.168.1.103", 780, LocalDateTime.of(2025, 4, 21, 17, 45, 18))
        );
        
        for (OperationLog log : logs) {
            operationLogMapper.insert(log);
        }
    }
    
    private OperationLog createLog(Long userId, String username, String module, String operation, 
                                   String description, String requestMethod, String requestUrl, 
                                   String ipAddress, Integer duration, LocalDateTime createTime) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setModule(module);
        log.setOperation(operation);
        log.setDescription(description);
        log.setRequestMethod(requestMethod);
        log.setRequestUrl(requestUrl);
        log.setIpAddress(ipAddress);
        log.setStatus(1);
        log.setDuration(duration);
        log.setCreateTime(createTime);
        return log;
    }
}
