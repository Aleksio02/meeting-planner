import React, { useState, useEffect, useCallback } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import { meetingsAPI } from "../api/meetings";
import { invitesAPI } from "../api/invites";
import Header from "../components/Header";
import Calendar from "../components/Calendar";
import EventList from "../components/EventList";
import EventEdit from "../components/EventEdit";
import EventView from "../components/EventView";
import CreateEventForm from "../components/CreateEventForm";
import "../styles/HomePage.css";

const HomePage = () => {
  const { user, loading } = useAuth();
  const { addToast } = useToast();
  const [events, setEvents] = useState([]);
  const [isLoadingEvents, setIsLoadingEvents] = useState(true);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isViewOpen, setIsViewOpen] = useState(false);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [selectedEventId, setSelectedEventId] = useState(null);

  const loadMeetings = useCallback(async () => {
    try {
      const currentUserId = user?.currentUser?.id || user?.id;
      const { data: meetingsData } = await meetingsAPI.getList();
      
      let acceptedMeetingIds = [];
      try {
        const { data: invitesData } = await invitesAPI.getList({ page: 0, pageSize: 100 });
        acceptedMeetingIds = invitesData
          .filter(inv => inv.userId?.id === currentUserId && inv.status === 'Принято')
          .map(inv => inv.meetingId?.id)
          .filter(Boolean);
      } catch (e) {}

      const mappedEvents = meetingsData.map(meeting => ({
        id: meeting.id,
        title: meeting.title,
        date: meeting.startsAt ? new Date(meeting.startsAt).toISOString().split('T')[0] : '',
        startTime: meeting.startsAt ? new Date(meeting.startsAt).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }) : '',
        endTime: meeting.duration ? new Date(new Date(meeting.startsAt).getTime() + meeting.duration * 60000).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }) : '',
        description: meeting.description,
        isMyEvent: meeting.owner?.id === currentUserId,
        status: meeting.status,
      }));

      const filtered = mappedEvents.filter(event => {
        if (event.isMyEvent) return true;
        if (acceptedMeetingIds.includes(event.id)) return true;
        return false;
      });
      
      setEvents(filtered);
    } catch (error) {
      setEvents([]);
    } finally {
      setIsLoadingEvents(false);
    }
  }, [user]);

  useEffect(() => { if (user) loadMeetings(); }, [user, loadMeetings]);

  const currentEvent = events.find(ev => ev.id === selectedEventId);

  if (loading) return <div className="loading">Загрузка...</div>;
  if (!user) return <Navigate to="/login" />;

  const handleCreateClick = () => setIsCreateOpen(true);
  const handleCloseCreate = () => setIsCreateOpen(false);
  const handleEventCreated = (newEvent) => setEvents(prev => [newEvent, ...prev]);
  const refreshMeetings = () => loadMeetings();
  const handleEditClick = (id) => { setSelectedEventId(id); setIsEditOpen(true); };
  const handleEventClick = (id) => { setSelectedEventId(id); setIsViewOpen(true); };

  const handleSaveEdit = async (eventData) => {
    try {
      const updateData = {};
      if (eventData.title) updateData.title = eventData.title;
      if (eventData.description !== undefined) updateData.description = eventData.description;
      
      // Если изменили дату и время — формируем startsAt и duration
      if (eventData.date && eventData.startTime && eventData.endTime) {
        updateData.startsAt = `${eventData.date}T${eventData.startTime}:00`;
        const [sh, sm] = eventData.startTime.split(':').map(Number);
        const [eh, em] = eventData.endTime.split(':').map(Number);
        updateData.duration = (eh * 60 + em) - (sh * 60 + sm);
      }
      
      await meetingsAPI.update(eventData.id, updateData);
      
      setEvents(prev => prev.map(ev => (ev.id === eventData.id ? { 
        ...ev, 
        title: eventData.title || ev.title,
        description: eventData.description !== undefined ? eventData.description : ev.description,
        date: eventData.date || ev.date,
        startTime: eventData.startTime || ev.startTime,
        endTime: eventData.endTime || ev.endTime,
      } : ev)));
      
      setIsEditOpen(false);
      setSelectedEventId(null);
      addToast(' Изменения сохранены!', 'success', 3000);
    } catch (error) {
      const msg = error.response?.data?.errorMessage || 'Ошибка при сохранении';
      addToast(` ${msg}`, 'error', 6000);
    }
  };

  const handleCloseEdit = () => { setIsEditOpen(false); setSelectedEventId(null); };
  const handleCloseView = () => { setIsViewOpen(false); setSelectedEventId(null); };

  return (
    <div className="home-page">
      <Header onRefreshMeetings={refreshMeetings} />
      <button className="create-button" onClick={handleCreateClick}>+ Создать событие</button>
      <div className="calendar-wrapper"><Calendar onDateSelect={(date) => console.log(date)} /></div>
      {isLoadingEvents ? (
        <div className="event-list-loading"><div className="spinner" />Загрузка встреч...</div>
      ) : (
        <EventList events={events} onEventClick={handleEventClick} onEditClick={handleEditClick} />
      )}
      {isCreateOpen && <CreateEventForm onClose={handleCloseCreate} onCreated={handleEventCreated} />}
      {isEditOpen && <EventEdit isOpen={isEditOpen} onClose={handleCloseEdit} eventData={currentEvent} onSave={handleSaveEdit} />}
      {isViewOpen && <EventView isOpen={isViewOpen} onClose={handleCloseView} eventId={selectedEventId} />}
    </div>
  );
};

export default HomePage;