import Dropdown from '../components/ui/Dropdown.jsx';
import MatchCard from '../components/MatchCard.jsx';
import { useEffect, useState } from 'react';
import { api } from '../api/client.js';
import { useRouteLoaderData } from 'react-router';

export default function Matches() {
	const [matches, setMatches] = useState([]);
	const [error, setError] = useState(null);
	const [hasSkills, setHasSkills] = useState(null);

	const user = useRouteLoaderData('protected');
	const userId = user.id;

	useEffect(() => {
		let ignore = false;

		api.get(`/users/${userId}/profile`)
			.then((profile) => {
				const foundSkills =
					profile.learnSkills.length != 0 ||
					profile.teachSkills.length != 0;
				if (ignore) return;
				setHasSkills(foundSkills);

				if (!foundSkills) return; // Nothing to match, so skip the request for matches
				return api.get('/matches').then((data) => {
					if (!ignore) setMatches(data);
				});
			})
			.catch((err) => {
				if (!ignore) setError(err.message);
			});

		return () => {
			ignore = true;
		};
	}, []); // Fetching the matches once when the component loads. LATER: refresh when filters/sorting are changed

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
					{hasSkills === false && (
						<div className="text-subtitle text-base font-main font-g-regular">
							You have no skills in any of your sections. Assign
							them first in /signup/skills to see people with who
							you can match.
						</div>
					)}
					{error && <div>{error}</div>}

					{!error && hasSkills && matches.length === 0 ? (
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
