# JWT Token Test Script (PowerShell)
# This script demonstrates how to get JWT tokens and use them with the API

$baseUrl = "http://localhost:8080"
$apiUrl = "$baseUrl/api/laptops"
$authUrl = "$baseUrl/api/auth"

Write-Host "=== JWT Token Authentication Test Script ===" -ForegroundColor Green
Write-Host ""

# Function to get JWT token
function Get-JwtToken {
    param(
        [string]$Username = "admin",
        [string]$Password = "laptop123"
    )
    
    Write-Host "Getting JWT token for user: $Username" -ForegroundColor Yellow
    
    $loginData = @{
        username = $Username
        password = $Password
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "$authUrl/login" -Method POST -ContentType "application/json" -Body $loginData
        Write-Host "✅ Token obtained successfully!" -ForegroundColor Green
        Write-Host "Token Type: $($response.token_type)" -ForegroundColor Cyan
        Write-Host "Expires In: $($response.expires_in) seconds" -ForegroundColor Cyan
        Write-Host "Username: $($response.username)" -ForegroundColor Cyan
        Write-Host ""
        return $response.access_token
    } catch {
        Write-Host "❌ Failed to get token: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# Function to get demo token (no authentication required)
function Get-DemoToken {
    Write-Host "Getting demo JWT token..." -ForegroundColor Yellow
    
    try {
        $response = Invoke-RestMethod -Uri "$authUrl/demo-token" -Method POST
        Write-Host "✅ Demo token obtained successfully!" -ForegroundColor Green
        Write-Host "Token Type: $($response.token_type)" -ForegroundColor Cyan
        Write-Host "Expires In: $($response.expires_in) seconds" -ForegroundColor Cyan
        Write-Host "Note: $($response.note)" -ForegroundColor Cyan
        Write-Host ""
        return $response.access_token
    } catch {
        Write-Host "❌ Failed to get demo token: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# Function to validate token
function Test-JwtToken {
    param([string]$Token)
    
    Write-Host "Validating JWT token..." -ForegroundColor Yellow
    
    try {
        $headers = @{
            "Authorization" = "Bearer $Token"
        }
        
        $response = Invoke-RestMethod -Uri "$authUrl/validate" -Method POST -Headers $headers
        
        if ($response.valid) {
            Write-Host "✅ Token is valid!" -ForegroundColor Green
            Write-Host "Username: $($response.username)" -ForegroundColor Cyan
            Write-Host "Role: $($response.role)" -ForegroundColor Cyan
        } else {
            Write-Host "❌ Token is invalid: $($response.message)" -ForegroundColor Red
        }
        Write-Host ""
    } catch {
        Write-Host "❌ Token validation failed: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
    }
}

# Function to test API endpoints with JWT
function Test-ApiWithJwt {
    param([string]$Token)
    
    Write-Host "Testing API endpoints with JWT token..." -ForegroundColor Yellow
    
    $headers = @{
        "Authorization" = "Bearer $Token"
        "Content-Type" = "application/json"
    }
    
    # Test 1: Get all laptops
    Write-Host "1. Getting all laptops..." -ForegroundColor Cyan
    try {
        $laptops = Invoke-RestMethod -Uri $apiUrl -Method GET -Headers $headers
        Write-Host "✅ Found $($laptops.Count) laptops" -ForegroundColor Green
    } catch {
        Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
    
    # Test 2: Create a new laptop
    Write-Host "2. Creating a new laptop..." -ForegroundColor Cyan
    $laptopData = @{
        brand = "Apple"
        model = "MacBook Pro M3"
        price = 2399.99
        description = "Latest MacBook Pro with M3 chip"
        processor = "Apple M3 Pro"
        ramSizeGB = 18
        storageSizeGB = 512
        storageType = "SSD"
        screenSize = "14 inches"
    } | ConvertTo-Json
    
    try {
        $newLaptop = Invoke-RestMethod -Uri $apiUrl -Method POST -Headers $headers -Body $laptopData
        Write-Host "✅ Laptop created with ID: $($newLaptop.id)" -ForegroundColor Green
        $laptopId = $newLaptop.id
    } catch {
        Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
        $laptopId = $null
    }
    Write-Host ""
    
    # Test 3: Search laptops
    Write-Host "3. Searching laptops by brand 'Apple'..." -ForegroundColor Cyan
    try {
        $searchResults = Invoke-RestMethod -Uri "$apiUrl/search/brand/Apple" -Method GET -Headers $headers
        Write-Host "✅ Found $($searchResults.Count) Apple laptops" -ForegroundColor Green
    } catch {
        Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
    
    # Test 4: Update laptop (if we created one)
    if ($laptopId) {
        Write-Host "4. Updating laptop ID $laptopId..." -ForegroundColor Cyan
        $updateData = @{
            brand = "Apple"
            model = "MacBook Pro M3 (Updated)"
            price = 2299.99
            description = "Updated MacBook Pro with M3 chip - Special Price!"
            processor = "Apple M3 Pro"
            ramSizeGB = 18
            storageSizeGB = 512
            storageType = "SSD"
            screenSize = "14 inches"
        } | ConvertTo-Json
        
        try {
            $updatedLaptop = Invoke-RestMethod -Uri "$apiUrl/$laptopId" -Method PUT -Headers $headers -Body $updateData
            Write-Host "✅ Laptop updated successfully" -ForegroundColor Green
        } catch {
            Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
        }
        Write-Host ""
    }
}

# Main script execution
Write-Host "Choose authentication method:"
Write-Host "1. Login with username/password (admin/laptop123)"
Write-Host "2. Get demo token (no authentication required)"
Write-Host "3. Enter existing JWT token"
$choice = Read-Host "Enter choice (1, 2, or 3)"

$jwtToken = $null

switch ($choice) {
    "1" {
        $username = Read-Host "Enter username (default: admin)"
        if (-not $username) { $username = "admin" }
        
        $password = Read-Host "Enter password (default: laptop123)" -AsSecureString
        if ($password.Length -eq 0) { 
            $password = ConvertTo-SecureString "laptop123" -AsPlainText -Force 
        }
        $passwordPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($password))
        
        $jwtToken = Get-JwtToken -Username $username -Password $passwordPlain
    }
    "2" {
        $jwtToken = Get-DemoToken
    }
    "3" {
        $jwtToken = Read-Host "Enter your JWT token"
    }
    default {
        Write-Host "Invalid choice. Exiting." -ForegroundColor Red
        exit 1
    }
}

if (-not $jwtToken) {
    Write-Host "❌ No token available. Exiting." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== JWT Token Information ===" -ForegroundColor Green
Write-Host "Token: $($jwtToken.Substring(0, 50))..." -ForegroundColor Gray
Write-Host ""

# Validate the token
Test-JwtToken -Token $jwtToken

# Test API endpoints
Test-ApiWithJwt -Token $jwtToken

Write-Host "=== Curl Examples ===" -ForegroundColor Green
Write-Host ""
Write-Host "To get a token:" -ForegroundColor Yellow
Write-Host 'curl -X POST "http://localhost:8080/api/auth/login" -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"laptop123\"}"' -ForegroundColor Gray
Write-Host ""
Write-Host "To get a demo token:" -ForegroundColor Yellow
Write-Host 'curl -X POST "http://localhost:8080/api/auth/demo-token"' -ForegroundColor Gray
Write-Host ""
Write-Host "To use the token:" -ForegroundColor Yellow
Write-Host "curl -H `"Authorization: Bearer $jwtToken`" -X GET http://localhost:8080/api/laptops" -ForegroundColor Gray
Write-Host ""

Write-Host "=== Test completed ===" -ForegroundColor Green
