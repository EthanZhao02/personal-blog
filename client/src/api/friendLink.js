import request from '../utils/request'

export function getFriendLinks() {
  return request({ url: '/friend/link/list', method: 'get' })
}

export function getAllFriendLinks() {
  return request({ url: '/friend/link/all', method: 'get' })
}

export function applyFriendLink(data) {
  return request({ url: '/friend/link/apply', method: 'post', data })
}

export function approveFriendLink(id, isActive) {
  return request({ url: `/friend/link/approve/${id}`, method: 'put', data: { isActive } })
}

export function updateFriendLink(id, data) {
  return request({ url: `/friend/link/update/${id}`, method: 'put', data })
}

export function deleteFriendLink(id) {
  return request({ url: `/friend/link/delete/${id}`, method: 'delete' })
}
