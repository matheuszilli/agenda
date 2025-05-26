import { useState, useEffect } from 'react';
import { 
  type ChairRoom, 
  type ChairRoomSchedule, 
  type DayScheduleConfig,
  type RecurringScheduleRequest,
  type ExceptionScheduleRequest,
  type ConflictCheckRequest,
  chairRoomService 
} from '../../services/chairRoomService';
import { subsidiaryService, type Subsidiary } from '../../services/subsidiaryService';
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

const daysOfWeek = [
    'Domingo',
    'Segunda-feira',
    'Terça-feira',
    'Quarta-feira',
    'Quinta-feira',
    'Sexta-feira',
    'Sábado'
];

// Estrutura para os horários recorrentes por dia da semana
const defaultWeekSchedule: { [key: string]: DayScheduleConfig } = {
  0: { open: true, openTime: '08:00', closeTime: '18:00' }, // Domingo
  1: { open: true, openTime: '08:00', closeTime: '18:00' }, // Segunda
  2: { open: true, openTime: '08:00', closeTime: '18:00' }, // Terça
  3: { open: true, openTime: '08:00', closeTime: '18:00' }, // Quarta
  4: { open: true, openTime: '08:00', closeTime: '18:00' }, // Quinta
  5: { open: true, openTime: '08:00', closeTime: '18:00' }, // Sexta
  6: { open: true, openTime: '08:00', closeTime: '18:00' }, // Sábado
};

