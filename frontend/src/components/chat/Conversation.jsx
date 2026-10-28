import { Fragment, useEffect, useRef, useState } from 'react';
import { useRouteLoaderData } from 'react-router';
import { api } from '../../api/client.js';
import Profile from '../ui/Profile';
import arrowUpIcon from '../../assets/arrow-up.svg';

const POLL_MS = 3000; // time between polls
const MAX_LENGTH = 2000; // same limit as MessageText on the backend

// Middle column of the Chat page
// friend: open friend's profile
export default function Conversation({ friend }) {
	// The logged-in user, loaded by protectedLoader (route with id protected)
	const me = useRouteLoaderData('protected');

	const [messages, setMessages] = useState([]);
	const [loaded, setLoaded] = useState(false);
	const [error, setError] = useState(null);
	const listRef = useRef(null);
	// The send message box
	const [draft, setDraft] = useState('');
	const [sending, setSending] = useState(false);
	const [sendError, setSendError] = useState(null);

	const firstName = friend.displayName.split(' ')[0];
	const text = draft.trim();
	const canSend = text !== '' && !sending; // text exist and no message is still sending
	const notice = sendError ?? error;

	// Polling
	// Stops when the user leaves the page or switches friend
	useEffect(() => {
		let cancelled = false;
		alert;
		async function load() {
			try {
				const fresh = await api.get(`/chats/${friend.userId}/messages`);
				if (cancelled) return; // ignore late answer
				setMessages((current) => mergeById(current, fresh));
				setError(null);
			} catch {
				if (!cancelled) setError("Couldn't load messages.");
			} finally {
				if (!cancelled) setLoaded(true);
			}
		}

		load();
		const timer = setInterval(load, POLL_MS);

		return () => {
			cancelled = true;
			clearInterval(timer);
		};
	}, [friend.userId]);

	// Scroll to the newest message whenever one is added
	useEffect(() => {
		const list = listRef.current;
		if (list) list.scrollTop = list.scrollHeight;
	}, [messages.length]);

	async function handleSubmit(e) {
		e.preventDefault();
		if (!canSend) return;

		setSending(true);
		setSendError(null);
		try {
			const saved = await api.post(`/chats/${friend.userId}/messages`, {
				text,
			});
			setMessages((current) => mergeById(current, [saved]));
			setDraft('');
		} catch (err) {
			setSendError(
				err.status && err.status < 500
					? err.message
					: "Couldn't send your message.",
			);
		} finally {
			setSending(false);
		}
	}

	return (
		<section className="flex min-h-0 flex-col overflow-hidden rounded-container-card border border-container-border bg-card-bg shadow-container">
			<header className="flex items-center gap-3 border-b border-container-border px-5 py-4">
				<Profile name={friend.displayName} />
				<h2 className="truncate text-lg text-strong-text">
					{friend.displayName}
				</h2>
			</header>

			{/* relative: the notice below is positioned inside this box */}
			<div className="relative flex min-h-0 flex-1 flex-col">
				{notice && (
					<p
						role={sendError ? 'alert' : 'status'}
						className="absolute left-1/2 top-3 z-10 max-w-[90%] -translate-x-1/2 rounded-full bg-error-box-bg px-3 py-1 text-center text-xs text-error-box-text shadow-nav-tab"
					>
						{notice}
					</p>
				)}
				<div
					ref={listRef}
					aria-live="polite"
					className="min-h-0 flex-1 overflow-y-auto px-5 py-4"
				>
					{!loaded && (
						<p className="text-sm text-subtitle">
							Loading messages
						</p>
					)}

					{loaded && messages.length === 0 && !error && (
						<p className="mt-10 text-center text-sm text-subtitle">
							No messages yet. Say hi to {firstName}.
						</p>
					)}

					{messages.map((message, index) => {
						const mine = message.senderId === me.id;
						const newDay =
							index === 0 ||
							dayKey(messages[index - 1].sentAt) !==
								dayKey(message.sentAt);

						return (
							<Fragment key={message.id}>
								{newDay && (
									<p className="my-4 text-center font-subtitle text-[11px] uppercase tracking-wider text-subtitle">
										{dayLabel(message.sentAt)}
									</p>
								)}

								<div
									className={`mb-3 flex flex-col ${mine ? 'items-end' : 'items-start'}`}
								>
									<p
										className={`max-w-[70%] whitespace-pre-wrap break-words rounded-message px-3.5 py-2.5 text-sm leading-5 ${
											mine
												? 'bg-dark-button-bg text-dark-button-text'
												: 'bg-received-message-bg text-strong-text'
										}`}
									>
										{message.text}
									</p>

									{/* Figma: the time only shows under the other person's messages */}
									<time
										dateTime={message.sentAt}
										className="mt-1 px-1 text-[11px] text-subtitle"
									>
										{timeLabel(message.sentAt)}
									</time>
								</div>
							</Fragment>
						);
					})}
				</div>
			</div>

			<form
				onSubmit={handleSubmit}
				className="border-t border-container-border px-5 py-4"
			>
				<div className="flex items-center gap-3">
					{/* Send message box */}
					<input
						value={draft}
						onChange={(e) => {
							setDraft(e.target.value);
							setSendError(null);
						}}
						maxLength={MAX_LENGTH}
						placeholder={`Message ${firstName}`}
						aria-label={`Message ${friend.displayName}`}
						className="h-12 min-w-0 flex-1 rounded-input border border-border bg-input-bg px-4 text-sm text-strong-text outline-none transition-colors placeholder:text-input-placeholder focus:border-active-input-border"
					/>
					{/* Round send message button */}
					<button
						type="submit"
						disabled={!canSend}
						aria-label="Send message"
						className="flex size-12 shrink-0 cursor-pointer items-center justify-center rounded-full bg-dark-button-bg transition duration-250 enabled:hover:shadow-dark-button-hover enabled:active:bg-dark-button-pressed-bg disabled:cursor-default disabled:bg-light-button-pressed-bg"
					>
						<img src={arrowUpIcon} alt="" className="size-[18px]" />
					</button>
				</div>
			</form>
		</section>
	);
}

// Adds new messages to the list without losing the ones we already have
function mergeById(current, incoming) {
	const byId = new Map(current.map((message) => [message.id, message]));
	incoming.forEach((message) => byId.set(message.id, message));
	return [...byId.values()].sort((a, b) => a.id - b.id);
}

function dayKey(iso) {
	return new Date(iso).toDateString();
}

function dayLabel(iso) {
	const date = new Date(iso);
	const startOfDay = (d) =>
		new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime();
	const daysAgo = Math.round(
		(startOfDay(new Date()) - startOfDay(date)) / 86_400_000,
	);

	if (daysAgo === 0) return 'Today';
	if (daysAgo === 1) return 'Yesterday';
	if (daysAgo < 7)
		return date.toLocaleDateString('en-IE', { weekday: 'long' });
	return date.toLocaleDateString('en-IE', {
		day: 'numeric',
		month: 'short',
		year: 'numeric',
	});
}

function timeLabel(iso) {
	return new Date(iso).toLocaleTimeString('en-IE', {
		hour: '2-digit',
		minute: '2-digit',
	});
}
