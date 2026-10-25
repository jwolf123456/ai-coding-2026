import request from '../utils/request'
import type {ApiResult, PageResult, User} from '../types'

const BASE = '/api/user'

export function getUserPage(current: number, size: number) {
  return request.get<any, ApiResult<PageResult<User>>>(`${BASE}/page`, { params: { current, size } })
}

export function getUserById(id: number) {
  return request.get<any, ApiResult<User>>(`${BASE}/${id}`)
}

export function createUser(data: User) {
  return request.post<any, ApiResult<void>>(BASE, data)
}

export function updateUser(data: User) {
  return request.put<any, ApiResult<void>>(BASE, data)
}

export function deleteUser(id: number) {
  return request.delete<any, ApiResult<void>>(`${BASE}/${id}`)
}