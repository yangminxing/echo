import axios from 'axios'
import type { LoginRequest, LoginResponse } from '../types/login'

const apiClient = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded'
  }
})

apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

export const login = (data: LoginRequest) => {
  return apiClient.post<LoginResponse>('/user/login', null, {
    params: data
  })
}

export const logout = () => {
  return apiClient.post<{ code: number; msg: string }>('/user/logout')
}

export default apiClient