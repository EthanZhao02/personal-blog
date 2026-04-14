import request from '@/utils/request'

// 获取文章列表
export const getArticleList = (current, size, categoryId, tagId, keyword) => {
  const params = { current, size }
  if (categoryId) params.categoryId = categoryId
  if (tagId) params.tagId = tagId
  if (keyword) params.keyword = keyword
  return request.get('/article/list', { params })
}

// 获取文章详情
export const getArticleDetail = (id) => {
  return request.get(`/article/detail/${id}`)
}

// 获取当前用户的文章列表（包含未发布的）
export const getMyArticles = (current, size) => {
  return request.get('/article/my', { params: { current, size } })
}

// 发布文章
export const createArticle = (data) => {
  return request.post('/article/publish', data)
}

// 更新文章
export const updateArticle = (data) => {
  return request.put('/article/update', data)
}

// 删除文章
export const deleteArticle = (id) => {
  return request.delete(`/article/delete/${id}`)
}
