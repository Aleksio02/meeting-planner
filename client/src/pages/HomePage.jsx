// src/pages/HomePage.jsx
import React, { useState } from "react";
import Header from "../components/Header";
import Calendar from "../components/Calendar";
import EventList from "../components/EventList";  // Импортируем список мероприятий
import CreateEventForm from "../components/CreateEventForm";
import EventView from "../components/EventView";
import "../styles/HomePage.css";

const HomePage = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEventViewOpen, setIsEventViewOpen] = useState(false);
  const [selectedDate, setSelectedDate] = useState(new Date(2025, 6, 3));
  const [selectedEventId, setSelectedEventId] = useState(null);

  const handleOpenModal = () => setIsModalOpen(true);
  const handleCloseModal = () => setIsModalOpen(false);
  
  const handleDateSelect = (date) => {
    setSelectedDate(date);
  };

   // Обработчик клика по событию из списка
  const handleEventClick = (eventId) => {
    console.log('Открыть событие с ID:', eventId);
    setSelectedEventId(eventId);
    setIsEventViewOpen(true); // Открываем модалку
  };

  const handleCloseEventView = () => {
    setIsEventViewOpen(false);
    setSelectedEventId(null);
  };

  return (
    <div className="home-page">
      <Header />
      
      {/* Кнопка создания */}
      <button className="create-button" onClick={handleOpenModal}>
        + Создать
      </button>
      
      {/* Календарь */}
      <div className="calendar-wrapper">
        <Calendar onDateSelect={handleDateSelect} />
      </div>

      {/* Список мероприятий */}
     <EventList 
        selectedDate={selectedDate} 
        onEventClick={handleEventClick}  // Вот это ключевое!
      />

      {/* Модальное окно */}
      {isModalOpen && <CreateEventForm onClose={handleCloseModal} />}
       {/* Модальное окно просмотра события */}
      <EventView 
        isOpen={isEventViewOpen}
        onClose={handleCloseEventView}
        eventId={selectedEventId}
      />
    </div>
  );
};

export default HomePage;