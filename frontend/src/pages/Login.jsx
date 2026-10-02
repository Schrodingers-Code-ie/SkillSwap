import { api } from '../api/client.js';
import { useEffect, useState } from 'react';
import Button from '../components/ui/Button.jsx';
import Input from '../components/ui/Input.jsx';
import Card from '../components/ui/Card.jsx';

export default function Login() {
	const [health, setHealth] = useState('checking...');

	useEffect(() => {
		api.get('/health')
			.then((data) => setHealth(data.status))
			.catch(() => setHealth('backend unreachable'));
	}, []);
	return (
		<main>
			<h1>SkillSwap</h1>
			<p>Backend: {health}</p>

			<div>Testing style tokens:</div>
			<div className="text-title">Big title color</div>
			<div className="text-title-faded">Faded title color</div>
			<div className="text-main-content">Main content color</div>
			<div className="text-subtitle">Subtitle color</div>
			<div className="font-main">Geist font</div>
			<div className="font-project-name">Newsreader font</div>
			<div className="font-subtitle">Geist Mono font</div>
			<div className="font-main font-g-medium">Geist medium</div>
			<div className="font-subtitle font-gm-bold">Geist Mono bold</div>

			<Button
				darkStyle
				className="w-110 my-5"
				onClick={() => console.log('clicked')}
				type="submit"
			>
				Sign in
			</Button>
			<Button onClick={() => console.log('clicked')} type="submit">
				Decline
			</Button>

			<Input
				type="email"
				id="email"
				name="userEmail"
				titleText="Email"
				placeholderText="Your email"
			></Input>
			<Input
				type="text"
				id="fullName"
				name="userFullName"
				titleText="Full name"
				placeholderText="Your full name"
				className="w-100"
			></Input>

			<Card className="w-110">
				<div>Review:</div>
				<div>Jane Doe</div>
				<div>
					This is a test review, which will be in a separate component
					later.
				</div>
			</Card>
		</main>
	);
}
