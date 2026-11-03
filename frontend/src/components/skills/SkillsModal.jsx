import { useEffect, useState } from 'react';
import {
	addLearnSkill,
	addTeachSkill,
	getSkills,
	removeLearnSkill,
	removeTeachSkill,
} from '../../api/skills';
import useLogout from '../../hooks/useLogout';
import { useProfile } from './profile-context';
import SkillCard from './SkillCard';
import SkillForm from './SkillForm';

// onClose is only used in edit mode (opened from "Edit your skills")
export default function SkillsModal({ onClose }) {
	const { profile, reload, setupOpen, finishSetup, missingSkills } =
		useProfile();
	const { logout, error: logoutError } = useLogout();
	const [catalogue, setCatalogue] = useState([]);
	const [error, setError] = useState('');
	const [step, setStep] = useState(() =>
		setupOpen && profile.teachSkills.length > 0 ? 2 : 1,
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
			await reload();
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
				className="w-full max-w-3xl rounded-container-card bg-card-bg p-10 text-left shadow-container"
			>
				<div className="mb-6 flex items-center justify-between">
					<div className="flex items-center gap-4 text-xs font-semibold tracking-wide text-strong-text">
						STEP {step} OF 2
						<span className="flex gap-1">
							<span className="h-1 w-20 rounded bg-dark-button-bg" />
							<span
								className={`h-1 w-20 rounded ${step === 2 ? 'bg-dark-button-bg' : 'bg-card-border'}`}
							/>
						</span>
					</div>
					{setupOpen && (
						<div className="text-right">
							<button
								type="button"
								onClick={logout}
								className="text-sm text-subtitle underline"
							>
								Log out
							</button>
							{logoutError && (
								<p className="text-xs text-error">
									{logoutError}
								</p>
							)}
						</div>
					)}
				</div>

				<h2 id="skills-title" className="text-2xl text-strong-text">
					{teaching
						? 'What can you teach?'
						: 'What do you want to learn?'}
				</h2>
				<p className="mb-6 mt-2 text-sm text-subtitle">
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
					<p role="alert" className="mt-3 text-sm text-error">
						{error}
					</p>
				)}

				<h3 className="mb-3 mt-8 text-sm text-strong-text">
					{teaching
						? 'Skills you can teach'
						: 'Skills you want to learn'}{' '}
					({items.length})
				</h3>
				<div className="max-h-64 space-y-3 overflow-y-auto">
					{items.length === 0 && (
						<p className="text-sm text-subtitle">No skills yet.</p>
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

				<div className="mt-8 flex items-center justify-between border-t border-card-border pt-6">
					{step === 2 ? (
						<button
							type="button"
							className="text-strong-text underline"
							onClick={() => setStep(1)}
						>
							Back
						</button>
					) : (
						<span />
					)}
					{teaching ? (
						<button
							type="button"
							disabled={profile.teachSkills.length === 0}
							onClick={() => setStep(2)}
							className="rounded-full bg-dark-button-bg px-8 py-3 text-dark-button-text disabled:opacity-40"
						>
							Continue
						</button>
					) : (
						<button
							type="button"
							disabled={missingSkills}
							onClick={setupOpen ? finishSetup : onClose}
							className="rounded-full bg-dark-button-bg px-8 py-3 text-dark-button-text disabled:opacity-40"
						>
							{setupOpen ? 'Finish setup' : 'Done'}
						</button>
					)}
				</div>
			</div>
		</div>
	);
}
