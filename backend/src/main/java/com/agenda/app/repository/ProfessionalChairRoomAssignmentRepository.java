package com.agenda.app.repository;

import com.agenda.app.model.ProfessionalChairRoomAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfessionalChairRoomAssignmentRepository
                extends JpaRepository<ProfessionalChairRoomAssignment, UUID> {

        /**
         * Busca todas as atribuições de um profissional para uma data específica
         */
        List<ProfessionalChairRoomAssignment> findByProfessional_IdAndDate(UUID professionalId, LocalDate date);

        /**
         * Busca todas as atribuições de uma cadeira/sala para uma data específica
         */
        List<ProfessionalChairRoomAssignment> findByChairRoom_IdAndDate(UUID chairRoomId, LocalDate date);

        /**
         * Busca atribuições recorrentes de um profissional para um dia da semana
         */
        List<ProfessionalChairRoomAssignment> findByProfessional_IdAndRecurringTrueAndDayOfWeek(UUID professionalId,
                        Integer dayOfWeek);

        /**
         * Busca atribuições recorrentes de uma cadeira/sala para um dia da semana
         */
        List<ProfessionalChairRoomAssignment> findByChairRoom_IdAndRecurringTrueAndDayOfWeek(UUID chairRoomId,
                        Integer dayOfWeek);

        /**
         * Busca uma atribuição específica (profissional + cadeira/sala + data)
         */
        Optional<ProfessionalChairRoomAssignment> findByProfessionalIdAndChairRoomIdAndDate(
                        UUID professionalId,
                        UUID chairRoomId,
                        LocalDate date);

        /**
         * Busca uma atribuição recorrente específica (profissional + cadeira/sala + dia
         * da semana)
         */
        Optional<ProfessionalChairRoomAssignment> findByProfessional_IdAndChairRoom_IdAndRecurringTrueAndDayOfWeek(
                        UUID professionalId, UUID chairRoomId, Integer dayOfWeek);

        /**
         * Busca atribuições para uma data específica (recorrentes ou não)
         * Inclui atribuições recorrentes para o dia da semana correspondente à data
         */

        @Query("SELECT a FROM ProfessionalChairRoomAssignment a " +
                        "WHERE a.professional.id = :professionalId " +
                        "AND (a.date = :date OR (a.recurring = true AND a.dayOfWeek = FUNCTION('DAYOFWEEK', :date)))")
        List<ProfessionalChairRoomAssignment> findEffectiveAssignmentsForDate(
                        @Param("professionalId") UUID professionalId,
                        @Param("date") LocalDate date);

        /**
         * Verifica se um profissional está atribuído a alguma cadeira/sala em uma data
         * específica
         */
        boolean existsByProfessional_IdAndDate(UUID professionalId, LocalDate date);

        /**
         * Verifica se uma cadeira/sala está atribuída a algum profissional em uma data
         * específica
         */
        boolean existsByChairRoom_IdAndDate(UUID chairRoomId, LocalDate date);
}