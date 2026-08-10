import { createBrowserRouter } from 'react-router-dom'
import { AppLayout } from '@/components/features/AppLayout'
import { LoginPage } from '@/pages/LoginPage'
import { RegisterPage } from '@/pages/RegisterPage'
import { DashboardPage } from '@/pages/DashboardPage'
import { MatchAnalysisPage } from '@/pages/MatchAnalysisPage'
import { RecommendationsPage } from '@/pages/RecommendationsPage'
import { CoachingPage } from '@/pages/CoachingPage'
import { CoachingChatPage } from '@/pages/CoachingChatPage'
import { PlayerProfilePage } from '@/pages/PlayerProfilePage'
import { RankPredictionPage } from '@/pages/RankPredictionPage'
import { GamificationPage } from '@/pages/GamificationPage'

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/register',
    element: <RegisterPage />,
  },
  {
    // El guard de autenticación real (redirigir a /login sin sesión) se
    // implementa en Fase 5, cuando coaching-platform expone /auth.
    element: <AppLayout />,
    children: [
      { path: '/', element: <DashboardPage /> },
      { path: '/analisis/:matchId', element: <MatchAnalysisPage /> },
      { path: '/recomendaciones', element: <RecommendationsPage /> },
      { path: '/coaching', element: <CoachingPage /> },
      { path: '/coaching/:sessionId/chat', element: <CoachingChatPage /> },
      { path: '/perfil', element: <PlayerProfilePage /> },
      { path: '/prediccion', element: <RankPredictionPage /> },
      { path: '/gamificacion', element: <GamificationPage /> },
    ],
  },
])
