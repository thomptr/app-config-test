package com.example.azureappconfigdemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
@ConfigurationProperties(prefix = "app")
@RefreshScope
public class AppConfigProps {
    
    private static final Logger logger = LoggerFactory.getLogger(AppConfigProperties.class);

    private String message;
    
    // private String messageFeatureEnabled;
    
    @PostConstruct
    public void init() {
        logger.info("🔄 AppConfigProps bean CREATED/REFRESHED with values:");
        logCurrentValues();
    }
    
    @PreDestroy
    public void destroy() {
        logger.info("🗑️ AppConfigProps bean DESTROYED (before refresh)");
    }
    
    public void logCurrentValues() {
        logger.info("   📧 message: {}", getMessage());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AppConfigProperties{" +
                "message='" + message + '}';
    }
}

