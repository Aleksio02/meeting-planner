// src/components/NotificationsModal.jsx
import React, { useState, useEffect } from 'react';
import '../styles/NotificationsModal.css';

const NotificationsModal = ({ isOpen, onClose, anchorRef }) => {
  const [position, setPosition] = useState({ top: 0, right: 0 });

  useEffect(() => {
    if (isOpen && anchorRef?.current) {
      const rect = anchorRef.current.getBoundingClientRect();
      setPosition({
        top: rect.bottom + 10,
        right: window.innerWidth - rect.right - 10
      });
    }
  }, [isOpen, anchorRef]);

  useEffect(() => {
    const handleResize = () => {
      if (isOpen && anchorRef?.current) {
        const rect = anchorRef.current.getBoundingClientRect();
        setPosition({
          top: rect.bottom + 10,
          right: window.innerWidth - rect.right - 10
        });
      }
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, [isOpen, anchorRef]);

  if (!isOpen) return null;

  const notifications = [
    {
      id: 1,
      type: 'invite',
      message: <>Вас пригласили на мероприятие <span className="event-name">"Созвон"</span></>,
      time: '5 мин назад'
    },
    {
      id: 2,
      type: 'vote',
      message: <>Началось голосование за выбор времени мероприятия <span className="event-name">"Созвон"</span></>,
      time: '15 мин назад'
    },
    {
      id: 3,
      type: 'reminder',
      message: <>Напоминание: <span className="event-name">СОЗВОН</span> через 15 минут</>,
      time: '1 час назад'
    },
    {
      id: 4,
      type: 'update',
      message: <>Мероприятие <span className="event-name">"Созвон"</span> изменено</>,
      time: '2 часа назад'
    },
    {
      id: 5,
      type: 'cancel',
      message: <>Мероприятие <span className="event-name">"Созвон"</span> отменено</>,
      time: 'вчера'
    }
  ];

  const handleButtonClick = (notification) => {
    switch (notification.type) {
      case 'vote':
        alert(' Переход к голосованию');
        break;
      case 'invite':
        alert(' Просмотр приглашения');
        break;
      case 'reminder':
        alert(' Просмотр напоминания');
        break;
      case 'update':
        alert(' Просмотр изменений');
        break;
      case 'cancel':
        alert(' Информация об отмене');
        break;
      default:
        alert(' Подробнее');
    }
  };

  return (
    <>
      <div className="modal-overlay" onClick={onClose} />
      
      <div 
        className="notifications-modal"
        style={{
          top: `${position.top}px`,
          right: `${position.right}px`
        }}
      >
        <div className="modal-header">
          <h3>Уведомления</h3>
          <button className="close-btn" onClick={onClose}>✕</button>
        </div>
        
        <div className="notifications-list">
          {notifications.map((notif) => (
            <div key={notif.id} className="notification-item">
              <div className="notification-header">
                <p className="notification-message">{notif.message}</p>
                <span className="notification-time">{notif.time}</span>
              </div>
              <div className="notification-content">
                <button 
                  className="vote-button"
                  onClick={() => handleButtonClick(notif)}
                >
                  Подробнее
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </>
  );
};

export default NotificationsModal;