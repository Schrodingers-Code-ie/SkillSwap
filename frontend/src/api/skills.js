async function request(path, options = {}) {
	const res = await fetch(path, {
		credentials: 'include',
		headers: { 'Content-Type': 'application/json' },
		...options,
	});
	const text = await res.text();
	const data = text ? JSON.parse(text) : null;
	if (!res.ok) {
		const error = new Error(data?.message ?? 'Something went wrong');
		error.status = res.status;
		throw error;
	}
	return data;
}

export const getSkills = () => request('/api/skills');
export const getMe = () => request('/api/auth/me');
export const getProfile = (id) => request(`/api/users/${id}/profile`);

export const addTeachSkill = (skillId, proficiency) =>
	request('/api/me/teach-skills', {
		method: 'POST',
		body: JSON.stringify({ skillId, proficiency }),
	});
export const addLearnSkill = (skillId) =>
	request('/api/me/learn-skills', {
		method: 'POST',
		body: JSON.stringify({ skillId }),
	});
export const removeTeachSkill = (skillId) =>
	request(`/api/me/teach-skills/${skillId}`, { method: 'DELETE' });
export const removeLearnSkill = (skillId) =>
	request(`/api/me/learn-skills/${skillId}`, { method: 'DELETE' });
