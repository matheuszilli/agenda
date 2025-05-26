import api from './api';
import { handleApiError } from './utils';

export interface ChairRoom {
    id?: string;
    name: string;
    subsidiaryId: string;
    description?: string;
    capacity: number;
    roomNumber: string;
}

export interface ChairRoomSchedule {
    id?: string;
    chairRoomId: string;
    date: string;
    dayOfWeek: number;
    openTime: string;
    closeTime: string;
    startTime?: string;
    endTime?: string;
    closed: boolean;
    customized?: boolean;
}

export interface DayScheduleConfig {
    open: boolean;
    openTime: string;
    closeTime: string;
}

export interface RecurringScheduleRequest {
    chairRoomId: string;
    weekSchedule: { [key: string]: DayScheduleConfig };
    startDate: string;
    endDate: string;
    replaceExisting: boolean;
}

export interface ExceptionScheduleRequest {
    chairRoomId: string;
    date: string;
    openTime: string;
    closeTime: string;
    closed: boolean;
    replaceExisting: boolean;
}

export interface ConflictCheckRequest {
    chairRoomId: string;
    daysOfWeek?: number[];
    dates?: string[];
    startDate?: string;
    endDate?: string;
    includeCustomized?: boolean;
}

export interface ConflictCheckResponse {
    chairRoomId: string;
    hasConflicts: boolean;
    conflictingDates: string[];
}

export const chairRoomService = {
    getAll: async (): Promise<ChairRoom[]> => {
        try {
            const response = await api.get('/api/chair-rooms');
            
            // Extrair array de cadeiras/salas do objeto paginado
            return response.data.content || [];
        } catch (error) {
            return handleApiError(error);
        }
    },

    getById: async (id: string): Promise<ChairRoom> => {
        try {
            const response = await api.get(`/api/chair-rooms/${id}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getBySubsidiary: async (subsidiaryId: string): Promise<ChairRoom[]> => {
        try {
            const response = await api.get(`/api/chair-rooms/subsidiary/${subsidiaryId}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    create: async (chairRoom: ChairRoom): Promise<ChairRoom> => {
        try {
            const response = await api.post('/api/chair-rooms', chairRoom);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    update: async (id: string, chairRoom: ChairRoom): Promise<ChairRoom> => {
        try {
            const response = await api.put(`/api/chair-rooms/${id}`, chairRoom);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    delete: async (id: string): Promise<void> => {
        try {
            await api.delete(`/api/chair-rooms/${id}`);
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Schedules
    getSchedules: async (chairRoomId: string): Promise<ChairRoomSchedule[]> => {
        try {
            const response = await api.get(`/api/chair-room-schedules/chair-room/${chairRoomId}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    createSchedule: async (schedule: ChairRoomSchedule): Promise<ChairRoomSchedule> => {
        try {
            const response = await api.post('/api/chair-room-schedules', schedule);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    updateSchedule: async (id: string, schedule: ChairRoomSchedule): Promise<ChairRoomSchedule> => {
        try {
            const response = await api.put(`/api/chair-room-schedules/${id}`, schedule);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    deleteSchedule: async (id: string): Promise<void> => {
        try {
            await api.delete(`/api/chair-room-schedules/${id}`);
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Novo método para verificar conflitos
    checkConflicts: async (request: ConflictCheckRequest): Promise<ConflictCheckResponse> => {
        try {
            const response = await api.post('/api/chair-room-schedules/conflicts-check', request);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Novo método para criar exceções (dias específicos)
    createException: async (request: ExceptionScheduleRequest): Promise<ChairRoomSchedule> => {
        try {
            const response = await api.post('/api/chair-room-schedules/exception', request);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Novo método para criar horários recorrentes
    createRecurringSchedule: async (request: RecurringScheduleRequest): Promise<ChairRoomSchedule[]> => {
        try {
            const response = await api.post('/api/chair-room-schedules/recurring', request, {
                params: { checkConflicts: true }
            });
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Método legado para compatibilidade (será removido no futuro)
    createOrUpdateSchedule: async (schedule: ChairRoomSchedule, replaceExisting: boolean = true): Promise<ChairRoomSchedule> => {
        try {
            // Converter para o novo formato ExceptionScheduleRequest
            const exceptionRequest: ExceptionScheduleRequest = {
                chairRoomId: schedule.chairRoomId,
                date: schedule.date,
                openTime: schedule.openTime,
                closeTime: schedule.closeTime,
                closed: schedule.closed,
                replaceExisting: replaceExisting
            };
            
            const response = await api.post('/api/chair-room-schedules/exception', exceptionRequest);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Método legado para compatibilidade (será removido no futuro)
    createRecurringScheduleViaLegacy: async (
        chairRoomId: string,
        daysOfWeek: number[],
        openTime: string,
        closeTime: string,
        startDate: string,
        endDate: string,
        replaceExisting: boolean = false
    ): Promise<ChairRoomSchedule[]> => {
        try {
            // Converter para o novo formato RecurringScheduleRequest
            const weekSchedule: { [key: string]: DayScheduleConfig } = {};
            
            // Inicializar todos os dias da semana como fechados
            for (let i = 0; i < 7; i++) {
                weekSchedule[i] = {
                    open: false,
                    openTime: '08:00',
                    closeTime: '18:00'
                };
            }
            
            // Configurar os dias selecionados como abertos
            for (const day of daysOfWeek) {
                weekSchedule[day] = {
                    open: true,
                    openTime: openTime,
                    closeTime: closeTime
                };
            }
            
            const recurringRequest: RecurringScheduleRequest = {
                chairRoomId,
                weekSchedule,
                startDate,
                endDate,
                replaceExisting
            };
            
            return await chairRoomService.createRecurringSchedule(recurringRequest);
        } catch (error) {
            return handleApiError(error);
        }
    }
};