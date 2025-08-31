#!/bin/bash

echo "=== Azure App Configuration Verification ==="
echo ""

echo "1. Environment Variables Check:"
echo "   AZURE_APP_CONFIG_ENABLED: ${AZURE_APP_CONFIG_ENABLED:-'NOT SET'}"
echo "   AZURE_APP_CONFIG_ENDPOINT: ${AZURE_APP_CONFIG_ENDPOINT:-'NOT SET'}"
echo "   AZURE_APP_CONFIG_CONNECTION_STRING: ${AZURE_APP_CONFIG_CONNECTION_STRING:-'NOT SET'}"
echo ""

echo "2. .env File Check:"
if [ -f .env ]; then
    echo "   .env file exists"
    echo "   Contents (excluding sensitive data):"
    grep -v "CONNECTION_STRING\|SECRET\|KEY" .env || echo "   No non-sensitive config found"
else
    echo "   .env file NOT found"
fi
echo ""

echo "3. Required Azure App Configuration Keys:"
echo "   Your Azure App Config should have these keys:"
echo "   - app.message"
echo "   - app.feature.enabled" 
echo "   - app.version"
echo "   - app.environment"
echo ""

echo "4. Bootstrap Configuration Check:"
if [ -f src/main/resources/bootstrap-azure.properties ]; then
    echo "   bootstrap-azure.properties exists"
else
    echo "   bootstrap-azure.properties NOT found"
fi
echo ""

echo "5. Testing Connection (if endpoint is set):"
if [ ! -z "$AZURE_APP_CONFIG_ENDPOINT" ] && [ "$AZURE_APP_CONFIG_ENDPOINT" != "NOT SET" ]; then
    echo "   Testing connection to: $AZURE_APP_CONFIG_ENDPOINT"
    curl -s --connect-timeout 5 "$AZURE_APP_CONFIG_ENDPOINT" > /dev/null
    if [ $? -eq 0 ]; then
        echo "   ✅ Endpoint is reachable"
    else
        echo "   ❌ Endpoint is not reachable or requires authentication"
    fi
else
    echo "   ⚠️  No endpoint set for testing"
fi
echo ""

echo "6. Azure CLI Check (if available):"
if command -v az &> /dev/null; then
    echo "   Azure CLI is installed"
    az account show --query "name" -o tsv 2>/dev/null || echo "   Not logged in to Azure CLI"
else
    echo "   Azure CLI not installed"
fi
