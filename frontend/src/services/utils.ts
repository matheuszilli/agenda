// Interface de erro da API
export interface ApiError {
    message: string;
    details: string;
    timestamp: string;
    path?: string;
}

export const handleApiError = (error: any): never => {
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