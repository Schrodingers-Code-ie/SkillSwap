export default function Input({
	type,
	id,
	name,
	titleText,
	placeholderText,
	className = '',
}) {
	return (
		<>
			<label
				htmlFor={id}
				className="mt-3 flex text-title font-main font-g-medium text-base"
			>
				{titleText}
			</label>
			<input
				type={type}
				id={id}
				name={name}
				placeholder={placeholderText}
				className={`w-80 h-10 px-3 my-2 text-subtitle font-main bg-input-bg border outline-none border-border focus:border-active-input-border text-input-placeholder rounded-input ${className}`}
			/>
		</>
	);
}
