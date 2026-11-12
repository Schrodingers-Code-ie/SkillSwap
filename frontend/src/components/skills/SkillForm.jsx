import { useId, useState } from 'react';
import LevelPicker from './LevelPicker';

export default function SkillForm({ catalogue, takenIds, withLevel, onAdd }) {
	const id = useId();
	const [query, setQuery] = useState('');
	const [open, setOpen] = useState(false);
	const [level, setLevel] = useState(null);
	const [busy, setBusy] = useState(false);

	const available = catalogue.filter((s) => !takenIds.has(s.id));
	//typing the full name, e.g. "python", picks it, same as clicking it
	const typed = query.trim().toLowerCase();
	const selected = available.find((s) => s.name.toLowerCase() === typed);
	//a skill that's already picked isn't suggested again
	const suggestions =
		open && query
			? available
					.filter(
						(s) =>
							s !== selected &&
							s.name.toLowerCase().includes(query.toLowerCase()),
					)
					.slice(0, 6)
			: [];

	const canAdd = selected && (!withLevel || level) && !busy;

	function type(value) {
		setQuery(value);
		setOpen(true);
	}

	function pick(skill) {
		setQuery(skill.name);
		setOpen(false);
	}

	async function submit() {
		setBusy(true);
		try {
			await onAdd(selected.id, level);
			setQuery('');
			setLevel(null);
		} catch {
			//shows the error message
		} finally {
			setBusy(false);
		}
	}

	return (
		<div className="rounded-card bg-light-button-bg px-[18px] pb-6 pt-4">
			<div className="relative">
				<label
					htmlFor={`${id}-skill`}
					className="mb-2.5 block text-sm text-title"
				>
					Skill
				</label>
				<input
					id={`${id}-skill`}
					value={query}
					onChange={(e) => type(e.target.value)}
					autoComplete="off"
					placeholder="Start typing, e.g. Python, guitar, Spanish"
					className="h-[39px] w-full rounded-input border border-border bg-input-bg px-3.5 text-sm text-strong-text outline-none placeholder:text-input-placeholder focus:border-active-input-border"
				/>
				{suggestions.length > 0 && (
					<ul className="absolute z-10 mt-1 w-full rounded-card border border-card-border bg-card-bg py-2 shadow-container">
						{suggestions.map((s) => (
							<li key={s.id}>
								<button
									type="button"
									onClick={() => pick(s)}
									className="w-full cursor-pointer px-4 py-2 text-left text-sm text-strong-text hover:bg-light-button-bg"
								>
									{s.name}
								</button>
							</li>
						))}
					</ul>
				)}
			</div>

			{withLevel && (
				<div className="mt-3">
					<p id={`${id}-level`} className="mb-2.5 text-sm text-title">
						Your level
					</p>
					<LevelPicker
						value={level}
						onChange={setLevel}
						labelId={`${id}-level`}
					/>
				</div>
			)}

			<button
				type="button"
				onClick={submit}
				disabled={!canAdd}
				className="mt-3.5 h-[39px] w-[124px] cursor-pointer rounded-full bg-light-button-bg text-[15px] text-light-button-text transition hover:shadow-light-hover active:bg-light-button-pressed-bg disabled:cursor-default disabled:opacity-50 disabled:shadow-none"
			>
				Add skill
			</button>
		</div>
	);
}
