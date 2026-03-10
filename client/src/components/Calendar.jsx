// src/components/Calendar.jsx
import React, { useState } from 'react';
import '../styles/Calendar.css';

const Calendar = () => {
  const [currentDate, setCurrentDate] = useState(new Date(2025, 5, 1)); // Июнь 2025

  // Названия месяцев
  const monthNames = [
    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
  ];

  // Переключение на предыдущий месяц
  const handlePrevMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 1));
  };

  // Переключение на следующий месяц
  const handleNextMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1));
  };

  // Получаем первый день месяца (0 - понедельник, 1 - вторник и т.д.)
  const getFirstDayOfMonth = () => {
    const firstDay = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1).getDay();
    return firstDay === 0 ? 6 : firstDay - 1; // Преобразуем воскресенье (0) в 6
  };

  // Получаем количество дней в месяце
  const getDaysInMonth = () => {
    return new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0).getDate();
  };

  // Получаем дни из предыдущего месяца
  const getPrevMonthDays = () => {
    const prevMonth = new Date(currentDate.getFullYear(), currentDate.getMonth(), 0);
    return prevMonth.getDate();
  };

  // Генерируем массив дней для отображения
  const generateDays = () => {
    const firstDayOfMonth = getFirstDayOfMonth();
    const daysInMonth = getDaysInMonth();
    const prevMonthDays = getPrevMonthDays();
    
    const days = [];

    // Добавляем дни предыдущего месяца
    for (let i = firstDayOfMonth; i > 0; i--) {
      days.push({
        day: prevMonthDays - i + 1,
        type: 'prev'
      });
    }

    // Добавляем дни текущего месяца
    for (let i = 1; i <= daysInMonth; i++) {
      days.push({
        day: i,
        type: 'current'
      });
    }

    // Добавляем дни следующего месяца до заполнения сетки (42 дня = 6 недель)
    const totalCells = 42;
    const remainingCells = totalCells - days.length;
    for (let i = 1; i <= remainingCells; i++) {
      days.push({
        day: i,
        type: 'next'
      });
    }

    return days;
  };

  const days = generateDays();

  return (
    <div className="calendar">
      {/* Заголовок с месяцем и стрелками */}
      <div className="calendar-header">
        <button className="month-nav" onClick={handlePrevMonth}>←</button>
        <h2>{monthNames[currentDate.getMonth()]} {currentDate.getFullYear()}</h2>
        <button className="month-nav" onClick={handleNextMonth}>→</button>
      </div>
      
      {/* Дни недели */}
      <div className="week-days">
        <span>ПН</span>
        <span>ВТ</span>
        <span>СР</span>
        <span>ЧТ</span>
        <span>ПТ</span>
        <span>СБ</span>
        <span>ВС</span>
      </div>

      {/* Сетка с числами */}
      <div className="days-grid">
        {days.map((day, index) => (
          <span 
            key={index} 
            className={day.type !== 'current' ? 'other-month' : ''}
          >
            {day.day}
          </span>
        ))}
      </div>
    </div>
  );
};

export default Calendar;