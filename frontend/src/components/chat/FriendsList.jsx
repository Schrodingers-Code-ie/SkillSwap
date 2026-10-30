import { useState } from 'react';
import Profile from '../ui/Profile';
import searchIcon from '../../assets/search.svg';

// Left column of the Chat page
// friends: profiles from the Chat page loader
// selectedId: userId of the open chat, decides which row gets the highlight
// onSelect: called with a userId when a friend is clicked
export default function FriendsList({
	friends,
	selectedId,
	onSelect,
	className = '',
}) {
	const [query, setQuery] = useState('');
	const search = query.trim().toLowerCase();
	const shown = friends.filter((friend) =>
		friend.displayName.toLowerCase().includes(search),
	);

	return (
		<aside className={`flex min-h-0 flex-col ${className}`}>
			<div className="flex shrink-0 items-baseline justify-between px-1.5">
				<h1 className="text-2xl uppercase text-title">Chat</h1>
				<p className="text-xs text-subtitle">
					{friends.length}{' '}
					{friends.length === 1 ? 'friend' : 'friends'}
				</p>
			</div>

			{/* Figma: from Navigation section Search Bar */}
			<label className="mt-3 flex shrink-0 h-[42px] items-center gap-2.5 rounded-full border border-transparent bg-nav-bg px-4 transition-colors focus-within:border-active-input-border">
				<img src={searchIcon} alt="" className="size-4 shrink-0" />
				<input
					type="search"
					value={query}
					onChange={(e) => setQuery(e.target.value)}
					placeholder="Search friends"
					aria-label="Search friends"
					className="w-full bg-transparent text-sm text-strong-text outline-none placeholder:text-input-placeholder"
				/>
			</label>

			<ul className="mt-3 flex max-h-[22dvh] min-h-0 flex-col gap-1 overflow-y-auto scrollbar-soft pr-3 md:max-h-none md:pr-1">
				{shown.map((friend) => {
					const active = friend.userId === selectedId;
					return (
						<li key={friend.userId}>
							<button
								type="button"
								onClick={() => onSelect(friend.userId)}
								aria-current={active || undefined}
								className={`flex w-full cursor-pointer items-center gap-3 rounded-card border p-2 text-left transition-colors duration-200 ${
									active
										? 'border-container-border bg-card-bg shadow-nav-tab'
										: 'border-transparent hover:bg-card-bg/60'
								}`}
							>
								<Profile name={friend.displayName} />
								<span className="truncate text-sm font-g-medium text-strong-text">
									{friend.displayName}
								</span>
							</button>
						</li>
					);
				})}

				{friends.length === 0 && (
					<li className="px-2 py-4 text-sm text-subtitle">
						No connections yet. You can chat once someone accepts
						your swap request.
					</li>
				)}
				{friends.length > 0 && shown.length === 0 && (
					<li className="px-2 py-4 text-sm text-subtitle">
						No friends match "{query}".
					</li>
				)}
			</ul>
		</aside>
	);
}
