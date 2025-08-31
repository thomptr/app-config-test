package com.example.azureappconfigdemo.controller;

import com.example.azureappconfigdemo.config.AppConfigProperties;
import com.example.azureappconfigdemo.service.FeatureFlagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class RefreshTestController {
    
    private static final Logger logger = LoggerFactory.getLogger(RefreshTestController.class);
    
    private final AppConfigProperties appConfigProperties;
    private final RefreshEndpoint refreshEndpoint;
    private final FeatureFlagService featureFlagService;
    
    @Autowired
    public RefreshTestController(AppConfigProperties appConfigProperties, RefreshEndpoint refreshEndpoint, FeatureFlagService featureFlagService) {
        this.appConfigProperties = appConfigProperties;
        this.refreshEndpoint = refreshEndpoint;
        this.featureFlagService = featureFlagService;
    }

    @GetMapping("/test-feature-enabled")
    public Map<String, Object> isTestFeatureEnabled() {
        logger.info("🏳️ Checking test-feature flag status");
        Map<String, Object> response = new HashMap<>();
        boolean isEnabled = featureFlagService.isTestFeatureEnabled();
        response.put("featureName", "test-feature");
        response.put("enabled", isEnabled);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("source", "Azure App Configuration Feature Flag");
        
        logger.info("🏳️ Feature flag result: {}", response);
        return response;
    }
    
    @GetMapping("/before-refresh")
    public Map<String, Object> getValuesBeforeRefresh() {
        logger.info("🔍 Getting values BEFORE refresh");
        Map<String, Object> response = new HashMap<>();
        response.put("message", appConfigProperties.getMessage());

        response.put("version", appConfigProperties.getVersion());
        response.put("environment", appConfigProperties.getEnvironment());
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("action", "BEFORE_REFRESH");
        
        logger.info("📊 Current values: {}", response);
        return response;
    }
    
    @PostMapping("/manual-refresh")
    public Map<String, Object> manualRefresh() {
        logger.info("🔄 MANUAL REFRESH TRIGGERED");
        
        // Get values before refresh
        logger.info("📊 Getting values BEFORE refresh...");
        Map<String, Object> beforeValues = new HashMap<>();
        beforeValues.put("message", appConfigProperties.getMessage());

        beforeValues.put("version", appConfigProperties.getVersion());
        beforeValues.put("environment", appConfigProperties.getEnvironment());
        
        // Trigger refresh
        logger.info("🔄 Calling refresh endpoint...");
        Collection<String> refreshedKeys = refreshEndpoint.refresh();
        logger.info("🔑 Refreshed keys: {}", refreshedKeys);
        
        // IMPORTANT: Access the bean to trigger recreation after refresh
        logger.info("🔍 Accessing bean to trigger recreation...");
        appConfigProperties.logCurrentValues();
        
        // Get values after refresh (this will use the new bean instance)
        logger.info("📊 Getting values AFTER refresh...");
        Map<String, Object> afterValues = new HashMap<>();
        afterValues.put("message", appConfigProperties.getMessage());

        afterValues.put("version", appConfigProperties.getVersion());
        afterValues.put("environment", appConfigProperties.getEnvironment());
        
        Map<String, Object> response = new HashMap<>();
        response.put("refreshedKeys", refreshedKeys);
        response.put("valuesChanged", !beforeValues.equals(afterValues));
        response.put("beforeRefresh", beforeValues);
        response.put("afterRefresh", afterValues);
        response.put("timestamp", LocalDateTime.now().toString());
        
        logger.info("📈 Refresh result: {}", response);
        return response;
    }
    
    @GetMapping("/after-refresh")
    public Map<String, Object> getValuesAfterRefresh() {
        logger.info("🔍 Getting values AFTER refresh");
        Map<String, Object> response = new HashMap<>();
        response.put("message", appConfigProperties.getMessage());

        response.put("version", appConfigProperties.getVersion());
        response.put("environment", appConfigProperties.getEnvironment());
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("action", "AFTER_REFRESH");
        
        logger.info("📊 Current values: {}", response);
        return response;
    }
}
