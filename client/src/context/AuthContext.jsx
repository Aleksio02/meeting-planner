import React, { createContext, useState, useContext, useEffect } from 'react';
import { authAPI } from '../api/auth';

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Проверка сессии при загрузке
  useEffect(() => {
    const checkSession = async () => {
      try {
        const response = await authAPI.validateSession();
        // Бекенд возвращает данные по-разному — проверяем все варианты
        const userData = response.data?.currentUser || response.data?.user || response.data;
        console.log('Сессия валидна:', userData);
        setUser(userData);
      } catch (error) {
        console.log('Сессия невалидна');
        setUser(null);
      } finally {
        setLoading(false);
      }
    };
    checkSession();
  }, []);

  const login = async (loginValue, password) => {
    const response = await authAPI.login({ login: loginValue, password });
    const userData = response.data?.currentUser || response.data?.user || response.data;
    setUser(userData);
    return response;
  };

  const logout = async () => {
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
};