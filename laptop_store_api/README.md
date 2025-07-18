# Laptop Store API

A RESTful API for managing a laptop store inventory with full CRUD operations.

## Features

- Complete CRUD operations for laptops
- Advanced search and filtering capabilities
- Data validation and error handling
- In-memory data storage for simplicity
- Sample data initialization
- Comprehensive API documentation

## Technologies Used

- Java 24
- Spring Boot 3.5.3
- Spring Web (REST APIs)
- Bean Validation
- Maven
- In-memory data storage

## Getting Started

### Prerequisites

- Java 24 or higher
- Maven 3.6 or higher

### Running the Application

1. Clone the repository or navigate to the project directory
2. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```
3. The API will be available at `http://localhost:8080`

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

## Data Storage

The application uses **in-memory data storage** with the following characteristics:

- Data is stored in a ConcurrentHashMap for thread safety
- IDs are auto-generated using AtomicLong
- Data persists only during application runtime
- Data is reset on each application restart
- No external database dependencies required

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

- The application uses in-memory storage, so data is reset on each restart
- No external database setup required
- CORS is enabled for all origins (configure appropriately for production)
- Validation annotations ensure data integrity
- Thread-safe operations using ConcurrentHashMap

## Advantages of In-Memory Storage

- **Simple Setup**: No database configuration required
- **Fast Performance**: All operations are in-memory
- **No Dependencies**: No need for database drivers or external services
- **Development Friendly**: Quick to start and test
- **Lightweight**: Minimal resource usage

## Future Enhancements

- Add persistent storage (File-based or Database)
- Add authentication and authorization
- Implement pagination for large datasets
- Add image upload functionality
- Add comprehensive unit and integration tests
- Implement caching strategies
- Add API documentation with Swagger/OpenAPI
