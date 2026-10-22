// React Router error boundary
import { Link, isRouteErrorResponse, useRouteError } from 'react-router';

export default function ErrorPage() {
	const error = useRouteError();

	if (isRouteErrorResponse(error)) {
		return (
			<div>
				<h1>{error.status}</h1>
				<p>{error.statusText}</p>
				<Link to="/login">Go to Login</Link>
			</div>
		);
	}

	// Errors from api/client.js carry the message from the backend response
	return (
		<div>
			<h1>Something went wrong</h1>
			<p>{error?.message || 'Please try again.'}</p>
			<Link to="/login">Go to Login</Link>
		</div>
	);
}
