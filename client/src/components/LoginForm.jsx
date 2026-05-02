import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../api/auth';
import { useToast } from '../context/ToastContext';

// Валидация одного поля
const validateField = (name, value) => {
  switch (name) {
    case 'login':
      if (!value.trim()) return 'Введите ник или почту';
      if (value.trim().length < 3) return 'Минимум 3 символа';
      return '';
    
    case 'password':
      if (!value) return 'Введите пароль';
      if (value.length < 1) return 'Введите пароль';
      return '';
    
    default:
      return '';
  }
};

const LoginForm = () => {
  const navigate = useNavigate();
  const { addToast } = useToast();

  const [formData, setFormData] = useState({
    login: '',
    password: '',
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    if (touched[name]) {
      const errorMsg = validateField(name, value);
      setErrors(prev => ({ ...prev, [name]: errorMsg }));
    }
  };

  const handleBlur = (e) => {
    const { name, value } = e.target;
    setTouched(prev => ({ ...prev, [name]: true }));
    const errorMsg = validateField(name, value);
    setErrors(prev => ({ ...prev, [name]: errorMsg }));
  };

  const validate = () => {
    const newErrors = {};
    let isValid = true;

    ['login', 'password'].forEach(field => {
      const errorMsg = validateField(field, formData[field]);
      newErrors[field] = errorMsg;
      if (errorMsg) isValid = false;
    });

    setErrors(newErrors);
    setTouched({ login: true, password: true });
    
    return isValid;
  };

  const extractErrors = (errorData) => {
    const messages = [];

    if (typeof errorData === 'string') {
      messages.push(errorData);
      return messages;
    }

    if (errorData?.message) {
      messages.push(errorData.message);
    }

    if (errorData?.errors && typeof errorData.errors === 'object' && !Array.isArray(errorData.errors)) {
      Object.entries(errorData.errors).forEach(([field, msg]) => {
        const errorText = Array.isArray(msg) ? msg.join(', ') : msg;
        messages.push(`${field}: ${errorText}`);
      });
    }

    if (Array.isArray(errorData?.errors)) {
      errorData.errors.forEach(err => {
        messages.push(`${err.field}: ${err.defaultMessage || err.message}`);
      });
    }

    return messages.length > 0 ? messages : ['Неизвестная ошибка сервера'];
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validate()) {
      const clientErrors = [];
      if (errors.login) clientErrors.push(`Логин: ${errors.login}`);
      if (errors.password) clientErrors.push(`Пароль: ${errors.password}`);
      
      if (clientErrors.length > 0) {
        clientErrors.forEach(msg => addToast(msg, 'error', 5000));
      }
      return;
    }

    setLoading(true);
    try {
      const response = await authAPI.login({
        login: formData.login.trim(),
        password: formData.password,
      });

      // Кука sessionId установится автоматически
      addToast(' Вход выполнен! Перенаправляем...', 'success', 3000);

      setTimeout(() => {
        navigate('/home');
      }, 1500);

    } catch (error) {
      const status = error.response?.status;
      const data = error.response?.data;
      const errorMessages = extractErrors(data);

      if (status === 401) {
        addToast(' Неверный логин или пароль', 'error', 6000);
      } else if (status === 403) {
        addToast(' Аккаунт заблокирован', 'error', 6000);
      } else if (status === 404) {
        addToast(' Пользователь не найден', 'error', 6000);
      } else if (!error.response) {
        addToast(' Нет соединения с сервером', 'error', 6000);
      } else {
        errorMessages.forEach(msg => addToast(`❌ ${msg}`, 'error', 6000));
      }
    } finally {
      setLoading(false);
    }
  };

  const handleRegisterClick = () => {
    navigate('/register');
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      handleSubmit(e);
    }
  };

  const showError = (fieldName) => {
    return touched[fieldName] && errors[fieldName];
  };

  return (
    <form onSubmit={handleSubmit} className="login-card" noValidate>
      <h1 className="login-title">Вход в аккаунт</h1>

      {/* Логин (ник или почта) */}
      <div className="input-group">
        <input
          name="login"
          type="text"
          placeholder="Ник или Email"
          className={`login-input ${showError('login') ? 'input-error' : touched.login && !errors.login ? 'input-success' : ''}`}
          value={formData.login}
          onChange={handleChange}
          onBlur={handleBlur}
          onKeyDown={handleKeyDown}
          maxLength={255}
          autoComplete="username"
        />
        {showError('login') && (
          <span className="field-hint">{errors.login}</span>
        )}
      </div>

      {/* Пароль */}
      <div className="input-group">
        <input
          name="password"
          type="password"
          placeholder="Пароль"
          className={`login-input ${showError('password') ? 'input-error' : touched.password && !errors.password ? 'input-success' : ''}`}
          value={formData.password}
          onChange={handleChange}
          onBlur={handleBlur}
          onKeyDown={handleKeyDown}
          maxLength={64}
          autoComplete="current-password"
        />
        {showError('password') && (
          <span className="field-hint">{errors.password}</span>
        )}
      </div>

      <button type="submit" className="login-button" disabled={loading}>
        {loading ? 'Вход...' : 'Войти'}
      </button>

      <div className="login-footer">
        <span className="login-footer-text">Нет аккаунта?</span>
        <span
          className="login-footer-link"
          onClick={handleRegisterClick}
        >
          Зарегистрироваться
        </span>
      </div>
    </form>
  );
};

export default LoginForm;