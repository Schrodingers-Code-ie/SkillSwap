import Dropdown from '../components/ui/Dropdown.jsx';
import MatchCard from '../components/MatchCard.jsx';
import { useEffect, useState } from 'react';
import { api } from '../api/client.js';

export default function Matches() {
	const [matches, setMatches] = useState([]);
	const [error, setError] = useState(null);

	useEffect(() => {
		let ignore = false;

		api.get('/matches')
			.then((data) => {
				if (ignore) return;
				console.log(data);
				setMatches(data);
				setError(null);
			})
			.catch((err) => {
				if (!ignore) setError(err.message);
			});

		return () => {
			ignore = true;
		};
	}, []);

	return (
		<div className="search-page-container px-12 py-8 flex justify-center">
			<div className="filters-section max-w-xl w-full text-center">
				FILTERS
			</div>
			<div className="suggestions-section w-full mx-10 max-w-3xl">
				<div className="header mb-4 flex items-start justify-between gap-6">
					<div className="titles">
						<h1 className="text-title text-3xl font-main font-g-medium">
							Suggested matches
						</h1>
						<p className="text-subtitle text-base font-main font-g-regular py-3">
							{matches.length}{' '}
							{matches.length == 1 ? 'person' : 'people'} you can
							swap skills with
						</p>
					</div>
					<div className="sort-dropdown mt-6 flex shrink-0 items-center gap-4 rounded-full border border-container-border bg-card-bg px-5 py-3 text-main-content font-main">
						<span className="font-main font-g-medium pr-4">
							Sort
						</span>
						<Dropdown />
					</div>
				</div>

				<div className="suggestions-list flex flex-col gap-4">
					{error && <div>{error}</div>}

					{matches.length === 0 ? (
						<div className="text-subtitle text-base font-main font-g-regular">
							No matching suggestions for you.
						</div>
					) : (
						matches.map((match) => (
							<MatchCard
								key={match.matchPersonId}
								match={match}
							/>
						))
					)}
				</div>
			</div>
			<div className="side-profile-section max-w-xl w-full text-center">
				SIDE PROFILE
			</div>
		</div>
	);
}
