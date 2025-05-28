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
    tradingName: string;
    address: Address;
    phone: string;
    documentNumber: string;
    typeOfDocument: string;
}

// Interface de erro da API
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

export const companyService = {
    getAll: async (): Promise<Company[]> => {
        try {
            const response = await api.get('/v1/companies');
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    getById: async (id: string): Promise<Company> => {
        try {
            const response = await api.get(`/v1/companies/${id}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    create: async (company: Company): Promise<Company> => {
        try {
            const response = await api.post('/v1/companies', company);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    update: async (id: string, company: Company): Promise<Company> => {
        try {
            const response = await api.put(`/v1/companies/${id}`, company);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    },

    delete: async (id: string): Promise<void> => {
        try {
            await api.delete(`/v1/companies/${id}`);
        } catch (error) {
            return handleApiError(error);
        }
    },

    // Método auxiliar para buscar subsidiárias de uma empresa
    getSubsidiaries: async (companyId: string): Promise<any[]> => {
        try {
            // Usando o endpoint correto no backend
            const response = await api.get(`/v1/subsidiaries/by-company/${companyId}`);
            return response.data;
        } catch (error) {
            return handleApiError(error);
        }
    }
};