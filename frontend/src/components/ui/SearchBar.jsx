import searchIcon from '../../assets/search.svg';

// Figma: from Navigation section grey pill with a search icon and text eg. value "Search friends" passed in props to input
// label: the placeholder text, read out by screen readers
// className: sizes the pill from outside, eg. w-full
// Other props (value, onChange) go to the input
export default function SearchInput({ label, className = '', ...props }) {
	return (
		<label
			className={`flex h-[42px] items-center gap-2.5 rounded-full border border-transparent bg-nav-bg px-4 transition-colors focus-within:border-active-input-border ${className}`}
		>
			<img src={searchIcon} alt="" className="size-4 shrink-0" />
			<input
				type="search"
				placeholder={label}
				aria-label={label}
				className="w-full bg-transparent text-sm text-strong-text outline-none placeholder:text-input-placeholder"
				{...props}
			/>
		</label>
	);
}
