import { NavLink } from 'react-router';

export default function Header() {
	return (
		<header>
			<nav>
				<NavLink to="/profile">Profile</NavLink>{' '}
				<NavLink to="/matches">Matches</NavLink>{' '}
				<NavLink to="/requests">Requests</NavLink>{' '}
				<NavLink to="/chat">Chat</NavLink>
			</nav>
		</header>
	);
}
