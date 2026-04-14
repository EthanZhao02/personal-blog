import request from '../utils/request'

export function getProjects() {
  return request({ url: '/project/list', method: 'get' })
}

export function getAllProjects() {
  return request({ url: '/project/all', method: 'get' })
}

export function addProject(data) {
  return request({ url: '/project/add', method: 'post', data })
}

export function updateProject(id, data) {
  return request({ url: `/project/update/${id}`, method: 'put', data })
}

export function deleteProject(id) {
  return request({ url: `/project/delete/${id}`, method: 'delete' })
}
