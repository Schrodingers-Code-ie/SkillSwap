import { LEVELS } from './levels';

export default function LevelPicker({ value, onChange }) {
	return (
		<div className="grid grid-cols-3 gap-2">
			{LEVELS.map((level) => (
				<button
					key={level.value}
					type="button"
					onClick={() => onChange(level.value)}
					aria-pressed={value === level.value}
					className={`rounded-input px-4 py-3 text-sm transition ${
						value === level.value
							? 'bg-dark-button-bg text-dark-button-text'
							: 'bg-black/5 text-strong-text hover:bg-black/10'
					}`}
				>
					{level.label}
				</button>
			))}
		</div>
	);
}
