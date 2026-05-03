package com.charging.station.aspect;

import com.charging.station.config.JwtUtil;
import com.charging.station.entity.OperationLog;
import com.charging.station.service.OperationLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private JwtUtil jwtUtil;

    // 定义切点 - 所有Controller的POST/PUT/DELETE方法
    @Pointcut("execution(* com.charging.station.controller.*Controller.*(..))")
    public void controllerPointcut() {}

    @AfterReturning("controllerPointcut()")
    public void logOperation(JoinPoint joinPoint) {
        try {
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) return;
            
            HttpServletRequest request = attributes.getRequest();
            String method = request.getMethod();
            
            // 只记录 POST/PUT/DELETE 操作
            if (!"POST".equals(method) && !"PUT".equals(method) && !"DELETE".equals(method)) {
                return;
            }
            
            // 获取当前用户
            String username = getCurrentUsername(request);
            Long userId = getCurrentUserId(request);
            
            // 获取方法信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method targetMethod = signature.getMethod();
            
            // 构建操作日志
            OperationLog log = new OperationLog();
            log.setUserId(userId);
            log.setUsername(username);
            log.setModule(getModuleName(joinPoint.getTarget().getClass().getSimpleName()));
            log.setOperation(getOperationName(targetMethod.getName(), method));
            log.setDescription(buildDescription(targetMethod.getName(), joinPoint.getArgs()));
            log.setRequestMethod(method);
            log.setRequestUrl(request.getRequestURI());
            log.setIpAddress(getClientIp(request));
            log.setStatus(1); // 成功
            log.setDuration(0); // 简化处理
            
            // 异步保存日志
            new Thread(() -> operationLogService.addLog(log)).start();
            
        } catch (Exception e) {
            // 日志记录失败不影响主业务
            System.err.println("记录操作日志失败: " + e.getMessage());
        }
    }
    
    private String getCurrentUsername(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                return jwtUtil.extractUsername(token);
            } catch (Exception e) {
                return "anonymous";
            }
        }
        return "anonymous";
    }
    
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                return jwtUtil.extractUserId(token);
            } catch (Exception e) {
                return 0L;
            }
        }
        return 0L;
    }
    
    private String getModuleName(String controllerName) {
        if (controllerName.contains("Device")) return "设备管理";
        if (controllerName.contains("Order")) return "订单管理";
        if (controllerName.contains("User")) return "用户管理";
        if (controllerName.contains("Fault")) return "故障处理";
        if (controllerName.contains("Station")) return "系统配置";
        if (controllerName.contains("Alarm")) return "告警配置";
        if (controllerName.contains("Report")) return "报表管理";
        if (controllerName.contains("Auth")) return "系统";
        return "其他";
    }
    
    private String getOperationName(String methodName, String httpMethod) {
        if (methodName.contains("add") || methodName.contains("create") || methodName.contains("insert")) {
            return httpMethod.equals("POST") ? "新增" : "创建";
        }
        if (methodName.contains("update") || methodName.contains("edit")) {
            return "修改";
        }
        if (methodName.contains("delete") || methodName.contains("remove")) {
            return "删除";
        }
        if (methodName.contains("login")) {
            return "登录";
        }
        if (methodName.contains("logout")) {
            return "退出";
        }
        if (methodName.contains("export")) {
            return "导出";
        }
        if (methodName.contains("restart") || methodName.contains("start") || methodName.contains("stop")) {
            return "设备控制";
        }
        return "操作";
    }
    
    private String buildDescription(String methodName, Object[] args) {
        StringBuilder desc = new StringBuilder();
        desc.append(getOperationName(methodName, "POST")).append("操作");
        if (args != null && args.length > 0 && args[0] != null) {
            desc.append(" - ").append(args[0].toString());
        }
        return desc.toString();
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip.split(",")[0].trim() : "unknown";
    }
}
