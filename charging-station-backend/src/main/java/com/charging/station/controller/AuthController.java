package com.charging.station.controller;

import com.charging.station.common.Result;
import com.charging.station.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        Map<String, Object> result = authService.login(username, password);
        return Result.success(result);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @GetMapping("/menus")
    public Result<java.util.List<Map<String, Object>>> getMenus() {
        java.util.List<Map<String, Object>> menus = java.util.List.of(
            createMenu(1L, 0L, "场站总览", "dashboard", "/dashboard"),
            createMenu(2L, 0L, "设备管理", "device", "/device"),
            createMenu(3L, 0L, "充电运营", "charging", "/charging"),
            createMenu(4L, 0L, "收益核算", "finance", "/finance"),
            createMenu(5L, 0L, "运营分析", "analytics", "/analytics"),
            createMenu(6L, 0L, "故障申报", "fault", "/fault"),
            createMenu(7L, 0L, "系统设置", "setting", "/system")
        );
        return Result.success(menus);
    }

    private Map<String, Object> createMenu(Long id, Long parentId, String name, String icon, String path) {
        Map<String, Object> menu = new java.util.HashMap<>();
        menu.put("id", id);
        menu.put("parentId", parentId);
        menu.put("menuName", name);
        menu.put("menuIcon", icon);
        menu.put("menuPath", path);
        return menu;
    }
}
