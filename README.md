# Azure App Configuration Demo

A Spring Boot application demonstrating dynamic configuration management using Azure App Configuration service.

## Features

- REST API endpoints to retrieve dynamic configuration values
- Automatic refresh of configuration without application restart
- Integration with Spring Cloud Azure
- Health check endpoints
- Support for feature flags

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Azure subscription
- Azure App Configuration resource

## Azure App Configuration Setup

### 1. Create Azure App Configuration Resource

```bash
# Create resource group (if not exists)
az group create --name myResourceGroup --location "East US"

# Create App Configuration store
az appconfig create --resource-group myResourceGroup --name myAppConfigStore --location "East US" --sku free
```

### 2. Add Configuration Keys

Add the following key-value pairs to your Azure App Configuration:

| Key | Value | Content Type |
|-----|-------|--------------|
| `app.message` | `Hello from Azure App Configuration!` | text/plain |
| `app.feature.enabled` | `true` | text/plain |

You can add these through Azure portal or using Azure CLI:

```bash
# Add configuration values
az appconfig kv set --name myAppConfigStore --key "app.message" --value "Hello from Azure App Configuration!"
az appconfig kv set --name myAppConfigStore --key "app.feature.enabled" --value "true"
```

### 3. Get Connection Details

Get your App Configuration endpoint:

```bash
az appconfig show --name myAppConfigStore --resource-group myResourceGroup --query endpoint --output tsv
```

## Configuration

### Option 1: Using Environment Variables

Set the following environment variables:

```bash
export AZURE_APP_CONFIG_ENDPOINT="https://your-app-config-name.azconfig.io"
```

### Option 2: Using Connection String

Alternatively, you can use a connection string:

```bash
# Get connection string
az appconfig credential list --name myAppConfigStore --resource-group myResourceGroup

export AZURE_APP_CONFIG_CONNECTION_STRING="your-connection-string-here"
```

Then update `application.properties` to use connection string instead of endpoint:

```properties
spring.cloud.azure.appconfiguration.stores[0].connection-string=${AZURE_APP_CONFIG_CONNECTION_STRING}
```

### Option 3: Using Azure Managed Identity

For production environments, configure managed identity:

```properties
spring.cloud.azure.appconfiguration.stores[0].endpoint=${AZURE_APP_CONFIG_ENDPOINT}
spring.cloud.azure.credential.managed-identity-enabled=true
```

## Running the Application

1. **Build the application:**
   ```bash
   mvn clean compile
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Or run with custom profile:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

## API Endpoints

### Get Configuration Message

```bash
GET http://localhost:8080/api/message
```

Response:
```json
{
  "message": "Hello from Azure App Configuration!",
  "featureEnabled": true,
  "timestamp": "2023-12-07T10:30:45.123",
  "source": "Azure App Configuration"
}
```

### Health Check

```bash
GET http://localhost:8080/api/health
```

### Actuator Endpoints

- Health: `GET http://localhost:8080/actuator/health`
- Refresh: `POST http://localhost:8080/actuator/refresh`

## Dynamic Configuration Updates

The application automatically refreshes configuration every 30 seconds when the monitored keys change in Azure App Configuration.

To manually trigger a refresh:

```bash
curl -X POST http://localhost:8080/actuator/refresh
```

## Testing Dynamic Updates

1. **Start the application**
2. **Call the API:** `curl http://localhost:8080/api/message`
3. **Update the value in Azure App Configuration:**
   ```bash
   az appconfig kv set --name myAppConfigStore --key "app.message" --value "Updated message from Azure!"
   ```
4. **Wait 30 seconds or trigger manual refresh**
5. **Call the API again** to see the updated value

## Environment-Specific Configuration

Use labels in Azure App Configuration for environment-specific values:

```bash
# Add environment-specific values
az appconfig kv set --name myAppConfigStore --key "app.message" --value "Development message" --label "dev"
az appconfig kv set --name myAppConfigStore --key "app.message" --value "Production message" --label "prod"

# Create keys with the default /application/ prefix
az appconfig kv set --name app-config-test-resource1 --key "/application/app.message" --value "Hello from Azure App Configuration!" --yes

az appconfig kv set --name app-config-test-resource1 --key "/application/app.feature.enabled" --value "true" --yes

az appconfig kv set --name app-config-test-resource1 --key "/application/app.version" --value "2.0.0-azure" --yes

az appconfig kv set --name app-config-test-resource1 --key "/application/app.environment" --value "azure" --yes

# Keep the sentinel key for refresh triggers
az appconfig kv set --name app-config-test-resource1 --key "sentinel" --value "1" --yes
```

Then set the environment variable:
```bash
export AZURE_APP_CONFIG_LABEL="dev"
```

## Troubleshooting

### Common Issues

1. **Connection Issues:**
   - Verify your endpoint URL is correct
   - Check network connectivity to Azure
   - Ensure proper authentication is configured

2. **Authentication Issues:**
   - Verify connection string or managed identity setup
   - Check Azure App Configuration access policies

3. **Configuration Not Refreshing:**
   - Verify monitoring is enabled
   - Check that the trigger key matches your configuration
   - Review application logs for refresh events

### Enable Debug Logging

Add to `application.properties`:

```properties
logging.level.com.azure.spring.cloud.appconfiguration=DEBUG
logging.level.org.springframework.cloud.context.refresh=DEBUG
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/azureappconfigdemo/
│   │   ├── AzureAppConfigDemoApplication.java    # Main application class
│   │   ├── config/
│   │   │   └── AppConfigConfiguration.java       # App Config setup
│   │   └── controller/
│   │       └── ConfigController.java             # REST controller
│   └── resources/
│       └── application.properties                # Configuration file
└── test/ (test files would go here)
```

## Dependencies

- Spring Boot 3.2.0
- Spring Cloud Azure 5.8.0
- Spring Boot Starter Web
- Spring Boot Starter Actuator
- Azure App Configuration libraries

## License

This project is licensed under the MIT License.
