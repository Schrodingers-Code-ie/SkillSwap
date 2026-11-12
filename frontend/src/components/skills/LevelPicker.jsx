import { LEVELS } from './levels';

export default function LevelPicker({ value, onChange, labelId }) {
	return (
		<div
			role="group"
			aria-labelledby={labelId}
			className="grid grid-cols-3 gap-2"
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