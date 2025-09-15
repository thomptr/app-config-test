package com.example.azureappconfigdemo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.azureappconfigdemo.config.AppConfigProperties;
import com.example.azureappconfigdemo.config.AppConfigProps;

import java.util.Collection;

@Service
public class AutoRefreshService {

    private static final Logger logger = LoggerFactory.getLogger(AutoRefreshService.class);
    
    private final RefreshEndpoint refreshEndpoint;
    private final AppConfigProperties appConfigProperties;
    private final AppConfigProps appConfigProps;

    @Autowired
    public AutoRefreshService(RefreshEndpoint refreshEndpoint, AppConfigProperties appConfigProperties, AppConfigProps appConfigProps) {
        this.refreshEndpoint = refreshEndpoint;
        this.appConfigProperties = appConfigProperties;
        this.appConfigProps = appConfigProps;
    }

    @Scheduled(fixedDelay = 5000) // Check every 5 seconds
    public void autoRefresh() {
        try {
            logger.debug("🔍 Checking for configuration changes...");
            Collection<String> refreshedKeys = refreshEndpoint.refresh();

            logger.info("🔍 AppConfigProperties : {}", appConfigProperties);
            logger.info("🔍 AppConfigProps : {}", appConfigProps);
            
            if (!refreshedKeys.isEmpty()) {
                logger.info("🔄 Auto-refresh detected changes in keys: {}", refreshedKeys);
            } else {
                logger.debug("✅ No configuration changes detected");
            }
        } catch (Exception e) {
            logger.warn("⚠️ Error during auto-refresh: {}", e.getMessage());
        }
    }
}
