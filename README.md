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

### Development Setup

```bash
# Clone the repository
git clone https://github.com/matheuszilli/app.git
cd app

# Build the project
./mvnw clean install

# Run the application (uses H2 in-memory database by default)
./mvnw spring-boot:run
```

### Database

The application uses H2 in-memory database for development and testing. The H2 console is enabled by default and can be accessed at:

```
http://localhost:8080/h2-console
```

Default credentials:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

For production, PostgreSQL is supported - see [Deployment Guide](./docs/Deploy.md) for details.

## Documentation

- [Architecture Overview](./docs/Architecture.md)
- [Domain Model](./docs/DomainModel.md)
- [Business Rules](./docs/BusinessRules.md)
- [API Reference](./docs/API.md)
- [Data Seed Guide](./docs/DataSeed.md)
- [Developer Guide](./docs/DevGuide.md)
- [Deployment Guide](./docs/Deploy.md)
- [Roadmap](./docs/Roadmap.md)
- [Changelog](./CHANGELOG.md)