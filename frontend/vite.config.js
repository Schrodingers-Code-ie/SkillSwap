import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // Send every /api request to the Spring Boot backend.
    // This avoids CORS problems while developing.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})