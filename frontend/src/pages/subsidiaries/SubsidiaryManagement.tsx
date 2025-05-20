import { useState, useEffect } from 'react';
import type { Subsidiary } from '../../services/subsidiaryService';
import type { Company } from '../../services/companyService';
import { subsidiaryService } from '../../services/subsidiaryService';
import { companyService } from '../../services/companyService';
import SubsidiaryForm from '../../components/forms/SubsidiaryForm';
import './SubsidiaryManagement.css';

export default function SubsidiaryManagement() {
    const [subsidiaries, setSubsidiaries] = useState<Subsidiary[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [currentSubsidiary, setCurrentSubsidiary] = useState<Subsidiary | undefined>();
    const [companyNames, setCompanyNames] = useState<Record<string, string>>({});

    const fetchSubsidiaries = async () => {
        try {
            setLoading(true);
            const data = await subsidiaryService.getAll();
            setSubsidiaries(data);
            setError('');

            // Buscar nomes das empresas (sem passar o data)
            fetchCompanyNames();
        } catch (err) {
            setError('Erro ao carregar subsidiárias');
            console.error('Erro:', err);
        } finally {
            setLoading(false);
        }
    };

    const fetchCompanyNames = async () => {
        try {
            const companies = await companyService.getAll();

            const namesMap: Record<string, string> = {};
            companies.forEach((company: Company) => {
                if (company.id) {
                    namesMap[company.id] = company.name;
                }
            });

            setCompanyNames(namesMap);
        } catch (err) {
            console.error('Erro ao buscar nomes de empresas:', err);
        }
    };

    useEffect(() => {
        fetchSubsidiaries();
    }, []);

    const handleEdit = (subsidiary: Subsidiary) => {
        setCurrentSubsidiary(subsidiary);
        setShowForm(true);
    };

    const handleDelete = async (id: string) => {
        if (!window.confirm('Tem certeza que deseja excluir esta subsidiária?')) {
            return;
        }

        try {
            await subsidiaryService.delete(id);
            fetchSubsidiaries(); // Recarrega a lista
        } catch (err) {
            setError('Erro ao excluir subsidiária');
            console.error('Erro:', err);
        }
    };

    const handleFormSubmit = async (data: Subsidiary) => {
        try {
            if (data.id) {
                // Atualizar subsidiária existente
                await subsidiaryService.update(data.id, data);
            } else {
                // Criar nova subsidiária
                await subsidiaryService.create(data);
            }

            setShowForm(false);
            setCurrentSubsidiary(undefined);
            fetchSubsidiaries(); // Recarrega a lista
        } catch (err) {
            setError('Erro ao salvar subsidiária');
            console.error('Erro:', err);
        }
    };

    const handleFormCancel = () => {
        setShowForm(false);
        setCurrentSubsidiary(undefined);
    };

    if (loading && subsidiaries.length === 0) return <div>Carregando...</div>;

    return (
        <div className="subsidiary-management">
            <div className="page-header">
                <h1>Gestão de Subsidiárias</h1>
                {!showForm && (
                    <button onClick={() => setShowForm(true)}>Nova Subsidiária</button>
                )}
            </div>

            {error && <div className="error-message">{error}</div>}

            {showForm ? (
                <div className="form-container">
                    <SubsidiaryForm
                        initialData={currentSubsidiary}
                        onSubmit={handleFormSubmit}
                        onCancel={handleFormCancel}
                    />
                </div>
            ) : (
                <div className="table-container">
                    {subsidiaries.length === 0 ? (
                        <p className="no-data">Nenhuma subsidiária encontrada</p>
                    ) : (
                        <table>
                            <thead>
                            <tr>
                                <th>Nome</th>
                                <th>Empresa</th>
                                <th>CNPJ</th>
                                <th>Cidade/UF</th>
                                <th>Ações</th>
                            </tr>
                            </thead>
                            <tbody>
                            {subsidiaries.map(subsidiary => (
                                <tr key={subsidiary.id}>
                                    <td>{subsidiary.name}</td>
                                    <td>{companyNames[subsidiary.companyId] || 'Empresa não encontrada'}</td>
                                    <td>{subsidiary.documentNumber}</td>
                                    <td>{subsidiary.address?.city}/{subsidiary.address?.state}</td>
                                    <td className="action-buttons">
                                        <button onClick={() => handleEdit(subsidiary)}>Editar</button>
                                        <button className="danger" onClick={() => subsidiary.id && handleDelete(subsidiary.id)}>Excluir</button>
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