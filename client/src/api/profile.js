import request from '@/utils/request'

export const getProfile = () => request.get('/profile')

export const updateProfile = (data) => request.put('/profile', data)
