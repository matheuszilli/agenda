# Deployment Guide

This document provides instructions for building, packaging, and deploying the Agenda MVP application.

## Table of Contents

- [Building for Production](#building-for-production)
- [Standalone JAR Deployment](#standalone-jar-deployment)
- [Docker Deployment](#docker-deployment)
- [Environment Configuration](#environment-configuration)
- [Database Setup](#database-setup)
- [Continuous Integration/Deployment](#continuous-integrationdeployment)

## Building for Production

### Maven Build

To build a production-ready JAR file:

```bash
./mvnw clean package -P prod -DskipTests
```

This will create a self-contained executable JAR file in the `target/` directory, typically named `app-0.0.1-SNAPSHOT.jar`.

### Verifying the Build

You can verify the JAR file by running:

```bash
java -jar target/app-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Standalone JAR Deployment

### System Requirements

- Java 21 or higher
- PostgreSQL 14 or higher
- Minimum 1GB RAM (2GB recommended)
- 1GB disk space

### Deployment Steps

1. Transfer the JAR file to your server:
   ```bash
   scp target/app-0.0.1-SNAPSHOT.jar user@server:/opt/agenda/
   ```

2. Create a systemd service file (if using systemd):
   ```bash
   # /etc/systemd/system/agenda.service
   [Unit]
   Description=Agenda MVP Application
   After=network.target postgresql.service

   [Service]
   User=agenda
   WorkingDirectory=/opt/agenda
   ExecStart=/usr/bin/java -jar /opt/agenda/app-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
   SuccessExitStatus=143
   TimeoutStopSec=10
   Restart=on-failure
   RestartSec=5

   [Install]
   WantedBy=multi-user.target
   ```

3. Start the service:
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl enable agenda
   sudo systemctl start agenda
   ```

4. Verify the service is running:
   ```bash
   sudo systemctl status agenda
   ```

## Docker Deployment

### Creating a Dockerfile

Create a `Dockerfile` in the project root with the following content:

```dockerfile
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Add a volume pointing to /tmp for temporary files
VOLUME /tmp

# Copy the JAR file
COPY target/*.jar app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

### Building the Docker Image

```bash
# Build the JAR file
./mvnw clean package -DskipTests

# Build the Docker image
docker build -t agenda-mvp:latest .
```

### Running with Docker

```bash
docker run -d \
  --name agenda-app \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/agenda \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  --restart unless-stopped \
  agenda-mvp:latest
```

### Docker Compose

For a complete deployment with PostgreSQL, create a `docker-compose.yml` file:

```yaml
version: '3.8'

services:
  app:
    image: agenda-mvp:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/agenda
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=secret
    depends_on:
      - db
    restart: unless-stopped

  db:
    image: postgres:14-alpine
    ports:
      - "5432:5432"
    environment:
      - POSTGRES_DB=agenda
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=secret
    volumes:
      - postgres-data:/var/lib/postgresql/data
    restart: unless-stopped

volumes:
  postgres-data:
```

Then run:

```bash
docker-compose up -d
```

## Environment Configuration

### Required Environment Variables

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profiles | `dev` |
| `SPRING_DATASOURCE_URL` | JDBC URL for database | `jdbc:h2:mem:testdb` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | (empty) |
| `SERVER_PORT` | Port for the web server | `8080` |

### Sample Environment File

Create a `.env` file for local development:

```properties
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/agenda
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SERVER_PORT=8080
```

## Database Setup

### PostgreSQL Setup

1. Create a new database and user:
   ```sql
   CREATE DATABASE agenda;
   CREATE USER agenda_user WITH ENCRYPTED PASSWORD 'StrongPassword123';
   GRANT ALL PRIVILEGES ON DATABASE agenda TO agenda_user;
   ```

2. Configure the application to use PostgreSQL:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/agenda
   spring.datasource.username=agenda_user
   spring.datasource.password=StrongPassword123
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.hibernate.ddl-auto=update
   ```

### Database Migration

The current MVP uses Hibernate's auto-schema update feature. For production environments, it's recommended to use a proper migration tool like Flyway or Liquibase in future versions.

## Continuous Integration/Deployment

### GitHub Actions CI Example

Create a `.github/workflows/ci.yml` file:

```yaml
name: Java CI with Maven

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'
        cache: maven
    
    - name: Build with Maven
      run: ./mvnw clean package -DskipTests
    
    - name: Run Tests
      run: ./mvnw test
    
    - name: Build Docker image
      run: docker build -t agenda-mvp:${{ github.sha }} .
    
    - name: Save Docker image
      if: github.ref == 'refs/heads/main'
      run: docker save agenda-mvp:${{ github.sha }} > agenda-mvp.tar
    
    - name: Upload Docker image
      if: github.ref == 'refs/heads/main'
      uses: actions/upload-artifact@v3
      with:
        name: docker-image
        path: agenda-mvp.tar
```

### Deployment Pipeline

```yaml
name: Deploy to Production

on:
  workflow_run:
    workflows: ["Java CI with Maven"]
    branches: [main]
    types: [completed]

jobs:
  deploy:
    runs-on: ubuntu-latest
    if: ${{ github.event.workflow_run.conclusion == 'success' }}
    
    steps:
    - name: Download Docker image
      uses: actions/download-artifact@v3
      with:
        name: docker-image
        path: .
    
    - name: Load Docker image
      run: docker load < agenda-mvp.tar
    
    - name: Deploy using SSH
      uses: appleboy/ssh-action@master
      with:
        host: ${{ secrets.DEPLOY_HOST }}
        username: ${{ secrets.DEPLOY_USER }}
        key: ${{ secrets.DEPLOY_SSH_KEY }}
        script: |
          docker pull agenda-mvp:${{ github.sha }}
          docker-compose down
          echo "AGENDA_IMAGE=agenda-mvp:${{ github.sha }}" > .env
          docker-compose up -d
```