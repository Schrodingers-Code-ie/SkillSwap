import { useLoaderData } from 'react-router';

// Connections come from friendsLoader in router/loaders.jsx.
// The API only returns user ids for now, so we show "User 5".
export default function Friends() {
	const friends = useLoaderData();

	return (
		<section className="p-8 font-main">
			<h1 className="text-3xl text-title font-g-medium">Friends</h1>

			{friends.length === 0 ? (
				<p className="mt-2 text-subtitle">No friends yet</p>
			) : (
				<ul className="mt-6 flex flex-col gap-3 max-w-xl">
					{friends.map((friend) => (
						<li key={friend.userId}>
							<FriendCard userId={friend.userId} />
						</li>
					))}
				</ul>
			)}
		</section>
	);
}

// Same style as the match cards on the Search page in Figma
function FriendCard({ userId }) {
	return (
		<div className="flex items-center gap-4 p-4 bg-card-bg border border-card-border rounded-card">
			<div className="w-12 h-12 shrink-0 rounded-full bg-dark-button-bg text-dark-button-text flex items-center justify-center font-g-medium">
				U{userId}
			</div>
			<span className="text-title font-g-medium">User {userId}</span>
		</div>
	);
}
