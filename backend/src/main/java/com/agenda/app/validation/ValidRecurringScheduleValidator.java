package com.agenda.app.validation;

import com.agenda.app.dto.DayScheduleConfigDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDate;
import java.util.Map;

/**
 * Validador para verificar se uma configuração de agendamento recorrente é válida
 */
public class ValidRecurringScheduleValidator implements ConstraintValidator<ValidRecurringSchedule, Object> {

    private String startDateField;
    private String endDateField;
    private String weekScheduleField;
    private String message;

    @Override
    public void initialize(ValidRecurringSchedule constraintAnnotation) {
        this.startDateField = constraintAnnotation.startDateField();
        this.endDateField = constraintAnnotation.endDateField();
        this.weekScheduleField = constraintAnnotation.weekScheduleField();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null values
        }

        LocalDate startDate = (LocalDate) new BeanWrapperImpl(value).getPropertyValue(startDateField);
        LocalDate endDate = (LocalDate) new BeanWrapperImpl(value).getPropertyValue(endDateField);
        Map<Integer, DayScheduleConfigDTO> weekSchedule = (Map<Integer, DayScheduleConfigDTO>) 
                new BeanWrapperImpl(value).getPropertyValue(weekScheduleField);

        // Se algum campo for nulo, deixamos outra validação tratar
        if (startDate == null || endDate == null || weekSchedule == null) {
            return true;
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();
        
        // Verificar se a data final é posterior à data inicial
        if (endDate.isBefore(startDate)) {
            context.buildConstraintViolationWithTemplate(
                    "End date must be after start date")
                    .addPropertyNode(endDateField)
                    .addConstraintViolation();
            isValid = false;
        }

        // Verificar se há pelo menos um dia configurado como aberto
        boolean hasOpenDay = weekSchedule.values().stream()
                .anyMatch(DayScheduleConfigDTO::isOpen);
        
        if (!hasOpenDay) {
            context.buildConstraintViolationWithTemplate(
                    "At least one day must be configured as open")
                    .addPropertyNode(weekScheduleField)
                    .addConstraintViolation();
            isValid = false;
        }
        
        // Verificar dias da semana válidos (1-7)
        for (Integer day : weekSchedule.keySet()) {
            if (day < 0 || day > 6) {
                context.buildConstraintViolationWithTemplate(
                        "Day of week must be between 0 and 6")
                        .addPropertyNode(weekScheduleField)
                        .addConstraintViolation();
                isValid = false;
                break;
            }
        }

        return isValid;
    }
}