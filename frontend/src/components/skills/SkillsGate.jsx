import { Navigate, Outlet, useLocation } from 'react-router';
import { useProfile } from './profile-context';

export default function SkillsGate() {
	const { loading, missingSkills } = useProfile();
	const { pathname } = useLocation();

	if (loading) return <p className="p-10">Loading…</p>;
	if (missingSkills && pathname !== '/profile') {
		return <Navigate to="/profile" replace />;
	}
	return <Outlet />;
}
