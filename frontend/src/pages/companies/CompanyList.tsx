import { useState, useEffect } from 'react';
import api from '../../services/api';

interface Company {
    id: string;
    name: string;
    documentNumber: string;
    phone: string;
}

export default function CompanyList() {
    const [companies, setCompanies] = useState<Company[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchCompanies = async () => {
            try {
                const response = await api.get('/companies');
                setCompanies(response.data);
                setLoading(false);
            } catch (err) {
                setError('Erro ao carregar empresas');
                setLoading(false);
                console.error('Erro:', err);
            }
        };

        fetchCompanies();
    }, []);

    if (loading) return <div>Carregando...</div>;
    if (error) return <div>{error}</div>;

    return (
        <div>
            <h1>Empresas</h1>
            {companies.length === 0 ? (
                <p>Nenhuma empresa encontrada</p>
            ) : (
                <ul>
                    {companies.map(company => (
                        <li key={company.id}>
                            {company.name} - {company.documentNumber} - {company.phone}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}