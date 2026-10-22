import { NavLink } from 'react-router';

import RequestsBell from './RequestsBell';

export default function Header() {
	return (
		<header className="flex items-center justify-between">
			<nav>
				<NavLink to="/profile">Profile</NavLink>{' '}
				<NavLink to="/matches">Matches</NavLink>{' '}
				<NavLink to="/friends">Friends</NavLink>{' '}
				<NavLink to="/chat">Chat</NavLink>
			</nav>
			<RequestsBell />
		</header>
	);
}
