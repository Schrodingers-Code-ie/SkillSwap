import SkillBubble from './ui/SkillBubble.jsx';
import { api } from '../api/client.js';
// import { useState } from 'react';

export default function MatchCard({ match }) {
	// 3 states of the same buttom to show different text: idle = "Add", sent = "Sent", cancel = "Cancel"
	// const [status, setStatus] = useState('idle');
	// const [requestId, setRequestId] = useState(null);
	// const [busy, setBusy] = useState(false);

	// async function handleButtonClick() {
	// 	if (busy) return; // Already doing some request

	// 	// Sent -> Cancel is just a UI step, no request sent yet
	// 	if (status === 'sent') {
	// 		setStatus('cancel');
	// 		return;
	// 	}

	// 	setBusy(true);
	// 	try {
	// 		if (status === 'idle') {
	// 			const createdSwapRequest = await api.post('/requests', {
	// 				receiverId: match.matchPersonId,
	// 			});
	// 			setRequestId(createdSwapRequest); // TODOOOOOOOOOOOOOOOOOOOOOOO - continue, check what we return from api/requests
	// 			setStatus(sent);
	// 		}
	// 	} catch (err) {
	// 		console.log(err);
	// 	}
	// }

	async function sendSwapRequest(suggestedUserId) {
		console.log(suggestedUserId);
		try {
			await api.post('/requests', {
				receiverId: suggestedUserId,
			});
		} catch (err) {
			console.log(err);
		}
	}

	function getInitials(name) {
		return name
			.split(' ')
			.map((part) => part[0])
			.join('')
			.slice(0, 2)
			.toUpperCase();
	}

	return (
		<div className="match-card flex items-center gap-4 rounded-card border border-container-border bg-card-bg px-5 py-4">
			<div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-full border-3 border-card-bg bg-avatar-bg text-lg font-main font-g-medium text-dark-button-text shadow-avatar">
				{getInitials(match.matchPersonName)}
			</div>

			<div className="min-w-0 flex-1">
				<div className="flex gap-4 items-center">
					<span className="font-main font-g-medium text-title">
						{match.matchPersonName}
					</span>
					<span className="text-sm font-main font-g-regular text-main-content">
						@{match.matchPersonUsername}
					</span>
				</div>

				<div className="mt-2 flex flex-wrap items-center gap-2">
					{match.skillsTheyCanTeachMe.map((skill) => (
						<SkillBubble key={skill} variant="green">
							{skill}
						</SkillBubble>
					))}
					<span className="mx-1 h-4 w-px bg-border" />
					{match.skillsICanTeachThem.map((skill) => (
						<SkillBubble key={skill} variant="yellow">
							{skill}
						</SkillBubble>
					))}
				</div>
			</div>

			<button
				type="button"
				className="flex shrink-0 cursor-pointer items-center gap-2 rounded-full bg-dark-button-bg px-15 py-3 font-main font-g-medium text-dark-button-text transition hover:shadow-dark-button-hover active:bg-dark-button-pressed-bg"
				onClick={() => sendSwapRequest(match.matchPersonId)}
			>
				<span className="text-lg leading-none">+</span>
				Add
			</button>
		</div>
	);
}
