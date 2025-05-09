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

## Database Configuration

### H2 Console

The H2 in-memory database console is enabled by default in development. Access it at:

```
http://localhost:8080/h2-console
```

Default connection details:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

### Database Schema Management

The application uses Hibernate's automatic schema generation with the `update` strategy. This means:

- Tables will be created automatically based on entity definitions
- New columns will be added when entities are modified
- Existing data will be preserved

Configure this in `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update
```

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