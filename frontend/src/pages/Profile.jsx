import { useState } from 'react';
import { removeLearnSkill, removeTeachSkill } from '../api/skills';
import Spinner from '../components/Spinner';
import SkillCard from '../components/skills/SkillCard';
import SkillsModal from '../components/skills/SkillsModal';
import { useProfile } from '../components/skills/profile-context';
import ProfileCircle from '../components/ui/Profile';

export default function Profile() {
	const { user, profile, error, loading, reload, setupOpen } = useProfile();
	const [editing, setEditing] = useState(false);
	const [actionError, setActionError] = useState('');
	const [removing, setRemoving] = useState(false);

	if (loading) {
		return (
			<div className="flex justify-center p-10">
				<Spinner />
			</div>
		);
	}
	if (!profile) return <p className="p-10 text-error">{error}</p>;

	async function remove(action, id) {
		if (removing) return;
		setRemoving(true);
		setActionError('');
		try {
			await action(id);
			await reload();
		} catch (e) {
			setActionError(e.message);
		} finally {
			setRemoving(false);
		}
	}

	return (
		<section className="mx-4 mb-4 min-h-[calc(100dvh-72px-16px)] rounded-container-card border border-container-border bg-card-bg p-6 shadow-container sm:p-10 lg:mx-[50px] lg:mb-9 lg:min-h-[calc(100dvh-72px-36px)]">
			<div className="text-center">
				<ProfileCircle
					name={profile.displayName}
					size="xl"
					className="mx-auto"
				/>
				<h1 className="mt-4 text-2xl text-strong-text">
					{profile.displayName}
				</h1>
				<p className="text-sm text-subtitle">@{user.username}</p>
				{profile.bio && (
					<p className="mx-auto mt-4 max-w-md text-sm text-subtitle">
						{profile.bio}
					</p>
				)}
				<button
					type="button"
					onClick={() => setEditing(true)}
					className="mt-6 h-7 cursor-pointer rounded-input bg-light-button-bg px-5 text-[13px] text-light-button-text transition hover:shadow-light-hover active:bg-light-button-pressed-bg"
				>
					Edit your skills
				</button>
			</div>

			<div className="mx-auto mt-2 max-w-[688px] text-left">
				<h2 className="mb-3.5 text-[13px] text-title">
					Skills you can teach ({profile.teachSkills.length})
				</h2>
				<div className="space-y-3">
					{profile.teachSkills.map((s) => (
						<SkillCard
							key={s.skillId}
							name={s.name}
							proficiency={s.proficiency}
							variant="profile"
							disabled={removing}
							onRemove={() => remove(removeTeachSkill, s.skillId)}
						/>
					))}
				</div>
				<hr className="my-7 border-divider" />
				<h2 className="mb-3.5 text-[13px] text-title">
					Skills you want to learn ({profile.learnSkills.length})
				</h2>
				<div className="space-y-3">
					{profile.learnSkills.map((s) => (
						<SkillCard
							key={s.skillId}
							name={s.name}
							variant="profile"
							disabled={removing}
							onRemove={() => remove(removeLearnSkill, s.skillId)}
						/>
					))}
				</div>
				{actionError && (
					<p role="alert" className="mt-3 text-sm text-error">
						{actionError}
					</p>
				)}
			</div>

			{(setupOpen || editing) && (
				<SkillsModal onClose={() => setEditing(false)} />
			)}
		</section>
	);
}
