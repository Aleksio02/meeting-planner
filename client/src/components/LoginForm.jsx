import React from "react";
import { useNavigate } from "react-router-dom";
import "../styles/LoginPage.css";

const LoginForm = () => {
      const navigate = useNavigate();
    
      const handleLoginClick = () => {
        navigate("/register");
      };
    
    return (
        <div className="login-card">
            <h1 className="login-title">Вход в аккаунт</h1>
             <input type="email" placeholder="Email" className="login-input" />
             <input type="password" placeholder="Пароль" className="login-input" />
             <button className="login-button">Войти</button>
              <div className="login-footer">
          <span className="login-footer-text">Нет аккаунта?</span>
          <span
            className="login-footer-link"
            onClick={handleLoginClick}
            style={{ cursor: "pointer" }}
          >
            Зарегистрироваться
          </span>
        </div>
        </div>
    );
};

export default LoginForm;