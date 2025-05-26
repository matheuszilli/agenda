import { useState, useEffect } from 'react';
import { type AppointmentResponse, appointmentService } from '../../services/appointmentService';
import './CalendarViews.css';

interface CalendarWeekViewProps {
  startDate: Date;
  subsidiaryId: string;
  professionalId?: string;
  onAppointmentClick?: (appointment: AppointmentResponse) => void;
  onDayClick?: (date: Date) => void;
}

// Gera os dias da semana a partir de uma data inicial
const generateWeekDays = (startDate: Date) => {
  const days = [];
  const currentDate = new Date(startDate);
  
  // Ajustar para começar na segunda-feira se startDate não for segunda
  const dayOfWeek = currentDate.getDay(); // 0 é domingo, 1 é segunda, etc.
  const diff = dayOfWeek === 0 ? -6 : 1 - dayOfWeek; // Ajuste para começar na segunda
  
  currentDate.setDate(currentDate.getDate() + diff);
  
  // Gerar 7 dias a partir da segunda-feira
  for (let i = 0; i < 7; i++) {
    const day = new Date(currentDate);
    days.push(day);
    currentDate.setDate(currentDate.getDate() + 1);
  }
  
  return days;
};

// Função para formatar hora
const formatTime = (dateString: string) => {
  return new Date(dateString).toLocaleTimeString('pt-BR', {
    hour: '2-digit',
    minute: '2-digit'
  });
};

export default function CalendarWeekView({
  startDate,
  subsidiaryId,
  professionalId,
  onAppointmentClick,
  onDayClick
}: CalendarWeekViewProps) {
  const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const weekDays = generateWeekDays(startDate);
  
  useEffect(() => {
    const fetchAppointments = async () => {
      try {
        setLoading(true);
        
        // Preparar as datas de início e fim da semana para buscar agendamentos
        const weekStart = weekDays[0].toISOString().split('T')[0];
        const weekEnd = weekDays[6].toISOString().split('T')[0];
        
        let appointments: AppointmentResponse[] = [];
        
        // Buscar agendamentos para a semana toda
        if (professionalId) {
          // Se tivermos um profissional específico, buscamos para ele
          // Nota: Ajuste este endpoint conforme sua API
          appointments = await appointmentService.getByProfessional(
            professionalId,
            `${weekStart},${weekEnd}` // Formato para range de datas
          );
        } else {
          // Buscamos para a subsidiária toda
          appointments = await appointmentService.getBySubsidiary(
            subsidiaryId,
            `${weekStart},${weekEnd}`
          );
        }
        
        setAppointments(appointments);
        setError('');
      } catch (err: any) {
        setError(`Erro ao carregar agendamentos: ${err.message}`);
        console.error('Erro:', err);
      } finally {
        setLoading(false);
      }
    };
    
    fetchAppointments();
  }, [startDate, subsidiaryId, professionalId]);
  
  // Filtra agendamentos para um dia específico
  const getAppointmentsForDay = (date: Date) => {
    const dayStart = new Date(date);
    dayStart.setHours(0, 0, 0, 0);
    
    const dayEnd = new Date(date);
    dayEnd.setHours(23, 59, 59, 999);
    
    return appointments.filter(app => {
      const appointmentDate = new Date(app.startTime);
      return appointmentDate >= dayStart && appointmentDate <= dayEnd;
    });
  };
  
  const handleDayClick = (day: Date) => {
    if (onDayClick) {
      onDayClick(day);
    }
  };
  
  if (loading) return <div className="loading">Carregando agendamentos...</div>;
  
  return (
    <div className="calendar-week-view">
      {error && <div className="error-message">{error}</div>}
      
      <div className="week-header">
        {weekDays.map((day, index) => (
          <div key={index} className="week-day">
            <div className="day-name">{day.toLocaleDateString('pt-BR', { weekday: 'short' })}</div>
            <div className="day-date">{day.getDate()}/{day.getMonth() + 1}</div>
          </div>
        ))}
      </div>
      
      <div className="week-body">
        {weekDays.map((day, index) => {
          const dayAppointments = getAppointmentsForDay(day);
          
          return (
            <div 
              key={index} 
              className="day-column"
              onClick={() => handleDayClick(day)}
            >
              {dayAppointments.length === 0 ? (
                <div className="no-appointments">Sem agendamentos</div>
              ) : (
                dayAppointments.map(appointment => (
                  <div 
                    key={appointment.id} 
                    className="week-appointment"
                    onClick={(e) => {
                      e.stopPropagation();
                      onAppointmentClick && onAppointmentClick(appointment);
                    }}
                  >
                    <div className="appointment-time">
                      {formatTime(appointment.startTime)} - {formatTime(appointment.endTime)}
                    </div>
                    <div className="appointment-info">
                      <div className="appointment-customer">
                        Cliente: {appointment.customerId}
                      </div>
                      <div className="appointment-service">
                        Serviço: {appointment.itemId}
                      </div>
                      {!professionalId && (
                        <div className="appointment-professional">
                          Profissional: {appointment.professionalId}
                        </div>
                      )}
                    </div>
                  </div>
                ))
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}