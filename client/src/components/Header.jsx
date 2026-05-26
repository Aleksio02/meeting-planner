import React, { useState, useRef, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { invitesAPI } from '../api/invites';
import InvitesModal from './InvitesModal';
import NotificationsModal from './NotificationsModal';
import '../styles/Header.css';

const Header = ({ onRefreshMeetings }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [isInvitesOpen, setIsInvitesOpen] = useState(false);
  const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
  const [newInvitesCount, setNewInvitesCount] = useState(0);
  const invitesBtnRef = useRef(null);
  const notificationBtnRef = useRef(null);

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  useEffect(() => {
    const loadNewCount = async () => {
      try {
        const { data } = await invitesAPI.getList({ status: 'PENDING', page: 0, pageSize: 100 });
        const viewedIds = JSON.parse(localStorage.getItem('viewedInvites') || '[]');
        const currentUserId = user?.currentUser?.id || user?.id;
        
        const filtered = Array.isArray(data)
          ? data.filter(inv => {
              const invitedUserId = inv.userId?.id;
              const ownerId = inv.meetingId?.owner?.id;
              return invitedUserId === currentUserId && ownerId !== currentUserId && !viewedIds.includes(inv.id);
            })
          : [];
        
        setNewInvitesCount(filtered.length);
      } catch (error) {}
    };

    if (user) {
      loadNewCount();
      const interval = setInterval(loadNewCount, 10000);
      return () => clearInterval(interval);
    }
  }, [user]);

  const handleInvitesOpen = () => {
    setIsInvitesOpen(true);
  };

  const handleInvitesClose = () => {
    setIsInvitesOpen(false);
    setNewInvitesCount(0);
  };

  return (
    <header className="header">
      <div className="header-left">
        <img src="/src/assets/icon.svg" alt="Meeting Planner" className="logo" />
        <span className="app-name">Meeting planner</span>
      </div>
      
      <div className="header-right">
        <button className="invitations-btn" ref={invitesBtnRef} onClick={handleInvitesOpen}>
          <img src="/src/assets/invitations.svg" alt="Invitations" />
          {newInvitesCount > 0 && <span className="badge">{newInvitesCount}</span>}
        </button>

        <button className="notification-btn" ref={notificationBtnRef} onClick={() => setIsNotificationsOpen(true)}>
          <img src="/src/assets/notification.svg" alt="Notifications" />
        </button>
        
        <div className="user-profile">
          <span className="username">{user?.username || user?.login || 'Гость'}</span>
          <div className="avatar">
            <img src="/src/assets/avatar.jpg" alt="Avatar" />
          </div>
          <button onClick={handleLogout} className="logout-btn" style={{ marginLeft: '10px' }}>Выйти</button>
        </div>
      </div>

      <InvitesModal 
        isOpen={isInvitesOpen} 
        onClose={handleInvitesClose} 
        anchorRef={invitesBtnRef} 
        onAccepted={onRefreshMeetings}
      />

      <NotificationsModal 
        isOpen={isNotificationsOpen} 
        onClose={() => setIsNotificationsOpen(false)} 
        anchorRef={notificationBtnRef} 
      />
    </header>
  );
};

export default Header;