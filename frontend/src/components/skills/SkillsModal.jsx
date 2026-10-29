import { useEffect, useState } from 'react';
import {
	addLearnSkill,
	addTeachSkill,
	getSkills,
	removeLearnSkill,
	removeTeachSkill,
} from '../../api/skills';
import { useProfile } from './profile-context';
import SkillCard from './SkillCard';
import SkillForm from './SkillForm';

// onClose is only passed when the user may close it (they already have one of each)
export default function SkillsModal({ onClose }) {
	const { profile, reload } = useProfile();
	const [catalogue, setCatalogue] = useState([]);
	const [error, setError] = useState('');
	const [step, setStep] = useState(() =>
		profile.teachSkills.length === 0 ? 1 : 2,
	);

	useEffect(() => {
		getSkills()
			.then(setCatalogue)
			.catch((e) => setError(e.message));
	}, []);

	const teaching = step === 1;
	const items = teaching ? profile.teachSkills : profile.learnSkills;
	const takenIds = new Set(items.map((s) => s.skillId));

	async function run(action) {
		setError('');
		try {
			await action();
			await reload(); // the page always shows what is in the database
		} catch (e) {
			setError(e.message);
			throw e;
		}
	}

	return (
		<div className="fixed inset-0 z-50 flex items-center justify-center overflow-y-auto bg-black/40 p-6">
			<div
				role="dialog"
				aria-modal="true"
				aria-labelledby="skills-title"
				className="w-full max-w-3xl rounded-[2rem] bg-[#fbf9f5] p-10 shadow-xl"
			>
				<div className="mb-6 flex items-center gap-4 text-xs font-semibold tracking-wide text-[#4a4136]">
					STEP {step} OF 2
					<span className="flex gap-1">
						<span className="h-1 w-20 rounded bg-[#3d362c]" />
						<span
							className={`h-1 w-20 rounded ${step === 2 ? 'bg-[#3d362c]' : 'bg-[#e3ddd6]'}`}
						/>
					</span>
				</div>

				<h2 id="skills-title" className="text-3xl text-[#4a4136]">
					{teaching
						? 'What can you teach?'
						: 'What do you want to learn?'}
				</h2>
				<p className="mb-6 mt-2 text-[#6b6154]">
					{teaching
						? 'Add each skill you are happy to share and how good you are at it.'
						: 'Add the skills you want to pick up.'}
				</p>

				<SkillForm
					key={step}
					catalogue={catalogue}
					takenIds={takenIds}
					withLevel={teaching}
					onAdd={(id, level) =>
						run(() =>
							teaching
								? addTeachSkill(id, level)
								: addLearnSkill(id),
						)
					}
				/>

				{error && (
					<p role="alert" className="mt-3 text-sm text-red-700">
						{error}
					</p>
				)}

				<h3 className="mb-3 mt-8 text-sm text-[#3d362c]">
					{teaching
						? 'Skills you can teach'
						: 'Skills you want to learn'}{' '}
					({items.length})
				</h3>
				<div className="max-h-64 space-y-3 overflow-y-auto">
					{items.length === 0 && (
						<p className="text-sm text-[#8a8173]">No skills yet.</p>
					)}
					{items.map((s) => (
						<SkillCard
							key={s.skillId}
							name={s.name}
							proficiency={s.proficiency}
							onRemove={() =>
								run(() =>
									teaching
										? removeTeachSkill(s.skillId)
										: removeLearnSkill(s.skillId),
								)
							}
						/>
					))}
				</div>

				<div className="mt-8 flex items-center justify-between border-t border-[#e3ddd6] pt-6">
					{step === 2 ? (
						<button
							className="underline"
							onClick={() => setStep(1)}
						>
							Back
						</button>
					) : (
						<span />
					)}
					{teaching ? (
						<button
							disabled={profile.teachSkills.length === 0}
							onClick={() => setStep(2)}
							className="rounded-full bg-[#4a4136] px-8 py-3 text-white shadow disabled:opacity-40"
						>
							Continue
						</button>
					) : (
						onClose && (
							<button
								onClick={onClose}
								className="rounded-full bg-[#4a4136] px-8 py-3 text-white shadow"
							>
								Done
							</button>
						)
					)}
				</div>
			</div>
		</div>
	);
}
