// src/pages/HomePage.jsx
import React, { useState } from "react";
import Header from "../components/Header";
import Calendar from "../components/Calendar";
import EventList from "../components/EventList";  // Импортируем список мероприятий
import CreateEventForm from "../components/CreateEventForm";
import "../styles/HomePage.css";

const HomePage = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedDate, setSelectedDate] = useState(new Date(2025, 6, 3)); // 03.07.2025

  const handleOpenModal = () => setIsModalOpen(true);
  const handleCloseModal = () => setIsModalOpen(false);
  
  const handleDateSelect = (date) => {
    setSelectedDate(date);
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
      <EventList selectedDate={selectedDate} />

      {/* Модальное окно */}
      {isModalOpen && <CreateEventForm onClose={handleCloseModal} />}
    </div>
  );
};

export default HomePage;