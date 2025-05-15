# Agenda MVP

> A clinic appointment scheduling system built with Spring Boot 3

## Overview

Agenda MVP is a comprehensive appointment scheduling solution designed for medical clinics and healthcare providers. It allows managing appointments between healthcare professionals and patients across multiple clinic locations.

### Key Features

- Schedule appointments with validation for availability
- Support for multiple subsidiaries (clinic locations)
- Professional availability management
- Pre-payment requirements for appointments within 2 days
- Appointment status tracking (PENDING, CONFIRMED, NOT_CONFIRMED, etc.)
- Payment processing support

## Running Locally

### Prerequisites

- Java 21
- Maven 3.8+
- Docker and Docker Compose (optional, for PostgreSQL setup)

### Development Setup

#### Option 1: Using H2 In-Memory Database (Quick Start)

```bash
# Clone the repository
git clone https://github.com/matheuszilli/app.git
cd app

# Build the project
./mvnw clean install

# Run the application with the default H2 configuration
./mvnw spring-boot:run
```

#### Option 2: Using PostgreSQL (Recommended for Testing)

```bash
# Clone the repository
git clone https://github.com/matheuszilli/app.git
cd app

# Start PostgreSQL container
docker-compose up -d

# Build the project
./mvnw clean install

# Run the application with the PostgreSQL configuration
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

### Database Configuration

#### H2 In-Memory Database

The application uses H2 in-memory database by default. The H2 console is enabled and can be accessed at:

```
http://localhost:8080/h2-console
```

Default H2 credentials:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `agenda_user`
- Password: `changeit`

#### PostgreSQL Database (via Docker)

The project includes a Docker Compose configuration for PostgreSQL:

```bash
# Start PostgreSQL container
docker-compose up -d

# Check if the container is running
docker ps
```

PostgreSQL connection details:
- Host: `localhost`
- Port: `5432`
- Database: `agenda`
- Username: `agenda_user`
- Password: `changeit`

To stop the PostgreSQL container:
```bash
docker-compose down
```

To persist data between restarts, the docker-compose configuration includes a volume for PostgreSQL data.

## Entity Relationship Diagram

For detailed information about the database structure, see the [Database Documentation](./docs/DataSeed.md).

## Documentation

- [Architecture Overview](./docs/Architecture.md)
- [Domain Model](./docs/DomainModel.md)
- [Business Rules](./docs/BusinessRules.md)
- [API Reference](./docs/API.md)
- [Database Documentation](./docs/DataSeed.md)
- [Developer Guide](./docs/DevGuide.md)
- [Deployment Guide](./docs/Deploy.md)
- [Roadmap](./docs/Roadmap.md)
- [Changelog](./CHANGELOG.md)