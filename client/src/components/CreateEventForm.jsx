import React, { useState } from "react";
import "../styles/CreateEventForm.css";
import TimePicker from "./TimePicker";
import DatePicker from "./DatePicker";

const CreateEventForm = ({ onClose }) => {
  const [selectedTime, setSelectedTime] = useState("");
  const [selectedDate, setSelectedDate] = useState(null);
  const [emailInput, setEmailInput] = useState("");
  const [emails, setEmails] = useState([]);

  const handleChange = (date) => {
    setSelectedDate(date);
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

  const handleRemoveEmail = (emailToRemove) => {
    setEmails(emails.filter((email) => email !== emailToRemove));
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
        {/* КРЕСТИК ЗАКРЫТИЯ */}
        <button className="modal-close-x" onClick={onClose}>✕</button>
        
        <div className="input-content">
          <input
            type="text"
            className="event-title-input"
            placeholder="Добавьте название"
          />
          <textarea
            className="event-discription-input"
            placeholder="Описание"
          ></textarea>
        </div>

        <div className="inputs-row">
          <img src="/src/assets/time.svg" alt="time" className="icon-time" />
          <DatePicker
            selected={selectedDate}
            onChange={handleChange}
            locale="ru"
            dateFormat="eee, dd MMM yyyy 'г.'"
            placeholderText="Выберите дату"
            className="datepicker-input"
            calendarClassName="event-calendar"
          />
          <TimePicker options={timeOptions} onChange={setSelectedTime} />
          <span className="time-separator"> _ </span>
          <TimePicker options={timeOptions} onChange={setSelectedTime} />
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
        <button className="event-create">Создать событие</button>
      </div>
    </div>
  );
};

export default CreateEventForm;