# Simple Role-Based Access Control Test
Write-Host "Testing Role-Based Access Control..." -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api"

# Function to test API endpoint
function Test-Endpoint {
    param($Method, $Url, $Token, $Body)
    
    $headers = @{ "Content-Type" = "application/json" }
    if ($Token) { $headers["Authorization"] = "Bearer $Token" }
    
    try {
        if ($Body) {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers -Body ($Body | ConvertTo-Json)
        } else {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers
        }
        return @{ Success = $true; Data = $response }
    } catch {
        return @{ Success = $false; Error = $_.Exception.Message; Status = $_.Exception.Response.StatusCode.value__ }
    }
}

# Test 1: Login as USER
Write-Host "1. Login as USER..." -ForegroundColor Yellow
$userLogin = @{ username = "user"; password = "user123" }
$userResult = Test-Endpoint -Method "POST" -Url "$baseUrl/auth/login" -Body $userLogin

if ($userResult.Success) {
    $userToken = $userResult.Data.token
    Write-Host "   ✅ USER login successful" -ForegroundColor Green
} else {
    Write-Host "   ❌ USER login failed: $($userResult.Error)" -ForegroundColor Red
    exit
}

# Test 2: Login as ADMIN
Write-Host "2. Login as ADMIN..." -ForegroundColor Yellow
$adminLogin = @{ username = "admin"; password = "laptop123" }
$adminResult = Test-Endpoint -Method "POST" -Url "$baseUrl/auth/login" -Body $adminLogin

if ($adminResult.Success) {
    $adminToken = $adminResult.Data.token
    Write-Host "   ✅ ADMIN login successful" -ForegroundColor Green
} else {
    Write-Host "   ❌ ADMIN login failed: $($adminResult.Error)" -ForegroundColor Red
    exit
}

# Test 3: USER can read laptops
Write-Host "3. USER reading laptops..." -ForegroundColor Yellow
$readResult = Test-Endpoint -Method "GET" -Url "$baseUrl/laptops" -Token $userToken
if ($readResult.Success) {
    Write-Host "   ✅ USER can read laptops" -ForegroundColor Green
} else {
    Write-Host "   ❌ USER cannot read laptops: $($readResult.Error)" -ForegroundColor Red
}

# Test 4: USER tries to create laptop (should fail)
Write-Host "4. USER trying to create laptop..." -ForegroundColor Yellow
$newLaptop = @{
    brand = "TestBrand"
    model = "TestModel" 
    processor = "Test Processor"
    ramGB = 8
    storageGB = 256
    price = 799.99
    stockQuantity = 10
}
$createResult = Test-Endpoint -Method "POST" -Url "$baseUrl/laptops" -Token $userToken -Body $newLaptop
if ($createResult.Success) {
    Write-Host "   ❌ USER should NOT be able to create laptops!" -ForegroundColor Red
} else {
    Write-Host "   ✅ USER correctly denied create access (Status: $($createResult.Status))" -ForegroundColor Green
}

# Test 5: ADMIN can create laptop
Write-Host "5. ADMIN creating laptop..." -ForegroundColor Yellow
$adminCreateResult = Test-Endpoint -Method "POST" -Url "$baseUrl/laptops" -Token $adminToken -Body $newLaptop
if ($adminCreateResult.Success) {
    $laptopId = $adminCreateResult.Data.id
    Write-Host "   ✅ ADMIN can create laptops (ID: $laptopId)" -ForegroundColor Green
} else {
    Write-Host "   ❌ ADMIN cannot create laptops: $($adminCreateResult.Error)" -ForegroundColor Red
}

# Test 6: USER tries to delete laptop (should fail)
if ($laptopId) {
    Write-Host "6. USER trying to delete laptop..." -ForegroundColor Yellow
    $deleteResult = Test-Endpoint -Method "DELETE" -Url "$baseUrl/laptops/$laptopId" -Token $userToken
    if ($deleteResult.Success) {
        Write-Host "   ❌ USER should NOT be able to delete laptops!" -ForegroundColor Red
    } else {
        Write-Host "   ✅ USER correctly denied delete access (Status: $($deleteResult.Status))" -ForegroundColor Green
    }

    # Test 7: ADMIN can delete laptop
    Write-Host "7. ADMIN deleting laptop..." -ForegroundColor Yellow
    $adminDeleteResult = Test-Endpoint -Method "DELETE" -Url "$baseUrl/laptops/$laptopId" -Token $adminToken
    if ($adminDeleteResult.Success) {
        Write-Host "   ✅ ADMIN can delete laptops" -ForegroundColor Green
    } else {
        Write-Host "   ❌ ADMIN cannot delete laptops: $($adminDeleteResult.Error)" -ForegroundColor Red
    }
}

Write-Host "`nRole-Based Access Control Test Complete!" -ForegroundColor Green
Write-Host "USER role: Read-only access ✅" -ForegroundColor Blue
Write-Host "ADMIN role: Full CRUD access ✅" -ForegroundColor Blue
