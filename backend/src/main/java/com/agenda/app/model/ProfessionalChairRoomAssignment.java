package com.agenda.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "professional_chair_room_assignments",
        indexes = {
                @Index(name = "idx_assignment_prof_date", columnList = "professional_id, date"),
                @Index(name = "idx_assignment_chair_date", columnList = "chair_room_id, date")
        })
@Getter @Setter @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProfessionalChairRoomAssignment extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chair_room_id", nullable = false)
    private ChairRoom chairRoom;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private LocalTime startTime;
    
    @Column(nullable = false)
    private LocalTime endTime;
    
    // Para atribuições recorrentes - usando @Min e @Max para garantir valores entre 1 e 7
    @Min(1)
    @Max(7)
    @Column(name = "day_of_week")
    private Integer dayOfWeek; // 1 = Segunda, 7 = Domingo
    
    @Column(name = "is_recurring")
    private boolean recurring = false;
    
    /**
     * Verifica se o profissional está atribuído à cadeira/sala no horário especificado
     */
    public boolean overlaps(LocalTime start, LocalTime end) {
        return !(end.isBefore(this.startTime) || start.isAfter(this.endTime));
    }
    
    /**
     * Cria uma instância de atribuição não recorrente
     */
    public static ProfessionalChairRoomAssignment createSingleDay(
            Professional professional, 
            ChairRoom chairRoom,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {
        
        ProfessionalChairRoomAssignment assignment = new ProfessionalChairRoomAssignment();
        assignment.setProfessional(professional);
        assignment.setChairRoom(chairRoom);
        assignment.setDate(date);
        assignment.setStartTime(startTime);
        assignment.setEndTime(endTime);
        assignment.setRecurring(false);
        assignment.setDayOfWeek(null);
        
        return assignment;
    }
    
    /**
     * Cria uma instância de atribuição recorrente
     */
    public static ProfessionalChairRoomAssignment createRecurring(
            Professional professional, 
            ChairRoom chairRoom,
            Integer dayOfWeek,
            LocalTime startTime,
            LocalTime endTime) {
        
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("Day of week must be between 1 (Monday) and 7 (Sunday)");
        }
        
        ProfessionalChairRoomAssignment assignment = new ProfessionalChairRoomAssignment();
        assignment.setProfessional(professional);
        assignment.setChairRoom(chairRoom);
        assignment.setDate(null); // Não tem data específica, é recorrente
        assignment.setStartTime(startTime);
        assignment.setEndTime(endTime);
        assignment.setRecurring(true);
        assignment.setDayOfWeek(dayOfWeek);
        
        return assignment;
    }
}