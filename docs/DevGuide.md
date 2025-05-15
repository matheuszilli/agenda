# Developer Guide

This guide provides instructions for setting up and working with the Agenda MVP codebase.

## Table of Contents

- [Development Environment Setup](#development-environment-setup)
- [Building the Project](#building-the-project)
- [Running Tests](#running-tests)
- [Database Configuration](#database-configuration)
- [Spring Profiles](#spring-profiles)
- [IDE Setup](#ide-setup)
- [Development Best Practices](#development-best-practices)

## Development Environment Setup

### Prerequisites

- Java 21 or higher
- Maven 3.8+ or the included Maven wrapper (`mvnw`)
- Git
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)
- PostgreSQL (for production-like environment)

### Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/app.git
   cd app
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Building the Project

The project uses Maven for dependency management and build automation.

### Common Maven Commands

```bash
# Clean the project (remove target directory)
./mvnw clean

# Compile the project
./mvnw compile

# Package the project (create JAR file)
./mvnw package

# Run tests
./mvnw test

# Clean and install
./mvnw clean install

# Skip tests during build
./mvnw clean install -DskipTests
```

## Running Tests

### Test Configuration

The application uses JUnit 5 and Spring Boot Test for testing. Tests are configured to run with an H2 in-memory database by default, which is automatically set up for each test suite.

### Running All Tests

```bash
./mvnw test
```

### Running a Specific Test Class

```bash
./mvnw test -Dtest=AppointmentServiceTest
```

### Running a Specific Test Method

```bash
./mvnw test -Dtest=AppointmentServiceTest#testScheduleAppointment
```

### Running Tests with Different Spring Profiles

You can run tests with specific Spring profiles to test different configurations:

```bash
./mvnw test -Dspring.profiles.active=test
```

### Test Coverage Report

To generate a test coverage report:

```bash
./mvnw verify
```

The coverage report will be available in `target/site/jacoco/index.html`.

## Manual Testing Setup

For manual testing and development, follow these steps to set up a suitable environment:

### Option 1: Testing with H2 In-Memory Database

1. **Start the application with H2:**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Access the H2 Console:**
   Open your browser and go to:
   ```
   http://localhost:8080/h2-console
   ```

3. **Configure H2 connection:**
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `agenda_user`
   - Password: `changeit`

4. **Create test data:**
   Use the SQL examples provided in [Database Documentation](./DataSeed.md) to populate the database with test data.

### Option 2: Testing with PostgreSQL (Recommended for Integration Testing)

1. **Start PostgreSQL container:**
   ```bash
   docker-compose up -d
   ```

2. **Start the application with the dev profile:**
   ```bash
   ./mvnw spring-boot:run -Dspring.profiles.active=dev
   ```

3. **Connect to PostgreSQL (optional):**
   ```bash
   docker exec -it agenda-pg psql -U agenda_user -d agenda
   ```

4. **Create test data:**
   You can use the SQL scripts from [Database Documentation](./DataSeed.md) directly in the PostgreSQL console, or create a DataSeedConfig class as described in the same document.

## API Testing

To test the REST API endpoints:

1. **Using cURL:**
   ```bash
   # Example: Create a new business service
   curl -X POST http://localhost:8080/api/services \
     -H "Content-Type: application/json" \
     -d '{"name":"New Service","description":"Description","price":100.00,"durationMinutes":30,"companyId":"company-uuid-here"}'

   # Example: Get all appointments
   curl -X GET http://localhost:8080/api/appointments
   ```

2. **Using Postman:**
   Import the provided Postman collection (if available) or create a new collection with the following request examples:

   - GET `/api/appointments` - List all appointments
   - POST `/api/appointments` - Create a new appointment
   - GET `/api/services` - List available services
   - POST `/api/services` - Create a new service

## Database Configuration

### H2 Console

The H2 in-memory database console is enabled by default in development. Access it at:

```
http://localhost:8080/h2-console
```

Default connection details:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `agenda_user`
- Password: `changeit`

### PostgreSQL Configuration

The application can be configured to use PostgreSQL as follows:

1. **Start PostgreSQL container:**
   ```bash
   docker-compose up -d
   ```

2. **Connection details:**
   - Host: `localhost`
   - Port: `5432`
   - Database: `agenda`
   - Username: `agenda_user`
   - Password: `changeit`

### Database Schema Management

The application uses Hibernate's automatic schema generation with the `update` strategy. This means:

- Tables will be created automatically based on entity definitions
- New columns will be added when entities are modified
- Existing data will be preserved

Configure this in `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
```

## Troubleshooting Common Issues

### Database Connection Issues

If you encounter errors connecting to the database:

1. **For H2:**
   - Check that the JDBC URL matches the configuration in `application.properties`
   - Verify the H2 console is enabled with `spring.h2.console.enabled=true`

2. **For PostgreSQL:**
   - Ensure the Docker container is running: `docker ps`
   - Verify connection properties in `application-dev.yml`
   - Test connection directly: `psql -h localhost -U agenda_user -d agenda`

### Test Data Issues

If test data is not being seeded properly:

1. Check if the `DataSeedConfig` class is correctly configured
2. Verify the correct profile is active
3. Examine the application logs for any errors during data seeding

### API Errors

For API errors:

1. Check the request format against the expected DTO structure
2. Verify that required fields are included and valid
3. Look for validation error messages in the response body

## Spring Profiles

The application uses Spring profiles to configure different environments:

### Development Profile

```properties
# application-dev.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
```

### Production Profile

```properties
# application-prod.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/agenda
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Setting Active Profiles

```bash
# Command line
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Environment variable
export SPRING_PROFILES_ACTIVE=dev,local
./mvnw spring-boot:run
```

## IDE Setup

### IntelliJ IDEA

1. Import the project:
   - File → Open → Select the `pom.xml` file → Open as Project

2. Configure Lombok:
   - Install the Lombok plugin (File → Settings → Plugins → Search "Lombok")
   - Enable annotation processing (File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable annotation processing)

3. Configure Run Configuration:
   - Run → Edit Configurations → Add New Configuration → Spring Boot
   - Main class: `com.agenda.app.AppApplication`
   - Program arguments: `--spring.profiles.active=dev`

### VS Code

1. Install Extensions:
   - Spring Boot Extension Pack
   - Java Extension Pack
   - Lombok Annotations Support

2. Open the project folder in VS Code

3. Configure `launch.json` for running the application:
   ```json
   {
     "configurations": [
       {
         "type": "java",
         "name": "Spring Boot",
         "request": "launch",
         "mainClass": "com.agenda.app.AppApplication",
         "projectName": "app",
         "args": "--spring.profiles.active=dev"
       }
     ]
   }
   ```

## Development Best Practices

### Code Style

- Follow standard Java coding conventions
- Use Lombok annotations to reduce boilerplate
- Prefer constructor injection over field injection for Spring components

### Entity Design

- All entities should extend the `BaseEntity` class
- Use appropriate validation annotations on entity fields
- Keep entities focused on domain concerns, not persistence details

### Service Layer Guidelines

- Keep services focused on business logic
- Use `@Transactional` annotations properly:
  - Read-only operations: `@Transactional(readOnly = true)`
  - Modifications: `@Transactional`
- Validate inputs before performing operations

### Repository Guidelines

- Use Spring Data JPA repository interfaces
- Prefer custom query methods using the method naming convention
- Use `@Query` for complex queries
- Use `@EntityGraph` for fetch optimizations

### Controller Guidelines

- Use DTOs for request/response objects
- Use `@Valid` for request validation
- Use appropriate status codes in responses
- Keep controllers thin, delegate to services