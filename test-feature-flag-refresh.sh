#!/bin/bash

echo "🧪 Testing Azure App Configuration Feature Flag Refresh"
echo "======================================================"

# Function to check feature flag in Azure
check_azure_flag() {
    echo "📋 Checking feature flag in Azure App Configuration..."
    az appconfig feature show --name app-config-test-resource1 --feature test-feature --fields name state lastModified 2>/dev/null | grep -E "(name|state|lastModified)" || echo "❌ Failed to get Azure feature flag status"
}

# Function to check feature flag in app
check_app_flag() {
    echo "🔍 Checking feature flag in application..."
    response=$(curl -s http://localhost:8084/api/test-feature 2>/dev/null)
    if [ $? -eq 0 ]; then
        echo "✅ App response: $response"
        echo "$response" | grep -o '"enabled":[^,]*'
    else
        echo "❌ Failed to connect to application"
    fi
}

# Function to trigger refresh
trigger_refresh() {
    echo "🔄 Triggering manual refresh..."
    refresh_result=$(curl -s -X POST http://localhost:8084/actuator/refresh 2>/dev/null)
    echo "📊 Refresh result: $refresh_result"
}

echo ""
echo "1️⃣ Initial state check:"
check_azure_flag
check_app_flag

echo ""
echo "2️⃣ Disabling feature flag in Azure..."
az appconfig feature disable --name app-config-test-resource1 --feature test-feature --yes >/dev/null 2>&1

echo ""
echo "3️⃣ State after disable (before refresh):"
check_azure_flag
check_app_flag

echo ""
echo "4️⃣ Triggering manual refresh..."
trigger_refresh

echo ""
echo "5️⃣ State after manual refresh:"
check_app_flag

echo ""
echo "6️⃣ Enabling feature flag in Azure..."
az appconfig feature enable --name app-config-test-resource1 --feature test-feature --yes >/dev/null 2>&1

echo ""
echo "7️⃣ State after enable (before refresh):"
check_azure_flag
check_app_flag

echo ""
echo "8️⃣ Waiting 6 seconds for automatic refresh..."
sleep 6

echo ""
echo "9️⃣ State after waiting (testing automatic refresh):"
check_app_flag

echo ""
echo "🔟 Triggering manual refresh to confirm:"
trigger_refresh

echo ""
echo "Final state:"
check_app_flag

echo ""
echo "🏁 Test completed!"
