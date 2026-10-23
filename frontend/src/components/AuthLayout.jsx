import { useRef } from 'react';
import { Link } from 'react-router';
import dividerLine from '../assets/auth-divider.svg';
// Photo by Vizag Explore on Unsplash (free to use under the Unsplash License)
import leaves from '../assets/leaves.webp';

const CREATORS = [
	'Vitalii Bodnia',
	'Kateryna Diachenko',
	'Joanna Maj',
	'Sofia Nikiforenko',
	'Krzysztof Lesnik',
];

// Shared frame for the Sign in and Sign up pages: the form card on the left,
// the welcome panel on the right (hidden on small screens).
// active is 'signin' or 'signup' and decides which tab is highlighted.
export default function AuthLayout({ active, children }) {
	return (
		<div className="relative min-h-screen bg-page-bg font-main">
			<p className="absolute left-[50px] top-[31px] text-sm text-strong-text">
				Skill Swap
			</p>

			{/* Form side takes 55% of the width, the welcome panel 45%. The form side never gets narrower than 460px */}
			<div className="grid min-h-screen items-center gap-10 px-4 py-20 lg:grid-cols-[minmax(460px,11fr)_minmax(0,9fr)] lg:py-[65px] lg:pl-[50px] lg:pr-[52px]">
				{/* On wide screens the card sits on a leaf picture as tall as the welcome panel */}
				<div className="relative flex items-center justify-center lg:h-full lg:min-h-[600px] lg:overflow-hidden lg:rounded-container-card lg:border lg:border-container-border lg:bg-photo-panel-bg lg:px-8 lg:shadow-container">
					{/* The photo is its own layer so only it gets blurred, not the card.
					    Strong blur and less colour turn it into soft green light,
					    scale-125 hides the faded edges the blur leaves around the photo */}
					<div
						aria-hidden
						style={{ backgroundImage: `url(${leaves})` }}
						className="absolute inset-0 hidden scale-125 bg-cover bg-center bg-no-repeat blur-[20px] saturate-75 lg:block"
					/>
					{/* Cream wash on top so the green matches the rest of the page */}
					<div
						aria-hidden
						className="absolute inset-0 hidden bg-card-bg/40 lg:block"
					/>
					<section className="relative w-full max-w-[433px] rounded-container-card border border-container-border bg-card-bg px-6 pt-16 pb-8 shadow-container sm:px-14">
						<AuthTabs active={active} />
						{children}
					</section>
				</div>

				<WelcomePanel />
			</div>
		</div>
	);
}

function AuthTabs({ active }) {
	// transition-colors fades the colour change instead of switching instantly
	const tab = (isActive) =>
		isActive
			? 'font-g-bold text-title'
			: 'text-title-faded transition-colors duration-300 ease-out hover:text-title';

	return (
		<nav className="mb-5 flex items-center gap-5 text-2xl">
			<Link to="/login" className={tab(active === 'signin')}>
				Sign in
			</Link>
			<div className="flex h-[27px] w-0 items-center justify-center">
				<img
					src={dividerLine}
					alt=""
					className="max-w-none rotate-90"
				/>
			</div>
			<Link to="/signup" className={tab(active === 'signup')}>
				Sign up
			</Link>
		</nav>
	);
}

function WelcomePanel() {
	return (
		<aside className="hidden h-full min-h-[600px] flex-col rounded-container-card border border-container-border bg-card-bg px-[51px] pt-[163px] pb-[29px] text-title shadow-container lg:flex">
			<p className="text-base">Welcome to</p>
			<h2 className="mt-5 font-project-name font-n-medium text-[64px] leading-none tracking-[1px]">
				Skill Swap
			</h2>
			<p className="mt-1 text-sm">
				Exchange knowledge, help each other and make friends.
			</p>

			<CreatorsMarquee />
		</aside>
	);
}

// The names scroll in one line and loop. The list is drawn twice and
// moved left by half its width, so the second copy takes the first one's place
function CreatorsMarquee() {
	const names = (hidden) => (
		<ul
			aria-hidden={hidden || undefined}
			className="flex shrink-0 gap-10 pr-10"
		>
			{CREATORS.map((name) => (
				<li key={name} className="whitespace-nowrap">
					{name}
				</li>
			))}
		</ul>
	);

	const trackRef = useRef(null);
	const frameRef = useRef(null);

	// CSS can only pause an animation instantly. To slow it down smoothly we change
	// its speed (playbackRate) a little every frame: 1 is normal speed, 0 is stopped.
	function easeSpeedTo(target) {
		const animation = trackRef.current.getAnimations()[0];
		if (!animation) return; // no animation when the user turned on reduced motion
		cancelAnimationFrame(frameRef.current);
		const from = animation.playbackRate;
		const start = performance.now();
		const step = (now) => {
			const progress = Math.min((now - start) / 600, 1); // 600ms
			const eased = 1 - (1 - progress) ** 2; // fast at first, gentle at the end
			animation.playbackRate = from + (target - from) * eased;
			if (progress < 1) frameRef.current = requestAnimationFrame(step);
		};
		frameRef.current = requestAnimationFrame(step);
	}

	return (
		<div
			onMouseEnter={() => easeSpeedTo(0)}
			onMouseLeave={() => easeSpeedTo(1)}
			className="mt-auto -mx-[7px] overflow-hidden text-xs [mask-image:linear-gradient(to_right,transparent,black_8%,black_92%,transparent)]"
		>
			<div
				ref={trackRef}
				className="flex w-max animate-marquee motion-reduce:animate-none"
			>
				{names(false)}
				{names(true)}
			</div>
		</div>
	);
}
