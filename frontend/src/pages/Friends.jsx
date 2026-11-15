import { useEffect, useRef, useState } from 'react';
import { Link, useLoaderData } from 'react-router';

import { api } from '../api/client.js';
import ProfilePanel from '../components/chat/ProfilePanel';
import PersonCard from '../components/ui/PersonCard.jsx';

// no Figma frame, it uses the Search page cards and panel
export default function Friends() {
	const { myProfile, friends } = useLoaderData();

	// first friend is open by default
	const [selectedId, setSelectedId] = useState(friends[0]?.userId ?? null);
	const selected = friends.find((friend) => friend.userId === selectedId);

	// profiles we already loaded, so each one is fetched only once
	const [profiles, setProfiles] = useState({});
	const requested = useRef(new Set());

	useEffect(() => {
		if (selectedId === null || requested.current.has(selectedId)) return;
		const id = selectedId;
		requested.current.add(id);

		async function loadProfile() {
			try {
				const profile = await api.get(`/users/${id}/profile`);
				setProfiles((current) => ({ ...current, [id]: profile }));
			} catch {
				requested.current.delete(id); // try again on next click
				setProfiles((current) => ({
					...current,
					[id]: { failed: true },
				}));
			}
		}
		loadProfile();
	}, [selectedId]);

	return (
		<div className="friends-page-container px-5 min-[701px]:px-12 py-4 min-[701px]:py-8 flex justify-center">
			<section className="w-full min-[701px]:mx-10 max-w-3xl">
				<div className="mb-4">
					<h1 className="text-title text-[28px] min-[701px]:text-3xl font-main font-g-medium">
						Friends
					</h1>
					<p className="text-subtitle text-base font-main font-g-regular py-3">
						{friends.length === 0
							? 'No friends yet'
							: `${friends.length} ${friends.length === 1 ? 'friend' : 'friends'}`}
					</p>
				</div>

				{friends.length > 0 && (
					<ul className="flex flex-col gap-4">
						{friends.map((friend) => (
							<li key={friend.userId}>
								<PersonCard
									name={nameOf(friend)}
									username={friend.username}
									skillsTheyCanTeachMe={
										friend.skillsTheyCanTeachMe
									}
									skillsICanTeachThem={
										friend.skillsICanTeachThem
									}
									selected={friend === selected}
									onSelect={() =>
										setSelectedId(friend.userId)
									}
									action={
										<Link
											to="/chat"
											onClick={(event) => {
												event.stopPropagation(); // don't open the panel too
											}}
											className="col-span-2 md:col-span-1 justify-self-start w-33 md:w-43 py-2 md:py-3 flex shrink-0 items-center justify-center rounded-full bg-dark-button-bg font-main font-g-medium text-dark-button-text transition hover:shadow-dark-button-hover active:bg-dark-button-pressed-bg"
										>
											Chat
										</Link>
									}
								/>
							</li>
						))}
					</ul>
				)}
			</section>

			{/* same box as on Search, only on 1440px+ */}
			{selected && (
				<div className="sticky top-8 hidden h-[calc(100vh-72px-64px)] w-[433px] shrink-0 self-start overflow-y-auto rounded-card border border-container-border bg-linear-to-b from-(--raw-light-pink) to-card-bg to-40% px-7 pt-9 pb-10 shadow-container min-[1440px]:block">
					<ProfilePanel
						friend={{ ...selected, displayName: nameOf(selected) }}
						profile={profiles[selected.userId]}
						myProfile={myProfile}
					/>
				</div>
			)}
		</div>
	);
}

// "User 5" if they have no profile
function nameOf(friend) {
	return friend.displayName ?? `User ${friend.userId}`;
}
