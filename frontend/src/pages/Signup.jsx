import { useState } from 'react';
import { useNavigate } from 'react-router';
import { api } from '../api/client.js';
import AuthLayout from '../components/AuthLayout.jsx';
import Button from '../components/ui/Button.jsx';
import Input from '../components/ui/Input.jsx';
import calendarIcon from '../assets/calendar.svg';

// Shown under the password field. The backend only checks the length,
// the other rules come from the design.
const PASSWORD_RULES = [
	{ label: '8 or more characters', test: (p) => p.length >= 8 },
	{ label: 'A number', test: (p) => /\d/.test(p) },
	{
		label: 'Upper and lower case',
		test: (p) => /[a-z]/.test(p) && /[A-Z]/.test(p),
	},
	{ label: 'A symbol', test: (p) => /[^A-Za-z0-9]/.test(p) },
];

// dateOfBirth is "YYYY-MM-DD" from the date input
function isAdult(dateOfBirth) {
	const [year, month, day] = dateOfBirth.split('-').map(Number);
	const eighteenthBirthday = new Date(year + 18, month - 1, day);
	return eighteenthBirthday <= new Date();
}

// Same checks the backend does, so most mistakes show up before sending
function validate({ displayName, username, dateOfBirth, email, password }) {
	const errors = {};
	if (!displayName.trim()) {
		errors.displayName = 'Enter your full name.';
	}
	if (username.trim().length < 3 || username.trim().length > 20) {
		errors.username = 'Username must be 3 to 20 characters.';
	} else if (!/^[A-Za-z0-9_]+$/.test(username.trim())) {
		errors.username = 'Only letters, numbers and underscore.';
	}
	if (!dateOfBirth) {
		errors.dateOfBirth = 'Enter your date of birth.';
	} else if (!isAdult(dateOfBirth)) {
		errors.dateOfBirth = 'You need to be at least 18.';
	}
	if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
		errors.email = 'Invalid email address.';
	}
	if (!PASSWORD_RULES.every((rule) => rule.test(password))) {
		errors.password = 'Include all the requirements in your password.';
	}
	return errors;
}

// The backend sends one message, not a list per field,
// so we guess the field from the words in it
function errorFromBackend(err, username) {
	const message = err.message;
	const lower = message.toLowerCase();
	if (lower.includes('username')) {
		return err.status === 409
			? { username: `@${username.trim()} is taken. Try another.` }
			: { username: message };
	}
	if (lower.includes('email')) return { email: message };
	if (lower.includes('password')) return { password: message };
	if (lower.includes('display name')) return { displayName: message };
	if (lower.includes('18')) return { dateOfBirth: message };
	return { form: message };
}

export default function Signup() {
	const [form, setForm] = useState({
		displayName: '',
		username: '',
		dateOfBirth: '',
		email: '',
		password: '',
	});
	const [errors, setErrors] = useState({});
	const [triedSubmit, setTriedSubmit] = useState(false);
	const [showPassword, setShowPassword] = useState(false);
	const [submitting, setSubmitting] = useState(false);
	const navigate = useNavigate();

	// One handler for every field, it uses the input's name to know which one changed
	function handleChange(e) {
		const { name, value } = e.target;
		setForm((prev) => ({ ...prev, [name]: value }));
		setErrors((prev) => ({ ...prev, [name]: undefined, form: undefined }));
	}

	async function handleSubmit(e) {
		e.preventDefault();
		setTriedSubmit(true);

		const found = validate(form);
		setErrors(found);
		if (Object.keys(found).length > 0) return;

		setSubmitting(true);
		try {
			await api.post('/auth/register', {
				email: form.email.trim(),
				username: form.username.trim(),
				password: form.password,
				displayName: form.displayName.trim(),
				// We only send whether they are 18+, the date itself is not stored
				confirmedAdult: true,
			});
		} catch (err) {
			setErrors(errorFromBackend(err, form.username));
			setSubmitting(false);
			return;
		}

		try {
			// Register only creates the user, login starts the session (JSESSIONID cookie)
			await api.post('/auth/login', {
				email: form.email.trim(),
				password: form.password,
			});
			navigate('/signup/skills');
		} catch {
			// The account exists now, so let them sign in by hand
			navigate('/login');
		}
	}

	const hasFieldErrors = Object.keys(errors).some(
		(key) => key !== 'form' && errors[key],
	);

	return (
		<AuthLayout active="signup">
			<form
				onSubmit={handleSubmit}
				noValidate
				className="flex flex-col gap-2"
			>
				<Input
					type="text"
					id="displayName"
					name="displayName"
					titleText="Full name"
					placeholderText="Your full name"
					autoComplete="name"
					className="w-full"
					value={form.displayName}
					onChange={handleChange}
					error={errors.displayName}
				/>

				<div className="grid grid-cols-2 gap-x-[30px]">
					<Input
						type="text"
						id="username"
						name="username"
						titleText="Username"
						placeholderText="Letters, numbers, _"
						autoComplete="username"
						className="w-full"
						value={form.username}
						onChange={handleChange}
						error={errors.username}
					/>
					<Input
						type="date"
						id="dateOfBirth"
						name="dateOfBirth"
						titleText="Date of birth"
						className="w-full"
						value={form.dateOfBirth}
						onChange={handleChange}
						// Open the browser's date picker from anywhere in the field
						onClick={(e) => e.currentTarget.showPicker?.()}
						error={errors.dateOfBirth}
						trailing={
							<img
								src={calendarIcon}
								alt=""
								className="pointer-events-none"
							/>
						}
					/>
				</div>

				<Input
					type="email"
					id="email"
					name="email"
					titleText="Email"
					placeholderText="name@example.com"
					autoComplete="email"
					className="w-full"
					value={form.email}
					onChange={handleChange}
					error={errors.email}
				/>

				<div>
					<Input
						type={showPassword ? 'text' : 'password'}
						id="password"
						name="password"
						titleText="Password"
						placeholderText="Your password"
						autoComplete="new-password"
						className="w-full"
						value={form.password}
						onChange={handleChange}
						trailing={
							<button
								type="button"
								onClick={() => setShowPassword((show) => !show)}
								className="cursor-pointer px-1 font-main font-g-medium text-xs text-subtitle"
							>
								{showPassword ? 'Hide' : 'Show'}
							</button>
						}
					/>
					<ul className="mt-[13px] grid grid-cols-2 gap-x-[30px] gap-y-[5px] pl-4 font-main text-xs leading-[15px]">
						{PASSWORD_RULES.map((rule) => (
							<li
								key={rule.label}
								className={`list-disc ${ruleColor(rule, form.password, triedSubmit)}`}
							>
								{rule.label}
							</li>
						))}
					</ul>
					{errors.password && (
						<p className="mt-2 font-main text-xs leading-[15px] text-error">
							{errors.password}
						</p>
					)}
				</div>

				<Button
					darkStyle
					type="submit"
					className="mt-3 h-[42px] w-full"
					disabled={submitting}
				>
					{submitting ? 'Creating account...' : 'Sign up'}
				</Button>

				{(hasFieldErrors || errors.form) && (
					<p
						role="alert"
						className="text-center font-main text-xs leading-[15px] text-error"
					>
						{errors.form ??
							'Fix the highlighted fields to continue.'}
					</p>
				)}
			</form>
		</AuthLayout>
	);
}

// Grey until they type, green when a rule is met,
// red for missed rules once they have tried to submit
function ruleColor(rule, password, triedSubmit) {
	if (rule.test(password)) return password ? 'text-correct' : 'text-subtitle';
	return triedSubmit ? 'text-error' : 'text-subtitle';
}
