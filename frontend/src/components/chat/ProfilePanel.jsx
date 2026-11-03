import Profile from '../ui/Profile';
import SkillBubble from '../ui/SkillBubble';

// Right column of the Chat page
// friend: the open friend's profile
// myProfile: the logged-in user's profile
export default function ProfilePanel({ friend, myProfile, className = '' }) {
	const firstName = friend.displayName.split(' ')[0];
	const myLearnIds = new Set(
		myProfile.learnSkills.map((skill) => skill.skillId),
	);
	const theirLearnIds = new Set(
		friend.learnSkills.map((skill) => skill.skillId),
	);

	// friend teaches it and logged-in user wants it, and the other way round
	const teachesYou = friend.teachSkills.filter((skill) =>
		myLearnIds.has(skill.skillId),
	);
	const youTeach = myProfile.teachSkills.filter((skill) =>
		theirLearnIds.has(skill.skillId),
	);

	return (
		<aside className={`flex flex-col items-start ${className}`}>
			<Profile name={friend.displayName} size="lg" />
			<h2 className="mt-3 break-words text-xl text-strong-text">
				{friend.displayName}
			</h2>

			<SkillSection
				title={`${firstName} teaches you`}
				skills={teachesYou}
				color="green"
			/>
			<SkillSection
				title={`You teach ${firstName}`}
				skills={youTeach}
				color="yellow"
			/>

			{teachesYou.length === 0 && youTeach.length === 0 && (
				<p className="mt-5 text-sm text-subtitle">
					No skills to swap yet. <br /> Add skills on your profile to
					see matches here.
				</p>
			)}
		</aside>
	);
}

function SkillSection({ title, skills, color }) {
	if (skills.length === 0) return null;

	return (
		<section className="mt-5">
			<h3 className="font-subtitle text-[11px] uppercase tracking-wider text-subtitle">
				{title}
			</h3>
			<ul className="mt-2 flex flex-wrap gap-1.5">
				{skills.map((skill) => (
					<li key={skill.skillId}>
						<SkillBubble color={color}>{skill.name}</SkillBubble>
					</li>
				))}
			</ul>
		</section>
	);
}
