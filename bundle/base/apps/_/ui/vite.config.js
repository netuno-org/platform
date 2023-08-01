import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/


/**
 * Library Mode
 * https://vitejs.dev/guide/build.html#library-mode
 */

export default defineConfig({
  plugins: [
    react()
  ],
  build: {
    sourcemap: true,
    rollupOptions: {
      input: 'src/index.jsx',
      output: {
        dir: './../public',
        entryFileNames: 'scripts/ui.js',
        assetFileNames: (assetInfo) => {
          const info = assetInfo.name.split(".");
          let extType = info[info.length - 1];
          if (/png|jpe?g|svg|gif|tiff|bmp|ico/i.test(extType)) {
            return `images/[name][extname]`;
          } else if (/css/i.test(extType)) {
            return `styles/ui[extname]`;
          } else {
            return `[name][extname]`;
          }
        },
        chunkFileNames: "ui-chunk.js",
        manualChunks: undefined,
      }
    }
  }
})
