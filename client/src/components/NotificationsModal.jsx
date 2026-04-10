import React, { useState, useEffect, useRef } from 'react';
import '../styles/NotificationsModal.css';

const NotificationsModal = ({ isOpen, onClose, anchorRef }) => {
  const [position, setPosition] = useState({ top: 0, left: 0 });
  const [isPositioned, setIsPositioned] = useState(false);
  const modalRef = useRef(null);

  const updatePosition = () => {
    if (anchorRef?.current) {
      const rect = anchorRef.current.getBoundingClientRect();
      
      // Получаем ширину модалки после рендера
      const modalWidth = modalRef.current?.offsetWidth || 300;
      
      setPosition({
        top: rect.bottom + 10,
        left: rect.right - modalWidth  // Сразу вычисляем правильную позицию
      });
      setIsPositioned(true);
    }
  };

  useEffect(() => {
    if (isOpen) {
      setIsPositioned(false);
      // Небольшая задержка чтобы DOM успел отрисоваться
      setTimeout(() => {
        updatePosition();
      }, 10);
    }
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return;

    const handleResize = () => {
      updatePosition();
    };

    window.addEventListener('resize', handleResize);
    window.addEventListener('scroll', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
      window.removeEventListener('scroll', handleResize);
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
        ref={modalRef}
        className={`notifications-modal ${!isPositioned ? 'hidden' : ''}`}
        style={{
          top: `${position.top}px`,
          left: `${position.left}px`
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