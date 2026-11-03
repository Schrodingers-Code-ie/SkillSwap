import { useState } from 'react';
import { useLoaderData } from 'react-router';
import FriendsList from '../components/chat/FriendsList';
import Conversation from '../components/chat/Conversation';
import ProfilePanel from '../components/chat/ProfilePanel';

// Figma: Chat page. Friend list on the left, messages in the middle, profile on the right
export default function Chat() {
	const { myProfile, friends } = useLoaderData();

	const [selectedId, setSelectedId] = useState(friends[0]?.userId ?? null);
	const selected = friends.find((friend) => friend.userId === selectedId);

	return (
		<div className="grid grid-cols-1 grid-rows-[auto_75dvh_auto] gap-6 px-4 pb-6 font-main [grid-template-areas:'list'_'chat'_'panel'] md:h-[calc(100dvh-72px)] md:grid-cols-[280px_minmax(0,1fr)] md:grid-rows-[minmax(12rem,1fr)_minmax(0,auto)] md:[grid-template-areas:'list_chat'_'panel_chat'] lg:pl-[52px] lg:pr-[52px] xl:grid-cols-[320px_minmax(0,1fr)_320px] xl:grid-rows-1 xl:[grid-template-areas:'list_chat_panel']">
			{/* Friend List on the left */}
			<FriendsList
				friends={friends}
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
					myProfile={myProfile}
					className="[grid-area:panel] min-h-0 overflow-y-auto scrollbar-soft"
				/>
			) : (
				<aside className="[grid-area:panel]" />
			)}
		</div>
	);
}
