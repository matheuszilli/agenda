import React, { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { type Company } from '../services/companyService';

// Definição da estrutura de subsidiárias
export interface Subsidiary {
    id: string;
    name: string;
    companyId: string;
    documentNumber: string;
    address?: {
        city: string;
        state: string;
    };
}

// Estrutura do contexto
interface CompanyContextType {
    selectedCompany: Company | null;
    selectedSubsidiary: Subsidiary | null;
    setSelectedCompany: (company: Company | null) => void;
    setSelectedSubsidiary: (subsidiary: Subsidiary | null) => void;
    isLoading: boolean;
}

// Valor padrão para o contexto
const defaultContext: CompanyContextType = {
    selectedCompany: null,
    selectedSubsidiary: null,
    setSelectedCompany: () => {},
    setSelectedSubsidiary: () => {},
    isLoading: false
};

// Criação do contexto
const CompanyContext = createContext<CompanyContextType>(defaultContext);

// Hook personalizado para usar o contexto
export const useCompany = () => useContext(CompanyContext);

// Propriedades do provedor
interface CompanyProviderProps {
    children: ReactNode;
}

// Provedor do contexto
export const CompanyProvider: React.FC<CompanyProviderProps> = ({ children }) => {
    const [selectedCompany, setSelectedCompany] = useState<Company | null>(null);
    const [selectedSubsidiary, setSelectedSubsidiary] = useState<Subsidiary | null>(null);
    const [isLoading, setIsLoading] = useState(false);

    // Recuperar do localStorage ao iniciar
    useEffect(() => {
        const storedCompany = localStorage.getItem('selectedCompany');
        const storedSubsidiary = localStorage.getItem('selectedSubsidiary');

        if (storedCompany) {
            try {
                setSelectedCompany(JSON.parse(storedCompany));
            } catch (error) {
                console.error('Error parsing stored company:', error);
                localStorage.removeItem('selectedCompany');
            }
        }

        if (storedSubsidiary) {
            try {
                setSelectedSubsidiary(JSON.parse(storedSubsidiary));
            } catch (error) {
                console.error('Error parsing stored subsidiary:', error);
                localStorage.removeItem('selectedSubsidiary');
            }
        }
    }, []);

    // Salvar no localStorage quando mudar
    useEffect(() => {
        if (selectedCompany) {
            localStorage.setItem('selectedCompany', JSON.stringify(selectedCompany));
        } else {
            localStorage.removeItem('selectedCompany');
        }
    }, [selectedCompany]);

    useEffect(() => {
        if (selectedSubsidiary) {
            localStorage.setItem('selectedSubsidiary', JSON.stringify(selectedSubsidiary));
        } else {
            localStorage.removeItem('selectedSubsidiary');
        }
    }, [selectedSubsidiary]);

    // Quando a empresa ativa mudar, resetar a subsidiária ativa
    useEffect(() => {
        if (selectedCompany && selectedSubsidiary && selectedSubsidiary.companyId !== selectedCompany.id) {
            setSelectedSubsidiary(null);
        }
    }, [selectedCompany, selectedSubsidiary]);

    const value = {
        selectedCompany,
        selectedSubsidiary,
        setSelectedCompany,
        setSelectedSubsidiary,
        isLoading
    };

    return (
        <CompanyContext.Provider value={value}>
            {children}
        </CompanyContext.Provider>
    );
};