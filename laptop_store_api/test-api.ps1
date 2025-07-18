# Laptop Store API Test Script (PowerShell)
# This script demonstrates API usage with and without authentication

$baseUrl = "http://localhost:8080/api/laptops"

Write-Host "=== Laptop Store API Test Script ===" -ForegroundColor Green
Write-Host ""

# Function to test API endpoints
function Test-Endpoint {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Data = "",
        [string]$Token = "",
        [bool]$UseBasicAuth = $false
    )
    
    Write-Host "Testing: $Method $Url" -ForegroundColor Yellow
    
    $headers = @{
        "Content-Type" = "application/json"
    }
    
    $credentials = $null
    
    if ($UseBasicAuth) {
        # Create credentials for HTTP Basic Auth
        $username = "admin"
        $password = "laptop123"
        $base64AuthInfo = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes(("{0}:{1}" -f $username, $password)))
        $headers["Authorization"] = "Basic $base64AuthInfo"
    } elseif ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }
    
    try {
        if ($Data) {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers -Body $Data
        } else {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers
        }
        
        $response | ConvertTo-Json -Depth 3
    } catch {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        }
    }
    
    Write-Host ""
    Write-Host "---" -ForegroundColor Gray
    Write-Host ""
}

# Check if running in development mode (no auth) or production mode (auth required)
Write-Host "Choose testing mode:"
Write-Host "1. Development Mode (No Authentication)"
Write-Host "2. Production Mode (HTTP Basic Authentication - Legacy)"
Write-Host "3. Production Mode (JWT Token Authentication - Current)"
$mode = Read-Host "Enter choice (1, 2, or 3)"

$jwtToken = ""
$useBasicAuth = $false

if ($mode -eq "2") {
    $useBasicAuth = $true
    Write-Host "Using HTTP Basic Authentication (admin/laptop123)" -ForegroundColor Yellow
} elseif ($mode -eq "3") {
    # Get JWT token first
    Write-Host "Getting JWT token..." -ForegroundColor Yellow
    $loginData = @{
        username = "admin"
        password = "laptop123"
    } | ConvertTo-Json
    
    try {
        $tokenResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginData
        $jwtToken = $tokenResponse.access_token
        Write-Host "✅ JWT token obtained successfully!" -ForegroundColor Green
    } catch {
        Write-Host "❌ Failed to get JWT token. You can also get a demo token from /api/auth/demo-token" -ForegroundColor Red
        $jwtToken = Read-Host "Enter your JWT token manually (or press Enter to exit)"
        if (-not $jwtToken) {
            Write-Host "No token provided. Exiting." -ForegroundColor Red
            exit 1
        }
    }
}

Write-Host ""
Write-Host "=== Testing API Endpoints ===" -ForegroundColor Green
Write-Host ""

# Test 1: Get all laptops
Test-Endpoint -Method "GET" -Url $baseUrl -Token $jwtToken -UseBasicAuth $useBasicAuth

# Test 2: Create a new laptop
$laptopData = @{
    brand = "Dell"
    model = "XPS 15"
    price = 1599.99
    description = "High-performance laptop for professionals"
    processor = "Intel Core i7-12th Gen"
    ramSizeGB = 16
    storageSizeGB = 512
    storageType = "SSD"
    screenSize = "15.6 inches"
} | ConvertTo-Json

Write-Host "Creating a new laptop..." -ForegroundColor Cyan
Test-Endpoint -Method "POST" -Url $baseUrl -Data $laptopData -Token $jwtToken -UseBasicAuth $useBasicAuth

# Test 3: Search by brand
Write-Host "Searching laptops by brand 'Dell'..." -ForegroundColor Cyan
Test-Endpoint -Method "GET" -Url "$baseUrl/search/brand/Dell" -Token $jwtToken -UseBasicAuth $useBasicAuth

# Test 4: Search by price range
Write-Host "Searching laptops by price range (1000-2000)..." -ForegroundColor Cyan
Test-Endpoint -Method "GET" -Url "$baseUrl/search/price?min=1000&max=2000" -Token $jwtToken -UseBasicAuth $useBasicAuth

# Test 5: Advanced search
Write-Host "Advanced search (brand=Dell, minRam=16)..." -ForegroundColor Cyan
Test-Endpoint -Method "GET" -Url "$baseUrl/search?brand=Dell&minRam=16" -Token $jwtToken -UseBasicAuth $useBasicAuth

Write-Host "=== Test completed ===" -ForegroundColor Green
