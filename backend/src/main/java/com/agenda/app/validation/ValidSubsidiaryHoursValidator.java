package com.agenda.app.validation;

import com.agenda.app.model.ChairRoom;
import com.agenda.app.model.Subsidiary;
import com.agenda.app.model.SubsidiaryScheduleEntry;
import com.agenda.app.repository.ChairRoomRepository;
import com.agenda.app.repository.SubsidiaryScheduleEntryRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Validador que verifica se o horário de uma sala está dentro do horário da subsidiária
 */
public class ValidSubsidiaryHoursValidator implements ConstraintValidator<ValidSubsidiaryHours, Object> {

    private String chairRoomIdField;
    private String dateField;
    private String openTimeField;
    private String closeTimeField;
    
    @Autowired
    private ChairRoomRepository chairRoomRepository;
    
    @Autowired
    private SubsidiaryScheduleEntryRepository subsidiaryScheduleRepository;

    @Override
    public void initialize(ValidSubsidiaryHours constraintAnnotation) {
        this.chairRoomIdField = constraintAnnotation.chairRoomIdField();
        this.dateField = constraintAnnotation.dateField();
        this.openTimeField = constraintAnnotation.openTimeField();
        this.closeTimeField = constraintAnnotation.closeTimeField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        UUID chairRoomId = (UUID) new BeanWrapperImpl(value).getPropertyValue(chairRoomIdField);
        LocalDate date = (LocalDate) new BeanWrapperImpl(value).getPropertyValue(dateField);
        LocalTime openTime = (LocalTime) new BeanWrapperImpl(value).getPropertyValue(openTimeField);
        LocalTime closeTime = (LocalTime) new BeanWrapperImpl(value).getPropertyValue(closeTimeField);

        // Se algum valor for nulo, deixamos outra validação tratar
        if (chairRoomId == null || date == null || openTime == null || closeTime == null) {
            return true;
        }

        try {
            // Buscar a sala/cadeira
            Optional<ChairRoom> chairRoomOpt = chairRoomRepository.findById(chairRoomId);
            if (chairRoomOpt.isEmpty()) {
                return true; // Deixar outra validação tratar
            }
            
            ChairRoom chairRoom = chairRoomOpt.get();
            Subsidiary subsidiary = chairRoom.getSubsidiary();
            
            // Buscar agendamento da subsidiária para esta data
            Optional<SubsidiaryScheduleEntry> scheduleOpt = subsidiaryScheduleRepository
                    .findBySubsidiaryIdAndDate(subsidiary.getId(), date)
                    .stream()
                    .findFirst();
            
            if (scheduleOpt.isEmpty()) {
                return true; // Sem horário definido para a subsidiária nesta data
            }
            
            SubsidiaryScheduleEntry subsidiarySchedule = scheduleOpt.get();
            
            // Se a subsidiária estiver fechada, qualquer horário é inválido
            if (subsidiarySchedule.isClosed()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Subsidiary is closed on this date")
                        .addPropertyNode(dateField)
                        .addConstraintViolation();
                return false;
            }
            
            // Verificar se o horário está dentro do horário da subsidiária
            boolean isValid = !openTime.isBefore(subsidiarySchedule.getOpenTime()) && 
                              !closeTime.isAfter(subsidiarySchedule.getCloseTime());
            
            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Chair room schedule must be within subsidiary hours: " + 
                        subsidiarySchedule.getOpenTime() + " - " + subsidiarySchedule.getCloseTime())
                        .addPropertyNode(openTimeField)
                        .addConstraintViolation();
            }
            
            return isValid;
        } catch (Exception e) {
            return true; // Em caso de erro, deixamos passar e o serviço tratará
        }
    }
}