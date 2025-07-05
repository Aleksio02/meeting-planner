import React from 'react';
import '../styles/RegisterPage.css';

const RegisterForm = () => {
  const handleLoginClick = () => {
    window.location.href = '/login';
  };

  return (
    <div className="register-container">
      <div className="register-card">
        <h1 className="register-title">Регистрация</h1>
        <input type="text" placeholder="Имя профиля" className="register-input" />
        <input type="email" placeholder="Email" className="register-input" />
        <input type="password" placeholder="Пароль" className="register-input" />
        <input type="password" placeholder="Повторите пароль" className="register-input" />

        <button className="register-button">Зарегистрироваться</button>

        <div className="register-footer">
          <span className="register-footer-text">Уже есть аккаунт?</span>
          <span className="register-footer-link" onClick={handleLoginClick}>Войти</span>
        </div>
      </div>
    </div>
  );
};

export default RegisterForm;
