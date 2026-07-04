import request from '../utils/request'

export function getFriendLinks(url) {
  return request({ url, method: 'get' })
}

export function addFriendLink(data) {
  return request({ url: '/friend/link/add', method: 'post', data })
}

export function updateFriendLink(id, data) {
  return request({ url: `/friend/link/update/${id}`, method: 'put', data })
}

export function deleteFriendLink(id) {
  return request({ url: `/friend/link/delete/${id}`, method: 'delete' })
}
