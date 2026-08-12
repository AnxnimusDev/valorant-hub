import axios from 'axios'

/**
 * Cliente HTTP único hacia el api-gateway. Ningún feature debe instanciar
 * axios por su cuenta ni apuntar directo a un microservicio — todo pasa por
 * el Gateway (JWT, CORS y rate limiting centralizados ahí, ver Fase 7).
 */
export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('valoranthub_access_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
