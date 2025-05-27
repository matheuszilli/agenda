import api from './api';
import type { Address } from './companyService';

export interface Subsidiary {
    id?: string;
    name: string;
    address: Address;
    documentNumber: string;
    companyId: string;
    openTime?: string;
    closeTime?: string;
}

export interface SubsidiarySchedule {
    id: string;
    subsidiaryId: string;
    date: string;
    openTime: string;
    closeTime: string;
    closed: boolean;
}

// Interface para erro da API (reuso do que já existe no companyService)
export interface ApiError {
    message: string;
    details: string;
    timestamp: string;
    path?: string;
}

const handleApiError = (error: any): never => {
    // Se for um erro do Axios com resposta do servidor
    if (error.response && error.response.data) {
        const apiError = error.response.data;
        
        // Formata o erro para um formato mais amigável
        let errorMessage = apiError.message || 'Erro desconhecido';
        
        // Adiciona detalhes se existirem
        if (apiError.details) {
            // Se details for um objeto (como em erros de validação)
            if (typeof apiError.details === 'object') {
                const fieldErrors = Object.entries(apiError.details)
                    .map(([field, msg]) => `${field}: ${msg}`)
                    .join('; ');
                    
                errorMessage += `. Detalhes: ${fieldErrors}`;
            } else {
                errorMessage += `. Detalhes: ${apiError.details}`;
            }
        }
        
        // Cria um erro com a mensagem formatada
        const formattedError = new Error(errorMessage);
        // Preserva a resposta original como propriedade do erro
        (formattedError as any).response = error.response;
        throw formattedError;
    }
    
    // Se for um erro de timeout ou de rede
    if (error.request) {
        throw new Error('Não foi possível conectar ao servidor. Verifique sua conexão.');
    }
    
    // Outro tipo de erro
    throw new Error(error.message || 'Ocorreu um erro inesperado');
};

export const subsidiaryService = {
    getAll: async (): Promise<Subsidiary[]> => {
        try {
            const response = await api.get('/v1/subsidiaries');
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getByCompany: async (companyId: string): Promise<Subsidiary[]> => {
        try {
            // Usando o endpoint correto que existe no backend
            const response = await api.get(`/v1/subsidiaries/by-company/${companyId}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getById: async (id: string): Promise<Subsidiary> => {
        try {
            const response = await api.get(`/v1/subsidiaries/${id}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    create: async (subsidiary: Subsidiary): Promise<Subsidiary> => {
        try {
            const response = await api.post('/v1/subsidiaries', subsidiary);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    update: async (id: string, subsidiary: Subsidiary): Promise<Subsidiary> => {
        try {
            const response = await api.put(`/v1/subsidiaries/${id}`, subsidiary);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    delete: async (id: string): Promise<void> => {
        try {
            await api.delete(`/v1/subsidiaries/${id}`);
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Métodos para gerenciar horários
    getSchedules: async (subsidiaryId: string): Promise<SubsidiarySchedule[]> => {
        try {
            const response = await api.get(`/v1/subsidiary-schedules/by-subsidiary/${subsidiaryId}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    createSchedule: async (schedule: Omit<SubsidiarySchedule, 'id'>): Promise<SubsidiarySchedule> => {
        try {
            const response = await api.post('/v1/subsidiary-schedules', schedule);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    updateSchedule: async (id: string, schedule: Omit<SubsidiarySchedule, 'id'>): Promise<SubsidiarySchedule> => {
        try {
            const response = await api.put(`/v1/subsidiary-schedules/${id}`, schedule);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    deleteSchedule: async (id: string): Promise<void> => {
        try {
            await api.delete(`/v1/subsidiary-schedules/${id}`);
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Método para criar horários recorrentes
    createRecurringSchedule: async (
        subsidiaryId: string, 
        openTime: string,
        closeTime: string,
        daysOfWeek: number[],
        startDate: string,
        endDate: string,
        replaceExisting: boolean = false
    ): Promise<number> => {
        try {
            const response = await api.post(`/v1/api/recurring-schedules/subsidiary/${subsidiaryId}`, {
                openTime,
                closeTime,
                daysOfWeek,
                startDate,
                endDate,
                replaceExisting
            });
            return response.data; // Retorna o número de horários criados
        } catch (error) {
            return handleApiError(error);
        }
    }
};