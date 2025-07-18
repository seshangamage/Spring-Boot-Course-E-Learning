# Database-Backed JWT Authentication Test Script
# This script demonstrates the new database-backed user and token management

$baseUrl = "http://localhost:8080"
$apiUrl = "$baseUrl/api/laptops"
$authUrl = "$baseUrl/api/auth"

Write-Host "=== Database-Backed JWT Authentication Test ===" -ForegroundColor Green
Write-Host ""

# Function to register a new user
function Register-User {
    param(
        [string]$Username,
        [string]$Email,
        [string]$Password
    )
    
    Write-Host "Registering new user: $Username ($Email)" -ForegroundColor Yellow
    
    $registerData = @{
        username = $Username
        email = $Email
        password = $Password
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "$authUrl/register" -Method POST -ContentType "application/json" -Body $registerData
        Write-Host "✅ User registered successfully!" -ForegroundColor Green
        Write-Host "Username: $($response.username)" -ForegroundColor Cyan
        Write-Host "Email: $($response.email)" -ForegroundColor Cyan
        Write-Host "Role: $($response.role)" -ForegroundColor Cyan
        Write-Host ""
        return $true
    } catch {
        Write-Host "❌ Registration failed: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
        return $false
    }
}

# Function to login and get JWT token
function Get-JwtToken {
    param(
        [string]$Username,
        [string]$Password
    )
    
    Write-Host "Logging in user: $Username" -ForegroundColor Yellow
    
    $loginData = @{
        username = $Username
        password = $Password
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "$authUrl/login" -Method POST -ContentType "application/json" -Body $loginData
        Write-Host "✅ Login successful!" -ForegroundColor Green
        Write-Host "Token Type: $($response.token_type)" -ForegroundColor Cyan
        Write-Host "Username: $($response.username)" -ForegroundColor Cyan
        Write-Host "Role: $($response.role)" -ForegroundColor Cyan
        Write-Host "Token ID: $($response.token_id)" -ForegroundColor Cyan
        Write-Host "Issued At: $($response.issued_at)" -ForegroundColor Cyan
        Write-Host ""
        return $response.access_token
    } catch {
        Write-Host "❌ Login failed: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
        return $null
    }
}

# Function to get user's active tokens
function Get-UserTokens {
    param([string]$Token)
    
    Write-Host "Getting user's active tokens..." -ForegroundColor Yellow
    
    try {
        $headers = @{
            "Authorization" = "Bearer $Token"
        }
        
        $response = Invoke-RestMethod -Uri "$authUrl/tokens" -Method GET -Headers $headers
        
        Write-Host "✅ Active tokens retrieved!" -ForegroundColor Green
        Write-Host "Total Active Tokens: $($response.active_tokens)" -ForegroundColor Cyan
        
        foreach ($tokenInfo in $response.tokens) {
            $current = if ($tokenInfo.is_current) { " (CURRENT)" } else { "" }
            Write-Host "  Token ID: $($tokenInfo.id)$current" -ForegroundColor Gray
            Write-Host "    Issued: $($tokenInfo.issued_at)" -ForegroundColor Gray
            Write-Host "    Expires: $($tokenInfo.expires_at)" -ForegroundColor Gray
            Write-Host "    IP: $($tokenInfo.ip_address)" -ForegroundColor Gray
            Write-Host ""
        }
        
    } catch {
        Write-Host "❌ Failed to get tokens: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
    }
}

# Function to logout (revoke current token)
function Invoke-Logout {
    param([string]$Token)
    
    Write-Host "Logging out (revoking current token)..." -ForegroundColor Yellow
    
    try {
        $headers = @{
            "Authorization" = "Bearer $Token"
        }
        
        $response = Invoke-RestMethod -Uri "$authUrl/logout" -Method POST -Headers $headers
        Write-Host "✅ Logged out successfully!" -ForegroundColor Green
        Write-Host $response.message -ForegroundColor Cyan
        Write-Host ""
    } catch {
        Write-Host "❌ Logout failed: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
    }
}

# Function to logout from all devices
function Invoke-LogoutAll {
    param([string]$Token)
    
    Write-Host "Logging out from all devices..." -ForegroundColor Yellow
    
    try {
        $headers = @{
            "Authorization" = "Bearer $Token"
        }
        
        $response = Invoke-RestMethod -Uri "$authUrl/logout-all" -Method POST -Headers $headers
        Write-Host "✅ Logged out from all devices!" -ForegroundColor Green
        Write-Host $response.message -ForegroundColor Cyan
        Write-Host ""
    } catch {
        Write-Host "❌ Logout all failed: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
    }
}

