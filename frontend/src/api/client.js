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
//
// A 401 means the server no longer knows our session (it timed out, or the
// backend restarted), so any 401 sends the user to /login. The exceptions are
// listed below: there a 401 is a normal answer that the caller handles itself.

import { loginPath } from '../utils/redirect.js';

// 401 here is expected: wrong password, or "nobody is logged in" for the login check
const HANDLES_401_ITSELF = ['/auth/login', '/auth/me'];

let redirectingToLogin = false;

function sessionExpired() {
	const { pathname, search } = window.location;
	const onAuthPage = pathname === '/login' || pathname === '/signup';
	if (!redirectingToLogin && !onAuthPage) {
		// Several requests can fail at once (e.g. a loader plus polling), only redirect once
		redirectingToLogin = true;
		// A full page load also clears timers and data left over from the old session
		window.location.assign(loginPath(pathname + search));
	}
	// Never settles, so the page doesn't flash an error while the browser leaves
	return new Promise(() => {});
}

function getCsrfToken() {
	const match = document.cookie.match(/(?:^|; )XSRF-TOKEN=([^;]*)/);
	return match ? decodeURIComponent(match[1]) : null;
}

async function request(path, options = {}) {
	const method = options.method ?? 'GET';
	if (method !== 'GET') {
		const token = getCsrfToken();
		if (token) {
			options = {
				...options,
				headers: { ...options.headers, 'X-XSRF-TOKEN': token },
			};
		}
	}
	const response = await fetch(`/api${path}`, options);

	if (response.status === 401 && !HANDLES_401_ITSELF.includes(path)) {
		return sessionExpired();
	}

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
