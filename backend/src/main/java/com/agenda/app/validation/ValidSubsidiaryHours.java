package com.agenda.app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validação para garantir que o horário de uma sala está dentro do horário da subsidiária
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidSubsidiaryHoursValidator.class)
@Documented
public @interface ValidSubsidiaryHours {
    String message() default "Chair room schedule must be within subsidiary operating hours";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    String chairRoomIdField() default "chairRoomId";
    String dateField() default "date";
    String openTimeField() default "openTime";
    String closeTimeField() default "closeTime";
}