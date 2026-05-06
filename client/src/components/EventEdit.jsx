import React, { useState, useEffect } from 'react';
import '../styles/EventEdit.css';

const EventEdit = ({ isOpen, onClose, eventData, onSave }) => {
  const [formData, setFormData] = useState({
    title: '', date: '', startTime: '', endTime: '', description: ''
  });

  useEffect(() => {
    if (isOpen) {
      if (eventData) {
        // Редактирование — заполняем данными
        setFormData({ ...eventData });
      } else {
        // Создание — очищаем поля
        setFormData({
          title: '', date: '', startTime: '', endTime: '', description: ''
        });
      }
    }
  }, [isOpen, eventData]);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  return (
    <>
      <div className="edit-overlay" onClick={onClose} />
      <div className="edit-modal">
        <button className="edit-close-x" onClick={onClose}>✕</button>
        <form className="edit-form" onSubmit={(e) => { e.preventDefault(); onSave(formData); }}>
          <h2 className="edit-header">
            {eventData ? 'Редактирование' : 'Новое событие'}
          </h2>

          <div className="edit-group">
            <label>Название события</label>
            <input 
              name="title" 
              value={formData.title} 
              onChange={handleChange} 
              className="edit-input" 
              placeholder="Введите название"
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
              placeholder="Добавьте описание события"
            />
          </div>

          <div className="edit-footer">
            <button type="button" className="edit-btn-secondary" onClick={onClose}>Отмена</button>
            <button type="submit" className="edit-btn-primary">
              {eventData ? 'Сохранить' : 'Создать'}
            </button>
          </div>
        </form>
      </div>
    </>
  );
};

export default EventEdit;