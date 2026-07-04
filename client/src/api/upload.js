import request from '@/utils/request'

export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 60000
  })
}

export const uploadAttachment = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload/attachment', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 60000
  })
}
