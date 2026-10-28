import { useState } from 'react';
import { useLoaderData } from 'react-router';
import { api } from '../api/client.js';
import FriendsList from '../components/chat/FriendsList';

export async function loader() {
	const connections = await api.get('/connections');
	// Two requests: GET /api/connections gives user ids, and GET /api/users/{id}/profile gives usernames
	const friends = await Promise.all(
		connections.map((connection) =>
			api.get(`/users/${connection.userId}/profile`).catch(() => ({
				// For now we have default placeholder, so one broken profile shouldn't break the whole page
				userId: connection.userId,
				displayName: 'Unknown user',
				teachSkills: [],
				learnSkills: [],
			})),
		),
	);
	return { friends };
}

// Figma: Chat page. Friend list on the left, messages in the middle, profile on the right
export default function Chat() {
	const { friends } = useLoaderData();
	const [selectedId, setSelectedId] = useState(friends[0]?.userId ?? null);
	const selected = friends.find((friend) => friend.userId === selectedId);

	return (
		<div className="grid h-[calc(100dvh-72px)] grid-cols-1 grid-rows-[auto_minmax(0,1fr)] gap-6 px-4 pb-6 font-main md:grid-cols-[250px_minmax(0,1fr)] md:grid-rows-1 lg:grid-cols-[250px_minmax(0,1fr)_240px] lg:pl-[50px] lg:pr-[52px]">
			{/* Friend List on the left */}
			<FriendsList
				friends={friends}
				selectedId={selectedId}
				onSelect={setSelectedId}
			/>

			{/* Step 5: Messages with the selected friend in the middle */}
			<section className="rounded-container-card border border-container-border bg-card-bg p-6 shadow-container">
				{selected
					? `Chat with ${selected.displayName}`
					: 'Pick a friend'}
			</section>

			{/* Step 7: Profile panel on the right */}
			<aside className="hidden lg:block" />
		</div>
	);
}
