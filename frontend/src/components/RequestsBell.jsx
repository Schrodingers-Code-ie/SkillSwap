import { useEffect, useState } from 'react';

import { api } from '../api/client';
import Button from './ui/Button';

// Bell in the Header with the number of incoming swap requests.
// Clicking it opens the Activity dropdown where requests can be accepted or declined.
// The Header is not a route, so this can't use a loader: it fetches in useEffect instead.
export default function RequestsBell() {
	const [incoming, setIncoming] = useState([]);
	const [isOpen, setIsOpen] = useState(false);
	const [error, setError] = useState(null);

	// Load once when the Header mounts
	useEffect(() => {
		api.get('/requests')
			.then((data) => setIncoming(data.incoming))
			.catch((err) => setError(err.message));
	}, []);

	// Load again after Accept or Decline, so the list and the badge stay in sync with the backend
	async function reload() {
		const data = await api.get('/requests');
		setIncoming(data.incoming);
	}

	// action is 'accept' or 'decline'
	async function respond(requestId, action) {
		setError(null);
		try {
			await api.post(`/requests/${requestId}/${action}`);
			await reload();
		} catch (err) {
			// e.g. 409 already accepted or 404 not found: show the message from the backend
			setError(err.message);
		}
	}

	return (
		<div className="relative">
			<button
				type="button"
				onClick={() => setIsOpen(!isOpen)}
				className="relative w-10 h-10 cursor-pointer rounded-full bg-card-bg border border-card-border flex items-center justify-center text-title"
			>
				<BellIcon />
				{incoming.length > 0 && (
					<span className="absolute -top-1 -right-1 min-w-5 h-5 px-1 rounded-full bg-dark-button-bg text-dark-button-text text-xs flex items-center justify-center">
						{incoming.length}
					</span>
				)}
			</button>

			{isOpen && (
				<div className="absolute right-0 mt-2 w-96 p-4 z-10 bg-card-bg border border-card-border rounded-card shadow-lg font-main">
					<div className="flex items-center justify-between">
						<h2 className="text-title font-g-semibold">ACTIVITY</h2>
						<button
							type="button"
							onClick={() => setIsOpen(false)}
							className="cursor-pointer text-subtitle"
						>
							✕
						</button>
					</div>

					<p className="mt-2 font-subtitle text-xs text-subtitle">
						REQUESTS · {incoming.length}
					</p>

					{error && <p className="mt-2 text-error">{error}</p>}

					{incoming.length === 0 ? (
						<p className="mt-3 text-subtitle">No requests</p>
					) : (
						<ul className="mt-3 flex flex-col gap-3">
							{incoming.map((request) => (
								<li
									key={request.id}
									className="p-3 bg-input-bg border border-card-border rounded-card"
								>
									<p className="text-main-content">
										<span className="text-title font-g-medium">
											User {request.senderId}
										</span>{' '}
										wants to swap skills with you
									</p>
									<div className="mt-3 flex gap-2">
										<Button
											darkStyle
											className="flex-1"
											onClick={() =>
												respond(request.id, 'accept')
											}
										>
											Accept
										</Button>
										<Button
											className="flex-1"
											onClick={() =>
												respond(request.id, 'decline')
											}
										>
											Decline
										</Button>
									</div>
								</li>
							))}
						</ul>
					)}
				</div>
			)}
		</div>
	);
}

function BellIcon() {
	return (
		<svg
			width="18"
			height="18"
			viewBox="0 0 24 24"
			fill="none"
			stroke="currentColor"
			strokeWidth="2"
			strokeLinecap="round"
			strokeLinejoin="round"
		>
			<path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9" />
			<path d="M10.3 21a1.94 1.94 0 0 0 3.4 0" />
		</svg>
	);
}
