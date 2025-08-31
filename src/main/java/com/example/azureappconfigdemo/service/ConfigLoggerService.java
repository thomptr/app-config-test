package com.example.azureappconfigdemo.service;

import com.example.azureappconfigdemo.config.AppConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@EnableScheduling
public class ConfigLoggerService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoggerService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private final AppConfigProperties appConfigProperties;

    @Autowired
    public ConfigLoggerService(AppConfigProperties appConfigProperties) {
        this.appConfigProperties = appConfigProperties;
    }

    @Scheduled(fixedDelay = 3000) // Log every 3 seconds
    public void logConfigMessage() {
        String timestamp = LocalDateTime.now().format(formatter);
        String currentMessage = appConfigProperties.getMessage();
        String currentVersion = appConfigProperties.getVersion();
        String currentEnvironment = appConfigProperties.getEnvironment();
        
        logger.info("⏰ {} | 📧 Message: \"{}\" | 📦 Version: {} | 🌍 Environment: {}", 
                   timestamp, 
                   currentMessage, 
                   currentVersion, 
                   currentEnvironment);
    }
}
