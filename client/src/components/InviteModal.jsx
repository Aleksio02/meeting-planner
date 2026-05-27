import React, { useState } from 'react';
import { usersAPI } from '../api/users';
import { useToast } from '../context/ToastContext';

const InviteModal = ({ isOpen, onClose, onAddParticipants, currentParticipants }) => {
  const [searchValue, setSearchValue] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [searchTimeout, setSearchTimeout] = useState(null);
  const [selectedUsers, setSelectedUsers] = useState([]);
  const { addToast } = useToast();

  const handleInputChange = (e) => {
    const value = e.target.value;
    setSearchValue(value);

    if (searchTimeout) clearTimeout(searchTimeout);

    if (value.trim().length < 2) {
      setSuggestions([]);
      setShowSuggestions(false);
      return;
    }

    const timeout = setTimeout(async () => {
      try {
        const { data } = await usersAPI.searchByUsername(value.trim());
        
        const searchTerm = value.trim().toLowerCase();
        const filtered = Array.isArray(data) 
          ? data.filter(user => {
              const username = (user.username || user.login || '').toLowerCase();
              const email = (user.email || '').toLowerCase();
              const alreadyAdded = selectedUsers.some(u => u.id === user.id) ||
                                   currentParticipants?.some(p => p.toLowerCase() === username);
              return (username.includes(searchTerm) || email.includes(searchTerm)) && !alreadyAdded;
            })
          : [];
        
        setSuggestions(filtered);
        setShowSuggestions(filtered.length > 0);
      } catch (error) {
        setSuggestions([]);
        setShowSuggestions(false);
      }
    }, 300);

    setSearchTimeout(timeout);
  };

  const handleSelectSuggestion = (user) => {
    if (selectedUsers.some(u => u.id === user.id)) {
      addToast('Этот участник уже добавлен', 'error');
      return;
    }
    setSelectedUsers(prev => [...prev, { id: user.id, username: user.username || user.login }]);
    setSearchValue('');
    setSuggestions([]);
    setShowSuggestions(false);
  };

  const handleAddParticipant = () => {
    const trimmed = searchValue.trim();
    if (!trimmed) return;

    if (selectedUsers.some(u => u.username.toLowerCase() === trimmed.toLowerCase())) {
      addToast('Этот участник уже добавлен', 'error');
      return;
    }

    if (suggestions.length > 0) {
      handleSelectSuggestion(suggestions[0]);
    } else {
      addToast('Пользователь не найден', 'error');
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleAddParticipant();
    }
  };

  const handleRemoveUser = (index) => {
    setSelectedUsers(prev => prev.filter((_, i) => i !== index));
  };

  const handleInvite = () => {
    if (selectedUsers.length === 0) {
      addToast('Выберите хотя бы одного участника', 'error');
      return;
    }
    onAddParticipants(selectedUsers.map(u => u.username));
    setSelectedUsers([]);
    setSearchValue('');
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="invite-modal-overlay" onClick={onClose}>
      <div className="invite-modal" onClick={(e) => e.stopPropagation()}>
        <div className="invite-header">
          <h3>Пригласить участников</h3>
          <button className="modal-close-x" onClick={onClose}>✕</button>
        </div>

        <div className="invite-body">
          <div className="email-add-section">
            <div className="autocomplete-wrapper">
              <input
                type="text"
                autoFocus
                placeholder="Никнейм участника"
                className="participant-input"
                value={searchValue}
                onChange={handleInputChange}
                onKeyDown={handleKeyDown}
                onFocus={() => suggestions.length > 0 && setShowSuggestions(true)}
                onBlur={() => setTimeout(() => setShowSuggestions(false), 200)}
              />
              
              {showSuggestions && suggestions.length > 0 && (
                <ul className="suggestions-list">
                  {suggestions.map((user) => (
                    <li
                      key={user.id}
                      className="suggestion-item"
                      onMouseDown={() => handleSelectSuggestion(user)}
                    >
                      <span className="suggestion-username">
                        {user.username || user.login}
                      </span>
                      {user.email && (
                        <span className="suggestion-email">{user.email}</span>
                      )}
                    </li>
                  ))}
                </ul>
              )}
            </div>
            <button className="add-btn" onClick={handleAddParticipant}>
              Добавить
            </button>
          </div>

          {selectedUsers.length > 0 && (
            <div className="email-tags-container">
              {selectedUsers.map((user, index) => (
                <div className="email-tag" key={user.id}>
                  {user.username}
                  <button className="remove-tag" onClick={() => handleRemoveUser(index)}>✕</button>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="invite-footer">
          <button className="cancel-btn" onClick={onClose}>Отмена</button>
          <button className="invite-btn" onClick={handleInvite} disabled={selectedUsers.length === 0}>
            Пригласить
          </button>
        </div>
      </div>
    </div>
  );
};

export default InviteModal;