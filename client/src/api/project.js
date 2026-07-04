import request from '../utils/request'

export function getProjects(url) {
  return request({ url, method: 'get' })
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
