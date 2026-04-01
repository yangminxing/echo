import axios from 'axios'
import type { FormDefinition, PageResult } from '../types/formDesigner'

const API_PREFIX = '/api/form-definition'

export const getFormList = () => {
  return axios.get<FormDefinition[]>(`${API_PREFIX}/list`)
}

export const getFormPage = (current: number, size: number) => {
  return axios.get<PageResult<FormDefinition>>(`${API_PREFIX}/page`, {
    params: { current, size }
  })
}

export const getFormById = (id: string) => {
  return axios.get<FormDefinition>(`${API_PREFIX}/${id}`)
}

export const saveForm = (data: FormDefinition) => {
  return axios.post<boolean>(`${API_PREFIX}`, data)
}

export const updateForm = (data: FormDefinition) => {
  return axios.put<boolean>(`${API_PREFIX}`, data)
}

export const deleteForm = (id: string) => {
  return axios.delete<boolean>(`${API_PREFIX}/${id}`)
}

export const syncToPhysicalTable = (id: string) => {
  return axios.post<boolean>(`${API_PREFIX}/sync/${id}`)
}
