# Business Rules

## Table of Contents

- [Appointment Scheduling Rules](#appointment-scheduling-rules)
- [Payment Rules](#payment-rules)
- [Status Management Rules](#status-management-rules)
- [Rules Excluded from MVP](#rules-excluded-from-mvp)

## Appointment Scheduling Rules

### Date and Time Validation

1. **Future Date Requirement**:
   - Appointments can only be scheduled for the current day or future dates
   - Code implementation: 
   ```java
   if (start.toLocalDate().isBefore(LocalDate.now()))
       throw new IllegalArgumentException("Start must be today or later");
   ```

2. **Service Duration**:
   - Appointment end time is automatically calculated based on the service duration
   - Code implementation:
   ```java
   LocalDateTime end = start.plusMinutes(svc.getDurationMinutes());
   ```

### Professional Availability

1. **No Overlapping Appointments Across Subsidiaries**:
   - A professional cannot have appointments at different subsidiaries that overlap in time
   - This prevents scheduling conflicts where a professional would need to be in two places at once
   - Code implementation:
   ```java
   if (appointmentRepository.existsOverlapInOtherSubsidiary(
           prof.getId(), sub.getId(), start, end)) {
       throw new ConflictException("Profissional já tem horário nesse período em outra filial");
   }
   ```

2. **Professional Working Hours**:
   - Professionals have defined working hours (`availableStart` and `availableEnd`)
   - (Note: The current code doesn't explicitly check this constraint, which may be a future implementation)

### Subsidiary Operating Hours

1. **Branch Operating Hours**:
   - Each subsidiary has defined opening and closing times
   - Each subsidiary has defined days of operation
   - (Note: The current code doesn't explicitly check this constraint, which may be a future implementation)

## Payment Rules

### Pre-Payment Requirements

1. **Service-Specific Pre-Payment**:
   - Some services require pre-payment, controlled by the `requiresPrePayment` flag on `BusinessService`
   - Pre-payment is conditionally enforced based on the appointment time

2. **Pre-Payment Window**:
   - Pre-payment is required when booking an appointment within 2 days of the appointment time
   - If booking more than 2 days in advance, pre-payment may not be required even for services that normally require it
   - Code implementation:
   ```java
   LocalDateTime deadline = startTime.minusDays(2);
   if (now.isBefore(deadline)) {
       return null; // No pre-payment needed yet
   }
   ```

3. **Payment Validation**:
   - When payment is required, the system verifies that:
     - Payment ID is provided
     - Payment exists in the system
     - Payment status is `COMPLETED`
   - If these conditions aren't met, a `PaymentRequiredException` is thrown

## Status Management Rules

### Appointment Status Flow

1. **Initial Status Determination**:
   - The initial status of an appointment is determined based on:
     - Whether pre-payment is required
     - Whether payment is provided
     - Whether the appointment is explicitly confirmed

2. **Status Rules**:
   - `PENDING`: Payment is required but not provided yet
   - `NOT_CONFIRMED`: Default status for appointments that don't require pre-payment or have pre-payment but aren't explicitly confirmed
   - `CONFIRMED`: Explicitly confirmed appointments (e.g., by receptionist)
   - `ATTENDING`, `COMPLETED`: Status changes that occur during or after the appointment

3. **Status Transitions**:
   ```
   PENDING → NOT_CONFIRMED → CONFIRMED → ATTENDING → COMPLETED
   ```
   (Any status can transition to `CANCELLED`)

## Rules Excluded from MVP

The following business rules are not implemented in the current MVP but may be considered for future releases:

1. **Room Allocation**:
   - Assigning specific rooms or resources to appointments
   - Managing room availability and conflicts

2. **Appointment Reminders**:
   - Automatic reminder notifications for upcoming appointments
   - Reminder job scheduling and processing

3. **Calendar Integration**:
   - Synchronization with external calendar systems
   - iCalendar/ICS format support

4. **Advanced Recurring Appointments**:
   - Support for recurring appointments (daily, weekly, monthly)
   - Exceptions and modifications to recurring appointments

5. **Waitlist Management**:
   - Managing waitlists for fully booked time slots
   - Automatic notifications when slots become available

6. **Cancellation Policies**:
   - Enforcement of cancellation windows
   - Late cancellation fees
   - No-show penalties

7. **Resource Management**:
   - Tracking and allocating equipment, tools, or other resources needed for appointments