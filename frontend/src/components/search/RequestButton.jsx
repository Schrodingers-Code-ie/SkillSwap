import { api } from '../../api/client.js';
import { useEffect, useState } from 'react';
import plus_icon from '../../assets/plus_icon.svg';
import tick_icon from '../../assets/tick_icon.svg';
import cross_icon from '../../assets/cross_icon.svg';

// The "Add" button, used on the match card and in the side profile panel
// personId: who we send the request to
// requestId: id of our pending request to this person (null if none), kept in Matches.jsx
// onRequestChange: tells Matches.jsx a request was sent or cancelled, so both buttons update
// onError: shows the error message wherever the button is used
// className: size and position, because the card and the panel use different sizes
export default function RequestButton({
	personId,
	requestId,
	onRequestChange,
	onError,
	className = '',
}) {
	const [confirming, setConfirming] = useState(false);
	const [busy, setBusy] = useState(false);

	// 3 states of the same button: idle = "Add", sent = "Sent", cancel = "Cancel?"
	// "Sent" comes from requestId, so the card and the panel always show the same thing
	const status = !requestId ? 'idle' : confirming ? 'cancel' : 'sent';

	async function handleButtonClick() {
		if (busy) return; // A request is already running, so clicks can't be spammed

		// Sent -> Cancel is just a UI step, no request sent yet
		if (status === 'sent') {
			setConfirming(true);
			return;
		}

		setBusy(true);
		try {
			if (status === 'idle') {
				const createdSwapRequest = await api.post('/requests', {
					receiverId: personId,
				});
				onRequestChange(personId, createdSwapRequest.id);
				setConfirming(false); // A new request always starts as "Sent"
			} else {
				// Clicked on "Cancel?" so cancel the request
				await api.delete(`/requests/${requestId}`);
				onRequestChange(personId, null);
				setConfirming(false);
			}
			onError(null);
		} catch (err) {
			onError(err.message);
		} finally {
			setBusy(false);
		}
	}

	// Timer for "Cancel?" to go back to "Sent" after 2s
	useEffect(() => {
		if (!confirming || busy) return; // Only start it while "Cancel?" is showing and nothing is running

		const timer = setTimeout(() => setConfirming(false), 2000);

		return () => clearTimeout(timer); // Clear the timer if the state changes first
	}, [confirming, busy]);

	const buttonStyles =
		status === 'idle'
			? 'bg-dark-button-bg text-dark-button-text hover:shadow-dark-button-hover active:bg-dark-button-pressed-bg'
			: status === 'sent'
				? 'bg-light-button-bg border-1 border-grey-button-border text-light-button-text hover:shadow-light-hover active:bg-light-button-pressed-bg'
				: 'text-light-button-text bg-red-button-bg border-1 border-red-button-border hover:shadow-light-hover active:bg-red-button-pressed-bg';

	return (
		<button
			type="button"
			className={`flex shrink-0 cursor-pointer items-center justify-center gap-2 rounded-full font-main font-g-medium transition disabled:cursor-auto disabled:opacity-60 disabled:hover:shadow-none ${buttonStyles} ${className}`}
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
	);
}
