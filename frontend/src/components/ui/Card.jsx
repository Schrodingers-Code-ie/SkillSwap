export default function Card({ children, className = '', ...props }) {
	return (
		<div
			className={`w-60 p-5 bg-card-bg border border-card-border rounded-container-card ${className}`}
			{...props}
		>
			{children}
		</div>
	);
}
