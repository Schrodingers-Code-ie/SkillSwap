import { levelLabel } from './levels';

export default function SkillCard({ name, proficiency, onRemove, disabled }) {
	return (
		<div className="flex items-center justify-between rounded-2xl border border-[#e3ddd6] bg-[#fffdf9] px-5 py-3">
			<div className="flex items-center gap-3">
				<span className="font-medium text-[#3d362c]">{name}</span>
				{proficiency && (
					<span className="rounded-full bg-[#f4e5b8] px-3 py-0.5 text-sm text-[#7a5b10]">
						{levelLabel(proficiency)}
					</span>
				)}
			</div>
			{onRemove && (
				<button
					type="button"
					onClick={onRemove}
					disabled={disabled}
					aria-label={`Remove ${name}`}
					className="text-[#4a4136] hover:opacity-60 disabled:opacity-30"
				>
					✕
				</button>
			)}
		</div>
	);
}
