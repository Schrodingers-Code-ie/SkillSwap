import FilterCheckbox from './FilterCheckbox.jsx';

export default function Filters({
	learnSkills,
	teachSkills,
	selectedLearn,
	selectedTeach,
	setSelectedLearn,
	setSelectedTeach,
	clearFilters,
}) {
	function toggleSkill(setSelected, skillName) {
		setSelected((prev) => {
			const next = new Set(prev); // Keep the old info from the set
			if (next.has(skillName))
				next.delete(skillName); // Remove the skill if it was unchecked
			else next.add(skillName); // Add a skill, because it was clicked for the first time
			return next;
		});
	}

	return (
		<div className="filters-section max-w-xs w-full text-center text-left">
			<div className="header flex justify-between items-baseline">
				<h1 className="text-title text-[28px] min-[701px]:text-2xl font-main font-g-semibold">
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
		</div>
	);
}
