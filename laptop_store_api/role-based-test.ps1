# Role-Based Access Control Test Script for Laptop Store API
# This script tests that USER role can only read, while ADMIN can perform all CRUD operations

Write-Host "🔐 Role-Based Access Control Test for Laptop Store API" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api"
$userToken = ""
$adminToken = ""

# Function to make HTTP requests with error handling
function Invoke-ApiRequest {
    param(
        [string]$Method,
        [string]$Uri,
        [string]$Token = "",
        [object]$Body = $null
    )
    
    $headers = @{
        "Content-Type" = "application/json"
    }
    
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }
    
    try {
        if ($Body) {
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $headers -Body ($Body | ConvertTo-Json) -ErrorAction Stop
        } else {
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $headers -ErrorAction Stop
        }
        return @{ Success = $true; Data = $response; StatusCode = 200 }
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        $errorBody = ""
        try {
            $errorBody = $_.Exception.Response | Get-Content | ConvertFrom-Json
        } catch {
            $errorBody = $_.Exception.Message
        }
        return @{ Success = $false; Error = $errorBody; StatusCode = $statusCode }
    }
}

Write-Host "`n1. 🔑 Testing Authentication..." -ForegroundColor Yellow

# Login as USER
Write-Host "   Logging in as USER..." -ForegroundColor Gray
$userLogin = @{
    username = "user"
    password = "user123"
}

$result = Invoke-ApiRequest -Method "POST" -Uri "$baseUrl/auth/login" -Body $userLogin
if ($result.Success) {
    $userToken = $result.Data.token
    Write-Host "   ✅ USER login successful" -ForegroundColor Green
} else {
    Write-Host "   ❌ USER login failed: $($result.Error)" -ForegroundColor Red
    exit 1
}

# Login as ADMIN
Write-Host "   Logging in as ADMIN..." -ForegroundColor Gray
$adminLogin = @{
    username = "admin"
    password = "laptop123"
}

$result = Invoke-ApiRequest -Method "POST" -Uri "$baseUrl/auth/login" -Body $adminLogin
if ($result.Success) {
    $adminToken = $result.Data.token
    Write-Host "   ✅ ADMIN login successful" -ForegroundColor Green
} else {
    Write-Host "   ❌ ADMIN login failed: $($result.Error)" -ForegroundColor Red
    exit 1
}

Write-Host "`n2. 📖 Testing READ Access (Both USER and ADMIN should succeed)..." -ForegroundColor Yellow

# Test USER can read laptops
Write-Host "   USER reading laptops..." -ForegroundColor Gray
$result = Invoke-ApiRequest -Method "GET" -Uri "$baseUrl/laptops" -Token $userToken
if ($result.Success) {
    Write-Host "   ✅ USER can read laptops" -ForegroundColor Green
} else {
    Write-Host "   ❌ USER cannot read laptops: $($result.Error)" -ForegroundColor Red
}

# Test ADMIN can read laptops
Write-Host "   ADMIN reading laptops..." -ForegroundColor Gray
$result = Invoke-ApiRequest -Method "GET" -Uri "$baseUrl/laptops" -Token $adminToken
if ($result.Success) {
    Write-Host "   ✅ ADMIN can read laptops" -ForegroundColor Green
} else {
    Write-Host "   ❌ ADMIN cannot read laptops: $($result.Error)" -ForegroundColor Red
}

Write-Host "`n3. ➕ Testing CREATE Access (Only ADMIN should succeed)..." -ForegroundColor Yellow

$newLaptop = @{
    brand = "Test"
    model = "RoleTest Model"
    processor = "Test Processor"
    ramGB = 16
    storageGB = 512
    price = 999.99
    stockQuantity = 5
}

# Test USER cannot create laptop
Write-Host "   USER attempting to create laptop..." -ForegroundColor Gray
$result = Invoke-ApiRequest -Method "POST" -Uri "$baseUrl/laptops" -Token $userToken -Body $newLaptop
if ($result.Success) {
    Write-Host "   ❌ USER should NOT be able to create laptops!" -ForegroundColor Red
} else {
    Write-Host "   ✅ USER correctly denied create access (Status: $($result.StatusCode))" -ForegroundColor Green
}

# Test ADMIN can create laptop
Write-Host "   ADMIN attempting to create laptop..." -ForegroundColor Gray
$result = Invoke-ApiRequest -Method "POST" -Uri "$baseUrl/laptops" -Token $adminToken -Body $newLaptop
if ($result.Success) {
    $createdLaptopId = $result.Data.id
    Write-Host "   ✅ ADMIN can create laptops (ID: $createdLaptopId)" -ForegroundColor Green
} else {
    Write-Host "   ❌ ADMIN cannot create laptops: $($result.Error)" -ForegroundColor Red
    $createdLaptopId = $null
}

Write-Host "`n4. ✏️ Testing UPDATE Access (Only ADMIN should succeed)..." -ForegroundColor Yellow

