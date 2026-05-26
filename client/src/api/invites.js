import api from './axios';

export const invitesAPI = {
  getList: (params) => api.get('/invites', { params }),  // ← убрал /api/
  getById: (id) => api.get(`/invites/${id}`),
  accept: (id) => api.patch(`/invites/${id}/accept`),
  decline: (id) => api.patch(`/invites/${id}/decline`),
};