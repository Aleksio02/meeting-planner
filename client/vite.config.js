import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import svgr from "vite-plugin-svgr";

export default defineConfig({
  plugins: [react(), svgr()],
  server: {
    port: 3000,
    proxy: {
      '/api/auth': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/api/meetings': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/api/events': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
    },
  },
});