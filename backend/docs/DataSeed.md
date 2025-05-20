# Database Documentation

This document provides comprehensive information about the database structure and instructions for seeding the database with initial data for development and testing purposes.

## Table of Contents

- [Database Schema](#database-schema)
- [Entity Relationship Diagram](#entity-relationship-diagram)
- [Key Entities Description](#key-entities-description)
- [Database Setup](#database-setup)
- [H2 Database Seeding](#h2-database-seeding)
- [PostgreSQL Seeding](#postgresql-seeding)
- [Sample Data Script](#sample-data-script)

## Database Schema

The application uses a relational database with the following primary tables:

| Table Name | Description |
|------------|-------------|
| companies | Stores company information |
| subsidiaries | Stores branch/location information for companies |
| professionals | Healthcare providers who deliver services |
| customers | Patients who receive services |
| appointments | Scheduled appointments between professionals and customers |
| services | Available medical services that can be booked |
| service_order | Orders/invoices for services rendered |
| payment | Payment transactions for service orders |
| medical_records | Patient medical records |
| medical_record_notes | Notes within medical records |
| users | System users with authentication details |

## Entity Relationship Diagram

```
+-------------+       +---------------+       +---------------+
|  COMPANY    |------>| SUBSIDIARY    |------>| PROFESSIONAL  |
+-------------+       +---------------+       +---------------+
      |                     |                       |
      |                     |                       |
      v                     v                       v
+-------------+       +---------------+       +---------------+
| BUSINESS    |<------| APPOINTMENT   |<------| CUSTOMER      |
| SERVICE     |       +---------------+       +---------------+
+-------------+             |                       |
                            |                       |
                            v                       v
                     +---------------+       +---------------+
                     | SERVICE ORDER |------>| MEDICAL       |
                     +---------------+       | RECORD        |
                            |               +---------------+
                            |                      |
                            v                      v
                     +---------------+       +---------------+
                     | PAYMENT       |       | MEDICAL       |
                     |               |       | RECORD NOTES  |
                     +---------------+       +---------------+
```

## Key Entities Description

### BaseEntity
All entities extend this abstract class which provides:
- UUID primary key
- Created/Updated timestamps
- Version for optimistic locking
- Active/Deleted flags for soft deletion

### Company
- Core entity representing the medical business
- Has multiple subsidiaries (branches/clinics)
- Contains basic company information (name, address, phone)

### Subsidiary
- Branch/clinic location of a company
- Has operating hours and days
- Hosts professionals who work at this location

### Professional
- Healthcare providers (doctors, nurses, specialists)
- Connected to a specific subsidiary location
- Has availability time slots for appointments

### Customer
- Patients who book appointments
- Contains personal and contact information
- Has associated medical records

### Appointment
- Scheduled meeting between customer and professional
- References a specific service to be provided
- Has start/end time and status (PENDING, CONFIRMED, etc.)
- May be linked to a service order for payment

### BusinessService
- Medical services offered by the company
- Has price, duration, and pre-payment requirements
- Used when creating appointments

### ServiceOrder
- Invoice/billing record for services
- Contains pricing information
- Links to payment details
- Has status (OPEN, IN_PROGRESS, COMPLETED, etc.)

### Payment
- Financial transaction for a service order
- Supports multiple payment methods (CASH, CREDIT_CARD, etc.)
- Tracks payment status and details

## Database Setup

### Database Configuration

The application supports two database types:

1. **H2 In-Memory Database** (Default, for development)
   * Configuration in `application.properties`
   * Connection details:
     - JDBC URL: `jdbc:h2:mem:testdb`
     - Username: `agenda_user`
     - Password: `changeit`

2. **PostgreSQL** (Recommended for production-like testing)
   * Configuration in `application-dev.yml`
   * Docker setup included in `docker-compose.yml`
   * Connection details:
     - Host: `localhost`
     - Port: `5432`
     - Database: `agenda`
     - Username: `agenda_user`
     - Password: `changeit`

### Schema Generation

The application uses Hibernate's automatic schema generation with the `update` strategy:
```properties
spring.jpa.hibernate.ddl-auto=update
```

This means:
- Tables will be created automatically based on entity definitions
- New columns will be added when entities are modified
- Existing data will be preserved

## H2 Database Seeding

For the H2 in-memory database, data needs to be seeded each time the application starts. The recommended approach is to use a `CommandLineRunner` bean:

1. Create a new class in `com.agenda.app.config` called `DataSeedConfig`:

```java
package com.agenda.app.config;

import com.agenda.app.model.*;
import com.agenda.app.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
@Profile("dev") // Only run in development profile
public class DataSeedConfig {

    @Bean
    public CommandLineRunner seedData(
            CompanyRepository companyRepo,
            SubsidiaryRepository subsidiaryRepo,
            ProfessionalRepository professionalRepo,
            CustomerRepository customerRepo,
            ItemRepository serviceRepo) {

        return args -> {
            // Only seed if the database is empty
            if (companyRepo.count() > 0) {
                return;
            }

            // Seed companies
            Company company = new Company();
            company.setName("HealthCare Clinic");
            company.setAddress("123 Medical St, City");
            company.setPhone("(123) 456-7890");
            companyRepo.save(company);

            // Seed subsidiaries
            Subsidiary subsidiary = new Subsidiary();
            subsidiary.setName("Main Branch");
            subsidiary.setAddress("123 Medical St, City");
            subsidiary.setOpenTime(LocalTime.of(8, 0));
            subsidiary.setCloseTime(LocalTime.of(18, 0));
            subsidiary.setDaysOpen(SubsidiaryDaysOpen.MONDAY);
            subsidiary.setCompany(company);
            subsidiaryRepo.save(subsidiary);

            // Seed professionals
            Professional professional = new Professional();
            professional.setFirstName("John");
            professional.setLastName("Doe");
            professional.setEmail("john.doe@example.com");
            professional.setPhone("(123) 555-1234");
            professional.setDocumentNumber("12345678900");
            professional.setAddress("456 Doctor St, City");
            professional.setAvailableStart(LocalTime.of(9, 0));
            professional.setAvailableEnd(LocalTime.of(17, 0));
            professional.setSubsidiary(subsidiary);
            professionalRepo.save(professional);

            // Seed customers
            Customer customer = new Customer();
            customer.setFirstName("Jane");
            customer.setLastName("Smith");
            customer.setEmail("jane.smith@example.com");
            customer.setPhone("(123) 555-5678");
            customer.setDocumentNumber("98765432100");
            customer.setAddress("789 Patient St, City");
            customer.setDateOfBirth(LocalDate.of(1990, 1, 15));
            customerRepo.save(customer);

            // Seed services
            Item service1 = new Item();
            service1.setName("General Consultation");
            service1.setDescription("General medical consultation");
            service1.setPrice(new BigDecimal("150.00"));
            service1.setRequiresPrePayment(false);
            service1.setDurationMinutes(30);
            service1.setCompany(company);
            serviceRepo.save(service1);

            Item service2 = new Item();
            service2.setName("Specialized Consultation");
            service2.setDescription("Specialized medical consultation");
            service2.setPrice(new BigDecimal("250.00"));
            service2.setRequiresPrePayment(true);
            service2.setDurationMinutes(60);
            service2.setCompany(company);
            serviceRepo.save(service2);
        };
    }
}
```

2. Add the following to your `application.properties` to enable the dev profile:

```properties
spring.profiles.active=dev
```

## PostgreSQL Seeding

For PostgreSQL, you have two options:

### Option 1: Use the Same CommandLineRunner Approach

1. Configure the application to use PostgreSQL by adding the following to `application-prod.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/agenda
spring.datasource.username=agenda_user
spring.datasource.password=changeit
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

2. Run the application with the `prod` profile but without disabling the `dev` profile to ensure the seeder still runs:

```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev,prod
```

### Option 2: Use SQL Scripts

1. Create an SQL script file at `src/main/resources/data.sql`:

```sql
-- Only insert if tables are empty
DO $$
BEGIN
  IF (SELECT COUNT(*) FROM companies) = 0 THEN
    -- Insert company
    INSERT INTO companies (id, name, address, phone, created_at, updated_at, version, is_active, is_deleted)
    VALUES ('123e4567-e89b-12d3-a456-426614174000', 'HealthCare Clinic', '123 Medical St, City', '(123) 456-7890', NOW(), NOW(), 0, true, false);

    -- Insert subsidiary
    INSERT INTO subsidiaries (id, name, address, open_time, close_time, days_open, company_id, created_at, updated_at, version, is_active, is_deleted)
    VALUES ('123e4567-e89b-12d3-a456-426614174001', 'Main Branch', '123 Medical St, City', '08:00:00', '18:00:00', 'MONDAY', '123e4567-e89b-12d3-a456-426614174000', NOW(), NOW(), 0, true, false);

    -- Insert professional
    INSERT INTO professionals (id, first_name, last_name, full_name, document_number, address, phone, email, available_start, available_end, subsidiary_id, created_at, updated_at, version, is_active, is_deleted)
    VALUES ('123e4567-e89b-12d3-a456-426614174002', 'John', 'Doe', 'John Doe', '12345678900', '456 Doctor St, City', '(123) 555-1234', 'john.doe@example.com', '09:00:00', '17:00:00', '123e4567-e89b-12d3-a456-426614174001', NOW(), NOW(), 0, true, false);

    -- Insert customer
    INSERT INTO customers (id, first_name, last_name, full_name, email, phone, document_number, address, date_of_birth, created_at, updated_at, version, is_active, is_deleted)
    VALUES ('123e4567-e89b-12d3-a456-426614174003', 'Jane', 'Smith', 'Jane Smith', 'jane.smith@example.com', '(123) 555-5678', '98765432100', '789 Patient St, City', '1990-01-15', NOW(), NOW(), 0, true, false);

    -- Insert services
    INSERT INTO services (id, name, description, price, requires_pre_payment, duration_minutes, company_id, created_at, updated_at, version, is_active, is_deleted)
    VALUES
    ('123e4567-e89b-12d3-a456-426614174004', 'General Consultation', 'General medical consultation', 150.00, false, 30, '123e4567-e89b-12d3-a456-426614174000', NOW(), NOW(), 0, true, false),
    ('123e4567-e89b-12d3-a456-426614174005', 'Specialized Consultation', 'Specialized medical consultation', 250.00, true, 60, '123e4567-e89b-12d3-a456-426614174000', NOW(), NOW(), 0, true, false);
  END IF;
END $$;
```

2. Configure Spring Boot to initialize the database with this script by adding to `application-prod.properties`:

```properties
spring.sql.init.mode=always
```

## Sample Data Script

Here's a complete example of SQL that can be used to seed a full set of related data including appointments, service orders, and payments:

```sql
-- Insert sample appointment with payment
DO $$
DECLARE
  customer_id UUID;
  professional_id UUID;
  subsidiary_id UUID;
  service_id UUID;
  company_id UUID;
  service_order_id UUID;
  payment_id UUID;
  appointment_id UUID;
BEGIN
  -- Get existing IDs (assuming the basic data is already seeded)
  SELECT id INTO customer_id FROM customers LIMIT 1;
  SELECT id INTO professional_id FROM professionals LIMIT 1;
  SELECT id INTO subsidiary_id FROM subsidiaries LIMIT 1;
  SELECT id INTO service_id FROM services LIMIT 1;
  SELECT id INTO company_id FROM companies LIMIT 1;

  -- Create service order
  service_order_id := gen_random_uuid();
  INSERT INTO service_order (id, customer_id, professional_id, status, total_price, discount, created_at, updated_at, version, is_active, is_deleted)
  VALUES (service_order_id, customer_id, professional_id, 'COMPLETED', 150.00, 0.00, NOW(), NOW(), 0, true, false);

  -- Create payment
  payment_id := gen_random_uuid();
  INSERT INTO payment (id, service_order_id, payment_method, status, amount, installments, payment_date, created_at, updated_at, version, is_active, is_deleted)
  VALUES (payment_id, service_order_id, 'CREDIT_CARD', 'COMPLETED', 150.00, 1, NOW(), NOW(), NOW(), 0, true, false);

  -- Create appointment
  appointment_id := gen_random_uuid();
  INSERT INTO appointments (id, customer_id, professional_id, service_id, subsidiary_id, company_id, start_time, end_time, status, service_order_id, created_at, updated_at, version, is_active, is_deleted)
  VALUES (appointment_id, customer_id, professional_id, service_id, subsidiary_id, company_id,
         NOW() + INTERVAL '1 DAY', NOW() + INTERVAL '1 DAY' + INTERVAL '30 MINUTES',
         'CONFIRMED', service_order_id, NOW(), NOW(), 0, true, false);

  -- Associate service order with services
  INSERT INTO service_order_services (service_order_id, service_id)
  VALUES (service_order_id, service_id);

END $$;
```

Note: These scripts include UUID generation which is PostgreSQL specific. Adjust accordingly for other databases.

## Database Index Information

The application uses several database indexes to optimize performance:

| Entity | Index Name | Fields | Purpose |
|--------|------------|--------|---------|
| Appointment | idx_appointment_professional | professional_id | Speed up queries for a professional's appointments |
| Appointment | idx_appointment_start | start_time | Optimize date-range queries for appointments |
| Customer | idx_customer_fullname | full_name | Optimize customer name searches |
| Customer | idx_customer_email | email | Optimize customer lookup by email |
| Professional | idx_professional_fullname | full_name | Optimize professional name searches |

## Database Validation Rules

The application uses JPA validation annotations to ensure data integrity:

| Entity | Field | Validation | Description |
|--------|-------|------------|-------------|
| Customer | firstName | @NotBlank, @Size(max=50) | Required, max 50 chars |
| Customer | lastName | @NotBlank, @Size(max=50) | Required, max 50 chars |
| Customer | email | @Email, @NotBlank | Valid email format, required |
| Customer | documentNumber | @NotBlank | Required national ID |
| Customer | dateOfBirth | @Past | Must be in the past |
| Professional | firstName | @NotBlank, @Size(max=50) | Required, max 50 chars |
| Professional | lastName | @NotBlank, @Size(max=50) | Required, max 50 chars |
| Professional | email | @Email, @NotBlank | Valid email format, required |
| BusinessService | price | @DecimalMin("0.0") | Must be non-negative |
| BusinessService | name | @NotBlank | Required service name |