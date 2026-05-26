import React, { createContext, useState, useContext, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuth } from './AuthContext';

const NotificationContext = createContext(null);

export const NotificationProvider = ({ children }) => {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const clientRef = useRef(null);

  useEffect(() => {
    const userId = user?.currentUser?.id || user?.id;
    if (!userId) return;

    const client = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      connectHeaders: {},
      debug: (str) => console.log('STOMP:', str),
      onConnect: () => {
        console.log('WebSocket подключён');
        client.subscribe(`/notification/${userId}`, (message) => {
          try {
            const notification = JSON.parse(message.body);
            setNotifications(prev => [notification, ...prev]);
          } catch (e) {
            console.error('Ошибка парсинга уведомления:', e);
          }
        });
      },
      onDisconnect: () => console.log('WebSocket отключён'),
      onStompError: (frame) => {
        console.error('STOMP error:', frame.headers['message']);
      },
      reconnectDelay: 5000,
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [user]);

  const clearNotifications = () => setNotifications([]);

  return (
    <NotificationContext.Provider value={{ notifications, clearNotifications }}>
      {children}
    </NotificationContext.Provider>
  );
};

export const useNotifications = () => useContext(NotificationContext);