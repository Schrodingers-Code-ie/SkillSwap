export default function Button({
	darkStyle,
	className = '',
	children,
	...props
}) {
	return (
		<button
			className={`w-40 h-11 cursor-pointer rounded-full flex items-center justify-center font-main font-g-medium text-base ${
				darkStyle
					? 'bg-dark-button-bg text-dark-button-text hover:shadow-dark-button-hover active:bg-dark-button-pressed-bg'
					: 'bg-light-button-bg text-light-button-text hover:shadow-md active:bg-light-button-pressed-bg'
			} transition duration-250 ${className}`}
			{...props}
		>
			{children}
		</button>
	);
}
