# OAuth2 Security Setup Guide

## Overview
This guide explains how to configure OAuth2 JWT token-based authentication for the Laptop Store API.

## Current Security Configuration

### Development Mode (No Authentication)
- **Profile**: `dev`
- **Security**: Disabled
- **Usage**: For development and testing
- **Command**: `mvn spring-boot:run -Dspring.profiles.active=dev`

### Production Mode (OAuth2 Authentication)
- **Profile**: Default (production)
- **Security**: Currently HTTP Basic Authentication (temporary)
- **Usage**: For production deployment (will be OAuth2 when configured)
- **Credentials**: admin / laptop123 (temporary)

## OAuth2 Configuration Steps

### 1. Choose OAuth2 Provider
You can use various OAuth2 providers:
- **Auth0**: `https://auth0.com/`
- **Okta**: `https://okta.com/`
- **AWS Cognito**: `https://aws.amazon.com/cognito/`
- **Azure AD**: `https://azure.microsoft.com/en-us/services/active-directory/`
- **Google**: `https://developers.google.com/identity/protocols/oauth2`
- **Custom OAuth2 Server**: Spring Authorization Server

### 2. Update Application Properties
Once you have an OAuth2 provider, update `application.properties`:

```properties
# OAuth2 Resource Server Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://your-oauth2-server
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://your-oauth2-server/.well-known/jwks.json

# Optional: Custom claim mappings
spring.security.oauth2.resourceserver.jwt.audiences=laptop-store-api
```

### 3. Enable JWT Configuration
In `SecurityConfig.java`, uncomment the OAuth2 configuration and configure:
- Replace HTTP Basic with OAuth2 resource server
- `jwtDecoder()` bean
- `jwtAuthenticationConverter()` bean

```java
// Replace this temporary HTTP Basic configuration:
.httpBasic(httpBasic -> {
    httpBasic.realmName("Laptop Store API");
})

// With OAuth2 JWT configuration:
.oauth2ResourceServer(oauth2 -> oauth2
    .jwt(jwt -> {
        jwt.decoder(jwtDecoder());
    })
)
```

### 4. API Usage with Tokens

#### Getting Access Token
1. **Authenticate with OAuth2 provider**
2. **Obtain JWT access token**
3. **Include token in API requests**

#### Making Authenticated Requests

**Current (HTTP Basic - Temporary):**
```bash
# Include Basic Auth credentials
curl -u admin:laptop123 -X GET http://localhost:8080/api/laptops
```

**Future (OAuth2 JWT):**
```bash
# Include Bearer token in Authorization header
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -X GET http://localhost:8080/api/laptops
```

### 5. Token Structure
JWT tokens should contain:
- `sub`: Subject (user ID)
- `aud`: Audience (laptop-store-api)
- `iss`: Issuer (OAuth2 server)
- `exp`: Expiration time
- `roles`: User roles (optional)

### 6. Role-Based Access Control (Future Enhancement)
You can add role-based access:
```java
.requestMatchers(HttpMethod.GET, "/api/laptops/**").hasAnyRole("USER", "ADMIN")
.requestMatchers(HttpMethod.POST, "/api/laptops").hasRole("ADMIN")
.requestMatchers(HttpMethod.PUT, "/api/laptops/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.DELETE, "/api/laptops/**").hasRole("ADMIN")
```

## Testing

### Development Testing (No Auth)
```bash
# Start in development mode
mvn spring-boot:run -Dspring.profiles.active=dev

# Test endpoints without authentication
curl -X GET http://localhost:8080/api/laptops
```

### Production Testing (With Auth)
```bash
# Start in production mode
mvn spring-boot:run

# Test with HTTP Basic Authentication (temporary)
curl -u admin:laptop123 -X GET http://localhost:8080/api/laptops

# Once OAuth2 is configured, use JWT token instead:
# curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
#      -X GET http://localhost:8080/api/laptops
```

## Security Features Included

### ✅ Current Implementation
- Spring Security integration
- HTTP Basic Authentication (temporary)
- CORS configuration
- Profile-based security (dev/prod)
- Stateless session management
- Default admin user (admin/laptop123)

### 🔄 Ready to Configure
- OAuth2 Resource Server support
- JWT decoder for token validation
- Custom authority mapping
- Role-based access control
- OAuth2 provider integration

### 🚀 Future Enhancements
- Rate limiting
- API key authentication (alternative)
- Audit logging
- User management endpoints
- Token refresh mechanism

## Example OAuth2 Providers Setup

### Auth0 Configuration
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://your-tenant.auth0.com/
spring.security.oauth2.resourceserver.jwt.audiences=laptop-store-api
```

### Okta Configuration
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://your-tenant.okta.com/oauth2/default
```

### AWS Cognito Configuration
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://cognito-idp.region.amazonaws.com/your-user-pool-id
```

## Next Steps
1. **Choose OAuth2 provider**
2. **Update application.properties**
3. **Uncomment JWT configuration in SecurityConfig**
4. **Test with real JWT tokens**
5. **Add role-based access control if needed**
6. **Deploy to production**

## Troubleshooting
- **401 Unauthorized**: Check token validity and configuration
- **403 Forbidden**: Check user roles and permissions
- **CORS issues**: Update CORS configuration in SecurityConfig
- **Token parsing errors**: Verify JWT issuer and audience settings
