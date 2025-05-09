# Changelog

All notable changes to the Agenda MVP project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Documentation structure with README, architecture docs, domain model, and more
- Setup guides for development, deployment, and testing

## [0.0.1-SNAPSHOT] - 2023-05-09

### Added
- Initial MVP implementation
- Core domain model with Company, Subsidiary, Professional, and Customer entities
- Appointment scheduling functionality with conflict detection
- Payment service with pre-payment requirements
- JPA repositories with custom queries
- Initial REST API endpoint for appointment creation
- H2 database configuration for development
- Integration with MapStruct for DTO mapping
- Basic validation for appointment scheduling

### Changed
- Enhanced BaseEntity with audit fields
- Improved appointment status management

### Fixed
- Professional availability conflict checking
- Appointment date validation