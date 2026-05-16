import React, { useState } from "react";
import { meetingsAPI } from "../api/meetings";
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
  const [emailInput, setEmailInput] = useState("");
  const [emails, setEmails] = useState([]);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const handleDateChange = (date) => {
    setSelectedDate(date);
    setErrors(prev => ({ ...prev, date: '' }));
  };
  
  const isValidEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  };
  
  const handleAddEmail = () => {
    const trimmed = emailInput.trim();
    if (
      trimmed &&
      !emails.includes(trimmed) &&
      emails.length < 5 &&
      isValidEmail(trimmed)
    ) {
      setEmails([...emails, trimmed]);
      setEmailInput("");
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleAddEmail();
    }
  };

  const handleRemoveEmail = (emailToRemove) => {
    setEmails(emails.filter((email) => email !== emailToRemove));
  };

  const validate = () => {
    const newErrors = {};

    if (!title.trim()) newErrors.title = 'Введите название';
    if (!selectedDate) newErrors.date = 'Выберите дату';
    if (!startTime) newErrors.startTime = 'Выберите время начала';
    if (!endTime) newErrors.endTime = 'Выберите время окончания';
    if (startTime && endTime && startTime >= endTime) {
      newErrors.endTime = 'Окончание должно быть позже начала';
    }

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
      addToast('Проверьте заполнение полей', 'error');
      return;
    }

    const [startHour, startMin] = startTime.split(':').map(Number);
    const [endHour, endMin] = endTime.split(':').map(Number);
    const durationMinutes = (endHour * 60 + endMin) - (startHour * 60 + startMin);

    const meetingData = {
      title: title.trim(),
      description: description.trim(),
      date: formatDate(selectedDate),
      startTime: startTime,
      endTime: endTime,
      duration: durationMinutes,
      participants: emails,
      status: "PLANNED",
    };

    console.log('Отправляем:', meetingData);

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
          participants: data.participants || emails,
        });
      }

      onClose();

    } catch (error) {
      console.error('Ошибка создания:', error);
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
            className="event-title-input"
            placeholder="Добавьте название"
            value={title}
            onChange={(e) => {
              setTitle(e.target.value);
              setErrors(prev => ({ ...prev, title: '' }));
            }}
            style={errors.title ? { borderColor: '#ff4757' } : {}}
          />
          {errors.title && <span className="field-hint">{errors.title}</span>}
          
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
          {errors.date && <span className="field-hint">{errors.date}</span>}
          
          <TimePicker 
            options={timeOptions} 
            onChange={(val) => {
              setStartTime(val);
              setErrors(prev => ({ ...prev, startTime: '' }));
            }} 
          />
          {errors.startTime && <span className="field-hint">{errors.startTime}</span>}
          
          <span className="time-separator"> — </span>
          
          <TimePicker 
            options={timeOptions} 
            onChange={(val) => {
              setEndTime(val);
              setErrors(prev => ({ ...prev, endTime: '' }));
            }} 
          />
          {errors.endTime && <span className="field-hint">{errors.endTime}</span>}
        </div>

        <div className="inputs-row column-layout">
          <div className="email-add-section">
            <img src="/src/assets/user.svg" alt="user" className="icon-user" />
            <input
              type="email"
              placeholder="Добавить участников"
              className="user-input"
              value={emailInput}
              onChange={(e) => setEmailInput(e.target.value)}
              onKeyDown={handleKeyDown}
            />
            <button
              className="add-button"
              onClick={handleAddEmail}
              disabled={
                emails.length >= 5 ||
                !isValidEmail(emailInput.trim()) ||
                emails.includes(emailInput.trim())
              }
            >
              Добавить
            </button>
          </div>

          <div className="email-tags-container">
            {emails.map((email) => (
              <div className="email-tag" key={email}>
                {email}
                <button
                  className="remove-tag"
                  onClick={() => handleRemoveEmail(email)}
                >
                  ✕
                </button>
              </div>
            ))}
          </div>
          {emails.length >= 5 && (
            <p className="limit-warning">
              Можно добавить не более 5 участников.
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