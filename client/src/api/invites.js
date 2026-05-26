import api from './axios';

export const invitesAPI = {
  create: (data) => api.post('/api/invites', data),
  createList: (data) => api.post('/api/invites/list', data),
  getList: (params) => api.get('/api/invites', { params }),
  getById: (id) => api.get(`/api/invites/${id}`),
  delete: (id) => api.delete(`/api/invites/${id}`),
  accept: (id) => api.patch(`/api/invites/${id}/accept`),
  decline: (id) => api.patch(`/api/invites/${id}/decline`),
};