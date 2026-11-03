import { useState } from 'react';
import { useNavigate } from 'react-router';
import { api } from '../api/client.js';

export default function useLogout() {
	const navigate = useNavigate();
	const [error, setError] = useState(null);

	async function logout() {
		setError(null);
		try {
			await api.post('/auth/logout');
			navigate('/login');
		} catch {
			setError("Couldn't log out, try again.");
		}
	}

	return { logout, error };
}
