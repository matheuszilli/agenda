import api from './api';
import type { Address } from './companyService';

export interface Subsidiary {
    id?: string;
    name: string;
    address: Address;
    documentNumber: string;
    companyId: string;
}

export const subsidiaryService = {
    getAll: async () => {
        const response = await api.get('/subsidiaries');
        return response.data;
    },

    getById: async (id: string) => {
        const response = await api.get(`/subsidiaries/${id}`);
        return response.data;
    },

    create: async (subsidiary: Subsidiary) => {
        const response = await api.post('/subsidiaries', subsidiary);
        return response.data;
    },

    update: async (id: string, subsidiary: Subsidiary) => {
        const response = await api.put(`/subsidiaries/${id}`, subsidiary);
        return response.data;
    },

    delete: async (id: string) => {
        await api.delete(`/subsidiaries/${id}`);
    }
};