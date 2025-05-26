import { useState, useEffect } from 'react';
import { type AppointmentResponse, appointmentService } from '../../services/appointmentService';
import './CalendarViews.css';

interface CalendarDayViewProps {
  date: Date;
  subsidiaryId: string;
  professionalId?: string;
  onAppointmentClick?: (appointment: AppointmentResponse) => void;
  onTimeSlotClick?: (time: string) => void;
}

// Gera horários das 8h às 20h com intervalo de 30 minutos
const generateTimeSlots = () => {
  const slots = [];
  const startHour = 8;
  const endHour = 20;
  
  for (let hour = startHour; hour <= endHour; hour++) {
    for (let minutes of [0, 30]) {
      const timeString = `${hour.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
      slots.push(timeString);
    }
  }
  
  return slots;
};

export default function CalendarDayView({ 
  date, 
  subsidiaryId, 
  professionalId,
  onAppointmentClick,
  onTimeSlotClick
}: CalendarDayViewProps) {
  const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const timeSlots = generateTimeSlots();
  
  useEffect(() => {
    const fetchAppointments = async () => {
      try {
        setLoading(true);
        let appointments: AppointmentResponse[] = [];
        
        // Se tivermos um profissional específico, buscamos agendamentos dele
        if (professionalId) {
          appointments = await appointmentService.getByProfessional(
            professionalId,
            date.toISOString().split('T')[0]
          );
        } else {
          // Caso contrário, buscamos todos da subsidiária
          appointments = await appointmentService.getBySubsidiary(
            subsidiaryId,
            date.toISOString().split('T')[0]
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
  }, [date, subsidiaryId, professionalId]);
  
  // Verifica se há agendamento no horário
  const getAppointmentForTimeSlot = (timeSlot: string) => {
    const [hours, minutes] = timeSlot.split(':').map(Number);
    const slotTime = new Date(date);
    slotTime.setHours(hours, minutes, 0, 0);
    
    return appointments.find(app => {
      const startTime = new Date(app.startTime);
      const endTime = new Date(app.endTime);
      
      return slotTime >= startTime && slotTime < endTime;
    });
  };
  
  const handleTimeSlotClick = (timeSlot: string) => {
    if (onTimeSlotClick) {
      const [hours, minutes] = timeSlot.split(':').map(Number);
      const slotDate = new Date(date);
      slotDate.setHours(hours, minutes, 0, 0);
      
      onTimeSlotClick(slotDate.toISOString());
    }
  };
  
  const formatDate = (dateString: string) => {
    const options: Intl.DateTimeFormatOptions = { 
      weekday: 'long', 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    };
    return new Date(dateString).toLocaleDateString('pt-BR', options);
  };
  
  if (loading) return <div className="loading">Carregando agendamentos...</div>;
  
  return (
    <div className="calendar-day-view">
      <h3 className="date-header">{formatDate(date.toISOString())}</h3>
      
      {error && <div className="error-message">{error}</div>}
      
      <div className="time-slots-container">
        {timeSlots.map(timeSlot => {
          const appointment = getAppointmentForTimeSlot(timeSlot);
          
          return (
            <div 
              key={timeSlot} 
              className={`time-slot ${appointment ? 'occupied' : 'available'}`}
              onClick={() => appointment 
                ? onAppointmentClick && onAppointmentClick(appointment)
                : handleTimeSlotClick(timeSlot)
              }
            >
              <div className="time-label">{timeSlot}</div>
              <div className="appointment-info">
                {appointment && (
                  <>
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
                  </>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}