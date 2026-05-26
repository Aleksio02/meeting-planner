import React, { useState, useEffect, useRef } from 'react';
import { invitesAPI } from '../api/invites';
import { useAuth } from '../context/AuthContext';
import '../styles/NotificationsModal.css';

const InvitesModal = ({ isOpen, onClose, anchorRef }) => {
  const { user } = useAuth();
  const [position, setPosition] = useState({ top: 0, left: 0 });
  const [isPositioned, setIsPositioned] = useState(false);
  const [invites, setInvites] = useState([]);
  const [loading, setLoading] = useState(false);
  const modalRef = useRef(null);
  const listRef = useRef(null);

  const updatePosition = () => {
    if (anchorRef?.current) {
      const rect = anchorRef.current.getBoundingClientRect();
      const modalWidth = modalRef.current?.offsetWidth || 400;
      setPosition({
        top: rect.bottom + 10,
        left: rect.right - modalWidth,
      });
      setIsPositioned(true);
    }
  };

  useEffect(() => {
    if (isOpen) {
      setIsPositioned(false);
      loadInvites();
      setTimeout(() => updatePosition(), 10);
    }
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return;
    window.addEventListener('resize', updatePosition);
    window.addEventListener('scroll', updatePosition);
    return () => {
      window.removeEventListener('resize', updatePosition);
      window.removeEventListener('scroll', updatePosition);
    };
  }, [isOpen]);

  // Скроллим вниз после загрузки
  useEffect(() => {
    if (!loading && invites.length > 0 && listRef.current) {
      listRef.current.scrollTop = listRef.current.scrollHeight;
    }
  }, [loading, invites]);

  const loadInvites = async () => {
    setLoading(true);
    try {
      const { data } = await invitesAPI.getList({
        status: 'PENDING',
        page: 0,
        pageSize: 20,
      });
      
      const currentUserId = user?.currentUser?.id || user?.id;
      
      const filtered = Array.isArray(data) 
        ? data.filter(invite => {
            const ownerId = invite.meetingId?.owner?.id;
            return ownerId !== currentUserId;
          })
        : [];
      
      setInvites(filtered);

      // Сохраняем все текущие ID как просмотренные
      const viewedIds = JSON.parse(localStorage.getItem('viewedInvites') || '[]');
      const newViewedIds = [...new Set([...viewedIds, ...filtered.map(inv => inv.id)])];
      localStorage.setItem('viewedInvites', JSON.stringify(newViewedIds));
      
    } catch (error) {
      console.error('Ошибка загрузки приглашений:', error);
      setInvites([]);
    } finally {
      setLoading(false);
    }
  };

  const formatTime = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const now = new Date();
    const diff = Math.floor((now - date) / 60000);
    if (diff < 1) return 'только что';
    if (diff < 60) return `${diff} мин назад`;
    if (diff < 1440) return `${Math.floor(diff / 60)} ч назад`;
    return date.toLocaleDateString('ru-RU');
  };

  if (!isOpen) return null;

  return (
    <>
      <div className="modal-overlay" onClick={onClose} />
      <div
        ref={modalRef}
        className={`notifications-modal ${!isPositioned ? 'hidden' : ''}`}
        style={{
          top: `${position.top}px`,
          left: `${position.left}px`,
        }}
      >
        <div className="modal-header">
          <h3>Приглашения</h3>
          <button className="close-btn" onClick={onClose}>✕</button>
        </div>

        <div className="notifications-list" ref={listRef}>
          {loading && <p style={{ color: '#fbb564', textAlign: 'center' }}>Загрузка...</p>}
          
          {!loading && invites.length === 0 && (
            <p style={{ color: '#808080', textAlign: 'center' }}>Нет входящих приглашений</p>
          )}

          {invites.map((invite) => (
            <div key={invite.id} className="notification-item">
              <div className="notification-header">
                <div className="notification-content">
                  <p className="notification-message">
                    Вас пригласили на мероприятие{' '}
                    <span className="event-name">
                      "{invite.meetingId?.title || 'Без названия'}"
                    </span>
                  </p>
                  <p style={{ color: '#808080', fontSize: 13, margin: '4px 0 0 0' }}>
                    От: {invite.meetingId?.owner?.username || 'Неизвестно'}
                  </p>
                  {invite.meetingId?.startsAt && (
                    <p style={{ color: '#808080', fontSize: 13, margin: '4px 0 0 0' }}>
                      {new Date(invite.meetingId.startsAt).toLocaleString('ru-RU')}
                    </p>
                  )}
                </div>
                <span className="notification-time">
                  {formatTime(invite.sentAt)}
                </span>
              </div>

              <div style={{ display: 'flex', gap: 10, marginTop: 10 }}>
                <button className="vote-button">
                  ✓ Принять
                </button>
                <button className="vote-button" style={{ color: '#ff4757' }}>
                  ✕ Отклонить
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </>
  );
};

export default InvitesModal;