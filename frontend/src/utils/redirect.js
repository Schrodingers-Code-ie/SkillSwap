// Helpers for sending the user to /login and back to where they were.
// The page to come back to travels in the URL: /login?redirectTo=/chat

// Builds the /login link that remembers the page to come back to
export function loginPath(returnTo) {
	if (!returnTo || returnTo === '/') return '/login';
	return `/login?redirectTo=${encodeURIComponent(returnTo)}`;
}

// Reads ?redirectTo= safely. Only paths inside our own app are allowed,
// so a link like /login?redirectTo=https://evil.example can't send people away.
export function safeRedirect(value, fallback = '/matches') {
	// "//x" and "/\x" both point to another site in the browser
	if (!value || !value.startsWith('/') || /^\/[/\\]/.test(value)) {
		return fallback;
	}
	if (value.startsWith('/login') || value.startsWith('/signup')) {
		return fallback;
	}
	return value;
}
