import api from './api';

export interface Address {
    street: string;
    number: string;
    complement?: string;
    neighbourhood?: string;
    city: string;
    state: string;
    zipCode: string;
}

export interface Company {
    id?: string;
    name: string;
    address: Address;
    phone: string;
    documentNumber: string;
}

export const companyService = {
    getAll: async () => {
        const response = await api.get('/companies');
        return response.data;
    },

    getById: async (id: string) => {
        const response = await api.get(`/companies/${id}`);
        return response.data;
    },

    create: async (company: Company) => {
        const response = await api.post('/companies', company);
        return response.data;
    },

    update: async (id: string, company: Company) => {
        const response = await api.put(`/companies/${id}`, company);
        return response.data;
    },

    delete: async (id: string) => {
        await api.delete(`/companies/${id}`);
    }
};