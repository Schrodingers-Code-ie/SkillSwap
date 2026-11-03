import { useState } from 'react';
import { removeLearnSkill, removeTeachSkill } from '../api/skills';
import SkillCard from '../components/skills/SkillCard';
import SkillsModal from '../components/skills/SkillsModal';
import { useProfile } from '../components/skills/profile-context';

const initials = (name) =>
	name
		.split(' ')
		.filter(Boolean)
		.map((w) => w[0])
		.slice(0, 2)
		.join('')
		.toUpperCase();

export default function Profile() {
	const { user, profile, error, loading, reload, setupOpen } = useProfile();
	const [editing, setEditing] = useState(false);
	const [actionError, setActionError] = useState('');

	if (loading) return <p className="p-10">Loading…</p>;
	if (!profile) return <p className="p-10 text-error">{error}</p>;

	async function remove(action, id) {
		setActionError('');
		try {
			await action(id);
			await reload();
		} catch (e) {
			setActionError(e.message);
		}
	}

	return (
		<section className="mx-auto max-w-5xl rounded-container-card bg-card-bg p-10 shadow-container">
			<div className="text-center">
				<div className="mx-auto flex h-24 w-24 items-center justify-center rounded-full bg-avatar-bg text-3xl font-g-semibold text-dark-button-text shadow-avatar">
					{initials(profile.displayName)}
				</div>
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
					className="mt-6 rounded-full bg-card-border px-5 py-2 text-sm text-strong-text"
				>
					Edit your skills
				</button>
			</div>

			<div className="mx-auto mt-8 max-w-3xl space-y-3 text-left">
				<h2 className="text-sm text-strong-text">
					Skills you can teach ({profile.teachSkills.length})
				</h2>
				{profile.teachSkills.map((s) => (
					<SkillCard
						key={s.skillId}
						name={s.name}
						proficiency={s.proficiency}
						onRemove={() => remove(removeTeachSkill, s.skillId)}
					/>
				))}
				<hr className="my-6 border-container-border" />
				<h2 className="text-sm text-strong-text">
					Skills you want to learn ({profile.learnSkills.length})
				</h2>
				{profile.learnSkills.map((s) => (
					<SkillCard
						key={s.skillId}
						name={s.name}
						onRemove={() => remove(removeLearnSkill, s.skillId)}
					/>
				))}
				{actionError && (
					<p role="alert" className="text-sm text-error">
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