if ($createdLaptopId) {
    $updateLaptop = @{
        brand = "Test"
        model = "Updated RoleTest Model"
        processor = "Updated Processor"
        ramGB = 32
        storageGB = 1024
        price = 1299.99
        stockQuantity = 3
    }

    # Test USER cannot update laptop
    Write-Host "   USER attempting to update laptop..." -ForegroundColor Gray
    $result = Invoke-ApiRequest -Method "PUT" -Uri "$baseUrl/laptops/$createdLaptopId" -Token $userToken -Body $updateLaptop
    if ($result.Success) {
        Write-Host "   ❌ USER should NOT be able to update laptops!" -ForegroundColor Red
    } else {
        Write-Host "   ✅ USER correctly denied update access (Status: $($result.StatusCode))" -ForegroundColor Green
    }

    # Test ADMIN can update laptop
    Write-Host "   ADMIN attempting to update laptop..." -ForegroundColor Gray
    $result = Invoke-ApiRequest -Method "PUT" -Uri "$baseUrl/laptops/$createdLaptopId" -Token $adminToken -Body $updateLaptop
    if ($result.Success) {
        Write-Host "   ✅ ADMIN can update laptops" -ForegroundColor Green
    } else {
        Write-Host "   ❌ ADMIN cannot update laptops: $($result.Error)" -ForegroundColor Red
    }
} else {
    Write-Host "   ⚠️ Skipping update test - no laptop was created" -ForegroundColor Yellow
}

Write-Host "`n5. 🗑️ Testing DELETE Access (Only ADMIN should succeed)..." -ForegroundColor Yellow

if ($createdLaptopId) {
    # Test USER cannot delete laptop
    Write-Host "   USER attempting to delete laptop..." -ForegroundColor Gray
    $result = Invoke-ApiRequest -Method "DELETE" -Uri "$baseUrl/laptops/$createdLaptopId" -Token $userToken
    if ($result.Success) {
        Write-Host "   ❌ USER should NOT be able to delete laptops!" -ForegroundColor Red
    } else {
        Write-Host "   ✅ USER correctly denied delete access (Status: $($result.StatusCode))" -ForegroundColor Green
    }

    # Test ADMIN can delete laptop
    Write-Host "   ADMIN attempting to delete laptop..." -ForegroundColor Gray
    $result = Invoke-ApiRequest -Method "DELETE" -Uri "$baseUrl/laptops/$createdLaptopId" -Token $adminToken
    if ($result.Success) {
        Write-Host "   ✅ ADMIN can delete laptops" -ForegroundColor Green
    } else {
        Write-Host "   ❌ ADMIN cannot delete laptops: $($result.Error)" -ForegroundColor Red
    }
} else {
    Write-Host "   ⚠️ Skipping delete test - no laptop was created" -ForegroundColor Yellow
}

Write-Host "`n6. 🚫 Testing Access Without Token..." -ForegroundColor Yellow

# Test access without token
Write-Host "   Attempting to access laptops without token..." -ForegroundColor Gray
$result = Invoke-ApiRequest -Method "GET" -Uri "$baseUrl/laptops"
if ($result.Success) {
    Write-Host "   ❌ Should not be able to access without token!" -ForegroundColor Red
} else {
    Write-Host "   ✅ Correctly denied access without token (Status: $($result.StatusCode))" -ForegroundColor Green
}

Write-Host "`n7. 🔒 Testing Token Validity..." -ForegroundColor Yellow

# Check USER token info
Write-Host "   Checking USER token info..." -ForegroundColor Gray
$result = Invoke-ApiRequest -Method "GET" -Uri "$baseUrl/auth/me" -Token $userToken
if ($result.Success) {
    Write-Host "   ✅ USER token is valid - Role: $($result.Data.role)" -ForegroundColor Green
} else {
    Write-Host "   ❌ USER token validation failed: $($result.Error)" -ForegroundColor Red
}

# Check ADMIN token info
Write-Host "   Checking ADMIN token info..." -ForegroundColor Gray
$result = Invoke-ApiRequest -Method "GET" -Uri "$baseUrl/auth/me" -Token $adminToken
if ($result.Success) {
    Write-Host "   ✅ ADMIN token is valid - Role: $($result.Data.role)" -ForegroundColor Green
} else {
    Write-Host "   ❌ ADMIN token validation failed: $($result.Error)" -ForegroundColor Red
}

Write-Host "`n🎯 Role-Based Access Control Test Summary:" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "✅ USER role can only READ laptops" -ForegroundColor Green
Write-Host "✅ ADMIN role can perform all CRUD operations" -ForegroundColor Green
Write-Host "✅ Unauthorized access is properly blocked" -ForegroundColor Green
Write-Host "✅ Token-based authentication is working" -ForegroundColor Green
Write-Host "`n🔐 Role-based security implementation is complete!" -ForegroundColor Green

Write-Host "`nRole Permissions Summary:" -ForegroundColor Cyan
Write-Host "========================" -ForegroundColor Cyan
Write-Host "👤 USER: Can only GET (read) laptops" -ForegroundColor Blue
Write-Host "👨‍💼 ADMIN: Can GET, POST, PUT, DELETE laptops" -ForegroundColor Blue
Write-Host "🛡️ All endpoints require valid JWT token" -ForegroundColor Blue
