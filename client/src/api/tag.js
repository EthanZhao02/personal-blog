import request from '@/utils/request'

// 获取标签列表
export const getTagList = () => {
  return request.get('/tag/list')
}

// 添加标签
export const addTag = (data) => {
  return request.post('/tag/add', data)
}

// 更新标签
export const updateTag = (data) => {
  return request.put('/tag/update', data)
}

// 删除标签
export const deleteTag = (id) => {
  return request.delete(`/tag/delete/${id}`)
}
