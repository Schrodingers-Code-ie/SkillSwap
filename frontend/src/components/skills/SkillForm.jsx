import { useState } from 'react';
import LevelPicker from './LevelPicker';

export default function SkillForm({ catalogue, takenIds, withLevel, onAdd }) {
	const [query, setQuery] = useState('');
	const [selected, setSelected] = useState(null);
	const [level, setLevel] = useState(null);
	const [busy, setBusy] = useState(false);

	const suggestions =
		query && !selected
			? catalogue
					.filter(
						(s) =>
							!takenIds.has(s.id) &&
							s.name.toLowerCase().includes(query.toLowerCase()),
					)
					.slice(0, 6)
			: [];

	const canAdd = selected && (!withLevel || level) && !busy;

	async function submit() {
		setBusy(true);
		try {
			await onAdd(selected.id, level);
			setQuery('');
			setSelected(null);
			setLevel(null);
		} catch {
			// the page shows the error message; keep the form as it is
		} finally {
			setBusy(false);
		}
	}

	return (
		<div className="space-y-4 rounded-3xl bg-[#f0ece8] p-6">
			<div className="relative">
				<label className="mb-2 block text-sm text-[#3d362c]">
					Skill
				</label>
				<input
					value={query}
					onChange={(e) => {
						setQuery(e.target.value);
						setSelected(null);
					}}
					placeholder="Start typing, e.g. Python, guitar, Spanish"
					className="w-full rounded-xl border border-[#e3ddd6] bg-[#fffdf9] px-4 py-3 outline-none"
				/>
				{suggestions.length > 0 && (
					<ul className="absolute z-10 mt-1 w-full rounded-2xl border border-[#e3ddd6] bg-[#fbf9f5] py-2 shadow-lg">
						{suggestions.map((s) => (
							<li key={s.id}>
								<button
									type="button"
									onClick={() => {
										setSelected(s);
										setQuery(s.name);
									}}
									className="w-full px-4 py-2 text-left hover:bg-[#f0ece8]"
								>
									{s.name}
								</button>
							</li>
						))}
					</ul>
				)}
			</div>

			{withLevel && (
				<div>
					<label className="mb-2 block text-sm text-[#3d362c]">
						Your level
					</label>
					<LevelPicker value={level} onChange={setLevel} />
				</div>
			)}

			<button
				type="button"
				onClick={submit}
				disabled={!canAdd}
				className="rounded-full bg-[#e3ded9] px-6 py-3 text-[#3d362c] shadow-sm disabled:opacity-50"
			>
				Add skill
			</button>
		</div>
	);
}
