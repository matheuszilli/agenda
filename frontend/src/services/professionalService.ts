import api from './api';
import { handleApiError } from './utils';

export interface Professional {
    id?: string;
    firstName: string;
    lastName: string;
    fullName?: string;
    documentNumber: string;
    address: Address;
    phone: string;
    email: string;
    subsidiaryId: string;
    services?: ProfessionalServiceConfig[];
}

export interface Address {
    street: string;
    number: string;
    complement?: string;
    neighbourhood?: string;
    city: string;
    state: string;
    zipCode: string;
}

export interface ProfessionalServiceConfig {
    serviceId: string;
    professionalId?: string;
    commissionType?: string;
    commissionValue?: number;
}

export const professionalService = {
    listAll: async (): Promise<Professional[]> => {
        try {
            const response = await api.get('/professionals');
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getById: async (id: string): Promise<Professional> => {
        try {
            const response = await api.get(`/professionals/${id}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getBySubsidiary: async (subsidiaryId: string): Promise<Professional[]> => {
        try {
            const response = await api.get(`/professionals/by-subsidiary/${subsidiaryId}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    create: async (professional: Professional): Promise<Professional> => {
        try {
            const response = await api.post('/professionals', professional);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    update: async (id: string, professional: Professional): Promise<Professional> => {
        try {
            const response = await api.put(`/professionals/${id}`, professional);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    delete: async (id: string): Promise<void> => {
        try {
            await api.delete(`/professionals/${id}`);
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Services associations
    getServices: async (professionalId: string): Promise<ProfessionalServiceConfig[]> => {
        try {
            const response = await api.get(`/professionals/${professionalId}/services`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    addService: async (professionalId: string, config: ProfessionalServiceConfig): Promise<void> => {
        try {
            await api.post(`/professionals/${professionalId}/services`, config);
        } catch (error) {
            return handleApiError(error);
        }
    },

    removeService: async (professionalId: string, serviceId: string): Promise<void> => {
        try {
            await api.delete(`/professionals/${professionalId}/services/${serviceId}`);
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Schedule management
    getSchedule: async (professionalId: string): Promise<any[]> => {
        try {
            const response = await api.get(`/professionals/${professionalId}/schedule`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    addScheduleEntry: async (professionalId: string, scheduleEntry: any): Promise<void> => {
        try {
            await api.post(`/professionals/${professionalId}/schedule`, scheduleEntry);
        } catch (error) {
            return handleApiError(error);
        }
    },

    removeScheduleEntry: async (professionalId: string, entryId: string): Promise<void> => {
        try {
            await api.delete(`/professionals/${professionalId}/schedule/${entryId}`);
        } catch (error) {
            return handleApiError(error);
        }
    }
};