import { useState } from 'react';
import './CalendarViews.css';

export const CalendarViewType = {
  DAY: 'day',
  WEEK: 'week',
  MONTH: 'month'
} as const;

export type CalendarViewType = typeof CalendarViewType[keyof typeof CalendarViewType];

interface CalendarNavigationProps {
  date: Date;
  view: CalendarViewType;
  onDateChange: (date: Date) => void;
  onViewChange: (view: CalendarViewType) => void;
}

export default function CalendarNavigation({
  date,
  view,
  onDateChange,
  onViewChange
}: CalendarNavigationProps) {
  
  // Navega para o dia, semana ou mês anterior
  const handlePrevious = () => {
    const newDate = new Date(date);
    
    switch (view) {
      case CalendarViewType.DAY:
        newDate.setDate(newDate.getDate() - 1);
        break;
      case CalendarViewType.WEEK:
        newDate.setDate(newDate.getDate() - 7);
        break;
      case CalendarViewType.MONTH:
        newDate.setMonth(newDate.getMonth() - 1);
        break;
    }
    
    onDateChange(newDate);
  };
  
  // Navega para o próximo dia, semana ou mês
  const handleNext = () => {
    const newDate = new Date(date);
    
    switch (view) {
      case CalendarViewType.DAY:
        newDate.setDate(newDate.getDate() + 1);
        break;
      case CalendarViewType.WEEK:
        newDate.setDate(newDate.getDate() + 7);
        break;
      case CalendarViewType.MONTH:
        newDate.setMonth(newDate.getMonth() + 1);
        break;
    }
    
    onDateChange(newDate);
  };
  
  // Navega para hoje
  const handleToday = () => {
    onDateChange(new Date());
  };
  
  // Gera o título do calendário baseado na visualização atual
  const getCalendarTitle = () => {
    switch (view) {
      case CalendarViewType.DAY:
        return date.toLocaleDateString('pt-BR', {
          weekday: 'long',
          day: 'numeric',
          month: 'long',
          year: 'numeric'
        });
      case CalendarViewType.WEEK:
        // Calcula o início e fim da semana
        const weekStart = new Date(date);
        const dayOfWeek = weekStart.getDay();
        const diff = dayOfWeek === 0 ? -6 : 1 - dayOfWeek; // Ajuste para começar na segunda-feira
        weekStart.setDate(weekStart.getDate() + diff);
        
        const weekEnd = new Date(weekStart);
        weekEnd.setDate(weekEnd.getDate() + 6);
        
        // Formata como "DD/MM - DD/MM/YYYY"
        const startStr = weekStart.toLocaleDateString('pt-BR', {
          day: '2-digit',
          month: '2-digit'
        });
        
        const endStr = weekEnd.toLocaleDateString('pt-BR', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric'
        });
        
        return `${startStr} - ${endStr}`;
      case CalendarViewType.MONTH:
        return date.toLocaleDateString('pt-BR', {
          month: 'long',
          year: 'numeric'
        });
      default:
        return '';
    }
  };
  
  return (
    <div className="calendar-navigation">
      <div className="navigation-buttons">
        <button className="nav-button" onClick={handlePrevious}>
          &lt; Anterior
        </button>
        <button className="today-button" onClick={handleToday}>
          Hoje
        </button>
        <button className="nav-button" onClick={handleNext}>
          Próximo &gt;
        </button>
      </div>
      
      <div className="calendar-title">
        {getCalendarTitle()}
      </div>
      
      <div className="view-buttons">
        <button 
          className={`view-button ${view === CalendarViewType.DAY ? 'active' : ''}`}
          onClick={() => onViewChange(CalendarViewType.DAY)}
        >
          Dia
        </button>
        <button 
          className={`view-button ${view === CalendarViewType.WEEK ? 'active' : ''}`}
          onClick={() => onViewChange(CalendarViewType.WEEK)}
        >
          Semana
        </button>
        <button 
          className={`view-button ${view === CalendarViewType.MONTH ? 'active' : ''}`}
          onClick={() => onViewChange(CalendarViewType.MONTH)}
        >
          Mês
        </button>
      </div>
    </div>
  );
}