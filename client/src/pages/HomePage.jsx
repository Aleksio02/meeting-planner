import React, { useState } from "react";
import Header from "../components/Header";
import Calendar from "../components/Calendar";
import EventList from "../components/EventList";
import EventEdit from "../components/EventEdit";
import EventView from "../components/EventView"; // Предполагаем, что он есть
import "../styles/HomePage.css";

const HomePage = () => {
  // Главный стейт с данными
  const [events, setEvents] = useState([
    { id: 1, title: "СОЗВОН (Моё)", date: "2025-07-07", startTime: "19:00", endTime: "20:30", description: "Обсуждение архитектуры", isMyEvent: true },
    { id: 2, title: "ОБЩАЯ ВСТРЕЧА", date: "2025-07-07", startTime: "20:00", endTime: "21:00", description: "Планерка", isMyEvent: false },
    { id: 3, title: "ДЕЙЛИ (Моё)", date: "2025-07-07", startTime: "11:00", endTime: "11:30", description: "Отчет за вчера", isMyEvent: true },
  ]);

  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isViewOpen, setIsViewOpen] = useState(false);
  const [selectedEventId, setSelectedEventId] = useState(null);

  // Находим объект выбранного события
  const currentEvent = events.find(ev => ev.id === selectedEventId);

  const handleEditClick = (id) => {
    setSelectedEventId(id);
    setIsEditOpen(true);
  };

  const handleEventClick = (id) => {
    setSelectedEventId(id);
    setIsViewOpen(true);
  };

  // Функция сохранения изменений
  const handleSaveEdit = (updatedData) => {
    setEvents(prev => prev.map(ev => (ev.id === updatedData.id ? updatedData : ev)));
    setIsEditOpen(false);
    setSelectedEventId(null);
  };

  return (
    <div className="home-page">
      <Header />
      <div className="calendar-wrapper">
        <Calendar onDateSelect={(date) => console.log(date)} />
      </div>

      <EventList 
        events={events} 
        onEventClick={handleEventClick} 
        onEditClick={handleEditClick} 
      />

      <EventEdit 
        isOpen={isEditOpen} 
        onClose={() => setIsEditOpen(false)} 
        eventData={currentEvent} 
        onSave={handleSaveEdit} 
      />
      
      {/* EventView если нужен */}
      <EventView 
        isOpen={isViewOpen} 
        onClose={() => setIsViewOpen(false)} 
        event={currentEvent} 
      />
    </div>
  );
};

export default HomePage;