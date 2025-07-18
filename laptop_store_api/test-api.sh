#!/bin/bash

# Laptop Store API Test Script
# This script demonstrates API usage with and without authentication

BASE_URL="http://localhost:8080/api/laptops"

echo "=== Laptop Store API Test Script ==="
echo ""

# Function to test API endpoints
test_endpoint() {
    local method=$1
    local url=$2
    local data=$3
    local token=$4
    
    echo "Testing: $method $url"
    
    if [ -n "$token" ]; then
        if [ -n "$data" ]; then
            curl -X "$method" \
                 -H "Authorization: Bearer $token" \
                 -H "Content-Type: application/json" \
                 -d "$data" \
                 "$url"
        else
            curl -X "$method" \
                 -H "Authorization: Bearer $token" \
                 "$url"
        fi
    else
        if [ -n "$data" ]; then
            curl -X "$method" \
                 -H "Content-Type: application/json" \
                 -d "$data" \
                 "$url"
        else
            curl -X "$method" "$url"
        fi
    fi
    
    echo ""
    echo "---"
    echo ""
}

# Check if running in development mode (no auth) or production mode (auth required)
echo "Choose testing mode:"
echo "1. Development Mode (No Authentication)"
echo "2. Production Mode (OAuth2 Authentication Required)"
read -p "Enter choice (1 or 2): " mode

if [ "$mode" = "2" ]; then
    read -p "Enter your JWT token: " JWT_TOKEN
    if [ -z "$JWT_TOKEN" ]; then
        echo "JWT token is required for production mode!"
        exit 1
    fi
fi

echo ""
echo "=== Testing API Endpoints ==="
echo ""

# Test 1: Get all laptops
test_endpoint "GET" "$BASE_URL" "" "$JWT_TOKEN"

# Test 2: Create a new laptop
LAPTOP_DATA='{
    "brand": "Dell",
    "model": "XPS 15",
    "price": 1599.99,
    "description": "High-performance laptop for professionals",
    "processor": "Intel Core i7-12th Gen",
    "ramSizeGB": 16,
    "storageSizeGB": 512,
    "storageType": "SSD",
    "screenSize": "15.6 inches"
}'

echo "Creating a new laptop..."
test_endpoint "POST" "$BASE_URL" "$LAPTOP_DATA" "$JWT_TOKEN"

# Test 3: Search by brand
echo "Searching laptops by brand 'Dell'..."
test_endpoint "GET" "$BASE_URL/search/brand/Dell" "" "$JWT_TOKEN"

# Test 4: Search by price range
echo "Searching laptops by price range (1000-2000)..."
test_endpoint "GET" "$BASE_URL/search/price?min=1000&max=2000" "" "$JWT_TOKEN"

# Test 5: Advanced search
echo "Advanced search (brand=Dell, minRam=16)..."
test_endpoint "GET" "$BASE_URL/search?brand=Dell&minRam=16" "" "$JWT_TOKEN"

echo "=== Test completed ==="
