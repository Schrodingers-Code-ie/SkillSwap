import { useState } from 'react';
import { removeLearnSkill, removeTeachSkill } from '../api/skills';
import Spinner from '../components/Spinner';
import EditDetailsForm from '../components/profile/EditDetailsForm';
import SkillCard from '../components/skills/SkillCard';
import SkillsModal from '../components/skills/SkillsModal';
import { useProfile } from '../components/skills/profile-context';
import ProfileCircle from '../components/ui/Profile';

export default function Profile() {
	const { user, profile, error, loading, reload, setupOpen } = useProfile();
	const [editing, setEditing] = useState(false);
	const [actionError, setActionError] = useState('');
	const [removing, setRemoving] = useState(false);
	const [editingDetails, setEditingDetails] = useState(false);
	const [detailsError, setDetailsError] = useState('');

	if (loading) {
		return (
			<div className="flex justify-center p-10">
				<Spinner />
			</div>
		);
	}
	if (!profile) return <p className="p-10 text-error">{error}</p>;

	// one delete at a time, so a double-click doesn't send two
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

	async function detailsSaved() {
		setEditingDetails(false);
		setDetailsError('');
		try {
			await reload();
		} catch (e) {
			setDetailsError(`Saved, but the page didn't refresh: ${e.message}`);
		}
	}

	return (
		<section className="mx-auto max-w-5xl rounded-container-card bg-card-bg p-10 shadow-container">
			<div className="text-center">
				<ProfileCircle
					name={profile.displayName}
					size="xl"
					className="mx-auto"
				/>
				{editingDetails ? (
					<EditDetailsForm
						profile={profile}
						onSaved={detailsSaved}
						onCancel={() => setEditingDetails(false)}
					/>
				) : (
					<>
						{/* Edit sits to the right of the name, so the name stays centred (Figma) */}
						<div className="relative mx-auto mt-5 w-fit">
							<h1 className="text-2xl text-title">
								{profile.displayName}
							</h1>
							<button
								type="button"
								onClick={() => setEditingDetails(true)}
								aria-label="Edit name and bio"
								className="absolute left-full top-1/2 ml-6 h-7 -translate-y-1/2 cursor-pointer rounded-input bg-light-button-bg px-5 text-[13px] text-light-button-text transition hover:shadow-light-hover active:bg-light-button-pressed-bg sm:ml-14"
							>
								Edit
							</button>
						</div>
						<p className="mt-1 text-sm text-subtitle">
							@{user.username}
						</p>
						{profile.bio && (
							<p className="mx-auto mt-4 max-w-md whitespace-pre-line text-sm text-title">
								{profile.bio}
							</p>
						)}
					</>
				)}
				{detailsError && (
					<p role="alert" className="mt-3 text-sm text-error">
						{detailsError}
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
