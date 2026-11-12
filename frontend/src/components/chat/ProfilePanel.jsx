import Profile from '../ui/Profile';
import SkillBubble from '../ui/SkillBubble';

// Right column of the Chat page
// friend: the open friend's profile// friend: the open friend's chat entry, used for the name (available straight away)
// profile: the friend's loaded profile with skills; undefined while loading, { failed: true } on error
// myProfile: the logged-in user's profile
export default function ProfilePanel({
	friend,
	profile,
	myProfile,
	className = '',
}) {
	const firstName = friend.displayName.split(' ')[0];

	return (
		<aside className={`flex flex-col items-start ${className}`}>
			<Profile name={friend.displayName} size="lg" />
			<h2 className="mt-3 break-words text-xl text-strong-text">
				{friend.displayName}
			</h2>

			{!profile && (
				<p role="status" className="mt-5 text-sm text-subtitle">
					Loading profile
				</p>
			)}

			{profile?.failed && (
				<p role="alert" className="mt-5 text-sm text-subtitle">
					Couldn't load {firstName}'s profile.
				</p>
			)}

			{profile && !profile.failed && (
				<SkillMatches
					profile={profile}
					myProfile={myProfile}
					firstName={firstName}
				/>
			)}
		</aside>
	);
}

// The skills both users can swap. Only rendered once the friend's profile has loaded.
function SkillMatches({ profile, myProfile, firstName }) {
	const myLearnIds = new Set(
		myProfile.learnSkills.map((skill) => skill.skillId),
	);
	const theirLearnIds = new Set(
		profile.learnSkills.map((skill) => skill.skillId),
	);

	// friend teaches it and logged-in user wants it, and the other way round
	const teachesYou = profile.teachSkills.filter((skill) =>
		myLearnIds.has(skill.skillId),
	);
	const youTeach = myProfile.teachSkills.filter((skill) =>
		theirLearnIds.has(skill.skillId),
	);

	return (
		<>
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
		</>
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
