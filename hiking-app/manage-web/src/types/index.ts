export interface User {
  id: number
  username: string
  password: string
  nickname: string
  avatar: string
  phone: string
  email: string
  status: number
  createTime: string
  updateTime: string
}

export interface Line {
  id: number
  title: string
  description: string
  distance: number
  difficulty: number
  startPoint: string
  endPoint: string
  coverImage: string
  status: number
  createTime: string
  updateTime: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface ApiResult<T = any> {
  code: number
  msg: string
  data: T
}