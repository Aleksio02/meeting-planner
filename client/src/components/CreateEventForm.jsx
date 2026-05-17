import React, { useState } from "react";
import { meetingsAPI } from "../api/meetings";
import { usersAPI } from "../api/users";
import { useToast } from "../context/ToastContext";
import TimePicker from "./TimePicker";
import DatePicker from "./DatePicker";
import "../styles/CreateEventForm.css";

const CreateEventForm = ({ onClose, onCreated }) => {
  const { addToast } = useToast();
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");
  const [selectedDate, setSelectedDate] = useState(null);
  const [inputValue, setInputValue] = useState("");
  const [participants, setParticipants] = useState([]);
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [searchTimeout, setSearchTimeout] = useState(null);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const handleDateChange = (date) => {
    setSelectedDate(date);
    setErrors(prev => ({ ...prev, date: '' }));
  };

  const handleInputChange = (e) => {
    const value = e.target.value;
    setInputValue(value);

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
              return username.includes(searchTerm) || email.includes(searchTerm);
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
    if (participants.find(p => p.id === user.id)) {
      addToast('Этот участник уже добавлен', 'error');
      return;
    }

    if (participants.length >= 10) {
      addToast('Максимум 10 участников', 'error');
      return;
    }

    setParticipants(prev => [...prev, { 
      username: user.username || user.login, 
      id: user.id 
    }]);
    setInputValue("");
    setSuggestions([]);
    setShowSuggestions(false);
  };

  const handleAddParticipant = () => {
    const trimmed = inputValue.trim();
    if (!trimmed) return;

    if (participants.find(p => p.username.toLowerCase() === trimmed.toLowerCase())) {
      addToast('Этот участник уже добавлен', 'error');
      return;
    }

    if (participants.length >= 10) {
      addToast('Максимум 10 участников', 'error');
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

  const handleRemoveParticipant = (index) => {
    setParticipants(participants.filter((_, i) => i !== index));
  };

  const validate = () => {
    const newErrors = {};
    if (!title.trim()) newErrors.title = true;
    if (!selectedDate) newErrors.date = true;
    if (!startTime) newErrors.startTime = true;
    if (!endTime) newErrors.endTime = true;
    if (startTime && endTime && startTime >= endTime) newErrors.endTime = true;

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const formatDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  const handleSubmit = async () => {
    if (!validate()) {
      addToast('Заполните обязательные поля', 'error');
      return;
    }

    const [startHour, startMin] = startTime.split(':').map(Number);
    const [endHour, endMin] = endTime.split(':').map(Number);
    const durationMinutes = (endHour * 60 + endMin) - (startHour * 60 + startMin);

    const invitedUserIds = participants.map(p => p.id);

    const meetingData = {
      title: title.trim(),
      description: description.trim(),
      date: formatDate(selectedDate),
      startTime: startTime,
      endTime: endTime,
      duration: durationMinutes,
      participants: [],
      invitedUserIds: invitedUserIds,
      status: "PLANNED",
    };

    setLoading(true);
    try {
      const { data } = await meetingsAPI.create(meetingData);
      addToast('✅ Встреча создана!', 'success', 3000);

      if (onCreated) {
        onCreated({
          id: data.id,
          title: data.title,
          date: data.date || formatDate(selectedDate),
          startTime: data.startTime || startTime,
          endTime: data.endTime || endTime,
          description: data.description || description,
          isMyEvent: true,
          participants: participants.map(p => p.username),
        });
      }

      onClose();

    } catch (error) {
      const data = error.response?.data;
      const errorMessage = data?.errorMessage || data?.message || 'Ошибка при создании встречи';

      if (!error.response) {
        addToast('❌ Нет соединения с сервером', 'error', 6000);
      } else {
        addToast(`❌ ${errorMessage}`, 'error', 6000);
      }
    } finally {
      setLoading(false);
    }
  };

  const timeOptions = [];
  for (let hour = 0; hour < 24; hour++) {
    for (let minutes = 0; minutes < 60; minutes += 15) {
      const h = hour.toString().padStart(2, "0");
      const m = minutes.toString().padStart(2, "0");
      timeOptions.push(`${h}:${m}`);
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close-x" onClick={onClose}>✕</button>
        
        <div className="input-content">
          <input
            type="text"
            className={`event-title-input ${errors.title ? 'input-error' : ''}`}
            placeholder="Добавьте название"
            value={title}
            onChange={(e) => {
              setTitle(e.target.value);
              setErrors(prev => ({ ...prev, title: '' }));
            }}
          />
          
          <textarea
            className="event-discription-input"
            placeholder="Описание"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          ></textarea>
        </div>

        <div className="inputs-row">
          <img src="/src/assets/time.svg" alt="time" className="icon-time" />
          <DatePicker
            selected={selectedDate}
            onChange={handleDateChange}
            locale="ru"
            dateFormat="eee, dd MMM yyyy 'г.'"
            placeholderText="Выберите дату"
            className={`datepicker-input ${errors.date ? 'input-error' : ''}`}
            calendarClassName="event-calendar"
          />
          
          <TimePicker 
            options={timeOptions}
            className={errors.startTime ? 'input-error' : ''}
            onChange={(val) => {
              setStartTime(val);
              setErrors(prev => ({ ...prev, startTime: '' }));
            }} 
          />
          
          <span className="time-separator"> — </span>
          
          <TimePicker 
            options={timeOptions}
            className={errors.endTime ? 'input-error' : ''}
            onChange={(val) => {
              setEndTime(val);
              setErrors(prev => ({ ...prev, endTime: '' }));
            }} 
          />
        </div>

        <div className="inputs-row column-layout">
          <div className="email-add-section">
            <img src="/src/assets/user.svg" alt="user" className="icon-user" />
            <div className="autocomplete-wrapper">
              <input
                type="text"
                placeholder="Никнейм участника"
                className="user-input"
                value={inputValue}
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
            <button
              className="add-button"
              onClick={handleAddParticipant}
              disabled={participants.length >= 10}
            >
              Добавить
            </button>
          </div>

          <div className="email-tags-container">
            {participants.map((p, index) => (
              <div className="email-tag" key={index}>
                {p.username}
                <button
                  className="remove-tag"
                  onClick={() => handleRemoveParticipant(index)}
                >
                  ✕
                </button>
              </div>
            ))}
          </div>
          {participants.length >= 10 && (
            <p className="limit-warning">
              Можно добавить не более 10 участников.
            </p>
          )}
        </div>

        <button 
          className="event-create" 
          onClick={handleSubmit}
          disabled={loading}
        >
          {loading ? 'Создание...' : 'Создать событие'}
        </button>
      </div>
    </div>
  );
};

export default CreateEventForm;