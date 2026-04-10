import React, { useState, useEffect } from 'react';
import InviteModal from './InviteModal';
import '../styles/EventView.css';

const EventView = ({ isOpen, onClose, eventId }) => {
  const [isClosing, setIsClosing] = useState(false);
  const [shouldRender, setShouldRender] = useState(false);
  const [isInviteOpen, setIsInviteOpen] = useState(false);
  const [showAllParticipants, setShowAllParticipants] = useState(false);
  
  // Участники теперь объекты со статусами: 'accepted', 'pending', 'declined'
  const [participants, setParticipants] = useState([]);

  useEffect(() => {
    if (isOpen) {
      setShouldRender(true);
      setIsClosing(false);
    } else {
      setIsClosing(true);
      setTimeout(() => {
        setShouldRender(false);
        setIsClosing(false);
        setIsInviteOpen(false);
        setShowAllParticipants(false);
      }, 200);
    }
  }, [isOpen]);

  const getEventData = (id) => {
    const events = {
      1: {
        title: "СОЗВОН",
        description: "Обсуждение архитектуры проекта и декомпозиция задач на спринт.",
        date: "07.07.2026",
        time: "19:00",
        creator: "1mperium",
        participants: [
          { name: "user1@mail.com", status: "accepted" },
          { name: "andrew_dev", status: "accepted" },
          { name: "maria.pro", status: "pending" },
          { name: "ivan_junior", status: "declined" },
          { name: "boss@corp.com", status: "pending" },
          { name: "designer_x", status: "accepted" },
          { name: "qa_lead", status: "pending" }
        ]
      }
    };
    return events[id] || events[1];
  };

  useEffect(() => {
    const data = getEventData(eventId);
    setParticipants(data.participants);
  }, [eventId]);

  const handleAddParticipant = (userName) => {
    setParticipants(prev => [...prev, { name: userName, status: 'pending' }]);
    setIsInviteOpen(false);
  };

  // Функция для отрисовки иконки статуса
  const renderStatusIcon = (status) => {
    switch (status) {
      case 'accepted': return <span className="status-icon accepted" title="Принял">✔</span>;
      case 'declined': return <span className="status-icon declined" title="Отклонил">✖</span>;
      case 'pending':  return <span className="status-icon pending" title="Ожидание">🕒</span>;
      default: return null;
    }
  };

  if (!shouldRender && !isClosing) return null;

  const event = getEventData(eventId);
  
  // Логика отображения: либо первые 5, либо все
  const displayedParticipants = showAllParticipants 
    ? participants 
    : participants.slice(0, 5);
  const remainingCount = participants.length - 5;

  return (
    <>
      <div className={`event-overlay ${isClosing ? 'closing' : ''}`} onClick={onClose} />

      <div className={`event-modal ${isClosing ? 'closing' : ''}`}>
        <button className="event-close-btn" onClick={onClose}>✕</button>

        <div className="event-content">
          <h2 className="event-title-modal">{event.title}</h2>

          <div className="event-datetime">
            <div className="datetime-item">
              <span className="datetime-label">Дата</span>
              <span className="datetime-value">{event.date}</span>
            </div>
            <div className="datetime-item">
              <span className="datetime-label">Время</span>
              <span className="datetime-value">{event.time}</span>
            </div>
          </div>

          <div className="description-section">
            <span className="section-label">Описание</span>
            <p className="description-text">{event.description}</p>
          </div>

          <div className="participants-section">
            <span className="section-label">Участники ({participants.length})</span>
            <div className="participants-list-static">
              {displayedParticipants.map((p, index) => (
                <div key={index} className="participant-pill">
                  {renderStatusIcon(p.status)}
                  <span className="p-name">{p.name}</span>
                </div>
              ))}
              
              {!showAllParticipants && remainingCount > 0 && (
                <button 
                  className="participant-pill more-btn" 
                  onClick={() => setShowAllParticipants(true)}
                >
                  и ещё {remainingCount}...
                </button>
              )}

              {showAllParticipants && participants.length > 5 && (
                <button 
                  className="participant-pill more-btn" 
                  onClick={() => setShowAllParticipants(false)}
                >
                  Скрыть
                </button>
              )}
            </div>
            
            <button className="invite-trigger-btn" onClick={() => setIsInviteOpen(true)}>
              + Пригласить участника
            </button>
          </div>

          <div className="creator-section">
            <span className="section-label">Организатор</span>
            <span className="creator-name">{event.creator}</span>
          </div>

          <div className="event-action">
            <button className="leave-btn">Покинуть событие</button>
          </div>
        </div>
      </div>

      <InviteModal 
        isOpen={isInviteOpen} 
        onClose={() => setIsInviteOpen(false)}
        onAddParticipant={handleAddParticipant}
        currentParticipants={participants.map(p => p.name)}
      />
    </>
  );
};

export default EventView;