import { levelLabel } from './levels';

export default function SkillCard({ name, proficiency, onRemove, disabled }) {
	return (
		<div className="flex items-center justify-between rounded-card border border-card-border bg-card-bg px-5 py-3">
			<div className="flex items-center gap-3">
				<span className="font-medium text-strong-text">{name}</span>
				{proficiency && (
					<span className="rounded-full bg-card-border px-3 py-0.5 text-sm">
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
					className="hover:opacity-60 disabled:opacity-30"
				>
					✕
				</button>
			)}
		</div>
	);
}
