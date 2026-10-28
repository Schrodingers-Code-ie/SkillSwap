// Figma: Icons avatar/profile circle
// TODO: add different circle colours

const SIZES = {
	sm: 'size-[38px] text-[13px]',
	md: 'size-[42px] text-sm',
	lg: 'size-[68px] text-[22px]',
};

// name: the text the initials come from
// size: one of the keys in SIZES, "md" by default, influences circle size and text size
// className: allows caller to add extra classes eg. hover effect
export default function Avatar({ name = '', size = 'md', className = '' }) {
	return (
		// span, so Avatar can be placed inside button
		<span
			// screen readers skip the letters
			aria-hidden
			className={`flex shrink-0 items-center justify-center rounded-full border-3 border-card-bg bg-avatar-bg font-g-semibold text-dark-button-text shadow-avatar ${SIZES[size]} ${className}`}
		>
			{initials(name)}
		</span>
	);
}

// creates inicials inside avatar/profile circle eg. "Aoife Wall" -> "AW"
function initials(name) {
	const letters = name
		.split(' ')
		.filter((word) => word !== '')
		.slice(0, 2)
		.map((word) => word[0].toUpperCase());
	return letters.join('') || '?';
}
