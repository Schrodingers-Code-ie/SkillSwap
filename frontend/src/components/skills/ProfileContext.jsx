import { useCallback, useEffect, useState } from 'react';
import { getMe, getProfile } from '../../api/skills';
import { ProfileContext } from './profile-context';

export function ProfileProvider({ children }) {
	const [profile, setProfile] = useState(null);
	const [error, setError] = useState('');
	const [loading, setLoading] = useState(true);

	// used by the skills pop-up after every add or remove
	const reload = useCallback(async () => {
		const me = await getMe();
		setProfile(await getProfile(me.id));
	}, []);

	// first load: state is only set after the requests finish
	useEffect(() => {
		let active = true;
		(async () => {
			try {
				const me = await getMe();
				const data = await getProfile(me.id);
				if (active) setProfile(data);
			} catch (e) {
				if (active) setError(e.message);
			} finally {
				if (active) setLoading(false);
			}
		})();
		return () => {
			active = false;
		};
	}, []);

	// matching pairs teach with learn, so the user needs at least one of each
	const missingSkills =
		!!profile &&
		(profile.teachSkills.length === 0 || profile.learnSkills.length === 0);

	return (
		<ProfileContext.Provider
			value={{ profile, error, loading, reload, missingSkills }}
		>
			{children}
		</ProfileContext.Provider>
	);
}
