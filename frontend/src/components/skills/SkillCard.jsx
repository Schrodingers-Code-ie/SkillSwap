import cross_icon from '../../assets/cross_icon.svg';
import { levelLabel } from './levels';

const BORDERS = {
	popup: 'border-skill-card-border',
	profile: 'border-profile-card-border',
};

export default function SkillCard({
	name,
	proficiency,
	onRemove,
	disabled,
	variant = 'popup',
}) {
	return (
		<div
			className={`flex min-h-11 items-center justify-between gap-3 rounded-card border bg-input-bg px-4 py-2 ${BORDERS[variant]}`}
		>
			<div className="flex min-w-0 items-center gap-2.5">
				<span className="truncate text-[15px] font-g-semibold text-strong-text">
					{name}
				</span>
				{proficiency && (
					<span className="inline-flex h-[19px] shrink-0 items-center rounded-full bg-teach-badge-bg px-2 text-xs font-g-medium text-orange-bubble">
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
					className="-mr-1 flex size-6 shrink-0 cursor-pointer items-center justify-center rounded-full hover:opacity-60 disabled:cursor-default disabled:opacity-30"
				>
					<img src={cross_icon} alt="" className="size-4" />
				</button>
			)}
		</div>
	);
}