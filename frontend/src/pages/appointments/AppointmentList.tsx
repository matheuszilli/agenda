import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { type AppointmentResponse, AppointmentStatus, appointmentService } from '../../services/appointmentService';
import { subsidiaryService } from '../../services/subsidiaryService';
import { customerService } from '../../services/customerService';
import { professionalService } from '../../services/professionalService';
import { itemService } from '../../services/itemService';
import './AppointmentList.css';

export default function AppointmentList() {
  const navigate = useNavigate();
  const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filter, setFilter] = useState({
    status: '',
    date: new Date().toISOString().split('T')[0],
    subsidiaryId: '',
    professionalId: ''
  });
  
  // Para mostrar nomes em vez de IDs
  const [subsidiaries, setSubsidiaries] = useState<any[]>([]);
  const [professionals, setProfessionals] = useState<any[]>([]);
  const [customers, setCustomers] = useState<Record<string, any>>({});
  const [services, setServices] = useState<Record<string, any>>({});
  
  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        setLoading(true);
        
        // Carregar subsidiárias
        const subsidiariesData = await subsidiaryService.getAll();
        setSubsidiaries(subsidiariesData);
        
        // Inicializar filtro com a primeira subsidiária se não estiver definida
        if (subsidiariesData.length > 0 && !filter.subsidiaryId) {
          setFilter(prev => ({
            ...prev,
            subsidiaryId: subsidiariesData[0].id ?? ''
          }));
        }
        
      } catch (err: any) {
        setError(`Erro ao carregar dados iniciais: ${err.message}`);
        console.error('Erro:', err);
      } finally {
        setLoading(false);
      }
    };
    
    fetchInitialData();
  }, []);
  
  // Quando a subsidiária mudar, carregar profissionais
  useEffect(() => {
    const fetchProfessionals = async () => {
      if (!filter.subsidiaryId) return;
      
      try {
        const data = await professionalService.getBySubsidiary(filter.subsidiaryId);
        setProfessionals(data);
      } catch (err) {
        console.error('Erro ao carregar profissionais:', err);
      }
    };
    
    fetchProfessionals();
  }, [filter.subsidiaryId]);
  
  // Carregar agendamentos com base nos filtros
  useEffect(() => {
    const fetchAppointments = async () => {
      if (!filter.subsidiaryId) return;
      
      try {
        setLoading(true);
        
        // Buscar agendamentos filtrados
        // Nota: ajuste de acordo com sua API
        let appointments: AppointmentResponse[];
        
        if (filter.professionalId) {
          appointments = await appointmentService.getByProfessional(
            filter.professionalId,
            filter.date
          );
        } else {
          appointments = await appointmentService.getBySubsidiary(
            filter.subsidiaryId,
            filter.date
          );
        }
        
        // Filtrar por status se especificado
        if (filter.status) {
          appointments = appointments.filter(app => app.status === filter.status);
        }
        
        setAppointments(appointments);
        
        // Carregar dados relacionados para exibição
        await fetchRelatedEntities(appointments);
        
      } catch (err: any) {
        setError(`Erro ao carregar agendamentos: ${err.message}`);
        console.error('Erro:', err);
      } finally {
        setLoading(false);
      }
    };
    
    fetchAppointments();
  }, [filter]);
  
  // Buscar entidades relacionadas para exibição
  const fetchRelatedEntities = async (appointments: AppointmentResponse[]) => {
    // Extrair IDs únicos
    const customerIds = Array.from(new Set(appointments.map(app => app.customerId)));
    const serviceIds = Array.from(new Set(appointments.map(app => app.itemId)));
    
    try {
      // Buscar clientes
      const customersMap: Record<string, any> = {};
      for (const id of customerIds) {
        if (!customers[id]) {
          try {
            const customer = await customerService.getById(id);
            customersMap[id] = customer;
          } catch (err) {
            console.error(`Erro ao carregar cliente ${id}:`, err);
          }
        }
      }
      
      // Buscar serviços
      const servicesMap: Record<string, any> = {};
      for (const id of serviceIds) {
        if (!services[id]) {
          try {
            const service = await itemService.getById(id);
            servicesMap[id] = service;
          } catch (err) {
            console.error(`Erro ao carregar serviço ${id}:`, err);
          }
        }
      }
      
      // Atualizar estado
      setCustomers(prev => ({ ...prev, ...customersMap }));
      setServices(prev => ({ ...prev, ...servicesMap }));
      
    } catch (err) {
      console.error('Erro ao carregar entidades relacionadas:', err);
    }
  };
  
  const handleFilterChange = (e: React.ChangeEvent<HTMLSelectElement | HTMLInputElement>) => {
    const { name, value } = e.target;
    setFilter(prev => ({
      ...prev,
      [name]: value
    }));
  };
  
  const handleStatusChange = async (appointmentId: string, newStatus: AppointmentStatus) => {
    try {
      await appointmentService.updateStatus(appointmentId, newStatus);
      
      // Atualizar lista localmente
      setAppointments(prev => 
        prev.map(app => 
          app.id === appointmentId 
            ? { ...app, status: newStatus } 
            : app
        )
      );
    } catch (err: any) {
      setError(`Erro ao atualizar status: ${err.message}`);
      console.error('Erro:', err);
    }
  };
  
  const handleNewAppointment = () => {
    if (filter.subsidiaryId) {
      navigate(`/agenda/${filter.subsidiaryId}/novo`);
    } else {
      setError('Selecione uma subsidiária para criar um novo agendamento');
    }
  };
  
  const handleEdit = (appointmentId: string) => {
    navigate(`/agenda/editar/${appointmentId}`);
  };
  
  const handleDelete = async (appointmentId: string) => {
    if (!window.confirm('Tem certeza que deseja cancelar este agendamento?')) {
      return;
    }
    
    try {
      await appointmentService.delete(appointmentId);
      
      // Atualizar lista removendo o agendamento
      setAppointments(prev => prev.filter(app => app.id !== appointmentId));
    } catch (err: any) {
      setError(`Erro ao cancelar agendamento: ${err.message}`);
      console.error('Erro:', err);
    }
  };
  
  // Formatar data e hora
  const formatDateTime = (isoString: string) => {
    return new Date(isoString).toLocaleString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };
  
  // Obter nome de entidade a partir do ID
  const getCustomerName = (id: string) => {
    const customer = customers[id];
    return customer 
      ? customer.fullName || `${customer.firstName} ${customer.lastName}`
      : 'Cliente não encontrado';
  };
  
  const getProfessionalName = (id: string) => {
    const professional = professionals.find(p => p.id === id);
    return professional 
      ? professional.fullName || `${professional.firstName} ${professional.lastName}`
      : 'Profissional não encontrado';
  };
  
  const getServiceName = (id: string) => {
    const service = services[id];
    return service ? service.name : 'Serviço não encontrado';
  };
  
  const getStatusLabel = (status: AppointmentStatus) => {
    switch (status) {
      case AppointmentStatus.SCHEDULED:
        return 'Agendado';
      case AppointmentStatus.CONFIRMED:
        return 'Confirmado';
      case AppointmentStatus.IN_PROGRESS:
        return 'Em Andamento';
      case AppointmentStatus.COMPLETED:
        return 'Concluído';
      case AppointmentStatus.CANCELED:
        return 'Cancelado';
      case AppointmentStatus.NO_SHOW:
        return 'Não Compareceu';
      default:
        return status;
    }
  };
  
  if (loading && subsidiaries.length === 0) return <div className="loading">Carregando...</div>;
  
  return (
    <div className="appointment-list-container">
      <div className="list-header">
        <h2>Agendamentos</h2>
        <button className="add-button" onClick={handleNewAppointment}>
          + Novo Agendamento
        </button>
      </div>
      
      {error && <div className="error-message">{error}</div>}
      
      <div className="filter-section">
        <div className="filter-group">
          <label>Subsidiária</label>
          <select
            name="subsidiaryId"
            value={filter.subsidiaryId}
            onChange={handleFilterChange}
          >
            <option value="">Todas</option>
            {subsidiaries.map(subsidiary => (
              <option key={subsidiary.id} value={subsidiary.id}>
                {subsidiary.name}
              </option>
            ))}
          </select>
        </div>
        
        <div className="filter-group">
          <label>Profissional</label>
          <select
            name="professionalId"
            value={filter.professionalId}
            onChange={handleFilterChange}
            disabled={!filter.subsidiaryId}
          >
            <option value="">Todos</option>
            {professionals.map(professional => (
              <option key={professional.id} value={professional.id}>
                {professional.fullName || `${professional.firstName} ${professional.lastName}`}
              </option>
            ))}
          </select>
        </div>
        
        <div className="filter-group">
          <label>Data</label>
          <input
            type="date"
            name="date"
            value={filter.date}
            onChange={handleFilterChange}
          />
        </div>
        
        <div className="filter-group">
          <label>Status</label>
          <select
            name="status"
            value={filter.status}
            onChange={handleFilterChange}
          >
            <option value="">Todos</option>
            <option value={AppointmentStatus.SCHEDULED}>Agendado</option>
            <option value={AppointmentStatus.CONFIRMED}>Confirmado</option>
            <option value={AppointmentStatus.IN_PROGRESS}>Em Andamento</option>
            <option value={AppointmentStatus.COMPLETED}>Concluído</option>
            <option value={AppointmentStatus.CANCELED}>Cancelado</option>
            <option value={AppointmentStatus.NO_SHOW}>Não Compareceu</option>
          </select>
        </div>
      </div>
      
      {loading ? (
        <div className="loading">Carregando agendamentos...</div>
      ) : appointments.length === 0 ? (
        <div className="no-data">
          Nenhum agendamento encontrado para os filtros selecionados.
        </div>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Horário</th>
              <th>Cliente</th>
              <th>Profissional</th>
              <th>Serviço</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {appointments.map(appointment => (
              <tr key={appointment.id}>
                <td>
                  {formatDateTime(appointment.startTime)}
                </td>
                <td>{getCustomerName(appointment.customerId)}</td>
                <td>{getProfessionalName(appointment.professionalId)}</td>
                <td>{getServiceName(appointment.itemId)}</td>
                <td>
                  <span className={`status-badge status-${appointment.status.toLowerCase()}`}>
                    {getStatusLabel(appointment.status)}
                  </span>
                </td>
                <td className="action-buttons">
                  <button 
                    className="edit-button"
                    onClick={() => handleEdit(appointment.id)}
                  >
                    Editar
                  </button>
                  
                  {appointment.status === AppointmentStatus.SCHEDULED && (
                    <button 
                      className="confirm-button"
                      onClick={() => handleStatusChange(appointment.id, AppointmentStatus.CONFIRMED)}
                    >
                      Confirmar
                    </button>
                  )}
                  
                  {(appointment.status === AppointmentStatus.SCHEDULED || 
                   appointment.status === AppointmentStatus.CONFIRMED) && (
                    <button 
                      className="delete-button"
                      onClick={() => handleDelete(appointment.id)}
                    >
                      Cancelar
                    </button>
                  )}
                  
                  {appointment.status === AppointmentStatus.CONFIRMED && (
                    <button 
                      className="progress-button"
                      onClick={() => handleStatusChange(appointment.id, AppointmentStatus.IN_PROGRESS)}
                    >
                      Iniciar
                    </button>
                  )}
                  
                  {appointment.status === AppointmentStatus.IN_PROGRESS && (
                    <button 
                      className="complete-button"
                      onClick={() => handleStatusChange(appointment.id, AppointmentStatus.COMPLETED)}
                    >
                      Concluir
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}