// src/pages/chairRooms/ChairRoomManagement.tsx
import { useState, useEffect } from 'react';
import type { ChairRoom, PageResponse } from '../../services/chairRoomService';
import { chairRoomService } from '../../services/chairRoomService';
import type { Subsidiary } from '../../services/subsidiaryService';
import { subsidiaryService } from '../../services/subsidiaryService';
import ChairRoomForm from '../../components/forms/ChairRoomForm';
import './ChairRoomManagement.css';

export default function ChairRoomManagement() {
    const [chairRoomPage, setChairRoomPage] = useState<PageResponse<ChairRoom> | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [currentChairRoom, setCurrentChairRoom] = useState<ChairRoom | undefined>();
    const [subsidiaryNames, setSubsidiaryNames] = useState<Record<string, string>>({});

    // Estado para paginação
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);

    const fetchChairRooms = async () => {
        try {
            setLoading(true);
            const data = await chairRoomService.getAll(currentPage, pageSize);

            // Agora temos o objeto de paginação completo
            setChairRoomPage(data);
            setError('');

            // Buscar nomes das subsidiárias
            fetchSubsidiaryNames();
        } catch (err) {
            setError('Erro ao carregar cadeiras/salas');
            console.error('Erro:', err);
        } finally {
            setLoading(false);
        }
    };

    const fetchSubsidiaryNames = async () => {
        try {
            const subsidiaries = await subsidiaryService.getAll();

            const namesMap: Record<string, string> = {};
            subsidiaries.forEach((subsidiary: Subsidiary) => {
                if (subsidiary.id) {
                    namesMap[subsidiary.id] = subsidiary.name;
                }
            });

            setSubsidiaryNames(namesMap);
        } catch (err) {
            console.error('Erro ao buscar nomes de subsidiárias:', err);
        }
    };

    useEffect(() => {
        fetchChairRooms();
    }, [currentPage, pageSize]);

    const handleEdit = (chairRoom: ChairRoom) => {
        setCurrentChairRoom(chairRoom);
        setShowForm(true);
    };

    const handleDelete = async (id: string) => {
        if (!window.confirm('Tem certeza que deseja excluir esta cadeira/sala?')) {
            return;
        }

        try {
            await chairRoomService.delete(id);
            fetchChairRooms(); // Recarrega a lista
        } catch (err) {
            setError('Erro ao excluir cadeira/sala');
            console.error('Erro:', err);
        }
    };

    const handleFormSubmit = async (data: ChairRoom) => {
        try {
            if (data.id) {
                // Atualizar cadeira existente
                await chairRoomService.update(data.id, data);
            } else {
                // Criar nova cadeira
                await chairRoomService.create(data);
            }

            setShowForm(false);
            setCurrentChairRoom(undefined);
            fetchChairRooms(); // Recarrega a lista
        } catch (err) {
            setError('Erro ao salvar cadeira/sala');
            console.error('Erro:', err);
        }
    };

    const handleFormCancel = () => {
        setShowForm(false);
        setCurrentChairRoom(undefined);
    };

    const handlePageChange = (newPage: number) => {
        setCurrentPage(newPage);
    };

    if (loading && !chairRoomPage) return <div>Carregando...</div>;

    // Extraímos os chairRooms do objeto de paginação
    const chairRooms = chairRoomPage?.content || [];

    return (
        <div className="chair-room-management">
            <div className="page-header">
                <h1>Gestão de Cadeiras/Salas</h1>
                {!showForm && (
                    <button onClick={() => setShowForm(true)}>Nova Cadeira/Sala</button>
                )}
            </div>

            {error && <div className="error-message">{error}</div>}

            {showForm ? (
                <div className="form-container">
                    <ChairRoomForm
                        initialData={currentChairRoom}
                        onSubmit={handleFormSubmit}
                        onCancel={handleFormCancel}
                    />
                </div>
            ) : (
                <div className="table-container">
                    {chairRooms.length === 0 ? (
                        <p className="no-data">Nenhuma cadeira/sala encontrada</p>
                    ) : (
                        <>
                            <table>
                                <thead>
                                <tr>
                                    <th>Nome</th>
                                    <th>Subsidiária</th>
                                    <th>Número</th>
                                    <th>Capacidade</th>
                                    <th>Descrição</th>
                                    <th>Ações</th>
                                </tr>
                                </thead>
                                <tbody>
                                {chairRooms.map(chairRoom => (
                                    <tr key={chairRoom.id}>
                                        <td>{chairRoom.name}</td>
                                        <td>{subsidiaryNames[chairRoom.subsidiaryId] || 'Subsidiária não encontrada'}</td>
                                        <td>{chairRoom.roomNumber}</td>
                                        <td>{chairRoom.capacity}</td>
                                        <td>{chairRoom.description || '-'}</td>
                                        <td className="action-buttons">
                                            <button onClick={() => handleEdit(chairRoom)}>Editar</button>
                                            <button
                                                className="secondary"
                                                onClick={() => window.location.href = `/cadeiras/horarios/${chairRoom.id}`}
                                            >
                                                Horários
                                            </button>
                                            <button className="danger" onClick={() => chairRoom.id && handleDelete(chairRoom.id)}>
                                                Excluir
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>

                            {/* Adiciona controles de paginação */}
                            {chairRoomPage && chairRoomPage.totalPages > 1 && (
                                <div className="pagination-controls">
                                    <button
                                        onClick={() => handlePageChange(currentPage - 1)}
                                        disabled={currentPage === 0}
                                    >
                                        Anterior
                                    </button>
                                    <span>
                    Página {currentPage + 1} de {chairRoomPage.totalPages}
                  </span>
                                    <button
                                        onClick={() => handlePageChange(currentPage + 1)}
                                        disabled={currentPage === chairRoomPage.totalPages - 1}
                                    >
                                        Próxima
                                    </button>
                                </div>
                            )}
                        </>
                    )}
                </div>
            )}
        </div>
    );
}