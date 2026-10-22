import { redirect } from 'react-router';

import { api } from '../api/client';

export async function homeLoader() {
	// Check authentication with Spring Boot.
	// If logged in, redirect to /matches.
	// If not logged in, redirect to /login.

	// Loader temprorary redirects to /login
	return redirect('/login');
}

export async function protectedLoader() {
	// Check authentication with Spring Boot.
	// If the user is not logged in, redirect to /login.

	return null;
}

export async function friendsLoader() {
	// Runs before the Friends page renders, the page reads it with useLoaderData().
	// Returns a list like [{ userId: 2 }, { userId: 5 }].
	// If the request fails, the error goes to the route's ErrorBoundary.
	return api.get('/connections');
}
