import { Navigate, Outlet, useLocation } from 'react-router';
import Spinner from '../Spinner';
import { useProfile } from './profile-context';

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
