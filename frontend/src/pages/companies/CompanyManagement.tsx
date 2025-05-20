import { useState, useEffect } from 'react';
import type { Company } from '../../services/companyService';
import { companyService } from '../../services/companyService';
import CompanyForm from '../../components/forms/CompanyForm';
import './CompanyManagement.css';

export default function CompanyManagement() {
    const [companies, setCompanies] = useState<Company[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [currentCompany, setCurrentCompany] = useState<Company | undefined>();

    const fetchCompanies = async () => {
        try {
            setLoading(true);
            const data = await companyService.getAll();
            setCompanies(data);
            setError('');
        } catch (err) {
            setError('Erro ao carregar empresas');
            console.error('Erro:', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCompanies();
    }, []);

    const handleEdit = (company: Company) => {
        setCurrentCompany(company);
        setShowForm(true);
    };

    const handleDelete = async (id: string) => {
        if (!window.confirm('Tem certeza que deseja excluir esta empresa?')) {
            return;
        }

        try {
            await companyService.delete(id);
            fetchCompanies(); // Recarrega a lista
        } catch (err) {
            setError('Erro ao excluir empresa');
            console.error('Erro:', err);
        }
    };

    const handleFormSubmit = async (data: Company) => {
        try {
            if (data.id) {
                // Atualizar empresa existente
                await companyService.update(data.id, data);
            } else {
                // Criar nova empresa
                await companyService.create(data);
            }

            setShowForm(false);
            setCurrentCompany(undefined);
            fetchCompanies(); // Recarrega a lista
        } catch (err) {
            setError('Erro ao salvar empresa');
            console.error('Erro:', err);
        }
    };

    const handleFormCancel = () => {
        setShowForm(false);
        setCurrentCompany(undefined);
    };

    if (loading && companies.length === 0) return <div>Carregando...</div>;

    return (
        <div className="company-management">
            <div className="page-header">
                <h1>Gestão de Empresas</h1>
                {!showForm && (
                    <button onClick={() => setShowForm(true)}>Nova Empresa</button>
                )}
            </div>

            {error && <div className="error-message">{error}</div>}

            {showForm ? (
                <div className="form-container">
                    <CompanyForm
                        initialData={currentCompany}
                        onSubmit={handleFormSubmit}
                        onCancel={handleFormCancel}
                    />
                </div>
            ) : (
                <div className="table-container">
                    {companies.length === 0 ? (
                        <p className="no-data">Nenhuma empresa encontrada</p>
                    ) : (
                        <table>
                            <thead>
                            <tr>
                                <th>Nome</th>
                                <th>CNPJ</th>
                                <th>Telefone</th>
                                <th>Cidade/UF</th>
                                <th>Ações</th>
                            </tr>
                            </thead>
                            <tbody>
                            {companies.map(company => (
                                <tr key={company.id}>
                                    <td>{company.name}</td>
                                    <td>{company.documentNumber}</td>
                                    <td>{company.phone}</td>
                                    <td>{company.address?.city}/{company.address?.state}</td>
                                    <td className="action-buttons">
                                        <button onClick={() => handleEdit(company)}>Editar</button>
                                        <button className="danger" onClick={() => company.id && handleDelete(company.id)}>Excluir</button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )}
                </div>
            )}
        </div>
    );
}