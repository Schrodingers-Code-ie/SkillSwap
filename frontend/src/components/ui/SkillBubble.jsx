export default function SkillBubble({ children, variant }) {
	const styles = {
		green: 'bg-(--raw-bg-dark-green) text-green-bubble',
		yellow: 'bg-(--raw-dirty-yellow) text-orange-bubble',
		grey: 'bg-(--raw-border-grey) text-grey-bubble',
	};

	return (
		<span
			className={`rounded-full px-4 py-1 text-sm font-main font-g-medium ${styles[variant]}`}
		>
			{children}
		</span>
	);
}
