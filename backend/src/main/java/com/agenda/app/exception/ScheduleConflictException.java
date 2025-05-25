package com.agenda.app.exception;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Exceção lançada quando há conflitos de horário
 */
public class ScheduleConflictException extends ConflictException {
    private final UUID chairRoomId;
    private final List<LocalDate> conflictingDates;

    public ScheduleConflictException(String message, UUID chairRoomId, List<LocalDate> conflictingDates) {
        super(message);
        this.chairRoomId = chairRoomId;
        this.conflictingDates = conflictingDates;
    }

    public UUID getChairRoomId() {
        return chairRoomId;
    }

    public List<LocalDate> getConflictingDates() {
        return conflictingDates;
    }
}