package com.agenda.app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validação para garantir que uma programação recorrente tenha configuração válida:
 * - A data final deve ser posterior à data inicial
 * - Deve ter pelo menos um dia configurado como aberto
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRecurringScheduleValidator.class)
@Documented
public @interface ValidRecurringSchedule {
    String message() default "Invalid recurring schedule configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    String startDateField() default "startDate";
    String endDateField() default "endDate";
    String weekScheduleField() default "weekSchedule";
}