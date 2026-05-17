import React, { useState, useEffect } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { meetingsAPI } from "../api/meetings";
import Header from "../components/Header";
import Calendar from "../components/Calendar";
import EventList from "../components/EventList";
import EventEdit from "../components/EventEdit";
import EventView from "../components/EventView";
import CreateEventForm from "../components/CreateEventForm";
import "../styles/HomePage.css";

const HomePage = () => {
  const { user, loading } = useAuth();
  const [events, setEvents] = useState([]);
  const [isLoadingEvents, setIsLoadingEvents] = useState(true);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isViewOpen, setIsViewOpen] = useState(false);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [selectedEventId, setSelectedEventId] = useState(null);

  // Загрузка встреч с бекенда при монтировании
  useEffect(() => {
    const loadMeetings = async () => {
      try {
        const { data } = await meetingsAPI.getList();
        // Маппим данные под формат EventList
        const mappedEvents = data.map(meeting => ({
          id: meeting.id,
          title: meeting.title,
          date: meeting.date,
          startTime: meeting.startTime,
          endTime: meeting.endTime,
          description: meeting.description,
          isMyEvent: meeting.isMyEvent || meeting.creatorId === user?.id,
          participants: meeting.participants || [],
          status: meeting.status,
        }));
        setEvents(mappedEvents);
      } catch (error) {
        console.error('Ошибка загрузки встреч:', error);
        setEvents([]);
      } finally {
        setIsLoadingEvents(false);
      }
    };

    if (user) {
      loadMeetings();
    }
  }, [user]);

  const currentEvent = events.find(ev => ev.id === selectedEventId);

  // Защита: если не авторизован — редирект
  if (loading) return <div className="loading">Загрузка...</div>;
  if (!user) return <Navigate to="/login" />;

  const handleCreateClick = () => {
    setIsCreateOpen(true);
  };

  const handleCloseCreate = () => {
    setIsCreateOpen(false);
  };

  // Когда встреча создана — добавляем в список
  const handleEventCreated = (newEvent) => {
    setEvents(prev => [newEvent, ...prev]);
  };

  const handleEditClick = (id) => {
    setSelectedEventId(id);
    setIsEditOpen(true);
  };

  const handleEventClick = (id) => {
    setSelectedEventId(id);
    setIsViewOpen(true);
  };

  const handleSaveEdit = (eventData) => {
    setEvents(prev => prev.map(ev => (ev.id === eventData.id ? eventData : ev)));
    setIsEditOpen(false);
    setSelectedEventId(null);
  };

  const handleCloseEdit = () => {
    setIsEditOpen(false);
    setSelectedEventId(null);
  };

  const handleCloseView = () => {
    setIsViewOpen(false);
    setSelectedEventId(null);
  };

  return (
    <div className="home-page">
      <Header />
      
      <button className="create-button" onClick={handleCreateClick}>
        + Создать событие
      </button>

      <div className="calendar-wrapper">
        <Calendar onDateSelect={(date) => console.log(date)} />
      </div>

      {isLoadingEvents ? (
        <div className="event-list-loading">
          <div className="spinner" />
          Загрузка встреч...
        </div>
      ) : (
        <EventList 
          events={events} 
          onEventClick={handleEventClick} 
          onEditClick={handleEditClick} 
        />
      )}

      {isCreateOpen && (
        <CreateEventForm 
          onClose={handleCloseCreate} 
          onCreated={handleEventCreated} 
        />
      )}

      {isEditOpen && (
        <EventEdit 
          isOpen={isEditOpen} 
          onClose={handleCloseEdit} 
          eventData={currentEvent}
          onSave={handleSaveEdit} 
        />
      )}
      
      {isViewOpen && (
        <EventView 
          isOpen={isViewOpen} 
          onClose={handleCloseView} 
          eventId={selectedEventId}
        />
      )}
    </div>
  );
};

export default HomePage;