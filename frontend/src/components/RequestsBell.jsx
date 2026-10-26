import { useCallback, useEffect, useRef, useState } from 'react';
import { useRevalidator } from 'react-router';

import { api } from '../api/client';
import Button from './ui/Button';
import Card from './ui/Card';

// Bell in the Header with the number of incoming swap requests.
// Clicking it opens the Activity dropdown where requests can be accepted or declined.
// The Header is not a route, so this can't use a loader: it fetches in useEffect instead.
export default function RequestsBell() {
	const [incoming, setIncoming] = useState([]);
	const [isOpen, setIsOpen] = useState(false);
	const [error, setError] = useState(null);
	// id of the request being accepted or declined, so the buttons can't be clicked twice
	const [busyId, setBusyId] = useState(null);
	const menuRef = useRef(null);
	// Re-runs the loader of the page we're on, so /friends updates after Accept
	const revalidator = useRevalidator();

	// useCallback keeps the same function between renders, so the focus listener can be removed
	const load = useCallback(async () => {
		try {
			const data = await api.get('/requests');
			setIncoming(data.incoming);
		} catch (err) {
			setError(err.message);
		}
	}, []);

	// Load when the Header mounts, and again whenever the window gets focus
	useEffect(() => {
		api.get('/requests')
			.then((data) => setIncoming(data.incoming))
			.catch((err) => setError(err.message));
		window.addEventListener('focus', load);
		return () => window.removeEventListener('focus', load);
	}, [load]);

	// Close the dropdown when clicking outside it or pressing Escape (same as AccountMenu)
	useEffect(() => {
		if (!isOpen) return;
		function handleClick(e) {
			if (!menuRef.current.contains(e.target)) setIsOpen(false);
		}
		function handleKey(e) {
			if (e.key === 'Escape') setIsOpen(false);
		}
		document.addEventListener('mousedown', handleClick);
		document.addEventListener('keydown', handleKey);
		return () => {
			document.removeEventListener('mousedown', handleClick);
			document.removeEventListener('keydown', handleKey);
		};
	}, [isOpen]);

	function toggle() {
		if (!isOpen) load(); // fresh list every time the dropdown opens
		setIsOpen((open) => !open);
	}

	// action is 'accept' or 'decline'
	async function respond(requestId, action) {
		setError(null);
		setBusyId(requestId);
		try {
			await api.post(`/requests/${requestId}/${action}`);
			await load();
			revalidator.revalidate();
		} catch (err) {
			// e.g. 409 already accepted or 404 not found: show the message from the backend
			setError(err.message);
		} finally {
			setBusyId(null);
		}
	}

	return (
		<div className="relative" ref={menuRef}>
			<button
				type="button"
				onClick={toggle}
				aria-label={`Requests (${incoming.length})`}
				aria-expanded={isOpen}
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
				<div className="absolute right-0 mt-2 w-[400px] p-4 z-20 bg-card-bg border border-card-border rounded-card shadow-lg font-main">
					<div className="flex items-center justify-between">
						<h2 className="text-title font-g-semibold">ACTIVITY</h2>
						<button
							type="button"
							onClick={() => setIsOpen(false)}
							aria-label="Close"
							className="size-8 cursor-pointer rounded-full flex items-center justify-center text-subtitle hover:bg-page-bg"
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
								<li key={request.id}>
									<Card className="w-full">
										<div className="flex items-center gap-3">
											<div className="size-10 shrink-0 rounded-full bg-dark-button-bg text-dark-button-text flex items-center justify-center text-sm font-g-medium">
												U{request.senderId}
											</div>
											<p className="text-main-content">
												<span className="text-title font-g-medium">
													User {request.senderId}
												</span>{' '}
												wants to swap skills with you
											</p>
										</div>
										<div className="mt-3 flex gap-2">
											<Button
												darkStyle
												className="h-[38px] flex-1"
												disabled={busyId !== null}
												onClick={() =>
													respond(
														request.id,
														'accept',
													)
												}
											>
												Accept
											</Button>
											<Button
												className="h-[38px] flex-1"
												disabled={busyId !== null}
												onClick={() =>
													respond(
														request.id,
														'decline',
													)
												}
											>
												Decline
											</Button>
										</div>
									</Card>
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
			aria-hidden="true"
		>
			<path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9" />
			<path d="M10.3 21a1.94 1.94 0 0 0 3.4 0" />
		</svg>
	);
}
