import request from '../utils/request'

export function getCommentList(articleId) {
  return request({ url: `/comment/list/${articleId}`, method: 'get' })
}

export function addComment(data) {
  return request({ url: '/comment/add', method: 'post', data })
}

export function deleteComment(id) {
  return request({ url: `/comment/delete/${id}`, method: 'delete' })
}
