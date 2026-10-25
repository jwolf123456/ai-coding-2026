import request from '../utils/request'
import type {ApiResult, Line, PageResult} from '../types'

const BASE = '/api/line'

export function getLinePage(current: number, size: number) {
  return request.get<any, ApiResult<PageResult<Line>>>(`${BASE}/page`, { params: { current, size } })
}

export function getLineById(id: number) {
  return request.get<any, ApiResult<Line>>(`${BASE}/${id}`)
}

export function createLine(data: Line) {
  return request.post<any, ApiResult<void>>(BASE, data)
}

export function updateLine(data: Line) {
  return request.put<any, ApiResult<void>>(BASE, data)
}

export function deleteLine(id: number) {
  return request.delete<any, ApiResult<void>>(`${BASE}/${id}`)
}