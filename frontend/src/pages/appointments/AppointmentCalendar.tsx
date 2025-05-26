import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { type AppointmentResponse, appointmentService } from '../../services/appointmentService';
import { subsidiaryService } from '../../services/subsidiaryService';
import { professionalService } from '../../services/professionalService';
import { customerService } from '../../services/customerService';
import { itemService } from '../../services/itemService';
import CalendarDayView from '../../components/calendar/CalendarDayView';
import CalendarWeekView from '../../components/calendar/CalendarWeekView';
import CalendarMonthView from '../../components/calendar/CalendarMonthView';
import CalendarNavigation, { CalendarViewType } from '../../components/calendar/CalendarNavigation';
import AppointmentForm from '../../components/forms/AppointmentForm';
import './AppointmentCalendar.css';

export default function AppointmentCalendar() {
  const { subsidiaryId } = useParams<{ subsidiaryId: string }>();
  const navigate = useNavigate();
  
  const [currentDate, setCurrentDate] = useState(new Date());
  const [currentView, setCurrentView] = useState<CalendarViewType>(CalendarViewType.WEEK);
  const [subsidiary, setSubsidiary] = useState<any>(null);
  const [selectedAppointment, setSelectedAppointment] = useState<AppointmentResponse | null>(null);
  const [showAppointmentForm, setShowAppointmentForm] = useState(false);
  const [newAppointmentTime, setNewAppointmentTime] = useState<string | null>(null);
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Nome cacheado de entidades para exibição
  const [entityNames, setEntityNames] = useState<{
    customers: Record<string, string>,
    professionals: Record<string, string>,
    services: Record<string, string>
  }>({
    customers: {},
    professionals: {},
    services: {}
  });
  
  // Carregar informações da subsidiária
  useEffect(() => {
    const fetchSubsidiary = async () => {
      if (!subsidiaryId) return;
      
      try {
        setLoading(true);
        const data = await subsidiaryService.getById(subsidiaryId);
        setSubsidiary(data);
      } catch (err: any) {
        setError(`Erro ao carregar subsidiária: ${err.message}`);
        console.error('Erro:', err);
      } finally {
        setLoading(false);
      }
    };
    
    fetchSubsidiary();
  }, [subsidiaryId]);
  
  // Carregar nomes de entidades para exibição
  useEffect(() => {
    const fetchEntityNames = async () => {
      if (!subsidiaryId) return;
      
      try {
        // Carregar clientes, profissionais e serviços em paralelo
        const [
          customers,
          professionals,
          services
        ] = await Promise.all([
          customerService.getByCompany(subsidiary?.companyId || ''),
          professionalService.getBySubsidiary(subsidiaryId),
          itemService.getBySubsidiary(subsidiaryId)
        ]);
        
        // Criar mapas de IDs para nomes
        const customerNames: Record<string, string> = {};
        customers.forEach(customer => {
          customerNames[customer.id!] = customer.fullName || `${customer.firstName} ${customer.lastName}`;
        });
        
        const professionalNames: Record<string, string> = {};
        professionals.forEach(professional => {
          professionalNames[professional.id!] = professional.fullName || `${professional.firstName} ${professional.lastName}`;
        });
        
        const serviceNames: Record<string, string> = {};
        services.forEach(service => {
          serviceNames[service.id!] = service.name;
        });
        
        setEntityNames({
          customers: customerNames,
          professionals: professionalNames,
          services: serviceNames
        });
      } catch (err) {
        console.error('Erro ao carregar nomes de entidades:', err);
      }
    };
    
    if (subsidiary?.companyId) {
      fetchEntityNames();
    }
  }, [subsidiary]);
  
  // Manipuladores de eventos
  const handleDateChange = (date: Date) => {
    setCurrentDate(date);
  };
  
  const handleViewChange = (view: CalendarViewType) => {
    setCurrentView(view);
  };
  
  const handleAppointmentClick = (appointment: AppointmentResponse) => {
    setSelectedAppointment(appointment);
    setShowAppointmentForm(true);
  };
  
  const handleDayClick = (date: Date) => {
    setCurrentDate(date);
    setCurrentView(CalendarViewType.DAY);
  };
  
  const handleTimeSlotClick = (time: string) => {
    setNewAppointmentTime(time);
    setShowAppointmentForm(true);
  };
  
  const handleFormCancel = () => {
    setSelectedAppointment(null);
    setNewAppointmentTime(null);
    setShowAppointmentForm(false);
  };
  
  const handleFormSubmit = async (data: any) => {
    try {
      setLoading(true);
      
      if (selectedAppointment) {
        // Atualizar agendamento existente
        await appointmentService.update(selectedAppointment.id, data);
      } else {
        // Criar novo agendamento
        await appointmentService.create(data);
      }
      
      // Fechar formulário e limpar seleção
      setSelectedAppointment(null);
      setNewAppointmentTime(null);
      setShowAppointmentForm(false);
      
      // Atualizar a visualização atual
      // Nota: idealmente deveríamos recarregar os dados aqui
      
    } catch (err: any) {
      setError(`Erro ao salvar agendamento: ${err.message}`);
      console.error('Erro ao salvar agendamento:', err);
    } finally {
      setLoading(false);
    }
  };
  
  if (loading && !subsidiary) return <div className="loading">Carregando...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (!subsidiary) return <div className="error-message">Subsidiária não encontrada</div>;
  
  // Renderiza o formulário ou a visualização do calendário
  if (showAppointmentForm) {
    return (
      <div className="appointment-calendar-container">
        <AppointmentForm
          initialData={selectedAppointment ? {
            id: selectedAppointment.id,
            customerId: selectedAppointment.customerId,
            professionalId: selectedAppointment.professionalId,
            itemId: selectedAppointment.itemId,
            chairRoomId: '', // Necessário obter esta informação
            subsidiaryId: selectedAppointment.subsidiaryId,
            companyId: selectedAppointment.companyId,
            startTime: selectedAppointment.startTime,
            endTime: selectedAppointment.endTime,
            notes: ''
          } : undefined}
          subsidiaryId={subsidiaryId || ''}
          companyId={subsidiary.companyId}
          selectedDate={newAppointmentTime || undefined}
          onSubmit={handleFormSubmit}
          onCancel={handleFormCancel}
        />
      </div>
    );
  }
  
  return (
    <div className="appointment-calendar-container">
      <h2>Agenda - {subsidiary.name}</h2>
      
      <CalendarNavigation
        date={currentDate}
        view={currentView}
        onDateChange={handleDateChange}
        onViewChange={handleViewChange}
      />
      
      {currentView === CalendarViewType.DAY && (
        <CalendarDayView
          date={currentDate}
          subsidiaryId={subsidiaryId || ''}
          onAppointmentClick={handleAppointmentClick}
          onTimeSlotClick={handleTimeSlotClick}
        />
      )}
      
      {currentView === CalendarViewType.WEEK && (
        <CalendarWeekView
          startDate={currentDate}
          subsidiaryId={subsidiaryId || ''}
          onAppointmentClick={handleAppointmentClick}
          onDayClick={handleDayClick}
        />
      )}
      
      {currentView === CalendarViewType.MONTH && (
        <CalendarMonthView
          date={currentDate}
          subsidiaryId={subsidiaryId || ''}
          onDateClick={handleDayClick}
        />
      )}
      
      <div className="appointment-actions">
        <button className="add-appointment-button" onClick={() => {
          setNewAppointmentTime(new Date().toISOString());
          setShowAppointmentForm(true);
        }}>
          + Novo Agendamento
        </button>
      </div>
    </div>
  );
}