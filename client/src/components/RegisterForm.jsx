import React from "react";
import { useNavigate } from "react-router-dom";
import "../styles/RegisterPage.css";

const RegisterForm = () => {
  const navigate = useNavigate();

  const handleLoginClick = () => {
    navigate("/login");
  };

  return (
      <div className="register-card">
        <h1 className="register-title">Регистрация</h1>
        <input type="text" placeholder="Имя профиля" className="register-input" />
        <input type="email" placeholder="Email" className="register-input" />
        <input type="password" placeholder="Пароль" className="register-input" />
        <input type="password" placeholder="Повторите пароль" className="register-input" />

        <button className="register-button">Зарегистрироваться</button>

        <div className="register-footer">
          <span className="register-footer-text">Уже есть аккаунт?</span>
          <span
            className="register-footer-link"
            onClick={handleLoginClick}
            style={{ cursor: "pointer" }}
          >
            Войти
          </span>
        </div>
      </div>
  );
};

export default RegisterForm;
