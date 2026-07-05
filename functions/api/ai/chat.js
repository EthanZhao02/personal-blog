const json = (body, status = 200) => new Response(JSON.stringify(body), {
  status,
  headers: {
    'Content-Type': 'application/json; charset=utf-8',
    'Cache-Control': 'no-store',
  },
})

const extractText = (response) => {
  if (typeof response?.output_text === 'string') return response.output_text
  const chunks = []
  for (const item of response?.output || []) {
    for (const part of item?.content || []) {
      if (part?.type === 'output_text' && part?.text) chunks.push(part.text)
    }
  }
  return chunks.join('\n').trim()
}

const sanitizeMessages = (messages = []) => messages
  .filter(item => item && (item.role === 'user' || item.role === 'assistant'))
  .slice(-8)
  .map(item => ({
    role: item.role,
    content: String(item.content || '').slice(0, 1000),
  }))

export async function onRequestPost({ request, env }) {
  if (!env.OPENAI_API_KEY) {
    return json({ error: 'OPENAI_API_KEY is not configured.' }, 503)
  }

  let payload
  try {
    payload = await request.json()
  } catch {
    return json({ error: 'Invalid JSON body.' }, 400)
  }

  const question = String(payload.question || '').trim().slice(0, 1000)
  if (!question) return json({ error: 'Question is required.' }, 400)

  const locale = payload.locale === 'en' ? 'en' : 'zh'
  const siteContext = locale === 'en'
    ? 'You are the AI guide for Ethan ZhiYu, a personal technology blog about Web development, backend practice, NLP/AI learning, projects, friend links, messages, and an about page. Answer concisely and guide visitors to the right section.'
    : '你是 Ethan 智域个人技术博客的 AI 导览。这个站点记录 Web 开发、后端实践、NLP/AI 学习、项目矩阵、友链申请、留言板和个人档案。回答要简洁、有礼貌，并尽量引导访客去合适的页面。'

  const messages = [
    ...sanitizeMessages(payload.messages),
    { role: 'user', content: question },
  ]

  const upstream = await fetch('https://api.openai.com/v1/responses', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${env.OPENAI_API_KEY}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      model: env.OPENAI_MODEL || 'gpt-5.5',
      instructions: siteContext,
      input: messages,
      max_output_tokens: 420,
    }),
  })

  const data = await upstream.json().catch(() => ({}))
  if (!upstream.ok) {
    return json({
      error: 'OpenAI request failed.',
      detail: data.error?.message || upstream.statusText,
    }, upstream.status)
  }

  return json({ answer: extractText(data) || '暂时没有生成可显示的回答，请稍后再试。' })
}

export async function onRequestOptions() {
  return new Response(null, { status: 204 })
}
