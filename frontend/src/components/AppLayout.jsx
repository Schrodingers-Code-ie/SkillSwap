import { Outlet, useNavigation } from 'react-router';
import Header from './Header';
import Spinner from './Spinner';

export default function AppLayout() {
	const navigation = useNavigation();
	const isNavigating = Boolean(navigation.location);

	return (
		<>
			{/* Header stays visible on all authenticated pages */}
			<Header />

			<main className="page-container">
				{/* Displays the current page */}
				<Outlet />

				{/* Show spinner on top of the current page while navigating */}
				{isNavigating && (
					<div className="loading-overlay">
						<Spinner />
					</div>
				)}
			</main>
		</>
	);
}
