import React, { useState, useRef, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { invitesAPI } from '../api/invites';
import InvitesModal from './InvitesModal';
import '../styles/Header.css';

const Header = ({ onRefreshMeetings }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [isInvitesOpen, setIsInvitesOpen] = useState(false);
  const [newInvitesCount, setNewInvitesCount] = useState(0);
  const invitesBtnRef = useRef(null);

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const loadNewCount = async () => {
    try {
      const { data } = await invitesAPI.getList({ page: 0, pageSize: 100 });
      const currentUserId = user?.currentUser?.id || user?.id;
      
      const count = Array.isArray(data)
        ? data.filter(inv => {
            return inv.userId?.id === currentUserId && 
                   inv.meetingId?.owner?.id !== currentUserId && 
                   inv.status === 'Ожидает';
          }).length
        : 0;
      
      setNewInvitesCount(count);
    } catch (error) {}
  };

  useEffect(() => {
    if (user) {
      loadNewCount();
      const interval = setInterval(loadNewCount, 10000);
      return () => clearInterval(interval);
    }
  }, [user]);

  const handleInvitesOpen = () => setIsInvitesOpen(true);
  
  const handleInvitesClose = () => {
    setIsInvitesOpen(false);
    loadNewCount();
  };

  const handleInviteAction = () => {
    loadNewCount();
    if (onRefreshMeetings) onRefreshMeetings();
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

        <button className="notification-btn">
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
        onAccepted={handleInviteAction}
      />
    </header>
  );
};

export default Header;