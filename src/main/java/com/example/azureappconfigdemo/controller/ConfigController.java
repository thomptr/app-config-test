package com.example.azureappconfigdemo.controller;

import com.example.azureappconfigdemo.config.AppConfigProperties;
import com.example.azureappconfigdemo.service.FeatureFlagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConfigController {

    private final AppConfigProperties appConfigProperties;
    private final FeatureFlagService featureFlagService;

    @Autowired
    public ConfigController(AppConfigProperties appConfigProperties, FeatureFlagService featureFlagService) {
        this.appConfigProperties = appConfigProperties;
        this.featureFlagService = featureFlagService;
    }

    @GetMapping("/message")
    public Map<String, Object> getMessage() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", appConfigProperties.getMessage());
        response.put("version", appConfigProperties.getVersion());
        response.put("environment", appConfigProperties.getEnvironment());
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("source", "Azure App Configuration (RefreshScope Bean)");
        
        return response;
    }

    @GetMapping("/test-feature")
    public Map<String, Object> isTestFeatureEnabled() {
        Map<String, Object> response = new HashMap<>();
        boolean isEnabled = featureFlagService.isTestFeatureEnabled();
        response.put("featureName", "test-feature");
        response.put("enabled", isEnabled);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("source", "Azure App Configuration Feature Flag");
        
        return response;
    }

    @GetMapping("/config")
    public AppConfigProperties getConfig() {
        return appConfigProperties;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "Azure App Config Demo");
        status.put("timestamp", LocalDateTime.now().toString());
        
        return status;
    }
}
