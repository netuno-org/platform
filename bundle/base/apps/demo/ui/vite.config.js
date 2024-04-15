import { defineConfig } from 'vite'
import vitePluginImp from 'vite-plugin-imp';
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
      },
      onLog(level, log, handler) {
        if (log.cause && log.cause.message === `Can't resolve original location of error.`) {
          return;
        }
        handler(level, log);
      },
      onwarn: (warning, warn) => {
        if (warning.code === 'MODULE_LEVEL_DIRECTIVE' || warning.code == 'EVAL') {
          return;
        }
        warn(warning);
      }
    }
  }
})
