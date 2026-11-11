import { useEffect, useRef, useState } from 'react';
import { useLoaderData } from 'react-router';
import { api } from '../api/client.js';
import FriendsList from '../components/chat/FriendsList';
import Conversation from '../components/chat/Conversation';
import ProfilePanel from '../components/chat/ProfilePanel';

const POLL_MS = 5000; // how often the chat list is refreshed

// Figma: Chat page. Friend list on the left, messages in the middle, profile on the right
export default function Chat() {
	// initialChats: from GET /api/chats in chatLoader, already sorted newest first
	const { myProfile, chats: initialChats } = useLoaderData();
	// The list on screen. Starts with what the loader fetched, then each poll replaces it
	const [chats, setChats] = useState(initialChats);

	const [selectedId, setSelectedId] = useState(
		initialChats[0]?.userId ?? null,
	);
	const selected = chats.find((chat) => chat.userId === selectedId);

	// Refresh the chat list every POLL_MS while the Chat page is open
	useEffect(() => {
		let cancelled = false;

		async function refresh() {
			try {
				const fresh = await api.get('/chats');
				if (cancelled) return; // page closed while waiting: ignore the late answer
				setChats(fresh);
				// If nothing was selected (list was empty), open the first chat that appears
				setSelectedId((current) => current ?? fresh[0]?.userId ?? null);
			} catch {
				// Keep showing the last list; the next poll tries again
			}
		}

		const timer = setInterval(refresh, POLL_MS);

		return () => {
			cancelled = true;
			clearInterval(timer);
		};
	}, []);

	// Profiles (with skills) of friends you've opened, by userId: { 2: {...}, 3: {...} }
	// /api/chats has no skills, so each profile is loaded the first time you select that friend
	const [profiles, setProfiles] = useState({});
	// userIds already requested, so each profile is fetched only once
	const requested = useRef(new Set());

	// Load the selected friend's profile if it hasn't been requested yet.
	// Depends only on selectedId, so the 5-second poll never fetches profiles.
	useEffect(() => {
		if (selectedId === null || requested.current.has(selectedId)) return;
		const id = selectedId;
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
	}, [selectedId]);

	// Called by Conversation after you send a message, so that friend jumps to
	// the top with your text as the preview straight away, without waiting for the next poll
	function handleSend(message) {
		setChats((current) => {
			const friend = current.find(
				(chat) => chat.userId === message.receiverId,
			);
			if (!friend) return current; // not in the list (shouldn't happen): change nothing
			const others = current.filter(
				(chat) => chat.userId !== message.receiverId,
			);
			return [{ ...friend, lastMessage: message }, ...others];
		});
	}

	return (
		<div className="grid grid-cols-1 grid-rows-[auto_75dvh_auto] gap-6 px-4 pb-6 font-main [grid-template-areas:'list'_'chat'_'panel'] md:h-[calc(100dvh-72px)] md:grid-cols-[280px_minmax(0,1fr)] md:grid-rows-[minmax(12rem,1fr)_minmax(0,auto)] md:[grid-template-areas:'list_chat'_'panel_chat'] lg:pl-[52px] lg:pr-[52px] xl:grid-cols-[320px_minmax(0,1fr)_320px] xl:grid-rows-1 xl:[grid-template-areas:'list_chat_panel']">
			{/* Friend List on the left */}
			<FriendsList
				friends={chats}
				selectedId={selectedId}
				onSelect={setSelectedId}
				className="[grid-area:list] max-h-[45dvh] md:max-h-none"
			/>

			{/* Messages with the selected friend in the middle */}
			{selected ? (
				// A different key defines different component, so no reusing it, thus previous conversation's polling stops
				<Conversation
					key={selected.userId}
					friend={selected}
					onSend={handleSend}
					className="[grid-area:chat]"
				/>
			) : (
				<section className="flex items-center justify-center rounded-container-card border border-container-border bg-card-bg p-6 text-sm text-subtitle shadow-container [grid-area:chat]">
					Your chats will show here once you have a connection.
				</section>
			)}

			{/* Profile panel on the right */}
			{selected ? (
				<ProfilePanel
					friend={selected}
					profile={profiles[selected.userId]}
					myProfile={myProfile}
					className="[grid-area:panel] min-h-0 overflow-y-auto scrollbar-soft"
				/>
			) : (
				<aside className="[grid-area:panel]" />
			)}
		</div>
	);
}
