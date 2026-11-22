import { useEffect, useState } from 'react';
import { api } from '../../api/client.js';
import cross_icon from '../../assets/cross_icon.svg';

// The "Block" button at the bottom of the Chat profile panel, with an error line under it if blocking fails.
// Works like "Cancel?" on RequestButton: first click asks "Block?", second click blocks
// onBlocked: called after blocking, so the Chat page can remove them from the list
export default function BlockButton({ userId, onBlocked }) {
	const [confirming, setConfirming] = useState(false);
	const [busy, setBusy] = useState(false);
	const [error, setError] = useState('');

	async function handleClick() {
		if (busy) return; // A request is already running, so clicks can't be spammed

		// Block -> Block? is just a UI step, nothing sent yet
		if (!confirming) {
			setConfirming(true);
			return;
		}

		setBusy(true);
		setError('');
		try {
			await api.post(`/blocks/${userId}`);
			onBlocked();
		} catch (err) {
			setError(err.message);
			setConfirming(false);
		} finally {
			setBusy(false);
		}
	}

	// Timer for "Block?" to go back to "Block" after 2s, same as "Cancel?"
	useEffect(() => {
		if (!confirming || busy) return;

		const timer = setTimeout(() => setConfirming(false), 2000);

		return () => clearTimeout(timer); // Clear the timer if the state changes first
	}, [confirming, busy]);

	const buttonStyles = confirming
		? 'bg-red-button-bg border-1 border-red-button-border hover:shadow-light-hover active:bg-red-button-pressed-bg'
		: 'bg-light-button-bg border-1 border-grey-button-border hover:shadow-light-hover active:bg-light-button-pressed-bg';

	return (
		<>
			<button
				type="button"
				onClick={handleClick}
				disabled={busy}
				className={`mt-8 flex cursor-pointer items-center justify-center gap-2 rounded-full px-5 py-2 font-main text-sm font-g-medium text-light-button-text transition disabled:cursor-auto disabled:opacity-60 disabled:hover:shadow-none ${buttonStyles}`}
			>
				{confirming ? (
					<>
						<img src={cross_icon} alt="cross icon"></img>
						Block?
					</>
				) : (
					'Block'
				)}
			</button>
			{error && <p className="mt-2 text-sm text-error">{error}</p>}
		</>
	);
}
