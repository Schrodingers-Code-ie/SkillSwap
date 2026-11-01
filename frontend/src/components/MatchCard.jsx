import SkillBubble from './ui/SkillBubble.jsx';
import { api } from '../api/client.js';
import { useEffect, useState } from 'react';
import plus_icon from '../assets/plus_icon.svg';
import tick_icon from '../assets/tick_icon.svg';
import cross_icon from '../assets/cross_icon.svg';

export default function MatchCard({ match }) {
	// 3 states of the same buttom to show different text: idle = "Add", sent = "Sent", cancel = "Cancel"
	const [status, setStatus] = useState('idle');
	const [busy, setBusy] = useState(false);
	const [requestId, setRequestId] = useState(null);
	const [error, setError] = useState(null);

	async function handleButtonClick() {
		if (busy) return; // Already doing some request, so that we can't spam clicks and brake the API call

		// Sent -> Cancel is just a UI step, no request sent yet
		if (status === 'sent') {
			setStatus('cancel');
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
				setError(null);
			} else {
				// Clicked on "Cancel?" so cancel the request
				await api.delete(`/requests/${requestId}`);
				setRequestId(null);
				setStatus('idle');
				setError(null);
			}
		} catch (err) {
			setError(err.message);
		} finally {
			setBusy(false);
		}
	}

	// useEffect plays the role of a timer here for "Cancel?" to go back to "Sent"
	useEffect(() => {
		if (status !== 'cancel' && busy) return; // If the button is not "Cancel?" or a call is running, don't start the timer

		const timer = setTimeout(() => setStatus('sent'), 2000); // 2s timer

		return () => clearTimeout(timer); // Clear the timer once finished
	}, [status, busy]);

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
		<>
			{error && (
				<p
					role="alert"
					className="rounded-input bg-error-box-bg px-3 py-3 font-main text-sm leading-5 text-error-box-text"
				>
					{error}
				</p>
			)}
			<div className="match-card grid grid-cols-[auto_1fr] items-center gap-x-4 gap-y-3 md:flex md:gap-4 rounded-card border border-container-border bg-card-bg px-5 py-4">
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

				<button
					type="button"
					className={`col-span-2 md:col-span-1 justify-self-start flex shrink-0 cursor-pointer items-center justify-center gap-2 rounded-full w-33 md:w-43 py-2 md:py-3 font-main font-g-medium transition disabled:cursor-auto disabled:opacity-60 disabled:hover:shadow-none ${buttonStyles}`}
					disabled={busy}
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
		</>
	);
}
