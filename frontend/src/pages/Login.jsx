import { useState } from 'react';
import { useNavigate } from 'react-router';
import { api } from '../api/client.js';
import AuthLayout from '../components/AuthLayout.jsx';
import Button from '../components/ui/Button.jsx';
import Input from '../components/ui/Input.jsx';

const WRONG_LOGIN =
	'The email or password is incorrect. Check them and try again.';

export default function Login() {
	const [form, setForm] = useState({ email: '', password: '' });
	const [error, setError] = useState(null);
	const [showPassword, setShowPassword] = useState(false);
	const [submitting, setSubmitting] = useState(false);
	const navigate = useNavigate();

	function handleChange(e) {
		const { name, value } = e.target;
		setForm((prev) => ({ ...prev, [name]: value }));
	}

	async function handleSubmit(e) {
		e.preventDefault();
		if (!form.email.trim() || !form.password) {
			setError('Enter your email and password.');
			return;
		}

		setError(null);
		setSubmitting(true);
		try {
			// On success Spring sets the JSESSIONID cookie, the browser keeps it for us
			await api.post('/auth/login', {
				email: form.email.trim(),
				password: form.password,
			});
			navigate('/matches');
		} catch (err) {
			// 401 means wrong email or password, the backend does not say which one on purpose
			setError(err.status === 401 ? WRONG_LOGIN : err.message);
			setSubmitting(false);
		}
	}

	return (
		<AuthLayout active="signin">
			<form
				onSubmit={handleSubmit}
				noValidate
				className="flex flex-col gap-2"
			>
				{error && (
					<p
						role="alert"
						className="mb-3 rounded-input bg-error-box-bg px-3 py-3 font-main text-sm leading-5 text-error-box-text"
					>
						{error}
					</p>
				)}

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
				/>

				<Input
					type={showPassword ? 'text' : 'password'}
					id="password"
					name="password"
					titleText="Password"
					placeholderText="Your password"
					autoComplete="current-password"
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

				<Button
					darkStyle
					type="submit"
					className="mt-3 h-[42px] w-full"
					disabled={submitting}
				>
					{submitting ? 'Signing in...' : 'Sign in'}
				</Button>
			</form>
		</AuthLayout>
	);
}
