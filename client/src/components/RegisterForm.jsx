import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../api/auth';
import { useToast } from '../context/ToastContext';

const validateField = (name, value, formData) => {
  switch (name) {
    case 'username':
      if (!value.trim()) return 'Введите имя профиля';
      if (value.trim().length < 3) return 'Минимум 3 символа';
      if (value.trim().length > 30) return 'Максимум 30 символов';
      if (!/^[a-zA-Zа-яА-ЯёЁ0-9 _\-]+$/.test(value.trim())) {
        return 'Только буквы, цифры, пробел, _ и -';
      }
      return '';
    
    case 'email':
      if (!value.trim()) return 'Введите почту';
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) return 'Некорректный формат почты';
      if (value.length > 255) return 'Слишком длинная почта';
      return '';
    
    case 'password':
      if (!value) return 'Введите пароль';
      if (value.length < 8) return 'Минимум 8 символов';
      if (value.length > 64) return 'Максимум 64 символа';
      if (!/[A-ZА-ЯЁ]/.test(value)) return 'Добавьте заглавную букву';
      if (!/[a-zа-яё]/.test(value)) return 'Добавьте строчную букву';
      if (!/[0-9]/.test(value)) return 'Добавьте цифру';
      return '';
    
    case 'confirmPassword':
      if (!value) return 'Подтвердите пароль';
      if (value !== formData.password) return 'Пароли не совпадают';
      return '';
    
    default:
      return '';
  }
};

const RegisterForm = () => {
  const navigate = useNavigate();
  const { addToast } = useToast();

  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    if (touched[name]) {
      const errorMsg = validateField(name, value, { ...formData, [name]: value });
      setErrors(prev => ({ ...prev, [name]: errorMsg }));
    }
  };

  const handleBlur = (e) => {
    const { name, value } = e.target;
    setTouched(prev => ({ ...prev, [name]: true }));
    const errorMsg = validateField(name, value, formData);
    setErrors(prev => ({ ...prev, [name]: errorMsg }));
  };

  const validate = () => {
    const newErrors = {};
    let isValid = true;

    ['username', 'email', 'password', 'confirmPassword'].forEach(field => {
      const errorMsg = validateField(field, formData[field], formData);
      newErrors[field] = errorMsg;
      if (errorMsg) isValid = false;
    });

    setErrors(newErrors);
    setTouched({ username: true, email: true, password: true, confirmPassword: true });
    
    return isValid;
  };

  const extractErrors = (errorData) => {
    const messages = [];

    if (!errorData) return ['Неизвестная ошибка сервера'];

    // Формат бекенда: { errorCode: 400, errorMessage: "..." }
    if (errorData.errorMessage) {
      messages.push(errorData.errorMessage);
      return messages;
    }

    if (typeof errorData === 'string') {
      messages.push(errorData);
      return messages;
    }

    if (errorData?.message) {
      messages.push(errorData.message);
    }

    if (errorData?.errors && typeof errorData.errors === 'object' && !Array.isArray(errorData.errors)) {
      const fieldNames = {
        login: 'Имя профиля',
        email: 'Почта',
        password: 'Пароль',
        username: 'Имя профиля',
      };
      Object.entries(errorData.errors).forEach(([field, msg]) => {
        const fieldName = fieldNames[field] || field;
        const errorText = Array.isArray(msg) ? msg.join(', ') : msg;
        messages.push(`${fieldName}: ${errorText}`);
      });
    }

    if (Array.isArray(errorData?.errors)) {
      errorData.errors.forEach(err => {
        const fieldNames = {
          login: 'Имя профиля',
          email: 'Почта',
          password: 'Пароль',
        };
        const fieldName = fieldNames[err.field] || err.field;
        messages.push(`${fieldName}: ${err.defaultMessage || err.message}`);
      });
    }

    return messages.length > 0 ? messages : ['Неизвестная ошибка сервера'];
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validate()) {
      return;
    }

    setLoading(true);
    try {
      await authAPI.register({
        login: formData.username.trim(),
        email: formData.email.trim().toLowerCase(),
        password: formData.password,
      });

      addToast('✅ Регистрация успешна! Перенаправляем...', 'success', 3000);

      setTimeout(() => {
        navigate('/login');
      }, 1500);

    } catch (error) {
      const data = error.response?.data;
      
      // Получаем сообщение из формата бекенда
      const errorMessage = data?.errorMessage || extractErrors(data).join('. ');

      if (errorMessage.toLowerCase().includes('username or email exists')) {
        addToast(' Пользователь с таким именем или почтой уже существует', 'error', 6000);
      } else if (data?.errorCode === 400 || error.response?.status === 400) {
        addToast(` ${errorMessage}`, 'error', 6000);
      } else if (!error.response) {
        addToast(' Нет соединения с сервером', 'error', 6000);
      } else {
        addToast(` ${errorMessage}`, 'error', 6000);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleLoginClick = () => {
    navigate('/login');
  };

  const showError = (fieldName) => {
    return touched[fieldName] && errors[fieldName];
  };

  return (
    <form onSubmit={handleSubmit} className="register-card" noValidate>
      <h1 className="register-title">Регистрация</h1>

      <div className="input-group">
        <input
          name="username"
          type="text"
          placeholder="Имя профиля"
          className={`register-input ${showError('username') ? 'input-error' : touched.username && !errors.username ? 'input-success' : ''}`}
          value={formData.username}
          onChange={handleChange}
          onBlur={handleBlur}
          maxLength={30}
          autoComplete="username"
        />
        {showError('username') && (
          <span className="field-hint">{errors.username}</span>
        )}
      </div>

      <div className="input-group">
        <input
          name="email"
          type="email"
          placeholder="Email"
          className={`register-input ${showError('email') ? 'input-error' : touched.email && !errors.email ? 'input-success' : ''}`}
          value={formData.email}
          onChange={handleChange}
          onBlur={handleBlur}
          maxLength={255}
          autoComplete="email"
        />
        {showError('email') && (
          <span className="field-hint">{errors.email}</span>
        )}
      </div>

      <div className="input-group">
        <input
          name="password"
          type="password"
          placeholder="Пароль"
          className={`register-input ${showError('password') ? 'input-error' : touched.password && !errors.password ? 'input-success' : ''}`}
          value={formData.password}
          onChange={handleChange}
          onBlur={handleBlur}
          maxLength={64}
          autoComplete="new-password"
        />
        {showError('password') && (
          <span className="field-hint">{errors.password}</span>
        )}
      </div>

      <div className="input-group">
        <input
          name="confirmPassword"
          type="password"
          placeholder="Повторите пароль"
          className={`register-input ${showError('confirmPassword') ? 'input-error' : touched.confirmPassword && !errors.confirmPassword ? 'input-success' : ''}`}
          value={formData.confirmPassword}
          onChange={handleChange}
          onBlur={handleBlur}
          maxLength={64}
          autoComplete="new-password"
        />
        {showError('confirmPassword') && (
          <span className="field-hint">{errors.confirmPassword}</span>
        )}
      </div>

      <button type="submit" className="register-button" disabled={loading}>
        {loading ? 'Регистрация...' : 'Зарегистрироваться'}
      </button>

      <div className="register-footer">
        <span className="register-footer-text">Уже есть аккаунт?</span>
        <span className="register-footer-link" onClick={handleLoginClick}>
          Войти
        </span>
      </div>
    </form>
  );
};

export default RegisterForm;