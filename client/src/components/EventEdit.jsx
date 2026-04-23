import React, { useState, useEffect } from 'react';
import '../styles/EventEdit.css';

const EventEdit = ({ isOpen, onClose, eventData, onSave }) => {
  const [formData, setFormData] = useState({
    title: '',
    date: '',
    startTime: '',
    endTime: '',
    description: '',
  });

  // Заполняем форму данными при открытии
  useEffect(() => {
    if (isOpen && eventData) {
      setFormData(eventData);
    }
  }, [isOpen, eventData]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(formData);
  };

  return (
    <>
      <div className="edit-overlay" onClick={onClose} />
      <div className="edit-modal">
        <button className="edit-close-x" onClick={onClose}>✕</button>
        
        <form onSubmit={handleSubmit} className="edit-form">
          <h2 className="edit-header">Редактирование</h2>

          <div className="edit-group">
            <label>Название события</label>
            <input 
              type="text" 
              name="title" 
              value={formData.title} 
              onChange={handleChange} 
              className="edit-input"
            />
          </div>

          <div className="edit-row">
            <div className="edit-group">
              <label>Дата</label>
              <input 
                type="date" 
                name="date" 
                value={formData.date} 
                onChange={handleChange} 
                className="edit-input"
              />
            </div>
            <div className="edit-group">
              <label>Начало</label>
              <input 
                type="time" 
                name="startTime" 
                value={formData.startTime} 
                onChange={handleChange} 
                className="edit-input"
              />
            </div>
            <div className="edit-group">
              <label>Конец</label>
              <input 
                type="time" 
                name="endTime" 
                value={formData.endTime} 
                onChange={handleChange} 
                className="edit-input"
              />
            </div>
          </div>

          <div className="edit-group">
            <label>Описание</label>
            <textarea 
              name="description" 
              value={formData.description} 
              onChange={handleChange} 
              className="edit-input edit-textarea"
            />
          </div>

          <div className="edit-footer">
            <button type="button" className="edit-btn-secondary" onClick={onClose}>
              Отмена
            </button>
            <button type="submit" className="edit-btn-primary">
              Сохранить
            </button>
          </div>
        </form>
      </div>
    </>
  );
};

export default EventEdit;