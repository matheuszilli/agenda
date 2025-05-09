# Roadmap

This document outlines the planned features, improvements, and technical debt to be addressed in future releases of the Agenda MVP application.

## Table of Contents

- [Planned Features](#planned-features)
- [Technical Debt](#technical-debt)
- [Architecture Improvements](#architecture-improvements)
- [Release Timeline](#release-timeline)

## Planned Features

### Short-Term (Next 1-2 Releases)

#### Appointment Management Enhancements

- **Reminder System**
  - Scheduled job to send reminders for upcoming appointments
  - Email and SMS notification support
  - Configurable reminder times (24h, 12h, 2h before appointment)

- **Room Allocation**
  - Assign specific rooms to appointments
  - Room availability checking during scheduling
  - Room management interface

- **Professional Availability Calendar**
  - Enhanced view of professional availability
  - Block out times for breaks, meetings, etc.
  - Weekly/monthly availability patterns

#### Payment System Enhancements

- **External Payment Integration**
  - Integration with payment gateways (e.g., Stripe, PayPal)
  - Support for online pre-payments
  - Automated payment confirmation

- **Invoice Generation**
  - Generate PDF invoices for appointments
  - Email invoices to customers
  - Track payment history

#### User Experience

- **Customer Portal**
  - Self-service appointment booking
  - View upcoming and past appointments
  - Update personal information
  - View and download medical records

### Medium-Term (3-6 Months)

- **Multi-Company Support**
  - Enhanced multi-tenant functionality
  - Company-specific configuration
  - White-labeling options

- **Reporting and Analytics**
  - Business intelligence dashboard
  - Custom report generation
  - Performance metrics for professionals and services

- **Waiting List Management**
  - Register customers for waiting list when preferred slots are unavailable
  - Automatic notification when slots become available
  - Priority management

### Long-Term (6+ Months)

- **Integration with Medical Equipment**
  - Connect with diagnostic equipment
  - Import test results directly to medical records
  - Schedule equipment usage

- **Telehealth Support**
  - Virtual appointment scheduling
  - Video conferencing integration
  - Digital document sharing

- **Advanced Recurrence**
  - Complex recurring appointment patterns
  - Exception handling for recurring appointments
  - Series modification options

## Technical Debt

### Code Quality

- **Improve Test Coverage**
  - Add unit tests for all service methods
  - Implement integration tests for controllers
  - Set up performance tests for critical flows

- **Code Documentation**
  - Add JavaDoc to all public methods
  - Create architectural decision records (ADRs)
  - Document complex business rules

- **Refactoring**
  - Break down large service methods into smaller ones
  - Improve naming conventions
  - Extract common validation logic

### Infrastructure

- **Database Migrations**
  - Replace Hibernate auto-schema update with Flyway or Liquibase
  - Create proper migration scripts
  - Implement versioned database changes

- **Caching**
  - Implement caching for frequently accessed data
  - Configure Redis or Caffeine as cache provider
  - Optimize read-heavy operations

- **Monitoring and Logging**
  - Implement centralized logging
  - Set up application performance monitoring
  - Create alerts for critical errors

## Architecture Improvements

### API Design

- **Complete RESTful API**
  - Implement CRUD operations for all entities
  - Follow REST best practices
  - Create comprehensive API documentation
  - Implement HATEOAS for better API discoverability

- **GraphQL Support**
  - Add GraphQL API alongside REST
  - Optimize for frontend data requirements
  - Implement subscriptions for real-time updates

### Security Enhancements

- **Authentication and Authorization**
  - Implement OAuth2/JWT authentication
  - Role-based access control
  - Fine-grained permissions system

- **Data Protection**
  - Implement GDPR compliance features
  - Data anonymization for reporting
  - Enhanced audit logging for sensitive data access

### Scalability

- **Microservices Transition**
  - Split monolith into domain-specific services
  - Implement API gateway
  - Service discovery and registration

- **Async Processing**
  - Message queues for long-running operations
  - Event-driven architecture for certain features
  - Background processing for reports and notifications

## Release Timeline

| Version | Release Date   | Major Features                                              |
|---------|---------------|----------------------------------------------------------|
| 0.2.0   | Q3 2023       | Complete CRUD API, Reminder System, Integration Tests    |
| 0.3.0   | Q4 2023       | Room Allocation, External Payment Integration            |
| 0.4.0   | Q1 2024       | Customer Portal, Invoice Generation                      |
| 1.0.0   | Q2 2024       | Multi-Company Support, Production Readiness              |
| 1.1.0   | Q3 2024       | Reporting and Analytics, Enhanced Security               |
| 1.2.0   | Q4 2024       | Waiting List, Advanced Recurrence                        |
| 2.0.0   | Q2 2025       | Telehealth Support, Microservices Architecture           |

Note: This timeline is tentative and subject to change based on business priorities and available resources.