// The teach levels, in the same order and spelling as the backend's Proficiency
// enum, and the label shown for each one.
export const LEVELS = [
	{ value: 'BEGINNER', label: 'Beginner' },
	{ value: 'INTERMEDIATE', label: 'Intermediate' },
	{ value: 'ADVANCED', label: 'Advanced' },
];

export const levelLabel = (value) =>
	LEVELS.find((l) => l.value === value)?.label ?? value;
