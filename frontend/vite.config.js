import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';
import tailwindcss from '@tailwindcss/vite';

// https://vite.dev/config/
export default defineConfig({
	plugins: [react(), tailwindcss()],
	server: {
		// Send every /api request and the /ws WebSocket to the Spring Boot backend.
		// This avoids CORS problems while developing, and keeps the WebSocket same-origin.
		allowedHosts: true, // Needed for cloudflate tunnel to work
		proxy: {
			'/api': 'http://localhost:8080',
			'/ws': { target: 'http://localhost:8080', ws: true },
		},
		host: true,
		watch: { usePolling: true },
	},
});
