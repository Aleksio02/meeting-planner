import api from './axios';

export const meetingsAPI = {
  create: (data) => api.post('/meetings', data),
  getList: (params) => api.get('/meetings', { params }),
  getById: (id) => api.get(`/meetings/${id}`),
  update: (id, data) => api.patch(`/meetings/${id}`, data),
  delete: (id) => api.delete(`/meetings/${id}`),
  cancel: (id, data) => api.patch(`/meetings/${id}/cancel`, data),
  
};