import { useState, useEffect } from 'react';
import type { ChairRoom } from '../../services/chairRoomService';
import type { Subsidiary } from '../../services/subsidiaryService';
import { subsidiaryService } from '../../services/subsidiaryService';
import './ChairRoomForm.css';

interface ChairRoomFormProps {
    initialData?: ChairRoom;
    onSubmit: (data: ChairRoom) => void;
    onCancel: () => void;
}

const emptyChairRoom: ChairRoom = {
    name: '',
    subsidiaryId: '',
    description: '',
    capacity: 1,
    roomNumber: ''
};

export default function ChairRoomForm({ initialData = emptyChairRoom, onSubmit, onCancel }: ChairRoomFormProps) {
    const [formData, setFormData] = useState<ChairRoom>(initialData);
    const [subsidiaries, setSubsidiaries] = useState<Subsidiary[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchSubsidiaries = async () => {
            try {
                const data = await subsidiaryService.getAll();
                setSubsidiaries(data);

                // Se não tiver uma subsidiária selecionada e houver subsidiárias disponíveis
                if (!formData.subsidiaryId && data.length > 0) {
                    setFormData(prev => ({
                        ...prev,
                        subsidiaryId: data[0].id || ''
                    }));
                }
            } catch (err) {
                setError('Erro ao carregar subsidiárias');
                console.error('Erro:', err);
            } finally {
                setLoading(false);
            }
        };

        fetchSubsidiaries();
    }, []);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;

        setFormData(prev => ({
            ...prev,
            [name]: name === 'capacity' ? parseInt(value) || 0 : value
        }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
    };

    if (loading) return <div>Carregando subsidiárias...</div>;
    if (error) return <div className="error-message">{error}</div>;
    if (subsidiaries.length === 0) return <div>Nenhuma subsidiária cadastrada. Por favor, cadastre uma subsidiária primeiro.</div>;

    return (
        <form onSubmit={handleSubmit} className="chair-room-form">
            <h2 className="form-section-header">{initialData.id ? 'Editar Cadeira/Sala' : 'Nova Cadeira/Sala'}</h2>

            <div className="form-group full-width">
                <label>Subsidiária</label>
                <select
                    name="subsidiaryId"
                    value={formData.subsidiaryId}
                    onChange={handleChange}
                    required
                >
                    <option value="">Selecione uma subsidiária</option>
                    {subsidiaries.map(subsidiary => (
                        <option key={subsidiary.id} value={subsidiary.id}>
                            {subsidiary.name}
                        </option>
                    ))}
                </select>
            </div>

            <div className="form-group full-width">
                <label>Nome</label>
                <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                />
            </div>

            <div className="form-group">
                <label>Número da Sala</label>
                <input
                    type="text"
                    name="roomNumber"
                    value={formData.roomNumber}
                    onChange={handleChange}
                    required
                />
            </div>

            <div className="form-group">
                <label>Capacidade</label>
                <input
                    type="number"
                    min="1"
                    name="capacity"
                    value={formData.capacity}
                    onChange={handleChange}
                    required
                />
            </div>

            <div className="form-group full-width">
                <label>Descrição</label>
                <textarea
                    name="description"
                    value={formData.description || ''}
                    onChange={handleChange}
                    rows={3}
                />
            </div>

            <div className="form-buttons">
                <button type="button" className="secondary" onClick={onCancel}>Cancelar</button>
                <button type="submit">Salvar</button>
            </div>
        </form>
    );
}