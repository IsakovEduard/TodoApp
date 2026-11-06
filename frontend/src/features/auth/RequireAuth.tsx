import { Navigate, Outlet, useLocation } from 'react-router'
import { useAppSelector } from '../../app/hooks'

export function RequireAuth() {
  const hasToken = useAppSelector((state) => state.auth.token !== null)
  const location = useLocation()

  if (!hasToken) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }
  return <Outlet />
}
