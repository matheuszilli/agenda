import { useState, useEffect } from 'react';
import type { Professional } from '../../services/professionalService';
import {  professionalService } from '../../services/professionalService';
import ProfessionalForm from '../../components/forms/ProfessionalForm';
import './ProfessionalManagement.css';

export default function ProfessionalManagement() {
    const [professionals, setProfessionals] = useState<Professional[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [currentProfessional, setCurrentProfessional] = useState<Professional | undefined>();

    const fetchProfessionals = async () => {
        try {
            setLoading(true);
            const data = await professionalService.listAll();
            setProfessionals(data);
            setError('');
        } catch (err) {
            setError('Erro ao carregar profissionais');
            console.error('Erro:', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchProfessionals();
    }, []);

    const handleEdit = (professional: Professional) => {
        setCurrentProfessional(professional);
        setShowForm(true);
    };

    const handleDelete = async (id: string) => {
        if (!window.confirm('Tem certeza que deseja excluir este profissional?')) {
            return;
        }

        try {
            await professionalService.delete(id);
            fetchProfessionals(); // Recarrega a lista
        } catch (err) {
            setError('Erro ao excluir profissional');
            console.error('Erro:', err);
        }
    };

    const handleFormSubmit = async (data: Professional) => {
        try {
            if (data.id) {
                // Atualizar profissional existente
                await professionalService.update(data.id, data);
            } else {
                // Criar novo profissional
                await professionalService.create(data);
            }

            setShowForm(false);
            setCurrentProfessional(undefined);
            fetchProfessionals(); // Recarrega a lista
        } catch (err) {
            setError('Erro ao salvar profissional');
            console.error('Erro:', err);
        }
    };

    const handleFormCancel = () => {
        setShowForm(false);
        setCurrentProfessional(undefined);
    };

    if (loading && professionals.length === 0) return <div>Carregando...</div>;

    return (
        <div className="professional-management">
            <div className="page-header">
                <h1>Gestão de Profissionais</h1>
                {!showForm && (
                    <button onClick={() => setShowForm(true)}>Novo Profissional</button>
                )}
            </div>

            {error && <div className="error-message">{error}</div>}

            {showForm ? (
                <div className="form-container">
                    <ProfessionalForm
                        initialData={currentProfessional}
                        onSubmit={handleFormSubmit}
                        onCancel={handleFormCancel}
                    />
                </div>
            ) : (
                <div className="table-container">
                    {professionals.length === 0 ? (
                        <p className="no-data">Nenhum profissional encontrado</p>
                    ) : (
                        <table>
                            <thead>
                                <tr>
                                    <th>Nome</th>
                                    <th>CPF</th>
                                    <th>Telefone</th>
                                    <th>Cidade/UF</th>
                                    <th>Ações</th>
                                </tr>
                            </thead>
                            <tbody>
                                {professionals.map(professional => (
                                    <tr key={professional.id}>
                                        <td>{professional.fullName || 'Nome não informado'}</td>
                                        <td>{professional.documentNumber}</td>
                                        <td>{professional.phone}</td>
                                        <td>{professional.address?.city}/{professional.address?.state}</td>
                                        <td className="action-buttons">
                                            <button 
                                                className="edit-button"
                                                onClick={() => handleEdit(professional)}
                                            >
                                                Editar
                                            </button>
                                            <button 
                                                className="delete-button danger"
                                                onClick={() => professional.id && handleDelete(professional.id)}
                                            >
                                                Excluir
                                            </button>
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