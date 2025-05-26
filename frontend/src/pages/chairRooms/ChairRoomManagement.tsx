import { useState, useEffect } from 'react';
import { type ChairRoom, chairRoomService } from '../../services/chairRoomService';
import { subsidiaryService } from '../../services/subsidiaryService';
import ChairRoomForm from '../../components/forms/ChairRoomForm';
import './ChairRoomManagement.css';
import { useParams, useNavigate } from 'react-router-dom';

export default function ChairRoomManagement() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [chairRooms, setChairRooms] = useState<ChairRoom[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [currentChairRoom, setCurrentChairRoom] = useState<ChairRoom | null>(null);
    const [subsidiaryNames, setSubsidiaryNames] = useState<Record<string, string>>({});
    const [showForm, setShowForm] = useState(id === 'new' || !!id);
    const isNew = id === 'new';

    const fetchChairRooms = async () => {
        try {
            setLoading(true);
            const data = await chairRoomService.getAll();
            setChairRooms(data);
            setError('');

            // Buscar nomes das subsidiárias
            fetchSubsidiaryNames();
        } catch (err: any) {
            setError(`Erro ao carregar cadeiras/salas: ${err.message}`);
            console.error('Erro:', err);
        } finally {
            setLoading(false);
        }
    };

    const fetchChairRoom = async (chairRoomId: string) => {
        try {
            setLoading(true);
            const data = await chairRoomService.getById(chairRoomId);
            setCurrentChairRoom(data);
            setError('');
        } catch (err: any) {
            setError(`Erro ao carregar cadeira/sala: ${err.message}`);
            console.error('Erro:', err);
        } finally {
            setLoading(false);
        }
    };

    const fetchSubsidiaryNames = async () => {
        try {
            const subsidiaries = await subsidiaryService.getAll();
            const namesMap: Record<string, string> = {};
            subsidiaries.forEach((subsidiary: any) => {
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
        if (id && id !== 'new') {
            fetchChairRoom(id);
        } else {
            fetchChairRooms();
        }
    }, [id]);

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
            setSuccess('Cadeira/sala excluída com sucesso!');
            setTimeout(() => {
                setSuccess('');
                fetchChairRooms();
            }, 1500);
        } catch (err: any) {
            setError(`Erro ao excluir cadeira/sala: ${err.message}`);
            console.error('Erro:', err);
        }
    };

    const handleFormSubmit = async (data: ChairRoom) => {
        try {
            setLoading(true);
            if (data.id) {
                await chairRoomService.update(data.id, data);
                setSuccess('Cadeira/sala atualizada com sucesso!');
            } else {
                await chairRoomService.create(data);
                setSuccess('Cadeira/sala criada com sucesso!');
            }

            // Redirecionar para a lista após um breve delay
            setTimeout(() => {
                navigate('/chair-rooms');
            }, 1500);

        } catch (err: any) {
            setError(`Erro ao salvar cadeira/sala: ${err.message}`);
            console.error('Erro:', err);
            setLoading(false);
        }
    };

    const handleFormCancel = () => {
        // Reset dos estados relevantes
        setShowForm(false);
        setCurrentChairRoom(null);
        
        // Navegação para a página de listagem
        navigate('/chair-rooms', { replace: true });
    };

    if (loading && (id && id !== 'new')) return <div className="loading">Carregando...</div>;
    if (!isNew && id && error) return <div className="error-message">{error}</div>;
    if (success) return <div className="success-message">{success}</div>;

    // Se estamos na página de edição/criação, mostrar o formulário
    if (showForm) {
        return (
            <div className="chair-room-management-container">
                <ChairRoomForm
                    initialData={currentChairRoom || undefined}
                    onSubmit={handleFormSubmit}
                    onCancel={handleFormCancel}
                />
            </div>
        );
    }

    // Caso contrário, mostrar a lista
    return (
        <div className="chair-room-management-container">
            <div className="list-header">
                <h2>Cadeiras/Salas</h2>
                {!showForm && (
                <button className="add-button" onClick={() => setShowForm(true)}>+ Nova Cadeira/Sala</button>
                )}
            </div>

            {error && <div className="error-message">{error}</div>}

            {chairRooms.length === 0 ? (
                <div className="no-data">
                    Nenhuma cadeira/sala encontrada. Clique em "Nova Cadeira/Sala" para adicionar.
                </div>
            ) : (
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Subsidiária</th>
                            <th>Número</th>
                            <th>Capacidade</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        {Array.isArray(chairRooms)
                            ? chairRooms.map(chairRoom => (
                                <tr key={chairRoom.id}>
                                    <td>{chairRoom.name}</td>
                                    <td>{subsidiaryNames[chairRoom.subsidiaryId] || 'Subsidiária não encontrada'}</td>
                                    <td>{chairRoom.roomNumber}</td>
                                    <td>{chairRoom.capacity}</td>
                                    <td className="action-buttons">
                                        <button
                                            className="edit-button"
                                            onClick={() => chairRoom.id && handleEdit(chairRoom)}
                                        >
                                            Editar
                                        </button>
                                        <button
                                            className="delete-button"
                                            onClick={() => chairRoom.id && handleDelete(chairRoom.id)}
                                        >
                                            Excluir
                                        </button>
                                    </td>
                                </tr>
                            ))
                            : <tr><td colSpan={5}>Formato de dados inesperado</td></tr>
                        }
                    </tbody>
                </table>
            )}
        </div>
    );
}