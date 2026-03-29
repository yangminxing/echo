export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  code: number
  msg: string
  token?: string
}