import React, { useState } from 'react';

const InviteModal = ({ isOpen, onClose, onAddParticipant, currentParticipants }) => {
  const [searchValue, setSearchValue] = useState('');
  const [suggestions, setSuggestions] = useState([]);

  const allUsers = ["alex99", "andrew_dev", "anna@mail.com", "anton@gmail.com", "artem_pro", "user1@mail.com", "max_power", "ivan", "ivaad"];

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearchValue(value);
    if (value.length >= 3) {
      const filtered = allUsers.filter(user => 
        user.toLowerCase().includes(value.toLowerCase()) && !currentParticipants.includes(user)
      );
      setSuggestions(filtered);
    } else {
      setSuggestions([]);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="invite-modal-overlay">
      <div className="invite-modal">
        <div className="invite-header">
          <h3>Пригласить</h3>
          <button onClick={onClose}>✕</button>
        </div>
        <div className="invite-body">
          <input
            type="text"
            autoFocus
            placeholder="Ник или email..."
            className="participant-input"
            value={searchValue}
            onChange={handleSearchChange}
          />
          <div className="invite-suggestions">
            {suggestions.map((user, index) => (
              <div key={index} className="suggestion-item" onClick={() => {
                onAddParticipant(user);
                setSearchValue('');
              }}>
                {user}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default InviteModal;