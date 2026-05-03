package com.charging.station.controller;

import com.charging.station.common.Result;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    
    @GetMapping("/generate-password")
    public Result<String> generatePassword(@RequestParam String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode(rawPassword);
        return Result.success(encoded);
    }
    
    @GetMapping("/verify-password")
    public Result<Boolean> verifyPassword(@RequestParam String rawPassword, @RequestParam String encodedPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        return Result.success(matches);
    }
}
