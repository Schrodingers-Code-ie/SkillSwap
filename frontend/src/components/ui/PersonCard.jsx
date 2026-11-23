import SkillBubble from './SkillBubble.jsx';

// The person card from the Search page, Friends uses it too
// action is the card's button: Add on Search, Chat on Friends
export default function PersonCard({
	name,
	username,
	skillsTheyCanTeachMe,
	skillsICanTeachThem,
	selected,
	onSelect,
	action,
}) {
	// "Ann Smith" -> "AS" for the avatar circle
	function getInitials(name) {
		return name
			.split(' ')
			.map((part) => part[0])
			.join('')
			.slice(0, 2)
			.toUpperCase();
	}

	return (
		<div
			onClick={onSelect}
			className={`match-card grid grid-cols-[auto_1fr] items-center gap-x-4 gap-y-3 md:flex md:gap-4 rounded-card border bg-card-bg px-5 py-4 cursor-pointer ${selected ? 'border-title ring-1 ring-title' : 'border-container-border'}`}
		>
			<div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-full border-3 border-card-bg bg-avatar-bg text-lg font-main font-g-medium text-dark-button-text shadow-avatar">
				{getInitials(name)}
			</div>

			<div className="contents md:block md:min-w-0 md:flex-1">
				<div className="flex min-w-0 flex-wrap items-center gap-x-4">
					<span className="font-main font-g-medium text-title">
						{name}
					</span>
					{username && (
						<span className="text-sm font-main font-g-regular text-main-content">
							@{username}
						</span>
					)}
				</div>

				<div className="col-span-2 md:col-span-1 my-1 min-sm:my-2 flex flex-wrap items-center gap-2">
					{skillsTheyCanTeachMe.slice(0, 2).map((skill) => (
						<SkillBubble key={skill} color="green">
							{skill}
						</SkillBubble>
					))}
					{/* If there are more than 2 bubbles, show +N instead of all of them */}
					{skillsTheyCanTeachMe.length > 2 && (
						<SkillBubble color="green">
							+{skillsTheyCanTeachMe.length - 2}
						</SkillBubble>
					)}

					{/* Only show the divider when both sides have bubbles */}
					{skillsTheyCanTeachMe.length > 0 &&
						skillsICanTeachThem.length > 0 && (
							<span className="mx-1 h-4 w-px bg-border" />
						)}

					{skillsICanTeachThem.slice(0, 2).map((skill) => (
						<SkillBubble key={skill} color="yellow">
							{skill}
						</SkillBubble>
					))}
					{skillsICanTeachThem.length > 2 && (
						<SkillBubble color="yellow">
							+{skillsICanTeachThem.length - 2}
						</SkillBubble>
					)}
				</div>
			</div>

			{action}
		</div>
	);
}
