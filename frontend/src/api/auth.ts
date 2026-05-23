import axios from 'axios'

const api = axios.create({
  baseURL: '',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true
})

export interface LoginParams {
  username: string
  password: string
  realm: string
  clientId: string
  redirectUri: string
  state: string
  scope: string
  responseType: string
  nonce: string
  rememberMe: boolean
}

export interface LoginResult {
  success: boolean
  redirectUrl: string
  username: string
  email: string
}

export interface ThemeConfig {
  backgroundImage: string
  backgroundColor: string
  logoUrl: string
  title: string
  primaryColor: string
}

export async function login(params: LoginParams): Promise<LoginResult> {
  const { data } = await api.post<LoginResult>('/api/login', params)
  return data
}

export async function getThemeConfig(): Promise<ThemeConfig> {
  const { data } = await api.get<ThemeConfig>('/api/theme-config')
  return data
}
