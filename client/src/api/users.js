import api from './axios';

export const usersAPI = {
  searchByUsername: (username) => api.post('/users', { username }),
  getById: (id) => api.get(`/users/${id}`),
};