import { useState, useEffect } from 'react';
import { type AppointmentResponse, appointmentService } from '../../services/appointmentService';
import './CalendarViews.css';

interface CalendarMonthViewProps {
  date: Date;
  subsidiaryId: string;
  professionalId?: string;
  onDateClick?: (date: Date) => void;
}

// Dias da semana em português
const weekDays = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];

// Gera os dias do mês, incluindo dias do mês anterior e próximo para completar semanas
const generateMonthDays = (date: Date) => {
  const year = date.getFullYear();
  const month = date.getMonth();
  
  // Primeiro dia do mês
  const firstDay = new Date(year, month, 1);
  // Último dia do mês
  const lastDay = new Date(year, month + 1, 0);
  
  // Dia da semana do primeiro dia (0 = Domingo, 1 = Segunda, etc.)
  const firstDayOfWeek = firstDay.getDay();
  
  // Total de dias no mês
  const daysInMonth = lastDay.getDate();
  
  // Dias do mês anterior para completar a primeira semana
  const daysFromPrevMonth = firstDayOfWeek;
  
  // Array para armazenar todos os dias que serão exibidos
  const allDays = [];
  
  // Adicionar dias do mês anterior
  for (let i = daysFromPrevMonth - 1; i >= 0; i--) {
    const day = new Date(year, month, -i);
    allDays.push({
      date: day,
      isCurrentMonth: false
    });
  }
  
  // Adicionar dias do mês atual
  for (let i = 1; i <= daysInMonth; i++) {
    const day = new Date(year, month, i);
    allDays.push({
      date: day,
      isCurrentMonth: true
    });
  }
  
  // Calcular quantos dias do próximo mês precisamos para completar a grade
  const totalDaysNeeded = 42; // 6 semanas * 7 dias
  const remainingDays = totalDaysNeeded - allDays.length;
  
  // Adicionar dias do próximo mês
  for (let i = 1; i <= remainingDays; i++) {
    const day = new Date(year, month + 1, i);
    allDays.push({
      date: day,
      isCurrentMonth: false
    });
  }
  
  return allDays;
};

export default function CalendarMonthView({
  date,
  subsidiaryId,
  professionalId,
  onDateClick
}: CalendarMonthViewProps) {
  const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const monthDays = generateMonthDays(date);
  
  useEffect(() => {
    const fetchAppointments = async () => {
      try {
        setLoading(true);
        
        // Calcular o primeiro e último dia que está sendo exibido no calendário
        const firstDisplayedDay = monthDays[0].date;
        const lastDisplayedDay = monthDays[monthDays.length - 1].date;
        
        // Formatar datas para a API
        const startDate = firstDisplayedDay.toISOString().split('T')[0];
        const endDate = lastDisplayedDay.toISOString().split('T')[0];
        
        let appointments: AppointmentResponse[] = [];
        
        // Buscar agendamentos para o intervalo de datas
        if (professionalId) {
          // Para um profissional específico
          // Nota: Ajuste este endpoint conforme sua API
          appointments = await appointmentService.getByProfessional(
            professionalId,
            `${startDate},${endDate}` // Formato para range de datas
          );
        } else {
          // Para toda a subsidiária
          appointments = await appointmentService.getBySubsidiary(
            subsidiaryId,
            `${startDate},${endDate}`
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
  
  // Verifica se há agendamentos para uma data específica
  const getAppointmentsCountForDay = (date: Date) => {
    const dayStart = new Date(date);
    dayStart.setHours(0, 0, 0, 0);
    
    const dayEnd = new Date(date);
    dayEnd.setHours(23, 59, 59, 999);
    
    return appointments.filter(app => {
      const appointmentDate = new Date(app.startTime);
      return appointmentDate >= dayStart && appointmentDate <= dayEnd;
    }).length;
  };
  
  const handleDateClick = (date: Date) => {
    if (onDateClick) {
      onDateClick(date);
    }
  };
  
  if (loading) return <div className="loading">Carregando calendário...</div>;
  
  // Formatar o mês e ano para exibição
  const monthYearDisplay = date.toLocaleDateString('pt-BR', {
    month: 'long',
    year: 'numeric'
  });
  
  return (
    <div className="calendar-month-view">
      <h3 className="month-header">{monthYearDisplay}</h3>
      
      {error && <div className="error-message">{error}</div>}
      
      <div className="month-grid">
        {/* Cabeçalho com dias da semana */}
        <div className="weekday-header">
          {weekDays.map(day => (
            <div key={day} className="weekday">{day}</div>
          ))}
        </div>
        
        {/* Dias do mês */}
        <div className="days-grid">
          {monthDays.map((dayInfo, index) => {
            const appointmentsCount = getAppointmentsCountForDay(dayInfo.date);
            const isToday = new Date().toDateString() === dayInfo.date.toDateString();
            
            return (
              <div 
                key={index} 
                className={`day-cell ${!dayInfo.isCurrentMonth ? 'other-month' : ''} ${isToday ? 'today' : ''}`}
                onClick={() => handleDateClick(dayInfo.date)}
              >
                <div className="day-number">{dayInfo.date.getDate()}</div>
                {appointmentsCount > 0 && (
                  <div className="appointment-indicator">
                    {appointmentsCount} {appointmentsCount === 1 ? 'agendamento' : 'agendamentos'}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}