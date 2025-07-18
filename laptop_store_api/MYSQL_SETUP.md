# MySQL Setup Guide for Laptop Store API

## Installation Steps

### Windows:
1. Download MySQL from: https://dev.mysql.com/downloads/mysql/
2. Run the installer and follow the setup wizard
3. Set root password during installation (remember this password!)
4. Ensure MySQL Server is running

### Alternative - Using Docker:
```bash
docker run --name mysql-laptop-store -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=laptop_store_db -p 3306:3306 -d mysql:8.0
```

## Database Setup

### Method 1: MySQL Command Line
1. Open MySQL Command Line Client or Terminal
2. Login as root:
   ```bash
   mysql -u root -p
   ```
3. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS laptop_store_db;
   USE laptop_store_db;
   SHOW DATABASES;
   ```

### Method 2: MySQL Workbench
1. Open MySQL Workbench
2. Connect to your MySQL server
3. Run the SQL commands from above

## Configuration

Update your `application.properties` with your MySQL credentials:

```properties
# Replace 'password' with your actual MySQL root password
spring.datasource.url=jdbc:mysql://localhost:3306/laptop_store_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_actual_password_here
```

## Testing Connection

After setting up MySQL and updating the configuration:

1. Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

2. Check the console logs for successful database connection
3. The application should create the `laptops` table automatically
4. Sample data will be inserted on first run

## Verification

You can verify the setup by connecting to MySQL and running:

```sql
USE laptop_store_db;
SHOW TABLES;
SELECT * FROM laptops;
```

You should see the laptops table with 5 sample records.

## Common Issues

1. **Connection refused**: Ensure MySQL is running on port 3306
2. **Access denied**: Check username/password in application.properties
3. **Database not found**: Ensure the database was created successfully
4. **Permission denied**: Ensure the MySQL user has proper privileges

## Default Configuration

The application is configured with these default settings:
- Database: `laptop_store_db`
- Username: `root`
- Password: `password` (change this!)
- Port: `3306`
- Host: `localhost`
