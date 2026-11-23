import { Navigate, Outlet, useLocation } from 'react-router';
import Spinner from '../Spinner';
import { useProfile } from './profile-context';

// Renders the page you asked for, or sends you to /profile while either skill
// list is still empty, so new users finish setup before using the app.
export default function SkillsGate() {
	const { loading, missingSkills } = useProfile();
	const { pathname } = useLocation();

	if (loading) {
		return (
			<div className="flex justify-center p-10">
				<Spinner />
			</div>
		);
	}
	if (missingSkills && pathname !== '/profile') {
		return <Navigate to="/profile" replace />;
	}
	return <Outlet />;
}
