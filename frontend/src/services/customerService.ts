import api from './api';
import { handleApiError } from './utils';
import type { Address } from './companyService';

export interface Customer {
    id?: string;
    firstName: string;
    lastName: string;
    fullName?: string;
    email: string;
    phone: string;
    documentNumber: string;
    address: Address;
    companyId: string;
}

export const customerService = {
    getAll: async (): Promise<Customer[]> => {
        try {
            const response = await api.get('/customers');
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getById: async (id: string): Promise<Customer> => {
        try {
            const response = await api.get(`/customers/${id}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getByCompany: async (companyId: string): Promise<Customer[]> => {
        try {
            const response = await api.get(`/api/companies/${companyId}/customers`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    create: async (customer: Customer): Promise<Customer> => {
        try {
            const response = await api.post(`/api/companies/${customer.companyId}/customers`, customer);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    update: async (id: string, customer: Customer): Promise<Customer> => {
        try {
            const response = await api.put(`/api/companies/${customer.companyId}/customers/${id}`, customer);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    delete: async (companyId: string, id: string): Promise<void> => {
        try {
            await api.delete(`/api/companies/${companyId}/customers/${id}`);
        } catch (error) {
            return handleApiError(error);
        }
    },

    search: async (companyId: string, query: string): Promise<Customer[]> => {
        try {
            const response = await api.get(`/api/companies/${companyId}/customers/search?query=${query}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    }
};