export default function ChairRoomForm({ initialData = emptyChairRoom, onSubmit, onCancel }: ChairRoomFormProps) {
    const [formData, setFormData] = useState<ChairRoom>(initialData);
    const [subsidiaries, setSubsidiaries] = useState<Subsidiary[]>([]);
    const [schedules, setSchedules] = useState<ChairRoomSchedule[]>([]);
    const [loading, setLoading] = useState(false);
    const [schedulesLoading, setSchedulesLoading] = useState(false);
    const [error, setError] = useState('');
    const [activeTab, setActiveTab] = useState('chair-info');
    
    // Para informações de erro de validação
    const [errors, setErrors] = useState<Record<string, string>>({});

    // Estados para horários recorrentes
    const [weekSchedule, setWeekSchedule] = useState({ ...defaultWeekSchedule });
    const [recurrenceStart, setRecurrenceStart] = useState(new Date().toISOString().split('T')[0]);
    const [recurrenceEnd, setRecurrenceEnd] = useState(
      new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
    );
    
    // Estados para exceções
    const [exceptions, setExceptions] = useState<ChairRoomSchedule[]>([]);
    const [exceptionDate, setExceptionDate] = useState(new Date().toISOString().split('T')[0]);
    const [exceptionSchedule, setExceptionSchedule] = useState({
      openTime: '08:00',
      closeTime: '18:00',
      closed: false
    });
    
    // Estado para controle de conflitos
    const [conflicts, setConflicts] = useState<string[]>([]);
    const [showConflictDialog, setShowConflictDialog] = useState(false);
    const [pendingRecurrenceAction, setPendingRecurrenceAction] = useState<RecurringScheduleRequest | null>(null);
    
    useEffect(() => {
        const fetchSubsidiaries = async () => {
            try {
                setLoading(true);
                const data = await subsidiaryService.getAll();
                setSubsidiaries(data);

                // Se não tiver uma subsidiária selecionada e houver subsidiárias disponíveis
                if (!formData.subsidiaryId && data.length > 0) {
                    setFormData(prev => ({
                        ...prev,
                        subsidiaryId: data[0].id || ''
                    }));
                }
            } catch (err: any) {
                setError(`Erro ao carregar subsidiárias: ${err.message || 'Erro desconhecido'}`);
                console.error('Erro:', err);
            } finally {
                setLoading(false);
            }
        };

        fetchSubsidiaries();
    }, []);
    
    // Carregar horários quando já existe um ID de cadeira/sala
    useEffect(() => {
        if (initialData.id) {
            fetchSchedules(initialData.id);
        }
    }, [initialData.id]);
    
    const fetchSchedules = async (chairRoomId: string) => {
        try {
            setSchedulesLoading(true);
            const data = await chairRoomService.getSchedules(chairRoomId);
            setSchedules(data);
        } catch (err) {
            console.error('Erro ao carregar horários:', err);
        } finally {
            setSchedulesLoading(false);
        }
    };

    const validateForm = () => {
        const newErrors: Record<string, string> = {};
        
        // Validar campos obrigatórios
        if (!formData.name) newErrors.name = "O nome da sala/cadeira é obrigatório";
        if (!formData.roomNumber) newErrors.roomNumber = "O número da sala/cadeira é obrigatório";
        if (!formData.capacity || formData.capacity < 1) newErrors.capacity = "A capacidade deve ser maior que zero";
        if (!formData.subsidiaryId) newErrors.subsidiaryId = "A subsidiária é obrigatória";
        
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const { name, value, type } = e.target as HTMLInputElement;
        
        if (name === 'capacity') {
            setFormData({
                ...formData,
                capacity: parseInt(value) || 0
            });
        } else {
            setFormData({
                ...formData,
                [name]: value
            });
        }
        
        // Limpar erro após edição
        if (errors[name]) {
            setErrors({
                ...errors,
                [name]: ''
            });
        }
    };
    
    const handleDeleteSchedule = async (id: string) => {
        if (!window.confirm('Tem certeza que deseja excluir este horário?')) {
            return;
        }
        
        try {
            await chairRoomService.deleteSchedule(id);
            // Recarregar horários após excluir
            if (initialData.id) {
                fetchSchedules(initialData.id);
            }
        } catch (err) {
            console.error('Erro ao excluir horário:', err);
        }
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (validateForm()) {
            onSubmit(formData);
        }
    };

    // Agrupar os horários carregados por padrão e exceções
    useEffect(() => {
      if (schedules.length > 0) {
        // Identifica exceções (horários customizados)
        const customSchedules = schedules.filter(s => s.customized);
        setExceptions(customSchedules);
      }
    }, [schedules]);
    
    // Manipuladores para a tabela de horários recorrentes
    const handleWeekdayScheduleChange = (day: number, field: string, value: any) => {
      setWeekSchedule(prev => ({
        ...prev,
        [day]: {
          ...prev[day],
          [field]: value
        }
      }));
    };
    
    // Verificar conflitos antes de aplicar horários recorrentes
    const checkRecurringConflicts = async () => {
      if (!initialData.id) return;
      
      // Coletar os dias da semana que estão marcados como abertos
      const activeDays = Object.entries(weekSchedule)
        .filter(([_, config]) => config.open)
        .map(([day, _]) => parseInt(day)); // Usar formato 0-6 para o backend
      
      // Construir a request para verificação de conflitos
      const conflictRequest: ConflictCheckRequest = {
        chairRoomId: initialData.id,
        daysOfWeek: activeDays,
        startDate: recurrenceStart,
        endDate: recurrenceEnd,
        includeCustomized: true
      };
      
      try {
        const response = await chairRoomService.checkConflicts(conflictRequest);
        
        if (response.hasConflicts) {
          // Mostrar diálogo de confirmação
          setConflicts(response.conflictingDates);
          setShowConflictDialog(true);
          
          // Preparar os dados para a ação pendente
          const pendingAction: RecurringScheduleRequest = {
            chairRoomId: initialData.id,
            weekSchedule: weekSchedule,
            startDate: recurrenceStart,
            endDate: recurrenceEnd,
            replaceExisting: false
          };
          
          setPendingRecurrenceAction(pendingAction);
          return false;
        }
        
        return true;
      } catch (err) {
        console.error('Erro ao verificar conflitos:', err);
        setError(`Erro ao verificar conflitos: ${err}`);
        return false;
      }
    };
    
    // Aplicar horários recorrentes
    const handleApplyRecurringSchedule = async () => {
      if (!initialData.id) return;
      
      // Verificar conflitos primeiro
      const noConflicts = await checkRecurringConflicts();
      
      if (noConflicts) {
        // Se não há conflitos, prossegue com a aplicação
        const recurringRequest: RecurringScheduleRequest = {
          chairRoomId: initialData.id,
          weekSchedule: weekSchedule,
          startDate: recurrenceStart,
          endDate: recurrenceEnd,
          replaceExisting: false
        };
        
        await applyRecurringSchedules(recurringRequest);
      }
    };
    
    // Função para aplicar os horários recorrentes
    const applyRecurringSchedules = async (request: RecurringScheduleRequest) => {
      try {
        setLoading(true);
        
        await chairRoomService.createRecurringSchedule(request);
        
        // Recarregar horários
        if (initialData.id) {
          await fetchSchedules(initialData.id);
        }
        
        setShowConflictDialog(false);
        setPendingRecurrenceAction(null);
        alert('Horários recorrentes aplicados com sucesso!');
        
      } catch (err) {
        setError(`Erro ao aplicar horários recorrentes: ${err}`);
        console.error('Erro:', err);
      } finally {
        setLoading(false);
      }
    };
    
    // Adicionar uma exceção (dia específico)
    const handleAddException = async () => {
      if (!initialData.id) return;
      
      const exceptionRequest: ExceptionScheduleRequest = {
        chairRoomId: initialData.id,
        date: exceptionDate,
        openTime: exceptionSchedule.openTime,
        closeTime: exceptionSchedule.closeTime,
        closed: exceptionSchedule.closed,
        replaceExisting: true
      };
      
      try {
        await chairRoomService.createException(exceptionRequest);
        if (initialData.id) {
          await fetchSchedules(initialData.id);
        }
        alert('Exceção adicionada com sucesso!');
        
        // Resetar formulário de exceção
        setExceptionDate(new Date().toISOString().split('T')[0]);
        setExceptionSchedule({
          openTime: '08:00',
          closeTime: '18:00',
          closed: false
        });
        
      } catch (err) {
        setError(`Erro ao adicionar exceção: ${err}`);
        console.error('Erro:', err);
      }
    };
    
    // Excluir um horário específico
    const handleDeleteSpecificSchedule = async (id) => {
      if (!window.confirm('Deseja excluir apenas este horário específico?')) {
        return;
      }
      
      try {
        await chairRoomService.deleteSchedule(id);
        if (initialData.id) {
          await fetchSchedules(initialData.id);
        }
      } catch (err) {
        setError(`Erro ao excluir horário: ${err}`);
        console.error('Erro:', err);
      }
    };
    
    // Função extra: excluir todos os horários de um padrão
    const handleDeletePatternSchedule = async (dayOfWeek) => {
      const confirmDelete = window.confirm(
        `Deseja excluir TODOS os horários de ${daysOfWeek[dayOfWeek]}? Esta ação não pode ser desfeita.`
      );
      
      if (!confirmDelete || !initialData.id) return;
      
      try {
        setLoading(true);
        
        // Buscar todos os horários para este dia da semana
        const daySchedules = schedules.filter(s => s.dayOfWeek === dayOfWeek);
        
        // Excluir cada um
        for (const schedule of daySchedules) {
          if (schedule.id) {
            await chairRoomService.deleteSchedule(schedule.id);
          }
        }
        
        // Recarregar horários
        await fetchSchedules(initialData.id);
        
      } catch (err) {
        setError(`Erro ao excluir horários: ${err}`);
        console.error('Erro:', err);
      } finally {
        setLoading(false);
      }
    };

    if (loading) return <div>Carregando subsidiárias...</div>;
    if (error) return <div className="error-message">{error}</div>;
    if (subsidiaries.length === 0) return <div>Nenhuma subsidiária cadastrada. Por favor, cadastre uma subsidiária primeiro.</div>;

    // Renderização da interface
    return (
        <form onSubmit={handleSubmit} className="company-form">
            <h2 className="form-section-header">{initialData.id ? 'Editar Cadeira/Sala' : 'Nova Cadeira/Sala'}</h2>
            
            <div className="tabs">
                <button 
                    type="button"
                    className={`tab-button ${activeTab === 'chair-info' ? 'active' : ''}`}
                    onClick={() => setActiveTab('chair-info')}
                >
                    Informações da Sala/Cadeira
                </button>
                <button 
                    type="button"
                    className={`tab-button ${activeTab === 'schedule' ? 'active' : ''}`}
                    onClick={() => setActiveTab('schedule')}
                    disabled={!initialData.id}
                >
                    Horários
                </button>
            </div>
            
            {activeTab === 'chair-info' && (
                <div className="tab-content">
                    <div className="form-group">
                        <label>Subsidiária*</label>
                        <select
                            name="subsidiaryId"
                            value={formData.subsidiaryId}
                            onChange={handleChange}
                            required
                            className={errors.subsidiaryId ? 'error' : ''}
                        >
                            <option value="">Selecione uma subsidiária</option>
                            {subsidiaries.map(subsidiary => (
                                <option key={subsidiary.id} value={subsidiary.id}>
                                    {subsidiary.name}
                                </option>
                            ))}
                        </select>
                        {errors.subsidiaryId && <div className="error-message">{errors.subsidiaryId}</div>}
                    </div>

                    <div className="form-group">
                        <label>Nome da Sala/Cadeira*</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                            className={errors.name ? 'error' : ''}
                            placeholder="Nome da sala ou cadeira"
                        />
                        {errors.name && <div className="error-message">{errors.name}</div>}
                    </div>

                    <div className="form-group">
                        <label>Número da Sala/Cadeira*</label>
                        <input
                            type="text"
                            name="roomNumber"
                            value={formData.roomNumber}
                            onChange={handleChange}
                            required
                            className={errors.roomNumber ? 'error' : ''}
                            placeholder="Número ou identificador"
                        />
                        {errors.roomNumber && <div className="error-message">{errors.roomNumber}</div>}
                    </div>

                    <div className="form-group">
                        <label>Capacidade*</label>
                        <input
                            type="number"
                            name="capacity"
                            value={formData.capacity}
                            onChange={handleChange}
                            required
                            min="1"
                            className={errors.capacity ? 'error' : ''}
                            placeholder="Número de pessoas"
                        />
                        {errors.capacity && <div className="error-message">{errors.capacity}</div>}
                    </div>

                    <div className="form-group full-width">
                        <label>Descrição</label>
                        <textarea
                            name="description"
                            value={formData.description || ''}
                            onChange={handleChange}
                            placeholder="Descrição da sala ou cadeira"
                            rows={4}
                        />
                    </div>
                </div>
            )}
            
            {activeTab === 'schedule' && (
                <div className="tab-content">
                    {!initialData.id ? (
                        <div className="no-data">
                            Salve primeiro a sala/cadeira para gerenciar horários.
                        </div>
                    ) : (
                        <>
                          {/* Seção de Horários Recorrentes */}
                          <div className="recurring-schedule-section">
                            <h3>Definição de Horários Recorrentes</h3>
                            
                            <div className="date-range">
                              <div className="form-group">
                                <label>Data Início:</label>
                                <input
                                  type="date"
                                  value={recurrenceStart}
                                  onChange={(e) => setRecurrenceStart(e.target.value)}
                                />
                              </div>
                              
                              <div className="form-group">
                                <label>Data Término:</label>
                                <input
                                  type="date"
                                  value={recurrenceEnd}
                                  onChange={(e) => setRecurrenceEnd(e.target.value)}
                                />
                              </div>
                            </div>
                            
                            <table className="weekdays-schedule-table">
                              <thead>
                                <tr>
                                  <th>Dia</th>
                                  <th>Aberto</th>
                                  <th>Horário Abertura</th>
                                  <th>Horário Fechamento</th>
                                </tr>
                              </thead>
                              <tbody>
                                {Object.entries(weekSchedule).map(([day, config]) => (
                                  <tr key={day}>
                                    <td>{daysOfWeek[day]}</td>
                                    <td>
                                      <input
                                        type="checkbox"
                                        checked={config.open}
                                        onChange={(e) => 
                                          handleWeekdayScheduleChange(parseInt(day), 'open', e.target.checked)
                                        }
                                      />
                                    </td>
                                    <td>
                                      <input
                                        type="time"
                                        value={config.openTime}
                                        onChange={(e) => 
                                          handleWeekdayScheduleChange(parseInt(day), 'openTime', e.target.value)
                                        }
                                        disabled={!config.open}
                                      />
                                    </td>
                                    <td>
                                      <input
                                        type="time"
                                        value={config.closeTime}
                                        onChange={(e) => 
                                          handleWeekdayScheduleChange(parseInt(day), 'closeTime', e.target.value)
                                        }
                                        disabled={!config.open}
                                      />
                                    </td>
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                            
                            <button 
                              type="button"
                              className="primary"
                              onClick={handleApplyRecurringSchedule}
                            >
                              Aplicar Horários Recorrentes
                            </button>
                          </div>
                          
                          {/* Seção de Exceções */}
                          <div className="exceptions-section">
                            <h3>Exceções para Dias Específicos</h3>
                            
                            <div className="exception-form">
                              <div className="form-group">
                                <label>Data:</label>
                                <input
                                  type="date"
                                  value={exceptionDate}
                                  onChange={(e) => setExceptionDate(e.target.value)}
                                />
                              </div>
                              
                              <div className="form-group">
                                <label>Hora Abertura:</label>
                                <input
                                  type="time"
                                  value={exceptionSchedule.openTime}
                                  onChange={(e) => setExceptionSchedule({...exceptionSchedule, openTime: e.target.value})}
                                  disabled={exceptionSchedule.closed}
                                />
                              </div>
                              
                              <div className="form-group">
                                <label>Hora Fechamento:</label>
                                <input
                                  type="time"
                                  value={exceptionSchedule.closeTime}
                                  onChange={(e) => setExceptionSchedule({...exceptionSchedule, closeTime: e.target.value})}
                                  disabled={exceptionSchedule.closed}
                                />
                              </div>
                              
                              <div className="form-group checkbox-group">
                                <label>
                                  <input
                                    type="checkbox"
                                    checked={exceptionSchedule.closed}
                                    onChange={(e) => setExceptionSchedule({...exceptionSchedule, closed: e.target.checked})}
                                  />
                                  Fechado neste dia
                                </label>
                              </div>
                              
                              <button 
                                type="button" 
                                className="secondary"
                                onClick={handleAddException}
                              >
                                Adicionar Exceção
                              </button>
                            </div>
                            
                            {/* Lista de Exceções */}
                            {exceptions.length > 0 ? (
                              <div className="exceptions-list">
                                <h4>Dias com Horários Personalizados</h4>
                                <table>
                                  <thead>
                                    <tr>
                                      <th>Data</th>
                                      <th>Dia da Semana</th>
                                      <th>Status</th>
                                      <th>Abertura</th>
                                      <th>Fechamento</th>
                                      <th>Ações</th>
                                    </tr>
                                  </thead>
                                  <tbody>
                                    {exceptions.map(exception => (
                                      <tr key={exception.id} className="exception-row">
                                        <td>{new Date(exception.date).toLocaleDateString()}</td>
                                        <td>{daysOfWeek[new Date(exception.date).getDay()]}</td>
                                        <td>{exception.closed ? 'Fechado' : 'Aberto'}</td>
                                        <td>{exception.closed ? '-' : exception.openTime}</td>
                                        <td>{exception.closed ? '-' : exception.closeTime}</td>
                                        <td>
                                          <button 
                                            type="button" 
                                            className="delete-button"
                                            onClick={() => handleDeleteSpecificSchedule(exception.id)}
                                          >
                                            Excluir
                                          </button>
                                        </td>
                                      </tr>
                                    ))}
                                  </tbody>
                                </table>
                              </div>
                            ) : (
                              <p>Nenhuma exceção cadastrada.</p>
                            )}
                          </div>
                          
                          {/* Lista completa de horários */}
                          <div className="all-schedules-section">
                            <h3>Todos os Horários</h3>
                            {schedulesLoading ? (
                              <div className="loading-message">Carregando horários...</div>
                            ) : schedules.length === 0 ? (
                              <div className="no-data">
                                Nenhum horário definido para esta sala/cadeira.
                              </div>
                            ) : (
                              <table className="schedule-table">
                                <thead>
                                  <tr>
                                    <th>Data</th>
                                    <th>Dia da Semana</th>
                                    <th>Abertura</th>
                                    <th>Fechamento</th>
                                    <th>Status</th>
                                    <th>Tipo</th>
                                    <th>Ações</th>
                                  </tr>
                                </thead>
                                <tbody>
                                  {schedules.map(schedule => (
                                    <tr key={schedule.id} className={schedule.customized ? 'exception-row' : ''}>
                                      <td>{new Date(schedule.date).toLocaleDateString()}</td>
                                      <td>{daysOfWeek[schedule.dayOfWeek]}</td>
                                      <td>{schedule.openTime}</td>
                                      <td>{schedule.closeTime}</td>
                                      <td>{schedule.closed ? 'Fechado' : 'Aberto'}</td>
                                      <td>{schedule.customized ? 'Exceção' : 'Recorrente'}</td>
                                      <td className="action-buttons">
                                        <button 
                                          type="button"
                                          className="delete-button"
                                          onClick={() => handleDeleteSchedule(schedule.id!)}
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
                        </>
                    )}
                </div>
            )}

            <div className="form-buttons">
                <button type="button" className="secondary" onClick={onCancel}>Cancelar</button>
                <button type="submit" className="primary">Salvar</button>
            </div>
            
            {/* Modal de conflitos */}
            {showConflictDialog && (
              <div className="conflict-dialog">
                <div className="conflict-content">
                  <h3>Atenção: Conflito de Horários</h3>
                  <p>Os seguintes dias já possuem horários personalizados:</p>
                  <ul>
                    {conflicts.map((date, i) => (
                      <li key={i}>{new Date(date).toLocaleDateString()}</li>
                    ))}
                  </ul>
                  <p>Como deseja proceder?</p>
                  <div className="conflict-buttons">
                    <button 
                      type="button"
                      onClick={() => {
                        if (pendingRecurrenceAction) {
                          applyRecurringSchedules({
                            ...pendingRecurrenceAction,
                            replaceExisting: false
                          });
                        }
                      }}
                    >
                      Manter exceções existentes
                    </button>
                    <button 
                      type="button"
                      onClick={() => {
                        if (pendingRecurrenceAction) {
                          applyRecurringSchedules({
                            ...pendingRecurrenceAction,
                            replaceExisting: true
                          });
                        }
                      }}
                    >
                      Substituir com os novos horários
                    </button>
                    <button 
                      type="button"
                      onClick={() => {
                        setShowConflictDialog(false);
                        setPendingRecurrenceAction(null);
                      }}
                    >
                      Cancelar
                    </button>
                  </div>
                </div>
              </div>
            )}
        </form>
    );
}