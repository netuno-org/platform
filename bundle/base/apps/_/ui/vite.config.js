import { promises as fs } from 'fs';
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

const outputBasePath = './../public';
const outputFilePath = 'scripts/ui.js';

export default defineConfig({
  plugins: [
    react(),
    {
      closeBundle: async() => {
        // Hack Ant.Design v5 Performance Issues
        // Using Tables causes very slow interactions on the entire page because of an infinite loop,
        // that executes the scrollTo function repeatedly stressing the browser.
        const bundlePath = `${outputBasePath}/${outputFilePath}`
        let data = await fs.readFile(bundlePath, 'utf-8');
        data = data.replace('function scrollTo(', 'function $_scrollTo_antd_bug_$(');
        await fs.writeFile(bundlePath, data, 'utf-8');
      }
    }
  ],
  build: {
    sourcemap: true,
    rollupOptions: {
      input: 'src/index.jsx',
      output: {
        dir: outputBasePath,
        entryFileNames: outputFilePath,
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
