import checkbox_tick from '../../assets/checkbox_tick.svg';

export default function FilterCheckbox({ label }) {
	return (
		<label className="skill-checkbox flex items-center gap-3 my-4">
			<span className="relative flex h-5 w-5 shrink-0">
				<input
					type="checkbox"
					value=""
					className="peer h-5 w-5 appearance-none rounded border-1 border-checkbox-border bg-white transition-colors checked:bg-page-bg"
				/>
				<img
					src={checkbox_tick}
					alt=""
					className="pointer-events-none absolute inset-0 m-auto opacity-0 w-4 height-4 transition-opacity peer-checked:opacity-100"
				/>
			</span>
			<span className="font-main text-subtitle">{label}</span>
		</label>
	);
}
