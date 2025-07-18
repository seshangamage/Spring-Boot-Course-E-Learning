# Laptop Store API

A RESTful API for managing a laptop store inventory with full CRUD operations using MySQL database.

## Features

- Complete CRUD operations for laptops
- Advanced search and filtering capabilities
- Data validation and error handling
- MySQL database integration with JPA/Hibernate
- Sample data initialization
- Comprehensive API documentation
- Transaction management

## Technologies Used

- Java 24
- Spring Boot 3.5.3
- Spring Data JPA
- MySQL 8.0
- Hibernate ORM
- Maven
- Bean Validation

## Prerequisites

- Java 24 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- MySQL Workbench (optional, for database management)

## Database Setup

### 1. Install MySQL
Download and install MySQL from [https://dev.mysql.com/downloads/](https://dev.mysql.com/downloads/)

### 2. Create Database
Run the provided SQL script to set up the database:

```sql
-- Run in MySQL Command Line or MySQL Workbench
CREATE DATABASE IF NOT EXISTS laptop_store_db;
USE laptop_store_db;
```

Or execute the provided script file:
```bash
mysql -u root -p < database-setup.sql
```

### 3. Configure Database Connection
Update the `application.properties` file with your MySQL credentials:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/laptop_store_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

## Getting Started

### Running the Application

1. Clone the repository or navigate to the project directory
2. Ensure MySQL is running on your system
3. Update database credentials in `application.properties`
4. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```
5. The API will be available at `http://localhost:8080`

### Database Schema

The application automatically creates the following table structure:

```sql
CREATE TABLE laptops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description VARCHAR(500),
    processor VARCHAR(100) NOT NULL,
    ram_size_gb INT NOT NULL,
    storage_size_gb INT NOT NULL,
    storage_type VARCHAR(50),
    screen_size VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

## API Endpoints

### Base URL: `http://localhost:8080/api/laptops`

### CRUD Operations

#### 1. Create a Laptop
- **POST** `/api/laptops`
- **Request Body:**
```json
{
  "brand": "Dell",
  "model": "XPS 15",
  "price": 1599.99,
  "description": "High-performance laptop for professionals",
  "processor": "Intel Core i7-12th Gen",
  "ramSizeGB": 16,
  "storageSizeGB": 512,
  "storageType": "SSD",
  "screenSize": "15.6 inches"
}
```

#### 2. Get All Laptops
- **GET** `/api/laptops`
- **Response:** Array of laptop objects

#### 3. Get Laptop by ID
- **GET** `/api/laptops/{id}`
- **Response:** Single laptop object

#### 4. Update Laptop
- **PUT** `/api/laptops/{id}`
- **Request Body:** Updated laptop object (same format as create)

#### 5. Delete Laptop
- **DELETE** `/api/laptops/{id}`
- **Response:** Success message

### Search Operations

#### Search by Brand
- **GET** `/api/laptops/search/brand/{brand}`
- Example: `/api/laptops/search/brand/Dell`

#### Search by Model
- **GET** `/api/laptops/search/model?q={keyword}`
- Example: `/api/laptops/search/model?q=XPS`

#### Search by Price Range
- **GET** `/api/laptops/search/price?min={minPrice}&max={maxPrice}`
- Example: `/api/laptops/search/price?min=1000&max=2000`

#### Search by RAM Size
- **GET** `/api/laptops/search/ram/{ramSize}`
- Example: `/api/laptops/search/ram/16`

#### Search by Minimum Storage
- **GET** `/api/laptops/search/storage?min={storageSize}`
- Example: `/api/laptops/search/storage?min=512`

#### Search by Processor
- **GET** `/api/laptops/search/processor?q={keyword}`
- Example: `/api/laptops/search/processor?q=Intel`

#### Advanced Search with Multiple Filters
- **GET** `/api/laptops/search?brand={brand}&minPrice={min}&maxPrice={max}&minRam={ram}&minStorage={storage}`
- Example: `/api/laptops/search?brand=Dell&minPrice=1000&maxPrice=2000&minRam=8&minStorage=256`

## Data Model

### Laptop Entity

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | Long | Auto-generated | Unique identifier |
| brand | String | Yes | Laptop brand (max 50 chars) |
| model | String | Yes | Laptop model (max 100 chars) |
| price | BigDecimal | Yes | Price (must be > 0) |
| description | String | No | Product description (max 500 chars) |
| processor | String | Yes | Processor information (max 100 chars) |
| ramSizeGB | Integer | Yes | RAM size in GB (min 1) |
| storageSizeGB | Integer | Yes | Storage size in GB (min 1) |
| storageType | String | No | Storage type (SSD, HDD, etc.) |
| screenSize | String | No | Screen size information |
| createdAt | LocalDateTime | Auto-generated | Creation timestamp |
| updatedAt | LocalDateTime | Auto-generated | Last update timestamp |

## Sample Data

The application initializes with the following sample laptops:

1. **Dell XPS 13** - $1,299.99 (Intel i7, 16GB RAM, 512GB SSD)
2. **Apple MacBook Air M2** - $1,199.99 (Apple M2, 8GB RAM, 256GB SSD)
3. **HP Pavilion 15** - $699.99 (AMD Ryzen 5, 8GB RAM, 512GB SSD)
4. **Lenovo ThinkPad X1 Carbon** - $1,599.99 (Intel i7, 16GB RAM, 1TB SSD)
5. **ASUS ROG Strix G15** - $1,399.99 (AMD Ryzen 7, 16GB RAM, 512GB SSD)

## Database Features

### Persistent Storage
- Data persists across application restarts
- ACID compliance with MySQL transactions
- Automatic schema creation/updates with Hibernate

### Connection Pooling
- HikariCP connection pool for optimal performance
- Configurable pool settings for production environments

### Query Optimization
- Indexed columns for fast searching
- Custom JPQL queries for complex filtering
- Query logging for debugging and optimization

## Configuration Options

### Development Environment
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Production Environment
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.datasource.hikari.maximum-pool-size=20
```

## Error Handling

The API provides comprehensive error handling with appropriate HTTP status codes:

- **400 Bad Request** - Invalid input data or validation errors
- **404 Not Found** - Resource not found
- **409 Conflict** - Duplicate brand/model combination
- **500 Internal Server Error** - Unexpected server errors

Error responses include:
```json
{
  "timestamp": "2025-07-18T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error description",
  "errors": {
    "field": "Validation error message"
  }
}
```

## Testing with cURL

### Create a laptop:
```bash
curl -X POST http://localhost:8080/api/laptops \
  -H "Content-Type: application/json" \
  -d '{
    "brand": "Microsoft",
    "model": "Surface Laptop 5",
    "price": 1299.99,
    "description": "Premium laptop with touchscreen",
    "processor": "Intel Core i5-12th Gen",
    "ramSizeGB": 8,
    "storageSizeGB": 256,
    "storageType": "SSD",
    "screenSize": "13.5 inches"
  }'
