package com.example.azureappconfigdemo.service;

import com.azure.spring.cloud.feature.management.FeatureManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
@RefreshScope
public class FeatureFlagService {

    private static final Logger logger = LoggerFactory.getLogger(FeatureFlagService.class);
    
    private final FeatureManager featureManager;

    @Autowired
    public FeatureFlagService(FeatureManager featureManager) {
        this.featureManager = featureManager;
    }

    @PostConstruct
    public void init() {
        logger.info("🏳️ FeatureFlagService bean CREATED/REFRESHED");
    }
    
    @PreDestroy
    public void destroy() {
        logger.info("🗑️ FeatureFlagService bean DESTROYED (before refresh)");
    }

    public boolean isTestFeatureEnabled() {
        try {
            // Try synchronous method first
            boolean isEnabled = featureManager.isEnabled("test-feature");
            logger.info("🏳️ Feature flag 'test-feature' is: {} (sync method)", isEnabled ? "ENABLED" : "DISABLED");
            return isEnabled;
        } catch (Exception e) {
            logger.warn("⚠️ Error checking feature flag with sync method: {}", e.getMessage());
            try {
                // Fallback to async method
                boolean isEnabled = featureManager.isEnabledAsync("test-feature").block();
                logger.info("🏳️ Feature flag 'test-feature' is: {} (async method)", isEnabled ? "ENABLED" : "DISABLED");
                return isEnabled;
            } catch (Exception asyncE) {
                logger.error("❌ Error checking feature flag: {}", asyncE.getMessage());
                return false;
            }
        }
    }
}
