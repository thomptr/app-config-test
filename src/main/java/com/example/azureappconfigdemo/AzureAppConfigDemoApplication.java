package com.example.azureappconfigdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude = {
    com.azure.spring.cloud.appconfiguration.config.AppConfigurationAutoConfiguration.class
})
@EnableScheduling
public class AzureAppConfigDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AzureAppConfigDemoApplication.class, args);
    }
}
