import { useEffect, useRef, useState } from 'react';
import { Link, NavLink, useNavigate, useRouteLoaderData } from 'react-router';
import { api } from '../api/client.js';
import Profile from './ui/Profile.jsx';

const TABS = [
	{ to: '/matches', label: 'Search' },
	{ to: '/chat', label: 'Chat' },
	{ to: '/friends', label: 'Friends' },
];

export default function Header() {
	// The user that protectedLoader loaded (see router.jsx)
	const user = useRouteLoaderData('protected');

	return (
		<header className="flex h-[72px] items-center gap-6 px-4 font-main lg:pl-[50px] lg:pr-[52px]">
			<p className="hidden shrink-0 text-sm text-strong-text md:block lg:w-[194px]">
				Skill Swap
			</p>

			<nav className="flex rounded-full bg-nav-bg p-1 sm:gap-3.5">
				{TABS.map((tab) => (
					<NavLink
						key={tab.to}
						to={tab.to}
						// NavLink tells us if its link is the current page, so we can highlight it
						className={({ isActive }) =>
							`flex h-9 items-center rounded-full border px-3 text-sm font-g-medium transition-colors duration-200 sm:px-[17px] ${
								isActive
									? 'border-container-border bg-card-bg text-strong-text shadow-nav-tab'
									: // Hover: a faint version of the selected pill
										'border-transparent text-subtitle hover:bg-card-bg/60 hover:text-strong-text'
							}`
						}
					>
						{tab.label}
					</NavLink>
				))}
			</nav>

			<div className="ml-auto flex items-center gap-6">
				{/* The bell goes here (SCRUM-75) */}
				<AccountMenu user={user} />
			</div>
		</header>
	);
}

// The profile/avatar circle. Clicking it opens a small menu with "My profile" and "Log out"
function AccountMenu({ user }) {
	const [open, setOpen] = useState(false);
	const [error, setError] = useState(null);
	const menuRef = useRef(null);
	const navigate = useNavigate();

	// Close the menu when clicking outside it or pressing Escape
	useEffect(() => {
		if (!open) return;
		function handleClick(e) {
			if (!menuRef.current.contains(e.target)) setOpen(false);
		}
		function handleKey(e) {
			if (e.key === 'Escape') setOpen(false);
		}
		document.addEventListener('mousedown', handleClick);
		document.addEventListener('keydown', handleKey);
		return () => {
			document.removeEventListener('mousedown', handleClick);
			document.removeEventListener('keydown', handleKey);
		};
	}, [open]);

	async function handleLogout() {
		setError(null);
		try {
			// Spring ends the session on the server, so the cookie stops working
			await api.post('/auth/logout');
			navigate('/login');
		} catch {
			setError("Couldn't log out, try again.");
		}
	}

	return (
		<div className="relative" ref={menuRef}>
			<button
				type="button"
				onClick={() => setOpen((o) => !o)}
				aria-haspopup="menu"
				aria-expanded={open}
				aria-label="Account menu"
				className=" group flex cursor-pointer rounded-full"
			>
				<Profile
					name={user?.username}
					size="sm"
					className="transition group-hover:-translate-y-px group-hover:shadow-dark-button-hover"
				/>
			</button>

			{open && (
				<div
					role="menu"
					className="absolute right-0 top-full z-20 mt-2 w-44 rounded-card border border-container-border bg-card-bg p-1.5 text-sm shadow-container"
				>
					<Link
						to="/profile"
						role="menuitem"
						onClick={() => setOpen(false)}
						className="block rounded-lg px-3 py-2 text-strong-text transition-colors duration-150 hover:bg-page-bg"
					>
						My profile
					</Link>
					<button
						type="button"
						role="menuitem"
						onClick={handleLogout}
						className="block w-full cursor-pointer rounded-lg px-3 py-2 text-left text-strong-text transition-colors duration-150 hover:bg-error-box-bg hover:text-error-box-text"
					>
						Log out
					</button>
					{error && (
						<p className="px-3 pb-1 text-xs text-error">{error}</p>
					)}
				</div>
			)}
		</div>
	);
}
