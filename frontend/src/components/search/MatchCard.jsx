import PersonCard from '../ui/PersonCard.jsx';
import RequestButton from './RequestButton.jsx';
import { useState } from 'react';

// selected: this card is the one shown in the side profile panel
// onSelect: clicking anywhere on the card opens this person in the panel
export default function MatchCard({
	match,
	requestId,
	onRequestChange,
	selected,
	onSelect,
}) {
	const [error, setError] = useState(null);

	return (
		<>
			{error && (
				<p
					role="alert"
					className="rounded-input bg-error-box-bg px-3 py-3 font-main text-sm leading-5 text-error-box-text"
				>
					{error}
				</p>
			)}
			<PersonCard
				name={match.matchPersonName}
				username={match.matchPersonUsername}
				skillsTheyCanTeachMe={match.skillsTheyCanTeachMe}
				skillsICanTeachThem={match.skillsICanTeachThem}
				selected={selected}
				onSelect={onSelect}
				action={
					<RequestButton
						personId={match.matchPersonId}
						requestId={requestId}
						onRequestChange={onRequestChange}
						onError={setError}
						className="col-span-2 md:col-span-1 justify-self-start w-33 md:w-43 py-2 md:py-3"
					/>
				}
			/>
		</>
	);
}
