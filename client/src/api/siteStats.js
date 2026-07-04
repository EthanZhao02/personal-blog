const rawBaseURL = import.meta.env.VITE_API_BASE_URL || '/api'
const baseURL = rawBaseURL.replace(/\/$/, '')

const endpoint = (path) => `${baseURL}${path}`

const requestStats = async (path, options = {}) => {
  try {
    const response = await fetch(endpoint(path), {
      headers: options.body ? { 'Content-Type': 'application/json' } : undefined,
      ...options,
    })
    if (!response.ok) return null
    return await response.json()
  } catch {
    return null
  }
}

export const getSiteStats = () => requestStats('/site/stats')

export const recordSiteVisit = (data) => requestStats('/site/stats/visit', {
  method: 'POST',
  body: JSON.stringify(data),
})
