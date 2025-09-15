package com.example.azureappconfigdemo.config;

import com.example.azureappconfigdemo.service.FeatureFlagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
@RefreshScope
public class AppConfigProperties {
    
    private static final Logger logger = LoggerFactory.getLogger(AppConfigProperties.class);

    @Value("${app.message:Default message}")
    private String baseMessage;
    
    @Value("${app.message.feature-enabled:Feature-enabled message!}")
    private String featureEnabledMessage;
    
    @Autowired
    private FeatureFlagService featureFlagService;
       
    @Value("${app.version:1.0.0}")
    private String version;
    
    @Value("${app.environment:local}")
    private String environment;

    @PostConstruct
    public void init() {
        logger.info("🔄 AppConfigProperties bean CREATED/REFRESHED with values:");
        logCurrentValues();
    }
    
    @PreDestroy
    public void destroy() {
        logger.info("🗑️ AppConfigProperties bean DESTROYED (before refresh)");
    }
    
    public void logCurrentValues() {
        logger.info("   📧 message: {}", getMessage());
        logger.info("   📦 version: {}", version);
        logger.info("   🌍 environment: {}", environment);
        logger.info("   🏳️ test-feature enabled: {}", featureFlagService.isTestFeatureEnabled());
    }

    public String getMessage() {
        // Dynamic message based on feature flag
        boolean isFeatureEnabled = featureFlagService.isTestFeatureEnabled();
        return isFeatureEnabled ? featureEnabledMessage : baseMessage;
    }

    public String getBaseMessage() {
        return baseMessage;
    }

    public void setBaseMessage(String baseMessage) {
        this.baseMessage = baseMessage;
    }
    
    public String getFeatureEnabledMessage() {
        return featureEnabledMessage;
    }

    public void setFeatureEnabledMessage(String featureEnabledMessage) {
        this.featureEnabledMessage = featureEnabledMessage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @Override
    public String toString() {
        return "AppConfigProperties{" +
                "baseMessage='" + baseMessage + '\'' +
                "message='" + getMessage() + '\'' +
                ", version='" + version + '\'' +
                ", environment='" + environment + '\'' +
                ", featureEnabled=" + featureFlagService.isTestFeatureEnabled() +
                '}';
    }
}
