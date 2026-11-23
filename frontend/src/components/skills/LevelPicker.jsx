import { LEVELS } from './levels';

// Three toggle buttons (Beginner, Intermediate, Advanced) for picking your level
// on a teach skill. Only one can be pressed at a time.
export default function LevelPicker({ value, onChange, labelId }) {
	return (
		<div
			role="group"
			aria-labelledby={labelId}
			className="grid grid-rows-3 xs:grid-cols-3 xs:grid-rows-1 gap-2"
		>
			{LEVELS.map((level) => (
				<button
					key={level.value}
					type="button"
					onClick={() => onChange(level.value)}
					aria-pressed={value === level.value}
					className={`h-[39px] cursor-pointer rounded-input text-sm transition ${
						value === level.value
							? 'bg-dark-button-bg text-dark-button-text shadow-dark-button'
							: 'bg-light-button-bg text-light-button-text hover:bg-light-button-pressed-bg'
					}`}
				>
					{level.label}
				</button>
			))}
		</div>
	);
}
