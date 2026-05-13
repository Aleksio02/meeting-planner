import React, { useState } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Header from "../components/Header";
import Calendar from "../components/Calendar";
import EventList from "../components/EventList";
import EventEdit from "../components/EventEdit";
import EventView from "../components/EventView";
import CreateEventForm from "../components/CreateEventForm";
import "../styles/HomePage.css";

const HomePage = () => {
  const { user, loading } = useAuth();
  const [events, setEvents] = useState([
    { id: 1, title: "СОЗВОН (Моё)", date: "2025-07-07", startTime: "19:00", endTime: "20:30", description: "Обсуждение архитектуры", isMyEvent: true },
    { id: 2, title: "ОБЩАЯ ВСТРЕЧА", date: "2025-07-07", startTime: "20:00", endTime: "21:00", description: "Планерка", isMyEvent: false },
    { id: 3, title: "ДЕЙЛИ (Моё)", date: "2025-07-07", startTime: "11:00", endTime: "11:30", description: "Отчет за вчера", isMyEvent: true },
  ]);

  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isViewOpen, setIsViewOpen] = useState(false);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [selectedEventId, setSelectedEventId] = useState(null);

  const currentEvent = events.find(ev => ev.id === selectedEventId);

  if (loading) return <div className="loading">Загрузка...</div>;
  if (!user) return <Navigate to="/login" />;

  // Открыть форму создания
  const handleCreateClick = () => {
    setIsCreateOpen(true);
  };

  // Закрыть форму создания
  const handleCloseCreate = () => {
    setIsCreateOpen(false);
  };

  // Открыть редактирование
  const handleEditClick = (id) => {
    setSelectedEventId(id);
    setIsEditOpen(true);
  };

  // Клик по событию — просмотр
  const handleEventClick = (id) => {
    setSelectedEventId(id);
    setIsViewOpen(true);
  };

  // Сохранение при редактировании
  const handleSaveEdit = (eventData) => {
    setEvents(prev => prev.map(ev => (ev.id === eventData.id ? eventData : ev)));
    setIsEditOpen(false);
    setSelectedEventId(null);
  };

  // Закрытие модалки редактирования
  const handleCloseEdit = () => {
    setIsEditOpen(false);
    setSelectedEventId(null);
  };

  // Закрытие просмотра
  const handleCloseView = () => {
    setIsViewOpen(false);
    setSelectedEventId(null);
  };

  return (
    <div className="home-page">
      <Header />
      
      {/* Кнопка создания */}
      <button className="create-button" onClick={handleCreateClick}>
        + Создать событие
      </button>

      {/* Календарь */}
      <div className="calendar-wrapper">
        <Calendar onDateSelect={(date) => console.log(date)} />
      </div>

      {/* Список событий */}
      <EventList 
        events={events} 
        onEventClick={handleEventClick} 
        onEditClick={handleEditClick} 
      />

      {/* Форма создания события */}
      {isCreateOpen && <CreateEventForm onClose={handleCloseCreate} />}

      {/* Модалка редактирования */}
      {isEditOpen && (
        <EventEdit 
          isOpen={isEditOpen} 
          onClose={handleCloseEdit} 
          eventData={currentEvent}
          onSave={handleSaveEdit} 
        />
      )}
      
      {/* Просмотр события */}
      {isViewOpen && (
        <EventView 
          isOpen={isViewOpen} 
          onClose={handleCloseView} 
          event={currentEvent} 
        />
      )}
    </div>
  );
};

export default HomePage;