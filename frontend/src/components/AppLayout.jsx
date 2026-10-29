import { useNavigation } from 'react-router';
import Header from './Header';
import Spinner from './Spinner';
import { ProfileProvider } from "./skills/ProfileContext";
import SkillsGate from './skills/SkillsGate';

export default function AppLayout() {
    const navigation = useNavigation();
    const isNavigating = Boolean(navigation.location);

    return (
        <ProfileProvider>
            <div className="min-h-screen bg-page-bg">
                {/* Header stays visible on all authenticated pages */}
                <Header />

                <main className="page-container">
                    {/* Redirect users with missing skills to /profile */}
                    <SkillsGate />

                    {/* Show spinner while navigating */}
                    {isNavigating && (
                        <div className="loading-overlay">
                            <Spinner />
                        </div>
                    )}
                </main>
            </div>
        </ProfileProvider>
    );
}