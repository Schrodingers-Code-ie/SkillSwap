import SkillBubble from './ui/SkillBubble.jsx';
import { api } from '../api/client.js';
import { useState } from 'react';
import plus_icon from '../assets/plus_icon.png';
import cross_icon from '../assets/cross_icon.png';
import tick_icon from '../assets/tick_icon.png';

export default function MatchCard({ match }) {
	// 3 states of the same buttom to show different text: idle = "Add", sent = "Sent", cancel = "Cancel"
	const [status, setStatus] = useState('idle');
	const [requestId, setRequestId] = useState(null);
	const [busy, setBusy] = useState(false);

	async function handleButtonClick() {
		if (busy) return; // Already doing some request, so that we can't spam clicks and brake the API call

		// Sent -> Cancel is just a UI step, no request sent yet
		if (status === 'sent') {
			setStatus('cancel');
			console.log('CONFIRM DELETING.');
			return;
		}

		setBusy(true);
		try {
			if (status === 'idle') {
				const createdSwapRequest = await api.post('/requests', {
					receiverId: match.matchPersonId,
				});
				setRequestId(createdSwapRequest.id);
				setStatus('sent');
				console.log('REQUEST SENT to: ' + requestId);
			} else {
				// TODO: insert the api.delete() (SCRUM 93) in here to remove the request that was sent
				console.log('REQUEST DELETED.');
				setRequestId(null);
				setStatus('idle');
			}
		} catch (err) {
			console.log(err);
		} finally {
			setBusy(false);
		}
	}

	const buttonStyles =
		status === 'idle'
			? 'bg-dark-button-bg text-dark-button-text hover:shadow-dark-button-hover active:bg-dark-button-pressed-bg'
			: status === 'sent'
				? 'bg-light-button-bg border-1 border-grey-button-border text-light-button-text hover:shadow-light-hover active:bg-light-button-pressed-bg'
				: 'text-light-button-text bg-red-button-bg border-1 border-red-button-border hover:shadow-light-hover active:bg-red-button-pressed-bg';

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
				className={`flex shrink-0 cursor-pointer items-center justify-center gap-2 rounded-full w-43 py-3 font-main font-g-medium transition ${buttonStyles}`}
				onClick={handleButtonClick}
			>
				{status === 'idle' && (
					<>
						<img src={plus_icon} alt="plus icon"></img>
						Add
					</>
				)}
				{status === 'sent' && (
					<>
						<img src={tick_icon} alt="tick icon"></img>
						Sent
					</>
				)}
				{status === 'cancel' && (
					<>
						<img src={cross_icon} alt="cross icon"></img>
						Cancel?
					</>
				)}
			</button>
		</div>
	);
}
