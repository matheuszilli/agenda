import { useState, useEffect } from 'react';
import { type Appointment, AppointmentStatus, appointmentService } from '../../services/appointmentService';
import { type Customer, customerService } from '../../services/customerService';
import { type Professional, professionalService } from '../../services/professionalService';
import { type Item, itemService } from '../../services/itemService';
import { type ChairRoom, chairRoomService } from '../../services/chairRoomService';
import './CompanyForm.css';

interface AppointmentFormProps {
  initialData?: Appointment;
  subsidiaryId: string;
  companyId: string;
  selectedDate?: string;
  onSubmit: (data: Appointment) => void;
  onCancel: () => void;
}

const emptyAppointment: Appointment = {
  customerId: '',
  professionalId: '',
  chairRoomId: '',
  itemId: '',
  subsidiaryId: '',
  companyId: '',
  startTime: '',
  endTime: '',
  notes: ''
};

export default function AppointmentForm({
  initialData = emptyAppointment,
  subsidiaryId,
  companyId,
  selectedDate,
  onSubmit,
  onCancel
}: AppointmentFormProps) {
  const [formData, setFormData] = useState<Appointment>({
    ...initialData,
    subsidiaryId: subsidiaryId || initialData.subsidiaryId,
    companyId: companyId || initialData.companyId,
    startTime: selectedDate || initialData.startTime
  });
  
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [services, setServices] = useState<Item[]>([]);
  const [chairRooms, setChairRooms] = useState<ChairRoom[]>([]);
  const [selectedService, setSelectedService] = useState<Item | null>(null);
  const [availableProfessionals, setAvailableProfessionals] = useState<Professional[]>([]);
  const [availableChairRooms, setAvailableChairRooms] = useState<ChairRoom[]>([]);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(true);
  const [checkingAvailability, setCheckingAvailability] = useState(false);
  const [hasConflicts, setHasConflicts] = useState(false);
  
  // Carregar dados iniciais
  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        setLoading(true);
        
        // Carregar clientes, profissionais, serviços e salas em paralelo
        const [
          customersData,
          professionalsData,
          servicesData,
          chairRoomsData
        ] = await Promise.all([
          customerService.getByCompany(companyId),
          professionalService.getBySubsidiary(subsidiaryId),
          itemService.getBySubsidiary(subsidiaryId),
          chairRoomService.getBySubsidiary(subsidiaryId)
        ]);
        
        setCustomers(customersData);
        setProfessionals(professionalsData);
        setServices(servicesData);
        setChairRooms(chairRoomsData);
        
        // Se tivermos um serviço selecionado, carregue os profissionais que oferecem esse serviço
        if (initialData.itemId) {
          const service = servicesData.find(s => s.id === initialData.itemId);
          if (service) {
            setSelectedService(service);
            updateEndTime(initialData.startTime, service.durationMinutes);
          }
        }
      } catch (error) {
        console.error('Erro ao carregar dados iniciais:', error);
      } finally {
        setLoading(false);
      }
    };
    
    fetchInitialData();
  }, [subsidiaryId, companyId]);
  
  // Atualizar data de término baseado no serviço selecionado
  const updateEndTime = (startTime: string, durationMinutes: number) => {
    if (!startTime) return;
    
    const start = new Date(startTime);
    const end = new Date(start);
    end.setMinutes(start.getMinutes() + durationMinutes);
    
    setFormData(prev => ({
      ...prev,
      endTime: end.toISOString()
    }));
  };
  
  // Quando um serviço é selecionado, filtra os profissionais que o oferecem
  useEffect(() => {
    if (formData.itemId && professionals.length > 0 && services.length > 0) {
      const service = services.find(s => s.id === formData.itemId);
      setSelectedService(service || null);
      
      if (service) {
        // Atualizar a hora de término baseado na duração do serviço
        updateEndTime(formData.startTime, service.durationMinutes);
        
        // Aqui você pode filtrar os profissionais que oferecem este serviço
        // Isso dependerá de como sua API está estruturada
        // Por enquanto, usamos todos os profissionais
        setAvailableProfessionals(professionals);
        
        // Para salas/cadeiras, podemos usar todas por enquanto
        setAvailableChairRooms(chairRooms);
      }
    }
  }, [formData.itemId, professionals, services]);
  
  // Verificar disponibilidade quando os campos necessários estiverem preenchidos
  useEffect(() => {
    const checkAvailability = async () => {
      if (
        formData.startTime &&
        formData.endTime &&
        formData.subsidiaryId &&
        (formData.professionalId || formData.chairRoomId)
      ) {
        try {
          setCheckingAvailability(true);
          
          // Verificar conflitos
          const hasConflicts = await appointmentService.checkConflicts(formData);
          setHasConflicts(hasConflicts);
          
          if (hasConflicts) {
            setErrors(prev => ({
              ...prev,
              general: 'Há conflitos de agenda no horário selecionado'
            }));
          } else {
            setErrors(prev => {
              const newErrors = { ...prev };
              delete newErrors.general;
              return newErrors;
            });
          }
        } catch (error) {
          console.error('Erro ao verificar disponibilidade:', error);
        } finally {
          setCheckingAvailability(false);
        }
      }
    };
    
    checkAvailability();
  }, [formData.startTime, formData.endTime, formData.professionalId, formData.chairRoomId]);
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    
    // Lógica especial para lidar com a seleção de serviço
    if (name === 'itemId') {
      const service = services.find(s => s.id === value);
      setSelectedService(service || null);
      
      if (service && formData.startTime) {
        // Atualizar a hora de término baseado na duração do serviço
        updateEndTime(formData.startTime, service.durationMinutes);
      }
    }
    
    // Atualizar a hora de término quando a hora de início mudar
    if (name === 'startTime' && selectedService) {
      updateEndTime(value, selectedService.durationMinutes);
    }
    
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Limpar erro específico
    if (errors[name]) {
      setErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[name];
        return newErrors;
      });
    }
  };
  
  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    
    if (!formData.customerId) newErrors.customerId = 'Cliente é obrigatório';
    if (!formData.professionalId) newErrors.professionalId = 'Profissional é obrigatório';
    if (!formData.itemId) newErrors.itemId = 'Serviço é obrigatório';
    if (!formData.chairRoomId) newErrors.chairRoomId = 'Sala/Cadeira é obrigatória';
    if (!formData.startTime) newErrors.startTime = 'Horário de início é obrigatório';
    if (!formData.endTime) newErrors.endTime = 'Horário de término é obrigatório';
    
    // Verificar se há conflitos
    if (hasConflicts) {
      newErrors.general = 'Há conflitos de agenda no horário selecionado';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };
  
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (validateForm()) {
      onSubmit(formData);
    }
  };
  
  // Formatar data e hora para o formato do input datetime-local
  const formatDateTimeForInput = (isoString: string) => {
    if (!isoString) return '';
    return isoString.slice(0, 16); // "YYYY-MM-DDThh:mm"
  };
  
  if (loading) return <div className="loading">Carregando...</div>;
  
  return (
    <form onSubmit={handleSubmit} className="company-form">
      <h2 className="form-section-header">
        {initialData.id ? 'Editar Agendamento' : 'Novo Agendamento'}
      </h2>
      
      {errors.general && (
        <div className="error-message">{errors.general}</div>
      )}
      
      <div className="form-group">
        <label>Cliente*</label>
        <select
          name="customerId"
          value={formData.customerId}
          onChange={handleChange}
          className={errors.customerId ? 'error' : ''}
        >
          <option value="">Selecione um cliente</option>
          {customers.map(customer => (
            <option key={customer.id} value={customer.id}>
              {customer.fullName || `${customer.firstName} ${customer.lastName}`}
            </option>
          ))}
        </select>
        {errors.customerId && <div className="error-message">{errors.customerId}</div>}
      </div>
      
      <div className="form-group">
        <label>Serviço*</label>
        <select
          name="itemId"
          value={formData.itemId}
          onChange={handleChange}
          className={errors.itemId ? 'error' : ''}
        >
          <option value="">Selecione um serviço</option>
          {services.map(service => (
            <option key={service.id} value={service.id}>
              {service.name} - {service.price.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })} ({service.durationMinutes} min)
            </option>
          ))}
        </select>
        {errors.itemId && <div className="error-message">{errors.itemId}</div>}
      </div>
      
      <div className="form-group">
        <label>Profissional*</label>
        <select
          name="professionalId"
          value={formData.professionalId}
          onChange={handleChange}
          className={errors.professionalId ? 'error' : ''}
          disabled={!formData.itemId}
        >
          <option value="">Selecione um profissional</option>
          {availableProfessionals.map(professional => (
            <option key={professional.id} value={professional.id}>
              {professional.fullName || `${professional.firstName} ${professional.lastName}`}
            </option>
          ))}
        </select>
        {!formData.itemId && (
          <div className="info-message">Selecione um serviço primeiro</div>
        )}
        {errors.professionalId && <div className="error-message">{errors.professionalId}</div>}
      </div>
      
      <div className="form-group">
        <label>Sala/Cadeira*</label>
        <select
          name="chairRoomId"
          value={formData.chairRoomId}
          onChange={handleChange}
          className={errors.chairRoomId ? 'error' : ''}
          disabled={!formData.itemId}
        >
          <option value="">Selecione uma sala/cadeira</option>
          {availableChairRooms.map(chairRoom => (
            <option key={chairRoom.id} value={chairRoom.id}>
              {chairRoom.name} (Sala {chairRoom.roomNumber})
            </option>
          ))}
        </select>
        {!formData.itemId && (
          <div className="info-message">Selecione um serviço primeiro</div>
        )}
        {errors.chairRoomId && <div className="error-message">{errors.chairRoomId}</div>}
      </div>
      
      <div className="form-group">
        <label>Data e Hora de Início*</label>
        <input
          type="datetime-local"
          name="startTime"
          value={formatDateTimeForInput(formData.startTime)}
          onChange={handleChange}
          className={errors.startTime ? 'error' : ''}
        />
        {errors.startTime && <div className="error-message">{errors.startTime}</div>}
      </div>
      
      <div className="form-group">
        <label>Data e Hora de Término*</label>
        <input
          type="datetime-local"
          name="endTime"
          value={formatDateTimeForInput(formData.endTime)}
          onChange={handleChange}
          className={errors.endTime ? 'error' : ''}
          disabled={!selectedService}
        />
        {!selectedService && (
          <div className="info-message">Calculado automaticamente a partir do serviço</div>
        )}
        {errors.endTime && <div className="error-message">{errors.endTime}</div>}
      </div>
      
      <div className="form-group full-width">
        <label>Observações</label>
        <textarea
          name="notes"
          value={formData.notes || ''}
          onChange={handleChange}
          rows={4}
          placeholder="Informações adicionais sobre o agendamento"
        />
      </div>
      
      {checkingAvailability && (
        <div className="info-message">Verificando disponibilidade...</div>
      )}
      
      <div className="form-buttons">
        <button type="button" className="secondary" onClick={onCancel}>Cancelar</button>
        <button 
          type="submit" 
          className="primary" 
          disabled={checkingAvailability || hasConflicts}
        >
          Salvar
        </button>
      </div>
    </form>
  );
}