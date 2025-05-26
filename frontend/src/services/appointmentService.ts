import api from './api';
import { handleApiError } from './utils';

export const AppointmentStatus = {
    SCHEDULED: 'SCHEDULED',
    CONFIRMED: 'CONFIRMED',
    IN_PROGRESS: 'IN_PROGRESS',
    COMPLETED: 'COMPLETED',
    CANCELED: 'CANCELED',
    NO_SHOW: 'NO_SHOW'
} as const;

export type AppointmentStatus = typeof AppointmentStatus[keyof typeof AppointmentStatus];

export interface Appointment {
    id?: string;
    customerId: string;
    professionalId: string;
    chairRoomId: string;
    itemId: string;
    subsidiaryId: string;
    companyId: string;
    startTime: string;
    endTime: string;
    notes?: string;
    status?: AppointmentStatus;
    paymentId?: string;
}

export interface AppointmentResponse {
    id: string;
    professionalId: string;
    customerId: string;
    subsidiaryId: string;
    companyId: string;
    itemId: string;
    startTime: string;
    endTime: string;
    status: AppointmentStatus;
}

export interface AvailabilityRequest {
    subsidiaryId: string;
    professionalId?: string;
    chairRoomId?: string;
    itemId?: string;
    date: string;
}

export interface AvailabilitySlot {
    startTime: string;
    endTime: string;
    available: boolean;
    professionalId?: string;
    chairRoomId?: string;
}

export const appointmentService = {
    getAll: async (): Promise<AppointmentResponse[]> => {
        try {
            const response = await api.get('/appointments');
            return response.data.content || response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getById: async (id: string): Promise<AppointmentResponse> => {
        try {
            const response = await api.get(`/appointments/${id}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getByCustomer: async (customerId: string): Promise<AppointmentResponse[]> => {
        try {
            const response = await api.get(`/appointments/by-customer/${customerId}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getByProfessional: async (professionalId: string, date?: string): Promise<AppointmentResponse[]> => {
        try {
            let url = `/appointments/by-professional/${professionalId}`;
            if (date) {
                url += `?date=${date}`;
            }
            const response = await api.get(url);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getBySubsidiary: async (subsidiaryId: string, date?: string): Promise<AppointmentResponse[]> => {
        try {
            let url = `/appointments/by-subsidiary/${subsidiaryId}`;
            if (date) {
                url += `?date=${date}`;
            }
            const response = await api.get(url);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    create: async (appointment: Appointment): Promise<AppointmentResponse> => {
        try {
            const response = await api.post('/appointments', appointment);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    update: async (id: string, appointment: Appointment): Promise<AppointmentResponse> => {
        try {
            const response = await api.put(`/appointments/${id}`, appointment);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    delete: async (id: string): Promise<void> => {
        try {
            await api.delete(`/appointments/${id}`);
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Verifica disponibilidade para agendamento
    checkAvailability: async (request: AvailabilityRequest): Promise<AvailabilitySlot[]> => {
        try {
            const response = await api.post('/availability/check', request);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Verifica conflitos para um agendamento específico
    checkConflicts: async (appointment: Appointment): Promise<boolean> => {
        try {
            const response = await api.post('/appointments/check-conflicts', appointment);
            return response.data.hasConflicts;
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Alterar status de um agendamento
    updateStatus: async (id: string, status: AppointmentStatus): Promise<AppointmentResponse> => {
        try {
            const response = await api.patch(`/appointments/${id}/status`, { status });
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    }
};