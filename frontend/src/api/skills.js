import { api } from './client';

export const getSkills = () => api.get('/skills');
export const getProfile = (id) => api.get(`/users/${id}/profile`);

export const addTeachSkill = (skillId, proficiency) =>
	api.post('/me/teach-skills', { skillId, proficiency });
export const addLearnSkill = (skillId) =>
	api.post('/me/learn-skills', { skillId });
export const removeTeachSkill = (skillId) =>
	api.delete(`/me/teach-skills/${skillId}`);
export const removeLearnSkill = (skillId) =>
	api.delete(`/me/learn-skills/${skillId}`);
export const updateProfile = (displayName, bio) =>
	api.put('/me/profile', { displayName, bio });
