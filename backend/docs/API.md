# API Reference

## Table of Contents

- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Endpoint Details](#endpoint-details)
- [OpenAPI Specification](#openapi-specification)

## API Endpoints

| Method | Endpoint | Description | Controller |
|--------|----------|-------------|------------|
| POST | `/api/appointments` | Create a new appointment | AppointmentController |

## Authentication

Authentication is not implemented in the current MVP. Future releases may include OAuth2 or JWT-based authentication.

## Endpoint Details

### Create Appointment

Creates a new appointment in the system.

**Endpoint**: `POST /api/appointments`

**Request Body**:
```json
{
  "professionalId": "123e4567-e89b-12d3-a456-426614174000",
  "customerId": "123e4567-e89b-12d3-a456-426614174001",
  "subsidiaryId": "123e4567-e89b-12d3-a456-426614174002",
  "serviceId": "123e4567-e89b-12d3-a456-426614174003",
  "startTime": "2023-12-10T14:30:00",
  "paymentId": "123e4567-e89b-12d3-a456-426614174004"
}
```

**Response**:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174005",
  "professionalId": "123e4567-e89b-12d3-a456-426614174000",
  "customerId": "123e4567-e89b-12d3-a456-426614174001",
  "subsidiaryId": "123e4567-e89b-12d3-a456-426614174002",
  "serviceId": "123e4567-e89b-12d3-a456-426614174003",
  "startTime": "2023-12-10T14:30:00",
  "endTime": "2023-12-10T15:00:00",
  "status": "CONFIRMED"
}
```

**Status Codes**:
- `201 Created` - Appointment created successfully
- `400 Bad Request` - Invalid input parameters
- `402 Payment Required` - Payment required for this appointment
- `409 Conflict` - Scheduling conflict detected
- `500 Internal Server Error` - Server error

**Required Validations**:
- All IDs must be valid UUIDs
- Start time must be in the future
- Referenced entities must exist in the system

**Example cURL Request**:
```bash
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -d '{
    "professionalId": "123e4567-e89b-12d3-a456-426614174000",
    "customerId": "123e4567-e89b-12d3-a456-426614174001", 
    "subsidiaryId": "123e4567-e89b-12d3-a456-426614174002",
    "serviceId": "123e4567-e89b-12d3-a456-426614174003",
    "startTime": "2023-12-10T14:30:00",
    "paymentId": "123e4567-e89b-12d3-a456-426614174004"
  }'
```

## OpenAPI Specification

```yaml
openapi: 3.0.3
info:
  title: Agenda MVP API
  description: API for clinic appointment scheduling system
  version: 0.0.1-SNAPSHOT
servers:
  - url: http://localhost:8080
    description: Development server
paths:
  /api/appointments:
    post:
      summary: Create a new appointment
      operationId: createAppointment
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/AppointmentRequest'
      responses:
        '201':
          description: Appointment created successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/AppointmentResponse'
        '400':
          description: Invalid input parameters
        '402':
          description: Payment required for this appointment
        '409':
          description: Scheduling conflict detected
components:
  schemas:
    AppointmentRequest:
      type: object
      required:
        - professionalId
        - customerId
        - subsidiaryId
        - serviceId
        - startTime
      properties:
        professionalId:
          type: string
          format: uuid
          description: ID of the professional providing the service
        customerId:
          type: string
          format: uuid
          description: ID of the customer booking the appointment
        subsidiaryId:
          type: string
          format: uuid
          description: ID of the subsidiary where the appointment will take place
        serviceId:
          type: string
          format: uuid
          description: ID of the service to be provided
        startTime:
          type: string
          format: date-time
          description: Start time of the appointment in ISO-8601 format
        paymentId:
          type: string
          format: uuid
          description: Optional ID of a payment made for this appointment
    AppointmentResponse:
      type: object
      properties:
        id:
          type: string
          format: uuid
          description: Unique identifier for the appointment
        professionalId:
          type: string
          description: ID of the professional
        customerId:
          type: string
          description: ID of the customer
        subsidiaryId:
          type: string
          description: ID of the subsidiary
        serviceId:
          type: string
          description: ID of the service
        startTime:
          type: string
          format: date-time
          description: Start time of the appointment
        endTime:
          type: string
          format: date-time
          description: End time of the appointment
        status:
          type: string
          enum: [PENDING, CANCELLED, CONFIRMED, NOT_CONFIRMED, ATTENDING, COMPLETED]
          description: Current status of the appointment
```