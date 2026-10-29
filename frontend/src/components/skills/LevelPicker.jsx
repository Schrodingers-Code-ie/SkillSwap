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
					className={`rounded-xl px-4 py-3 text-sm transition ${
						value === level.value
							? 'bg-[#4a4136] text-white shadow'
							: 'bg-[#e7e3df] text-[#4a4136] hover:bg-[#ddd8d3]'
					}`}
				>
					{level.label}
				</button>
			))}
		</div>
	);
}
