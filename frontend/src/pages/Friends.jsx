import { useLoaderData } from 'react-router';

import Card from '../components/ui/Card';

// Connections come from friendsLoader in router/loaders.jsx.
// Each friend comes with their display name ("User 5" only if it's missing).
// Layout follows the Search page in Figma: 50px from the left like the logo, 660px cards.
export default function Friends() {
	const friends = useLoaderData();

	return (
		<section className="px-4 pt-5 font-main lg:px-[50px]">
			<h1 className="text-4xl text-title font-g-medium">Friends</h1>
			<p className="mt-2 text-sm text-subtitle">
				{friends.length === 0
					? 'No friends yet'
					: `${friends.length} ${friends.length === 1 ? 'friend' : 'friends'}`}
			</p>

			{friends.length > 0 && (
				<ul className="mt-6 flex max-w-[660px] flex-col gap-3">
					{friends.map((friend) => (
						<li key={friend.userId}>
							<FriendCard
								userId={friend.userId}
								name={friend.displayName}
							/>
						</li>
					))}
				</ul>
			)}
		</section>
	);
}

// Same style as the match cards on the Search page in Figma
function FriendCard({ userId, name }) {
	return (
		<Card className="w-full flex items-center gap-4">
			<div className="size-[50px] shrink-0 rounded-full bg-dark-button-bg text-dark-button-text flex items-center justify-center font-g-medium">
				{name?.[0]?.toUpperCase() ?? '?'}
			</div>
			<span className="text-title font-g-medium">
				{name ?? `User ${userId}`}
			</span>
		</Card>
	);
}
