import React, { useState } from "react";
import Header from "../components/Header";
import Calendar from "../components/Calendar";
import EventList from "../components/EventList";
import CreateEventForm from "../components/CreateEventForm";
import EventView from "../components/EventView";
import EventEdit from "../components/EventEdit";
import "../styles/HomePage.css";

const HomePage = () => {
  // 1. ГЛАВНЫЙ СТЕЙТ ДАННЫХ (теперь события живут здесь)
  const [events, setEvents] = useState([
    { id: 1, title: "СОЗВОН (Моё)", date: "2025-07-07", startTime: "19:00", endTime: "20:30", description: "Обсуждение архитектуры", isMyEvent: true },
    { id: 2, title: "ОБЩАЯ ВСТРЕЧА", date: "2025-07-07", startTime: "20:00", endTime: "21:00", description: "Планерка отдела", isMyEvent: false },
    { id: 3, title: "ДЕЙЛИ (Моё)", date: "2025-07-07", startTime: "11:00", endTime: "11:30", description: "Отчет по задачам", isMyEvent: true },
  ]);

  // Состояния для модалок
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isViewOpen, setIsViewOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);

  // Выбранные данные
  const [selectedDate, setSelectedDate] = useState(new Date(2025, 6, 7));
  const [selectedEventId, setSelectedEventId] = useState(null);

  // Находим объект выбранного события для передачи в модалки
  const currentEvent = events.find(ev => ev.id === selectedEventId);

  // --- ОБРАБОТЧИКИ ---

  // Клик по всей плашке (Просмотр)
  const handleEventClick = (id) => {
    setSelectedEventId(id);
    setIsViewOpen(true);
  };

  // Клик по карандашу (Редактирование)
  const handleEditClick = (id) => {
    setSelectedEventId(id);
    setIsEditOpen(true);
  };

  // СОХРАНЕНИЕ ОБНОВЛЕННЫХ ДАННЫХ
  const handleSaveEdit = (updatedData) => {
    setEvents(prevEvents => 
      prevEvents.map(ev => (ev.id === updatedData.id ? updatedData : ev))
    );
    setIsEditOpen(false);
    setSelectedEventId(null);
  };

  // СОЗДАНИЕ НОВОГО (заглушка для формы создания)
  const handleCreateEvent = (newEvent) => {
    const id = events.length + 1;
    setEvents([...events, { ...newEvent, id, isMyEvent: true }]);
    setIsCreateOpen(false);
  };

  return (
    <div className="home-page">
      <Header />
      
      <button className="create-button" onClick={() => setIsCreateOpen(true)}>
        + Создать
      </button>
      
      <div className="calendar-wrapper">
        <Calendar onDateSelect={(date) => setSelectedDate(date)} />
      </div>

      {/* Передаем наш массив events в список */}
      <EventList 
        events={events} 
        selectedDate={selectedDate}
        onEventClick={handleEventClick} 
        onEditClick={handleEditClick} 
      />

      {/* Окно создания */}
      {isCreateOpen && (
        <CreateEventForm 
          onClose={() => setIsCreateOpen(false)} 
          onSave={handleCreateEvent} 
        />
      )}

      {/* Окно просмотра */}
      <EventView 
        isOpen={isViewOpen}
        onClose={() => { setIsViewOpen(false); setSelectedEventId(null); }}
        event={currentEvent} // Передаем сам объект события
      />

      {/* Окно редактирования */}
      <EventEdit 
        isOpen={isEditOpen} 
        onClose={() => { setIsEditOpen(false); setSelectedEventId(null); }} 
        eventData={currentEvent} // Передаем старые данные в форму
        onSave={handleSaveEdit}  // Функция, которая обновит стейт здесь
      />
    </div>
  );
};

export default HomePage;