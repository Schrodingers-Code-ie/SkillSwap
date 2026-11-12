import SkillBubble from '../ui/SkillBubble.jsx';
import RequestButton from './RequestButton.jsx';
import { useState } from 'react';

// selected: this card is the one shown in the side profile panel
// onSelect: clicking anywhere on the card opens this person in the panel
export default function MatchCard({
	match,
	requestId,
	onRequestChange,
	selected,
	onSelect,
}) {
	const [error, setError] = useState(null);

	function getInitials(name) {
		return name
			.split(' ')
			.map((part) => part[0])
			.join('')
			.slice(0, 2)
			.toUpperCase();
	}

	return (
		<>
			{error && (
				<p
					role="alert"
					className="rounded-input bg-error-box-bg px-3 py-3 font-main text-sm leading-5 text-error-box-text"
				>
					{error}
				</p>
			)}
			<div
				onClick={onSelect}
				className={`match-card grid grid-cols-[auto_1fr] items-center gap-x-4 gap-y-3 md:flex md:gap-4 rounded-card border bg-card-bg px-5 py-4 cursor-pointer ${selected ? 'border-title ring-1 ring-title' : 'border-container-border'}`}
			>
				<div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-full border-3 border-card-bg bg-avatar-bg text-lg font-main font-g-medium text-dark-button-text shadow-avatar">
					{getInitials(match.matchPersonName)}
				</div>

				<div className="contents md:block md:min-w-0 md:flex-1">
					<div className="flex min-w-0 flex-wrap items-center gap-x-4">
						<span className="font-main font-g-medium text-title">
							{match.matchPersonName}
						</span>
						<span className="text-sm font-main font-g-regular text-main-content">
							@{match.matchPersonUsername}
						</span>
					</div>

					<div className="col-span-2 md:col-span-1 my-1 min-[701px]:my-2 flex flex-wrap items-center gap-2">
						{match.skillsTheyCanTeachMe.slice(0, 2).map((skill) => (
							<SkillBubble key={skill} color="green">
								{skill}
							</SkillBubble>
						))}
						{/* If there's more than 2 bubbles, indicate it but don't show all of them */}
						{match.skillsTheyCanTeachMe.length > 2 && (
							<SkillBubble color="green">
								+{match.skillsTheyCanTeachMe.length - 2}
							</SkillBubble>
						)}

						{/* Showing the divider only if both sides have bubbles - otherwise don't show it */}
						{match.skillsTheyCanTeachMe.length > 0 &&
							match.skillsICanTeachThem.length > 0 && (
								<span className="mx-1 h-4 w-px bg-border" />
							)}

						{match.skillsICanTeachThem.slice(0, 2).map((skill) => (
							<SkillBubble key={skill} color="yellow">
								{skill}
							</SkillBubble>
						))}
						{match.skillsICanTeachThem.length > 2 && (
							<SkillBubble color="yellow">
								+{match.skillsICanTeachThem.length - 2}
							</SkillBubble>
						)}
					</div>
				</div>

				<RequestButton
					personId={match.matchPersonId}
					requestId={requestId}
					onRequestChange={onRequestChange}
					onError={setError}
					className="col-span-2 md:col-span-1 justify-self-start w-33 md:w-43 py-2 md:py-3"
				/>
			</div>
		</>
	);
}
