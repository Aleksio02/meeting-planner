import React, { useState, useEffect } from 'react';
import { meetingsAPI } from '../api/meetings';
import { invitesAPI } from '../api/invites';
import { usersAPI } from '../api/users';
import { useToast } from '../context/ToastContext';
import InviteModal from './InviteModal';
import '../styles/EventView.css';

const EventView = ({ isOpen, onClose, eventId }) => {
  const { addToast } = useToast();
  const [isClosing, setIsClosing] = useState(false);
  const [shouldRender, setShouldRender] = useState(false);
  const [isInviteOpen, setIsInviteOpen] = useState(false);
  const [showAllParticipants, setShowAllParticipants] = useState(false);
  const [event, setEvent] = useState(null);
  const [participants, setParticipants] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setShouldRender(true); setIsClosing(false);
      if (eventId) loadEventData(eventId);
    } else {
      setIsClosing(true);
      setTimeout(() => {
        setShouldRender(false); setIsClosing(false);
        setIsInviteOpen(false); setShowAllParticipants(false);
        setEvent(null); setParticipants([]);
      }, 200);
    }
  }, [isOpen, eventId]);

  const loadEventData = async (id) => {
    setLoading(true);
    try {
      const { data: meeting } = await meetingsAPI.getById(id);
      const { data: invites } = await invitesAPI.getList({ meetingId: id, page: 0, pageSize: 100 });

      const mappedParticipants = invites.map(inv => ({
        id: inv.id,
        name: inv.userId?.username || inv.userId?.email || 'Неизвестно',
        email: inv.userId?.email || '',
        status: inv.status === 'Ожидает' ? 'pending' : inv.status === 'Принято' ? 'accepted' : 'declined',
        userId: inv.userId?.id,
      }));

      setEvent({
        title: meeting.title,
        description: meeting.description,
        date: meeting.startsAt ? new Date(meeting.startsAt).toLocaleDateString('ru-RU') : '',
        startTime: meeting.startsAt ? new Date(meeting.startsAt).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }) : '',
        endTime: meeting.duration ? new Date(new Date(meeting.startsAt).getTime() + meeting.duration * 60000).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' }) : '',
        creator: meeting.owner?.username || 'Неизвестно',
        ownerId: meeting.owner?.id,
      });

      setParticipants(mappedParticipants);
    } catch (error) {
      console.error('Ошибка загрузки встречи:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddParticipants = async (userNames) => {
    try {
      for (const userName of userNames) {
        try {
          const { data } = await usersAPI.searchByUsername(userName);
          if (data && data.length > 0) {
            const userId = data[0].id;
            await invitesAPI.create({ meetingId: eventId, userId: userId, status: 'PENDING' });
            setParticipants(prev => [...prev, { name: userName, status: 'pending', id: Date.now().toString() }]);
          }
        } catch (err) {
          console.warn(`Не удалось пригласить ${userName}:`, err);
        }
      }
      addToast('✅ Приглашения отправлены!', 'success', 3000);
    } catch (error) {
      addToast('❌ Ошибка при отправке приглашений', 'error', 6000);
    }
  };

  const renderStatusIcon = (status) => {
    switch (status) {
      case 'accepted': return <span className="status-icon accepted" title="Принял">✔</span>;
      case 'declined': return <span className="status-icon declined" title="Отклонил">✖</span>;
      case 'pending':  return <span className="status-icon pending" title="Ожидание">🕒</span>;
      default: return null;
    }
  };

  if (!shouldRender && !isClosing) return null;

  const displayedParticipants = showAllParticipants ? participants : participants.slice(0, 5);
  const remainingCount = participants.length - 5;

  return (
    <>
      <div className={`event-overlay ${isClosing ? 'closing' : ''}`} onClick={onClose} />
      <div className={`event-modal ${isClosing ? 'closing' : ''}`}>
        <button className="event-close-btn" onClick={onClose}>✕</button>
        {loading ? (
          <div className="event-content" style={{ textAlign: 'center', color: '#fbb564', padding: 40 }}>Загрузка...</div>
        ) : event ? (
          <div className="event-content">
            <h2 className="event-title-modal">{event.title}</h2>
            <div className="event-datetime">
              <div className="datetime-item"><span className="datetime-label">Дата</span><span className="datetime-value">{event.date}</span></div>
              <div className="datetime-item"><span className="datetime-label">Начало</span><span className="datetime-value">{event.startTime}</span></div>
              <div className="datetime-item"><span className="datetime-label">Окончание</span><span className="datetime-value">{event.endTime}</span></div>
            </div>
            <div className="description-section"><span className="section-label">Описание</span><p className="description-text">{event.description || 'Нет описания'}</p></div>
            <div className="participants-section">
              <span className="section-label">Участники ({participants.length})</span>
              <div className="participants-list-static">
                {participants.length === 0 && <span style={{ color: '#808080', fontSize: 14 }}>Нет участников</span>}
                {displayedParticipants.map((p, index) => (
                  <div key={p.id || index} className="participant-pill">{renderStatusIcon(p.status)}<span className="p-name">{p.name}</span></div>
                ))}
                {!showAllParticipants && remainingCount > 0 && <button className="participant-pill more-btn" onClick={() => setShowAllParticipants(true)}>и ещё {remainingCount}...</button>}
                {showAllParticipants && participants.length > 5 && <button className="participant-pill more-btn" onClick={() => setShowAllParticipants(false)}>Скрыть</button>}
              </div>
              <button className="invite-trigger-btn" onClick={() => setIsInviteOpen(true)}>+ Пригласить участника</button>
            </div>
            <div className="creator-section"><span className="section-label">Организатор</span><span className="creator-name">{event.creator}</span></div>
            <div className="event-action"><button className="leave-btn">Покинуть событие</button></div>
          </div>
        ) : (
          <div className="event-content" style={{ textAlign: 'center', color: '#808080', padding: 40 }}>Встреча не найдена</div>
        )}
      </div>
      <InviteModal isOpen={isInviteOpen} onClose={() => setIsInviteOpen(false)} onAddParticipants={handleAddParticipants} currentParticipants={participants.map(p => p.name)} />
    </>
  );
};

export default EventView;