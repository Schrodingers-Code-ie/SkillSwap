import { useState } from 'react';
import SkillCard from '../components/skills/SkillCard';
import SkillsModal from '../components/skills/SkillsModal';
import { useProfile } from '../components/skills/profile-context';

export default function Profile() {
	const { profile, error, loading, missingSkills } = useProfile();
	const [editing, setEditing] = useState(false);

	if (loading) return <p className="p-10">Loading…</p>;
	if (!profile) return <p className="p-10 text-red-700">{error}</p>;

	return (
		<main className="mx-auto max-w-3xl p-10 text-center">
			<h1 className="text-3xl text-[#3d362c]">{profile.displayName}</h1>
			{profile.bio && (
				<p className="mt-3 text-[#6b6154]">{profile.bio}</p>
			)}

			<button
				onClick={() => setEditing(true)}
				className="mt-6 rounded-full bg-[#e3ded9] px-5 py-2"
			>
				Edit your skills
			</button>

			<section className="mt-8 space-y-3 text-left">
				<h2 className="text-sm">
					Skills you can teach ({profile.teachSkills.length})
				</h2>
				{profile.teachSkills.map((s) => (
					<SkillCard
						key={s.skillId}
						name={s.name}
						proficiency={s.proficiency}
					/>
				))}
				<hr className="my-6 border-[#e3ddd6]" />
				<h2 className="text-sm">
					Skills you want to learn ({profile.learnSkills.length})
				</h2>
				{profile.learnSkills.map((s) => (
					<SkillCard key={s.skillId} name={s.name} />
				))}
			</section>

			{(missingSkills || editing) && (
				<SkillsModal
					onClose={
						missingSkills ? undefined : () => setEditing(false)
					}
				/>
			)}
		</main>
	);
}
