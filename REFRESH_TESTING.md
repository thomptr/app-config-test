# Azure App Configuration Refresh Testing Guide

This guide shows how to test and verify that Azure App Configuration dynamic refresh is working properly with `@RefreshScope` beans.

## 🔧 Setup

The application has been configured with:
- Enhanced logging for refresh events
- Debug endpoints to test refresh functionality
- `@PostConstruct` and `@PreDestroy` methods to track bean lifecycle

## 📊 Test Endpoints

### Debug Endpoints
- **`GET /test/before-refresh`** - Shows current configuration values
- **`POST /test/manual-refresh`** - Triggers manual refresh and shows before/after comparison
- **`GET /test/after-refresh`** - Shows values after refresh
- **`GET /debug/properties`** - Shows raw property availability in Spring Environment

### Standard Endpoints
- **`GET /api/message`** - Main application endpoint showing current config
- **`POST /actuator/refresh`** - Standard Spring Boot refresh endpoint

## 🧪 Testing Commands

### 1. Start the Application
```bash
# Load environment variables and start the app
export $(grep -v '^#' .env | xargs) && mvn spring-boot:run
```

### 2. Check Initial Values
```bash
# See what values are currently loaded
curl -s http://localhost:8084/test/before-refresh | jq .
```

### 3. Update Configuration in Azure App Configuration
```bash
# Update the message value
az appconfig kv set --name app-config-test-resource1 --key "app.message" --value "Updated from Azure!" --yes

# Update other values if desired
az appconfig kv set --name app-config-test-resource1 --key "app.version" --value "3.0.0-azure" --yes
az appconfig kv set --name app-config-test-resource1 --key "app.feature.enabled" --value "true" --yes
```

### 4. Trigger Refresh (Required for Changes to Take Effect)
```bash
# Update the sentinel key to trigger automatic refresh
az appconfig kv set --name app-config-test-resource1 --key "sentinel" --value "2" --yes

# OR trigger manual refresh via endpoint
curl -X POST http://localhost:8084/test/manual-refresh | jq .
```

### 5. Verify Changes
```bash
# Check if values have updated
curl -s http://localhost:8084/test/after-refresh | jq .

# Check main application endpoint
curl -s http://localhost:8084/api/message | jq .
```

## 🔍 What to Look For

### In Application Logs
When refresh works correctly, you should see:
```
🔄 MANUAL REFRESH TRIGGERED
📊 Getting values BEFORE refresh...
🔄 Calling refresh endpoint...
🔑 Refreshed keys: [app.message, app.version, app.feature.enabled]
🔍 Accessing bean to trigger recreation...
🗑️ AppConfigProperties bean DESTROYED (before refresh)
🔄 AppConfigProperties bean CREATED/REFRESHED with values:
   📧 message: Updated from Azure!
   🚀 featureEnabled: true
   📦 version: 3.0.0-azure
   🌍 environment: azure
📊 Getting values AFTER refresh...
```

**Important**: `@RefreshScope` beans are **lazy** - they're only recreated when accessed after a refresh, not immediately when `/actuator/refresh` is called.

### In API Response
The `/test/manual-refresh` endpoint will show:
```json
{
  "refreshedKeys": ["app.message", "app.version", "app.feature.enabled"],
  "valuesChanged": true,
  "beforeRefresh": {
    "message": "Default message",
    "featureEnabled": false,
    "version": "1.0.0",
    "environment": "local"
  },
  "afterRefresh": {
    "message": "Updated from Azure!",
    "featureEnabled": true,
    "version": "3.0.0-azure",
    "environment": "azure"
  }
}
```

## 🚨 Troubleshooting

### If Values Don't Update:

1. **Check Azure App Configuration Keys**
   ```bash
   az appconfig kv list --name app-config-test-resource1 --fields key value
   ```

2. **Verify Environment Variables**
   ```bash
   echo "AZURE_APP_CONFIG_ENABLED: $AZURE_APP_CONFIG_ENABLED"
   echo "AZURE_APP_CONFIG_ENDPOINT: $AZURE_APP_CONFIG_ENDPOINT"
   ```

3. **Check Application Logs**
   Look for Azure App Configuration connection errors or refresh failures

4. **Test Property Availability**
   ```bash
   curl -s http://localhost:8084/debug/properties | jq .
   ```

5. **Check Actuator Environment**
   ```bash
   curl -s http://localhost:8084/actuator/env | grep -A 5 -B 5 "app-config-test-resource1"
   ```

### Common Issues:

- **Sentinel key not updated**: Automatic refresh only triggers when the sentinel key changes
- **Refresh interval**: Wait at least 30 seconds between sentinel key updates
- **Connection issues**: Check Azure App Configuration connection string and permissions
- **Property precedence**: Local `application.properties` values may override Azure values

## 🔄 Automatic Refresh

The application is configured for automatic refresh every 30 seconds when the `sentinel` key changes. To test:

1. Update configuration values in Azure App Configuration
2. Update the sentinel key: `az appconfig kv set --name app-config-test-resource1 --key "sentinel" --value "3" --yes`
3. Wait 30+ seconds
4. Make a request to trigger refresh check: `curl http://localhost:8084/api/message`
5. Values should be updated automatically

## 📝 Configuration Files

- **`bootstrap.properties`** - Azure App Configuration connection and monitoring settings
- **`application.properties`** - Logging and actuator endpoint configuration
- **`AppConfigProperties.java`** - `@RefreshScope` bean with lifecycle logging
- **`.env`** - Environment variables for Azure connection

## 🎯 Success Criteria

✅ **Refresh is working when:**
- Bean destruction/creation logs appear
- `/test/manual-refresh` shows `valuesChanged: true`
- Updated values appear in API responses
- `refreshedKeys` array contains the changed properties

❌ **Refresh is NOT working when:**
- No bean lifecycle logs appear
- `valuesChanged: false` in refresh response
- Values remain at defaults despite Azure updates
- Empty `refreshedKeys` array
