import request from '../utils/request'

export function getMessageList() {
  return request({ url: '/message/list', method: 'get' })
}

export function addMessage(data) {
  return request({ url: '/message/add', method: 'post', data })
}

export function replyMessage(id, reply) {
  return request({ url: `/message/reply/${id}`, method: 'post', data: { reply } })
}

export function deleteMessage(id) {
  return request({ url: `/message/delete/${id}`, method: 'delete' })
}
