import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';
import tailwindcss from '@tailwindcss/vite';

// https://vite.dev/config/
export default defineConfig({
	plugins: [react(), tailwindcss()],
	server: {
		// Send every /api request to the Spring Boot backend.
		// This avoids CORS problems while developing.
		proxy: {
			'/api': 'http://localhost:8080',
		},
		host: true,
		watch: { usePolling: true },
	},
});
