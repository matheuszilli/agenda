package com.agenda.app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalTime;

/**
 * Validador que verifica se um horário final é posterior a um horário inicial
 */
public class TimeRangeValidator implements ConstraintValidator<TimeRange, Object> {

    private String startTimeField;
    private String endTimeField;
    private String message;

    @Override
    public void initialize(TimeRange constraintAnnotation) {
        this.startTimeField = constraintAnnotation.startTimeField();
        this.endTimeField = constraintAnnotation.endTimeField();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null values
        }

        LocalTime startTime = (LocalTime) new BeanWrapperImpl(value).getPropertyValue(startTimeField);
        LocalTime endTime = (LocalTime) new BeanWrapperImpl(value).getPropertyValue(endTimeField);

        // If either time is null, we'll let @NotNull handle it
        if (startTime == null || endTime == null) {
            return true;
        }

        boolean isValid = endTime.isAfter(startTime);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(endTimeField)
                    .addConstraintViolation();
        }

        return isValid;
    }
}