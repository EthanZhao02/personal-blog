import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import history from 'connect-history-api-fallback'

const repoName = process.env.GITHUB_REPOSITORY?.split('/')[1]
const pagesBase = process.env.BASE_PATH || (repoName ? `/${repoName}/` : '/')
const base = process.env.GITHUB_PAGES === 'true' ? pagesBase : '/'

export default defineConfig({
  base,
  plugins: [
    vue(),
    {
      name: 'spa-fallback',
      apply: 'serve',
      configureServer(server) {
        // 只对非 API 路径应用 history fallback
        server.middlewares.use((req, res, next) => {
          if (req.url.startsWith('/api') || req.url.startsWith('/comment') ||
              req.url.startsWith('/friend') || req.url.startsWith('/upload') ||
              req.url.startsWith('/uploads') || req.url.startsWith('/site') ||
              req.url.startsWith('/@')) {
            return next()
          }
          history({ index: '/index.html' })(req, res, next)
        })
      }
    }
  ],
  resolve: {
    alias: { '@': path.resolve(__dirname, 'src') }
  },
  server: {
    port: 3000,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api/, '')
      },
      '/api/friend': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api\/friend/, '/friend')
      },
      '/comment': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/friend/link': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/friend/apply': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/friend/all': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/site/stats': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/upload/file': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
