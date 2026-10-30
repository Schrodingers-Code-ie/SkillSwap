// Figma: from Bubbles section Skill Bubble
// color: "green" (they teach you, you need), "yellow" (you teach them, they need) or "grey" (no match)

const COLORS = {
	green: 'bg-green-bubble-bg text-green-bubble',
	yellow: 'bg-orange-bubble-bg text-orange-bubble',
	grey: 'bg-grey-bubble-bg text-grey-bubble',
};

export default function SkillBubble({ color = 'grey', children }) {
	return (
		<span
			className={`inline-flex h-6 items-center whitespace-nowrap rounded-full px-2.5 text-xs font-g-medium ${COLORS[color]}`}
		>
			{children}
		</span>
	);
}
