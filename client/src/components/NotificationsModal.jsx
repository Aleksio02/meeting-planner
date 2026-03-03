import React, { useState, useEffect } from 'react';
import '../styles/NotificationsModal.css';

const NotificationsModal = ({ isOpen, onClose, anchorRef }) => {
  const [position, setPosition] = useState({ top: 0, left: 0 });

  const updatePosition = () => {
    if (anchorRef?.current) {
      const rect = anchorRef.current.getBoundingClientRect();

      setPosition({
        top: rect.bottom + 10,
        left: rect.right
      });
    }
  };

  useEffect(() => {
    if (isOpen) {
      updatePosition();
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
    }
  ];

  const handleButtonClick = (notification) => {
    alert(`Тип уведомления: ${notification.type}`);
  };

  return (
    <>
      <div className="modal-overlay" onClick={onClose} />

      <div
        className="notifications-modal"
        style={{
          top: `${position.top}px`,
          left: `${position.left}px`,
          transform: 'translateX(-100%)'
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

              <button
                className="vote-button"
                onClick={() => handleButtonClick(notif)}
              >
                Подробнее
              </button>
            </div>
          ))}
        </div>
      </div>
    </>
  );
};

export default NotificationsModal;