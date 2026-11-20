import {
	useEffect,
	useRef,
	useState,
	useCallback,
	useSyncExternalStore,
} from 'react';
import { Link, useLoaderData, useRevalidator } from 'react-router';

import { api } from '../api/client.js';
import ProfilePanel from '../components/chat/ProfilePanel';
import PersonCard from '../components/ui/PersonCard.jsx';
import cross_icon from '../assets/cross_icon.svg';

const MD_QUERY = '(min-width: 1280px)';

// Re-renders the component when the screen crosses the breakpoint
function useMediaQuery(query) {
	return useSyncExternalStore(
		(onChange) => {
			const mql = window.matchMedia(query);
			mql.addEventListener('change', onChange);
			return () => mql.removeEventListener('change', onChange);
		},
		() => window.matchMedia(query).matches,
	);
}

// no Figma frame, it uses the Search page cards and panel
export default function Friends() {
	const { myProfile, friends } = useLoaderData();

	const [selectedId, setSelectedId] = useState(null); // Friend the user clicked
	const [panelOpen, setPanelOpen] = useState(false); // Only matters below md, where the panel is a floating window

	const isLg = useMediaQuery(MD_QUERY);
	const floating = !isLg;

	// At 1280px and above: the clicked friend, or the first friend by default
	// Below 1280px: only the clicked friend, and only while the floating window is open
	const clickedFriend = friends.find(
		(friend) => friend.userId === selectedId,
	);
	const selected = isLg
		? (clickedFriend ?? friends[0])
		: panelOpen
			? clickedFriend
			: undefined;
	const selectedUserId = selected?.userId ?? null;

	// profiles we already loaded, so each one is fetched only once
	const [profiles, setProfiles] = useState({});
	const requested = useRef(new Set());

	useEffect(() => {
		if (selectedUserId === null || requested.current.has(selectedUserId))
			return;
		const id = selectedUserId;
		requested.current.add(id);

		async function loadProfile() {
			try {
				const profile = await api.get(`/users/${id}/profile`);
				setProfiles((current) => ({ ...current, [id]: profile }));
			} catch {
				requested.current.delete(id); // try again next time this friend is selected
				setProfiles((current) => ({
					...current,
					[id]: { failed: true },
				}));
			}
		}
		loadProfile();
	}, [selectedUserId]);

	function handleSelect(friendId) {
		setSelectedId(friendId);
		setPanelOpen(true); // Opens the floating window below md, no effect at md and above
	}

	const closePanel = useCallback(() => setPanelOpen(false), []);
	const revalidator = useRevalidator();

	function handleBlocked() {
		setSelectedId(null);
		setPanelOpen(false);
		revalidator.revalidate();
	}

	// Close the floating window with Escape
	useEffect(() => {
		if (!floating || selectedUserId === null) return;
		function onKeyDown(e) {
			if (e.key === 'Escape') closePanel();
		}
		window.addEventListener('keydown', onKeyDown);
		return () => window.removeEventListener('keydown', onKeyDown);
	}, [floating, selectedUserId, closePanel]);

	const baseClasses =
		'flex-col overflow-y-auto rounded-card border border-container-border bg-linear-to-b from-(--raw-light-pink) to-card-bg to-40% px-7 pt-9 pb-10 shadow-container';

	// Different styles for different resolutions
	const layoutClasses = floating
		? 'fixed z-50 flex top-20 bottom-4 left-4 right-4 min-[480px]:inset-0 min-[480px]:m-auto min-[480px]:w-md min-[480px]:h-[calc(100vh-72px-64px)]'
		: 'sticky top-8 flex h-[calc(100vh-72px-64px)] max-w-md w-full shrink-0 self-start';

	return (
		<div className="friends-page-container px-5 min-md:px-12 py-4 min-md:py-8 flex justify-center">
			<section className="w-full min-md:mx-10 max-w-3xl">
				<div className="mb-4">
					<h1 className="text-title text-[28px] min-md:text-3xl font-main font-g-medium">
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
									onSelect={() => handleSelect(friend.userId)}
									action={
										<Link
											to={`/chat?user=${friend.userId}`}
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

			{selected && (
				<>
					{floating && (
						<div
							className="fixed inset-0 z-40 bg-black/30"
							onClick={closePanel}
						></div>
					)}
					<aside className={`${baseClasses} ${layoutClasses}`}>
						{floating && (
							<button
								type="button"
								aria-label="Close profile"
								onClick={closePanel}
								className="sticky -top-3 z-10 -mt-6 -mr-3 mb-[-12px] flex size-8 shrink-0 cursor-pointer items-center justify-center self-end rounded-full text-subtitle hover:bg-grey-bubble-bg"
							>
								<img
									src={cross_icon}
									alt="Cross icon"
									className="w-6"
								/>
							</button>
						)}
						<ProfilePanel
							friend={{
								...selected,
								displayName: nameOf(selected),
							}}
							profile={profiles[selected.userId]}
							myProfile={myProfile}
							onBlocked={handleBlocked}
						/>
					</aside>
				</>
			)}
		</div>
	);
}

// "User 5" if they have no profile
function nameOf(friend) {
	return friend.displayName ?? `User ${friend.userId}`;
}
