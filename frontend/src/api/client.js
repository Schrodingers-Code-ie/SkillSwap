// Shared helper for talking to the backend.
// Use it everywhere instead of calling fetch() directly,
//   const skills = await api.get('/skills')
//   await api.post('/friends', { receiverId: 5 })
//
// For file uploads, pass FormData and it is sent as-is:
//   const form = new FormData()
//   form.append('file', fileInput.files[0])
//   await api.post('/users/me/avatar', form)
//
// Paths are relative to /api, and Vite forwards them to the backend.

async function request(path, options = {}) {
	const response = await fetch(`/api${path}`, options);

	// Some responses (e.g. 204 No Content) have no body.
	const body = await response.json().catch(() => null);

	if (!response.ok) {
		const error = new Error(
			body?.message ?? `Request failed with status ${response.status}`,
		);
		error.status = response.status;
		error.body = body;
		throw error;
	}

	return body;
}

// Turns data into the right request body.
// FormData (files) is sent as-is so the browser can set the correct headers.
// Everything else is sent as JSON.
function withBody(method, data) {
	if (data instanceof FormData) {
		return { method, body: data };
	}
	return {
		method,
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(data),
	};
}

export const api = {
	get: (path) => request(path),
	post: (path, data) => request(path, withBody('POST', data)),
	put: (path, data) => request(path, withBody('PUT', data)),
	delete: (path) => request(path, { method: 'DELETE' }),
};
