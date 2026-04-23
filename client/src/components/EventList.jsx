import React, { useState } from 'react';
import '../styles/EventList.css';

const EventList = ({ selectedDate, onDateChange, onEventClick, onEditClick }) => {
  const [currentDate, setCurrentDate] = useState(selectedDate || new Date(2025, 6, 7));

  // Тестовые данные: только у событий с isMyEvent: true будет появляться иконка
  const events = [
    { id: 1, title: "СОЗВОН (Моё)", time: "19:00", date: "07.07.2025", isMyEvent: true },
    { id: 2, title: "ОБЩАЯ ВСТРЕЧА", time: "20:00", date: "07.07.2025", isMyEvent: false },
    { id: 3, title: "ДЕЙЛИ (Моё)", time: "11:00", date: "07.07.2025", isMyEvent: true },
    { id: 4, title: "Презентация", time: "15:30", date: "07.07.2025", isMyEvent: false },
  ];

  const formatDate = (date) => {
    const days = ["Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"];
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    return `${days[date.getDay()]} ${day}.${month}.${date.getFullYear()}`;
  };

  return (
    <div className="event-list">
      <div className="event-list-top">
        <div className="event-list-left">
          <div className="arrows-group">
            <button className="day-nav" onClick={() => setCurrentDate(prev => new Date(prev.setDate(prev.getDate() - 1)))}>←</button>
            <button className="day-nav" onClick={() => setCurrentDate(prev => new Date(prev.setDate(prev.getDate() + 1)))}>→</button>
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
            {/* Контейнер иконки — появляется только для моих событий */}
            {event.isMyEvent && (
              <div className="event-edit-container">
                <button 
                  className="pencil-btn-clean" 
                  onClick={(e) => {
                    e.stopPropagation(); // Останавливаем клик, чтобы не открылся просмотр
                    onEditClick && onEditClick(event.id);
                  }}
                >
                  <img src="/src/assets/edit-icon.svg" alt="edit" />
                </button>
              </div>
            )}

            <span className="event-title">{event.title}</span>
            <span className="event-time">{event.time} {event.date}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default EventList;