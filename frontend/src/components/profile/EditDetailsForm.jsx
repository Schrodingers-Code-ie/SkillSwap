import { useId, useState } from 'react';
import { updateProfile } from '../../api/skills';
import Button from '../ui/Button';
import Input from '../ui/Input';

const MAX_NAME = 100;
const MAX_BIO = 500;

export default function EditDetailsForm({ profile, onSaved, onCancel }) {
	const id = useId();
	const [name, setName] = useState(profile.displayName);
	const [bio, setBio] = useState(profile.bio ?? '');
	const [nameError, setNameError] = useState('');
	const [error, setError] = useState('');
	const [saving, setSaving] = useState(false);

	async function submit(e) {
		e.preventDefault();
		if (!name.trim()) {
			setNameError('Enter your full name.');
			return;
		}
		setNameError('');
		setError('');
		setSaving(true);
		try {
			await updateProfile(name.trim(), bio.trim() || null);
		} catch (err) {
			setError(err.message);
			setSaving(false);
			return;
		}
		onSaved();
	}

	return (
		<form
			onSubmit={submit}
			onKeyDown={(e) => e.key === 'Escape' && onCancel()}
			noValidate
			className="mx-auto mt-6 w-full max-w-md space-y-4 text-left"
		>
			<Input
				id={`${id}-name`}
				titleText="Full name"
				value={name}
				onChange={(e) => {
					setName(e.target.value);
					setNameError('');
				}}
				maxLength={MAX_NAME}
				error={nameError}
				autoFocus
				className="w-full"
			/>

			<div>
				<div className="mb-2.5 flex items-baseline justify-between">
					<label
						htmlFor={`${id}-bio`}
						className="font-main text-sm font-g-medium text-title"
					>
						Bio (optional)
					</label>
					<span className="text-xs text-subtitle">
						{bio.length} / {MAX_BIO}
					</span>
				</div>
				<textarea
					id={`${id}-bio`}
					value={bio}
					onChange={(e) => setBio(e.target.value)}
					maxLength={MAX_BIO}
					rows={3}
					placeholder="e.g. Final-year CS student. I like pairing on real projects."
					className="w-full resize-none rounded-input border border-border bg-input-bg px-3 py-2.5 font-main text-sm text-strong-text outline-none placeholder:text-input-placeholder focus:border-active-input-border"
				/>
			</div>

			{error && (
				<p role="alert" className="text-sm text-error">
					{error}
				</p>
			)}

			<div className="flex justify-center gap-3">
				<Button onClick={onCancel} disabled={saving} className="w-32">
					Cancel
				</Button>
				<Button
					type="submit"
					darkStyle
					disabled={saving}
					className="w-32 disabled:opacity-50"
				>
					{saving ? 'Saving…' : 'Save'}
				</Button>
			</div>
		</form>
	);
}
