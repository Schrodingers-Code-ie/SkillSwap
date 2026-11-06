import { useState, useEffect, useRef } from 'react';
import arrow from '../../assets/dropdown_arrow.svg';

const OPTIONS = ['Best match', 'Worst match'];

export default function Dropdown({ setSortingOption }) {
	const [isOpen, setIsOpen] = useState(false);
	const [selected, setSelected] = useState(OPTIONS[0]); // Displaying currently selected sort option
	const dropdownRef = useRef(null);

	useEffect(() => {
		if (!isOpen) return;
		function handleClick(e) {
			if (!dropdownRef.current.contains(e.target)) setIsOpen(false);
		}
		function handleKey(e) {
			if (e.key === 'Escape') setIsOpen(false);
		}
		document.addEventListener('mousedown', handleClick);
		document.addEventListener('keydown', handleKey);
		return () => {
			document.removeEventListener('mousedown', handleClick);
			document.removeEventListener('keydown', handleKey);
		};
	}, [isOpen]);

	return (
		<div className="dropdown relative">
			<button
				type="button"
				onClick={() => setIsOpen(!isOpen)}
				aria-expanded={isOpen}
				className="flex cursor-pointer items-center gap-3 font-g-medium text-strong-text"
			>
				{selected}
				<span
					className={`text-xs transition-transform ${isOpen ? 'rotate-180' : ''}`}
				>
					<img src={arrow} alt="dropdown arrow" />
				</span>
			</button>

			{isOpen && (
				<div
					ref={dropdownRef}
					className="absolute sm:right-0 top-full mt-4 min-w-40 overflow-hidden rounded-input border border-container-border bg-card-bg py-1 shadow-avatar"
				>
					{OPTIONS.map((option) => (
						/* Make each option a button we can click */
						<button
							key={option}
							type="button"
							onClick={() => {
								setSelected(option);
								setIsOpen(false);
								setSortingOption(option);
							}}
							className="block w-full cursor-pointer px-4 py-2 text-left text-main-content hover:bg-nav-bg"
						>
							{option}
						</button>
					))}
				</div>
			)}
		</div>
	);
}
