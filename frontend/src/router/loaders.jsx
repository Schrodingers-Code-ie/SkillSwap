import { redirect } from 'react-router';
import { api } from '../api/client.js';

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
// Logged out: go to /login. Logged in: the page opens and can read the user with useLoaderData.
export async function protectedLoader() {
	const user = await getCurrentUser();
	if (!user) return redirect('/login');
	return user;
}
