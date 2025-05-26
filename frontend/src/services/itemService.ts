import api from './api';
import { handleApiError } from './utils';

export interface Item {
    id?: string;
    name: string;
    description?: string;
    price: number;
    durationMinutes: number;
    requiresPrePayment: boolean;
    companyId: string;
    subsidiaryId?: string;
    active?: boolean;
}

export interface ProfessionalServiceAssignment {
    professionalId: string;
    serviceId: string;
}

export const itemService = {
    getAll: async (): Promise<Item[]> => {
        try {
            const response = await api.get('/services');
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getById: async (id: string): Promise<Item> => {
        try {
            const response = await api.get(`/services/${id}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getByCompany: async (companyId: string): Promise<Item[]> => {
        try {
            const response = await api.get(`/services/by-company/${companyId}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getBySubsidiary: async (subsidiaryId: string): Promise<Item[]> => {
        try {
            const response = await api.get(`/v1/services/by-subsidiary/${subsidiaryId}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getByProfessional: async (professionalId: string): Promise<Item[]> => {
        try {
            const response = await api.get(`/services/by-professional/${professionalId}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    create: async (item: Item): Promise<Item> => {
        try {
            const response = await api.post('/services', item);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    update: async (id: string, item: Item): Promise<Item> => {
        try {
            const response = await api.put(`/services/${id}`, item);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    delete: async (id: string): Promise<void> => {
        try {
            await api.delete(`/services/${id}`);
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Methods for assigning services to professionals
    assignToProfessional: async (assignment: ProfessionalServiceAssignment): Promise<void> => {
        try {
            await api.post('/professional-services', assignment);
        } catch (error) {
            return handleApiError(error);
        }
    },

    removeFromProfessional: async (assignment: ProfessionalServiceAssignment): Promise<void> => {
        try {
            await api.delete('/professional-services', { data: assignment });
        } catch (error) {
            return handleApiError(error);
        }
    }
};