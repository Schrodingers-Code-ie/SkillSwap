import { useEffect, useState } from 'react';
import FilterCheckbox from './FilterCheckbox.jsx';
import cross_icon from '../../assets/cross_icon.svg';
import filters_icon from '../../assets/filters_icon.svg';

export default function Filters({
	learnSkills,
	teachSkills,
	selectedLearn,
	selectedTeach,
	setSelectedLearn,
	setSelectedTeach,
	clearFilters,
}) {
	const [drawerOpen, setDrawerOpen] = useState(false); // Only used below lg (1024px)

	function toggleSkill(setSelected, skillName) {
		setSelected((prev) => {
			const next = new Set(prev); // Keep the old info from the set
			if (next.has(skillName))
				next.delete(skillName); // Remove the skill if it was unchecked
			else next.add(skillName); // Add a skill, because it was clicked for the first time
			return next;
		});
	}

	// Close the drawer with Escape
	useEffect(() => {
		if (!drawerOpen) return;
		function onKeyDown(e) {
			if (e.key === 'Escape') setDrawerOpen(false);
		}
		window.addEventListener('keydown', onKeyDown);
		return () => window.removeEventListener('keydown', onKeyDown);
	}, [drawerOpen]);

	// The filter list itself, rendered both in the sidebar (lg+) and in the drawer (below lg)
	const content = (
		<>
			<div className="header justify-between items-baseline">
				<h1 className="text-title text-[28px] min-sm:text-2xl font-main font-g-semibold">
					FILTERS
				</h1>
				<u
					className="cursor-pointer text-subtitle text-sm font-main font-g-regular"
					onClick={() => clearFilters()}
				>
					Clear the filters
				</u>
			</div>
			<div className="filter-for-teach pt-5">
				<p className="text-subtitle text-base font-main font-g-semibold pb-1">
					I WANT TO LEARN
				</p>
				<p className="text-subtitle text-base font-main font-g-regular">
					Only show people who teach the ticked skills.
				</p>
				<div className="checkboxes select-none mt-6">
					{learnSkills.map((skill) => (
						<FilterCheckbox
							key={skill.skillId}
							label={skill.name}
							checked={selectedLearn.has(skill.name)}
							onChange={() =>
								toggleSkill(setSelectedLearn, skill.name)
							}
						/>
					))}
					{learnSkills.length == 0 && (
						<p className="text-subtitle text-small font-main font-g-regular">
							Nothing in your learn skills section.
						</p>
					)}
				</div>
			</div>
			<div className="divider border-t-1 w-full my-7 border-border"></div>
			<div className="filter-for-learn">
				<p className="text-subtitle text-base font-main font-g-semibold pb-1">
					I CAN TEACH
				</p>
				<p className="text-subtitle text-base font-main font-g-regular">
					Only show people who want to learn the ticked skill.
				</p>
				<div className="checkboxes select-none mt-6">
					{teachSkills.map((skill) => (
						<FilterCheckbox
							key={skill.skillId}
							label={skill.name}
							checked={selectedTeach.has(skill.name)}
							onChange={() =>
								toggleSkill(setSelectedTeach, skill.name)
							}
						/>
					))}
					{teachSkills.length == 0 && (
						<p className="text-subtitle text-small font-main font-g-regular">
							Nothing in your teach skills section.
						</p>
					)}
				</div>
			</div>
		</>
	);

	const activeCount = selectedLearn.size + selectedTeach.size;

	return (
		<>
			{/* lg (1024px) and above: regular sidebar */}
			<div className="filters-section hidden lg:block max-w-xs text-left">
				{content}
			</div>

			{/* Below lg: round button + sliding panel from the left */}
			<div className="lg:hidden">
				<button
					type="button"
					aria-label="Open filters"
					onClick={() => setDrawerOpen(true)}
					className="fixed bottom-5 left-5 z-40 flex size-14 cursor-pointer items-center justify-center rounded-full border border-container-border bg-card-bg text-main-content shadow-container"
				>
					<img src={filters_icon} alt="Filters icon" />
					{activeCount > 0 && (
						<span className="absolute -top-1 -right-1 min-w-5 h-5 px-1 rounded-full bg-dark-button-bg text-dark-button-text text-xs flex items-center justify-center">
							{activeCount}
						</span>
					)}
				</button>

				<div
					onClick={() => setDrawerOpen(false)}
					className={`fixed inset-0 z-50 bg-black/40 transition-opacity duration-300 ${
						drawerOpen
							? 'opacity-100'
							: 'pointer-events-none opacity-0'
					}`}
				></div>

				{/* Sliding panel */}
				<div
					role="dialog"
					aria-label="Filters"
					aria-hidden={!drawerOpen}
					className={`filters-drawer fixed inset-y-0 left-0 z-50 w-72 max-w-[85vw] overflow-y-auto bg-card-bg px-6 pt-5 pb-8 text-left shadow-container transition-all duration-300 ease-out ${
						drawerOpen
							? 'translate-x-0 visible'
							: '-translate-x-full invisible'
					}`}
				>
					<button
						type="button"
						aria-label="Close filters"
						onClick={() => setDrawerOpen(false)}
						className="ml-auto -mr-2 flex size-8 cursor-pointer items-center justify-center rounded-full text-subtitle hover:bg-grey-bubble-bg"
					>
						<img
							src={cross_icon}
							alt="Cross icon"
							className="w-6"
						/>
					</button>
					{content}
				</div>
			</div>
		</>
	);
}
