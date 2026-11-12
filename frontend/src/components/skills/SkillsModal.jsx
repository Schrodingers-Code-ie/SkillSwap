import { useEffect, useState } from 'react';
import {
	addLearnSkill,
	addTeachSkill,
	getSkills,
	removeLearnSkill,
	removeTeachSkill,
} from '../../api/skills';
import cross_icon from '../../assets/cross_icon.svg';
import useLogout from '../../hooks/useLogout';
import { useProfile } from './profile-context';
import SkillCard from './SkillCard';
import SkillForm from './SkillForm';

const DARK_BUTTON =
	'h-[39px] w-[144px] cursor-pointer rounded-full bg-dark-button-bg text-[15px] text-dark-button-text shadow-dark-button transition hover:shadow-dark-button-hover active:bg-dark-button-pressed-bg disabled:cursor-default disabled:opacity-40 disabled:shadow-none';

//onClose is only used in edit mode
//if skills are missing,the pop-up can't be closed
export default function SkillsModal({ onClose }) {
	const { profile, reload, setupOpen, finishSetup, missingSkills } =
		useProfile();
	const { logout, error: logoutError } = useLogout();
	const [catalogue, setCatalogue] = useState([]);
	const [error, setError] = useState('');
	const [removing, setRemoving] = useState(false);
	const [step, setStep] = useState(() =>
		setupOpen && profile.teachSkills.length > 0 ? 2 : 1,
	);

	const closable = !setupOpen;

	useEffect(() => {
		getSkills()
			.then(setCatalogue)
			.catch((e) => setError(e.message));
	}, []);

	useEffect(() => {
		if (!closable) return;
		const onKey = (e) => e.key === 'Escape' && onClose();
		document.addEventListener('keydown', onKey);
		return () => document.removeEventListener('keydown', onKey);
	}, [closable, onClose]);

	const teaching = step === 1;
	const items = teaching ? profile.teachSkills : profile.learnSkills;
	const takenIds = new Set(items.map((s) => s.skillId));

	//the change is saved once action() is done
	async function run(action) {
		setError('');
		try {
			await action();
		} catch (e) {
			setError(e.message);
			throw e;
		}
		try {
			await reload();
		} catch (e) {
			setError(e.message);
		}
	}

	async function remove(skillId) {
		if (removing) return;
		setRemoving(true);
		try {
			await run(() =>
				teaching
					? removeTeachSkill(skillId)
					: removeLearnSkill(skillId),
			);
		} catch {
			// error is already shown
		} finally {
			setRemoving(false);
		}
	}

	function finish() {
		finishSetup();
		onClose?.();
	}

	return (
		<div
			className="fixed inset-0 z-50 flex items-center justify-center overflow-y-auto bg-black/40 p-6"
			onMouseDown={(e) => {
				if (closable && e.target === e.currentTarget) onClose();
			}}
		>
			<div
				role="dialog"
				aria-modal="true"
				aria-labelledby="skills-title"
				className="w-full max-w-[760px] rounded-container-card border border-container-border bg-card-bg p-9 text-left shadow-container"
			>
				<div className="mb-5 flex items-start justify-between">
					<div className="flex items-center gap-[30px] pt-1 text-[11px] font-g-medium uppercase tracking-wider text-subtitle">
						Step {step} of 2
						<span className="flex gap-1">
							<span className="h-1 w-[58px] rounded-full bg-strong-text" />
							<span
								className={`h-1 w-[58px] rounded-full ${step === 2 ? 'bg-strong-text' : 'bg-dark-button-bg/12'}`}
							/>
						</span>
					</div>
					{setupOpen ? (
						<div className="text-right">
							<button
								type="button"
								onClick={logout}
								className="cursor-pointer text-sm text-subtitle underline"
							>
								Log out
							</button>
							{logoutError && (
								<p className="text-xs text-error">
									{logoutError}
								</p>
							)}
						</div>
					) : (
						<button
							type="button"
							onClick={onClose}
							aria-label="Close"
							className="-mr-2 -mt-2 flex size-8 cursor-pointer items-center justify-center rounded-full hover:bg-light-button-bg"
						>
							<img src={cross_icon} alt="" className="size-4" />
						</button>
					)}
				</div>

				<h2 id="skills-title" className="text-2xl text-title">
					{teaching
						? 'What can you teach?'
						: 'What do you want to learn?'}
				</h2>
				<p className="mb-6 mt-2.5 text-sm text-subtitle">
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

				<h3 className="mb-3.5 mt-5 text-[13px] text-title">
					{teaching
						? 'Skills you can teach'
						: 'Skills you want to learn'}{' '}
					({items.length})
				</h3>
				<div className="scrollbar-soft max-h-64 space-y-3 overflow-y-auto">
					{items.length === 0 && (
						<p className="text-sm text-subtitle">No skills yet.</p>
					)}
					{items.map((s) => (
						<SkillCard
							key={s.skillId}
							name={s.name}
							proficiency={s.proficiency}
							disabled={removing}
							onRemove={() => remove(s.skillId)}
						/>
					))}
				</div>

				<div className="mt-6 flex items-center justify-between border-t border-card-border pt-3">
					{step === 2 ? (
						<button
							type="button"
							className="cursor-pointer text-sm text-strong-text underline"
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
							className={DARK_BUTTON}
						>
							Continue
						</button>
					) : (
						<button
							type="button"
							disabled={missingSkills}
							onClick={setupOpen ? finish : onClose}
							className={DARK_BUTTON}
						>
							{setupOpen ? 'Finish setup' : 'Done'}
						</button>
					)}
				</div>
			</div>
		</div>
	);
}