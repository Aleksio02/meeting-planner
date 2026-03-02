// src/components/Header.jsx
import React from 'react';
import '../styles/Header.css';

const Header = () => {
  return (
    <header className="header">
      <div className="header-left">
        <img 
          src="/src/assets/icon.svg" 
          alt="Meeting Planner" 
          className="logo"
        />
        <span className="app-name">Meeting planner</span>
      </div>
      
      <div className="header-right">
        <button className="invitations-btn">
          <img 
            src="/src/assets/invitations.svg" 
            alt="Invitations"
          />
        
        </button>

        <button className="notification-btn">
          <img 
            src="/src/assets/notification.svg" 
            alt="Notifications"
          />
          
        </button>
        
        <div className="user-profile">
          <span className="username">1mperium</span>
          <div className="avatar">
            <img 
              src="/src/assets/avatar.jpg" 
              alt="Avatar"
            />
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;