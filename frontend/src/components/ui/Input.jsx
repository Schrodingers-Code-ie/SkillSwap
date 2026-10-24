// className sizes the whole field (label + input), e.g. className="w-full".
// error shows a red border and message under the input, hint a grey one.
// trailing is placed inside the input on the right, e.g. an icon or a "Show" button.
export default function Input({
	type,
	id,
	name,
	titleText,
	placeholderText,
	error,
	hint,
	trailing,
	className = 'w-80',
	...props
}) {
	const message = error || hint;

	return (
		<div className={className}>
			<label
				htmlFor={id}
				className="mb-2.5 flex text-title font-main font-g-medium text-sm"
			>
				{titleText}
			</label>
			<div className="relative">
				<input
					type={type}
					id={id}
					name={name}
					placeholder={placeholderText}
					aria-invalid={error ? true : undefined}
					aria-describedby={message ? `${id}-message` : undefined}
					className={`w-full h-10 px-3 text-xs text-subtitle font-main bg-input-bg border outline-none placeholder:text-input-placeholder rounded-input ${
						error
							? 'border-error'
							: 'border-border focus:border-active-input-border'
					} ${trailing ? 'pr-12' : ''}`}
					{...props}
				/>
				{trailing && (
					<div className="absolute inset-y-0 right-2.5 flex items-center">
						{trailing}
					</div>
				)}
			</div>
			{message && (
				<p
					id={`${id}-message`}
					className={`mt-2 font-main text-xs leading-[15px] ${
						error ? 'text-error' : 'text-subtitle'
					}`}
				>
					{message}
				</p>
			)}
		</div>
	);
}