```

### Get all laptops:
```bash
curl http://localhost:8080/api/laptops
```

### Get laptop by ID:
```bash
curl http://localhost:8080/api/laptops/1
```

### Update a laptop:
```bash
curl -X PUT http://localhost:8080/api/laptops/1 \
  -H "Content-Type: application/json" \
  -d '{
    "brand": "Dell",
    "model": "XPS 13 Plus",
    "price": 1399.99,
    "description": "Updated premium ultrabook",
    "processor": "Intel Core i7-12th Gen",
    "ramSizeGB": 16,
    "storageSizeGB": 512,
    "storageType": "SSD",
    "screenSize": "13.4 inches"
  }'
```

### Delete a laptop:
```bash
curl -X DELETE http://localhost:8080/api/laptops/1
```

### Search laptops by brand:
```bash
curl http://localhost:8080/api/laptops/search/brand/Dell
```

## Development Notes

- The application uses MySQL for persistent data storage
- Database schema is automatically created/updated by Hibernate
- SQL queries are logged to console for debugging (can be disabled in production)
- CORS is enabled for all origins (configure appropriately for production)
- Validation annotations ensure data integrity
- Transaction management ensures data consistency

## Advantages of MySQL Integration

- **Persistent Storage**: Data survives application restarts
- **ACID Compliance**: Reliable transactions and data integrity
- **Scalability**: Can handle large datasets efficiently
- **Query Performance**: Optimized with proper indexing
- **Backup & Recovery**: Standard database backup solutions
- **Multi-user Support**: Concurrent access with proper locking

## Troubleshooting

### Common Issues

1. **Connection Refused Error**
   - Ensure MySQL server is running
   - Check if port 3306 is available
   - Verify database credentials in application.properties

2. **Database Access Denied**
   - Check username and password in application.properties
   - Ensure the MySQL user has proper permissions
   - Verify database exists

3. **Schema/Table Not Found**
   - Ensure `spring.jpa.hibernate.ddl-auto=update` is set
   - Check if the application has permissions to create tables
   - Verify the database name in the connection URL

### MySQL Commands

```sql
-- Check if database exists
SHOW DATABASES;

-- Check tables in database
USE laptop_store_db;
SHOW TABLES;

-- View table structure
DESCRIBE laptops;

-- Check sample data
SELECT * FROM laptops LIMIT 5;
```

## Future Enhancements

- Add database migration scripts with Flyway
- Implement database clustering for high availability
- Add read replicas for improved performance
- Implement database monitoring and alerting
- Add comprehensive unit and integration tests
- Implement caching with Redis
- Add API documentation with Swagger/OpenAPI
