import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { chairRoomService } from '../../services/chairRoomService';
import { subsidiaryService } from '../../services/subsidiaryService';
import './ChairRoomScheduleManagement.css';

interface Schedule {
    id?: string;
    chairRoomId: string;
    date: string;
    openTime: string;
    closeTime: string;
    closed: boolean;
}

export default function ChairRoomScheduleManagement() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [chairRoom, setChairRoom] = useState<any>(null);
    const [subsidiary, setSubsidiary] = useState<any>(null);
    const [schedules, setSchedules] = useState<Schedule[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Estado para o formulário de horário
    const [formData, setFormData] = useState<Schedule>({
        chairRoomId: id || '',
        date: new Date().toISOString().split('T')[0],
        openTime: '09:00',
        closeTime: '18:00',
        closed: false
    });

    useEffect(() => {
        if (!id) {
            setError('ID da cadeira/sala não fornecido');
            setLoading(false);
            return;
        }

        const loadData = async () => {
            try {
                setLoading(true);

                // Buscar dados da cadeira
                const chairRoomData = await chairRoomService.getById(id);
                setChairRoom(chairRoomData);

                // Buscar dados da subsidiária
                const subsidiaryData = await subsidiaryService.getById(chairRoomData.subsidiaryId);
                setSubsidiary(subsidiaryData);

                // Buscar horários da cadeira
                const schedulesData = await chairRoomService.getSchedules(id);
                setSchedules(Array.isArray(schedulesData) ? schedulesData : []);

                setError('');
            } catch (err) {
                setError('Erro ao carregar dados');
                console.error('Erro:', err);
            } finally {
                setLoading(false);
            }
        };

        loadData();
    }, [id]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        try {
            const response = await chairRoomService.createSchedule(formData);

            // Adiciona o novo horário à lista
            setSchedules(prev => [...prev, response]);

            // Reseta o formulário para a próxima entrada
            setFormData({
                ...formData,
                date: new Date().toISOString().split('T')[0]
            });

            alert('Horário adicionado com sucesso!');
        } catch (err) {
            setError('Erro ao salvar horário');
            console.error('Erro:', err);
        }
    };

    if (loading) return <div>Carregando...</div>;
    if (error) return <div className="error-message">{error}</div>;
    if (!chairRoom) return <div>Cadeira/sala não encontrada</div>;

    return (
        <div className="chair-room-schedule-management">
            <div className="page-header">
                <h1>Horários da Cadeira/Sala: {chairRoom.name}</h1>
                <button className="secondary" onClick={() => navigate('/cadeiras')}>Voltar</button>
            </div>

            <div className="info-panel">
                <div className="info-item">
                    <strong>Subsidiária:</strong> {subsidiary?.name || 'Não encontrada'}
                </div>
                <div className="info-item">
                    <strong>Número:</strong> {chairRoom.roomNumber}
                </div>
                <div className="info-item">
                    <strong>Capacidade:</strong> {chairRoom.capacity}
                </div>
            </div>

            <div className="schedule-form-container">
                <h2>Adicionar Horário</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-row">
                        <div className="form-group">
                            <label>Data</label>
                            <input
                                type="date"
                                name="date"
                                value={formData.date}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Hora de Abertura</label>
                            <input
                                type="time"
                                name="openTime"
                                value={formData.openTime}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Hora de Fechamento</label>
                            <input
                                type="time"
                                name="closeTime"
                                value={formData.closeTime}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-group checkbox-group">
                            <label>
                                <input
                                    type="checkbox"
                                    name="closed"
                                    checked={formData.closed}
                                    onChange={handleChange}
                                />
                                Fechado neste dia
                            </label>
                        </div>
                    </div>

                    <div className="form-buttons">
                        <button type="submit">Adicionar Horário</button>
                    </div>
                </form>
            </div>

            <div className="schedules-list">
                <h2>Horários Cadastrados</h2>

                {schedules.length === 0 ? (
                    <p className="no-data">Nenhum horário cadastrado</p>
                ) : (
                    <table>
                        <thead>
                        <tr>
                            <th>Data</th>
                            <th>Abertura</th>
                            <th>Fechamento</th>
                            <th>Status</th>
                            <th>Ações</th>
                        </tr>
                        </thead>
                        <tbody>
                        {schedules.map(schedule => (
                            <tr key={schedule.id}>
                                <td>{new Date(schedule.date).toLocaleDateString()}</td>
                                <td>{schedule.openTime}</td>
                                <td>{schedule.closeTime}</td>
                                <td>{schedule.closed ? 'Fechado' : 'Aberto'}</td>
                                <td>
                                    <button className="danger">Excluir</button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}