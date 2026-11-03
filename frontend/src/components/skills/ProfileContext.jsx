import { useCallback, useEffect, useState } from 'react';
import { useRouteLoaderData } from 'react-router';
import { getProfile } from '../../api/skills';
import { ProfileContext } from './profile-context';

const isMissing = (p) =>
	p.teachSkills.length === 0 || p.learnSkills.length === 0;

export function ProfileProvider({ children }) {
	const user = useRouteLoaderData('protected');
	const [profile, setProfile] = useState(null);
	const [error, setError] = useState('');
	const [loading, setLoading] = useState(true);
	const [setupOpen, setSetupOpen] = useState(false);

	const apply = useCallback((data) => {
		setProfile(data);
		if (isMissing(data)) setSetupOpen(true); // stays true until "Finish setup"
	}, []);

	const reload = useCallback(
		async () => apply(await getProfile(user.id)),
		[apply, user.id],
	);

	useEffect(() => {
		let active = true;
		getProfile(user.id)
			.then((data) => active && apply(data))
			.catch((e) => active && setError(e.message))
			.finally(() => active && setLoading(false));
		return () => {
			active = false;
		};
	}, [user.id, apply]);

	const value = {
		user,
		profile,
		error,
		loading,
		reload,
		setupOpen,
		finishSetup: () => setSetupOpen(false),
		missingSkills: !!profile && isMissing(profile),
	};

	return (
		<ProfileContext.Provider value={value}>
			{children}
		</ProfileContext.Provider>
	);
}
