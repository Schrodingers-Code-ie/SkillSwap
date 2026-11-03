export const LEVELS = [
	{ value: 'BEGINNER', label: 'Beginner' },
	{ value: 'INTERMEDIATE', label: 'Intermediate' },
	{ value: 'ADVANCED', label: 'Advanced' },
];

export const levelLabel = (value) =>
	LEVELS.find((l) => l.value === value)?.label ?? value;