# Function to test API with JWT
function Test-ApiWithJwt {
    param([string]$Token)
    
    Write-Host "Testing API with JWT token..." -ForegroundColor Yellow
    
    $headers = @{
        "Authorization" = "Bearer $Token"
        "Content-Type" = "application/json"
    }
    
    # Test getting laptops
    try {
        $laptops = Invoke-RestMethod -Uri $apiUrl -Method GET -Headers $headers
        Write-Host "✅ API test successful! Found $($laptops.Count) laptops" -ForegroundColor Green
    } catch {
        Write-Host "❌ API test failed: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

# Main script execution
Write-Host "Choose an option:"
Write-Host "1. Test with existing admin user (admin/laptop123)"
Write-Host "2. Register new user and test"
Write-Host "3. Test user registration only"
Write-Host "4. Test token management features"
$choice = Read-Host "Enter choice (1, 2, 3, or 4)"

switch ($choice) {
    "1" {
        # Test with default admin user
        $token = Get-JwtToken -Username "admin" -Password "laptop123"
        if ($token) {
            Get-UserTokens -Token $token
            Test-ApiWithJwt -Token $token
        }
    }
    "2" {
        # Register new user and test
        $username = Read-Host "Enter username"
        $email = Read-Host "Enter email"
        $password = Read-Host "Enter password" -AsSecureString
        $passwordPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($password))
        
        if (Register-User -Username $username -Email $email -Password $passwordPlain) {
            $token = Get-JwtToken -Username $username -Password $passwordPlain
            if ($token) {
                Get-UserTokens -Token $token
                Test-ApiWithJwt -Token $token
            }
        }
    }
    "3" {
        # Test registration only
        $username = Read-Host "Enter username"
        $email = Read-Host "Enter email"
        $password = Read-Host "Enter password" -AsSecureString
        $passwordPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto([System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($password))
        
        Register-User -Username $username -Email $email -Password $passwordPlain
    }
    "4" {
        # Test token management
        $token1 = Get-JwtToken -Username "admin" -Password "laptop123"
        if ($token1) {
            Write-Host "=== First Login ===" -ForegroundColor Magenta
            Get-UserTokens -Token $token1
            
            Write-Host "=== Second Login (same user) ===" -ForegroundColor Magenta
            $token2 = Get-JwtToken -Username "admin" -Password "laptop123"
            if ($token2) {
                Get-UserTokens -Token $token2
                
                Write-Host "=== Logout from current session ===" -ForegroundColor Magenta
                Invoke-Logout -Token $token2
                
                Write-Host "=== Try to use revoked token ===" -ForegroundColor Magenta
                Test-ApiWithJwt -Token $token2
                
                Write-Host "=== Use first token (should still work) ===" -ForegroundColor Magenta
                Test-ApiWithJwt -Token $token1
                
                Write-Host "=== Logout from all devices ===" -ForegroundColor Magenta
                Invoke-LogoutAll -Token $token1
                
                Write-Host "=== Try to use any token (should fail) ===" -ForegroundColor Magenta
                Test-ApiWithJwt -Token $token1
            }
        }
    }
    default {
        Write-Host "Invalid choice. Exiting." -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "=== Curl Examples ===" -ForegroundColor Green
Write-Host ""
Write-Host "Register new user:" -ForegroundColor Yellow
Write-Host 'curl -X POST "http://localhost:8080/api/auth/register" -H "Content-Type: application/json" -d "{\"username\":\"newuser\",\"email\":\"user@example.com\",\"password\":\"password123\"}"' -ForegroundColor Gray
Write-Host ""
Write-Host "Login:" -ForegroundColor Yellow
Write-Host 'curl -X POST "http://localhost:8080/api/auth/login" -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"laptop123\"}"' -ForegroundColor Gray
Write-Host ""
Write-Host "Get user tokens:" -ForegroundColor Yellow
Write-Host 'curl -H "Authorization: Bearer YOUR_TOKEN" -X GET "http://localhost:8080/api/auth/tokens"' -ForegroundColor Gray
Write-Host ""
Write-Host "Logout:" -ForegroundColor Yellow
Write-Host 'curl -H "Authorization: Bearer YOUR_TOKEN" -X POST "http://localhost:8080/api/auth/logout"' -ForegroundColor Gray
Write-Host ""

Write-Host "=== Test completed ===" -ForegroundColor Green
