import React, { useState } from 'react';
import '../styles/EventList.css';

const EventList = ({ events = [], selectedDate, onEventClick, onEditClick, onDateChange }) => {
  const [currentDate, setCurrentDate] = useState(selectedDate || new Date(2025, 6, 7));

  const formatDate = (date) => {
    const days = ["Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"];
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    return `${days[date.getDay()]} ${day}.${month}.${date.getFullYear()}`;
  };

  const changeDay = (offset) => {
    const newDate = new Date(currentDate);
    newDate.setDate(currentDate.getDate() + offset);
    setCurrentDate(newDate);
    if (onDateChange) onDateChange(newDate);
  };

  return (
    <div className="event-list">
      <div className="event-list-top">
        <div className="event-list-left">
          <div className="arrows-group">
            <button className="day-nav" onClick={() => changeDay(-1)}>←</button>
            <button className="day-nav" onClick={() => changeDay(1)}>→</button>
          </div>
          <h3>{formatDate(currentDate)}</h3>
        </div>
        <button className="today-button-top" onClick={() => setCurrentDate(new Date())}>
          Сегодня
        </button>
      </div>
      
      <p className="events-subtitle">Список мероприятий</p>

      <div className="events-container">
        {events.map(event => (
          <div 
            key={event.id} 
            className={`event-item ${event.isMyEvent ? 'my-event' : ''}`}
            onClick={() => onEventClick && onEventClick(event.id)}
          >
            {event.isMyEvent && (
              <div className="event-edit-wrapper">
                <button 
                  className="pencil-btn-clean" 
                  onClick={(e) => {
                    e.stopPropagation(); 
                    onEditClick(event.id);
                  }}
                >
                  <div className="pencil-icon-mask"></div>
                </button>
              </div>
            )}
            <span className="event-title">{event.title}</span>
            <span className="event-time">{event.startTime}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default EventList;