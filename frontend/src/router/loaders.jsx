import { redirect } from 'react-router';
import { api } from '../api/client.js';
import { loginPath } from '../utils/redirect.js';

// Asks the backend who is logged in. The browser sends the session cookie by itself.
// Returns the user, or null when nobody is logged in (the backend answers 401).
// Any other error (e.g. backend down) is thrown, so the ErrorPage shows it.
async function getCurrentUser() {
	try {
		return await api.get('/auth/me');
	} catch (err) {
		if (err.status === 401) return null;
		throw err;
	}
}

// "/" has no page of its own, it just sends you to the right place
export async function homeLoader() {
	const user = await getCurrentUser();
	return redirect(user ? '/matches' : '/login');
}

// Runs before every logged-in page (see router.jsx).
// Logged out: go to /login, which brings you back here after logging in.
// Logged in: the page opens and can read the user with useLoaderData.
export async function protectedLoader({ request }) {
	const user = await getCurrentUser();
	if (!user) {
		const { pathname, search } = new URL(request.url);
		return redirect(loginPath(pathname + search));
	}
	return user;
}

// Runs before /login and /signup (see router.jsx).
// Logged in: go to /matches. Logged out: the form shows as usual.
export async function guestLoader() {
	const user = await getCurrentUser();
	if (user) return redirect('/matches');
	return null;
}

export async function chatLoader() {
	const [me, chats] = await Promise.all([
		api.get('/auth/me'),
		api.get('/chats'),
	]);
	// Own profile is needed for the skill matching in the profile panel
	const myProfile = await api.get(`/users/${me.id}/profile`);

	return { myProfile, chats };
}

export async function friendsLoader() {
	// Runs before the Friends page renders, the page reads it with useLoaderData().
	// If a request fails, the error goes to the route's ErrorBoundary.
	const [me, friends] = await Promise.all([
		api.get('/auth/me'),
		api.get('/connections'),
	]);
	// my profile, the panel needs it for the skills
	const myProfile = await api.get(`/users/${me.id}/profile`);

	return { myProfile, friends };
}
