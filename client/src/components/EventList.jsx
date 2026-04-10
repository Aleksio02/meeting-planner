// src/components/EventList.jsx
import React, { useState } from 'react';
import '../styles/EventList.css';

const EventList = ({ selectedDate, onDateChange, onEventClick }) => {
  const [currentDate, setCurrentDate] = useState(selectedDate || new Date(2025, 6, 3));

  const formatDate = (date) => {
    const days = ["Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"];
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${days[date.getDay()]} ${day}.${month}.${year}`;
  };

  const handlePrevDay = () => {
    const newDate = new Date(currentDate);
    newDate.setDate(currentDate.getDate() - 1);
    setCurrentDate(newDate);
    if (onDateChange) onDateChange(newDate);
  };

  const handleNextDay = () => {
    const newDate = new Date(currentDate);
    newDate.setDate(currentDate.getDate() + 1);
    setCurrentDate(newDate);
    if (onDateChange) onDateChange(newDate);
  };

  const handleToday = () => {
    const today = new Date();
    setCurrentDate(today);
    if (onDateChange) onDateChange(today);
  };

   // Обработчик клика по событию
  const handleEventItemClick = (event) => {
    console.log('Клик по событию:', event);
    if (onEventClick) {
      onEventClick(event.id); // Передаём ID события наверх
    }
  };

  const events = [
    {
      id: 1,
      title: "СОЗВОН",
      time: "19:00",
      date: "07.07.2025"
    },
    {
      id: 2,
      title: "СОЗВОН",
      time: "19:00",
      date: "07.07.2025"
    }
  ];

  return (
    <div className="event-list">
      {/* Верхняя строка */}
      <div className="event-list-top">
        <div className="event-list-left">
          <div className="arrows-group">
            <button className="day-nav" onClick={handlePrevDay}>←</button>
            <button className="day-nav" onClick={handleNextDay}>→</button>
          </div>
          <h3>{formatDate(currentDate)}</h3>
        </div>
        <div className="event-list-right">
          <button className="today-button-top" onClick={handleToday}>
            Сегодня
          </button>
        </div>
      </div>
      
      <p className="events-subtitle">Список мероприятий</p>

      <div className="events-container">
        {events.map(event => (
           <div 
            key={event.id} 
            className="event-item"
            onClick={() => handleEventItemClick(event)} // Добавили onClick
          >
            <div className="event-icon">
              <img 
            src="/src/assets/edit-icon.svg" 
                alt="Edit"
              />
            </div>
            <span className="event-title">{event.title}</span>
            <span className="event-time">{event.time} {event.date}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default EventList;