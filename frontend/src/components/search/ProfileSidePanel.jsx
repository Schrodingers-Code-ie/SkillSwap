import { api } from '../../api/client.js';
import { useEffect, useState } from 'react';
import Profile from '../ui/Profile.jsx';
import RequestButton from './RequestButton.jsx';
import { levelLabel } from '../skills/levels.js';

// Figma: Search page, "Profile preview" on the right
// match: the selected match card, the username comes from here (the profile response has no username)
//   null when there's no one to show, then the panel shows an empty message
// myLearnSkills: the logged-in user's learn skills, for the "You want this" tag
// requestId, onRequestChange: same as on the card, so both "Add" buttons show the same state
export default function ProfileSidePanel({
	match,
	myLearnSkills,
	requestId,
	onRequestChange,
	className = '',
}) {
	// Remember which person the result is for, so we never show the previous person's profile
	const [loaded, setLoaded] = useState({
		id: null,
		profile: null,
		error: null,
	});
	const [buttonError, setButtonError] = useState(null);

	const personId = match?.matchPersonId ?? null;

	// Load the selected person's profile every time a different card is clicked
	useEffect(() => {
		if (personId === null) return; // No one to show, nothing to load
		let ignore = false;

		api.get(`/users/${personId}/profile`)
			.then((profile) => {
				if (!ignore) setLoaded({ id: personId, profile, error: null });
			})
			.catch((err) => {
				if (!ignore)
					setLoaded({
						id: personId,
						profile: null,
						error: err.message,
					});
			});

		return () => {
			ignore = true; // A newer card was clicked, so ignore this answer
		};
	}, [personId]);

	const isCurrent = loaded.id === personId;
	const profile = isCurrent ? loaded.profile : null;

	// Skills I want to learn, to tag their teach skills with "You want this"
	const myLearnIds = new Set(myLearnSkills.map((skill) => skill.skillId));

	return (
		// Same height for every state (profile, loading, no one), so the page doesn't jump
		// flex comes from className (hidden on small screens), it centres the Loading and empty text
		<aside
			className={`side-profile-section sticky top-8 h-[calc(100vh-72px-64px)] w-[433px] shrink-0 flex-col self-start overflow-y-auto rounded-card border border-container-border bg-linear-to-b from-(--raw-light-pink) to-card-bg to-40% px-7 pt-9 pb-10 shadow-container ${className}`}
		>
			{personId === null && (
				<p className="m-auto text-center font-main text-sm text-subtitle">
					No one to show currently
				</p>
			)}

			{personId !== null && !isCurrent && (
				<p className="m-auto text-center font-main text-sm text-subtitle">
					Loading…
				</p>
			)}

			{isCurrent && loaded.error && (
				<p
					role="alert"
					className="rounded-input bg-error-box-bg px-3 py-3 font-main text-sm leading-5 text-error-box-text"
				>
					{loaded.error}
				</p>
			)}

			{profile && (
				<>
					<div className="flex flex-col items-center text-center">
						<Profile name={profile.displayName} size="lg" />
						<h2 className="mt-3.5 font-main text-2xl font-g-regular tracking-[-0.32px] text-strong-text">
							{profile.displayName}
						</h2>
						<p className="mt-1 font-main text-sm text-subtitle">
							@{match.matchPersonUsername}
						</p>
						{profile.bio && (
							<p className="mt-4 max-w-[331px] font-main text-sm leading-[19px] text-title">
								{profile.bio}
							</p>
						)}

						{buttonError && (
							<p
								role="alert"
								className="mt-4 rounded-input bg-error-box-bg px-3 py-3 font-main text-sm leading-5 text-error-box-text"
							>
								{buttonError}
							</p>
						)}
						<RequestButton
							personId={personId}
							requestId={requestId}
							onRequestChange={onRequestChange}
							onError={setButtonError}
							className="mt-4 h-[39px] w-45 text-sm"
						/>
					</div>

					{profile.teachSkills.length > 0 && (
						<section className="mt-8">
							<h3 className="font-main text-[10px] font-g-semibold uppercase tracking-[0.88px] text-subtitle">
								Can teach
							</h3>
							<ul className="mt-2.5 flex flex-col gap-2.5">
								{profile.teachSkills.map((skill) => (
									<li
										key={skill.skillId}
										className="flex items-center justify-between gap-2 rounded-card border border-green-bubble/10 bg-green-bubble-bg/40 px-4 py-3.5"
									>
										<span className="font-main font-g-medium text-strong-text">
											{skill.name}
										</span>
										<span className="flex gap-2">
											{myLearnIds.has(skill.skillId) && (
												<span className="rounded-full bg-green-bubble-bg px-2 py-0.5 font-main text-[10px] font-g-medium uppercase tracking-[0.6px] text-green-bubble">
													You want this
												</span>
											)}
											<span className="rounded-full bg-grey-bubble-bg px-1.5 py-0.5 font-main text-[10px] font-g-medium uppercase tracking-[0.6px] text-subtitle">
												{levelLabel(skill.proficiency)}
											</span>
										</span>
									</li>
								))}
							</ul>
						</section>
					)}

					{profile.learnSkills.length > 0 && (
						<section className="mt-6">
							<h3 className="font-main text-[10px] font-g-semibold uppercase tracking-[0.88px] text-subtitle">
								Wants to learn
							</h3>
							<ul className="mt-2.5 flex flex-wrap gap-2">
								{profile.learnSkills.map((skill) => (
									<li
										key={skill.skillId}
										className="flex h-[29px] items-center rounded-full bg-orange-bubble-bg px-[13px] font-main text-sm font-g-medium text-orange-bubble"
									>
										{skill.name}
									</li>
								))}
							</ul>
						</section>
					)}
				</>
			)}
		</aside>
	);
}
