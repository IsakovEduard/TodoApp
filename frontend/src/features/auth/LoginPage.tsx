import { Navigate, useLocation, useNavigate } from 'react-router'
import { getErrorMessage, getErrorStatus } from '../../api/errors'
import type { V1Credentials } from '../../api/generated'
import { useLoginMutation } from '../../api/generated'
import { useAppDispatch, useAppSelector } from '../../app/hooks'
import { AuthLayout } from './AuthLayout'
import { CredentialsForm } from './CredentialsForm'
import { tokenReceived } from './authSlice'

const DEFAULT_DESTINATION = '/tasks'

export function LoginPage() {
  const dispatch = useAppDispatch()
  const navigate = useNavigate()
  const location = useLocation()
  const hasToken = useAppSelector((state) => state.auth.token !== null)
  const [login, { isLoading, error }] = useLoginMutation()

  if (hasToken) {
    return <Navigate to={DEFAULT_DESTINATION} replace />
  }

  async function handleSubmit(credentials: V1Credentials) {
    const result = await login({ v1Credentials: credentials })
    if (result.data) {
      dispatch(tokenReceived(result.data.token))
      const destination = (location.state as { from?: string } | null)?.from ?? DEFAULT_DESTINATION
      navigate(destination, { replace: true })
    }
  }

  const message = error
    ? getErrorStatus(error) === 401
      ? 'Invalid username or password.'
      : getErrorMessage(error)
    : null

  return (
    <AuthLayout>
      <CredentialsForm
        submitLabel="Log in"
        pendingLabel="Logging in..."
        passwordAutoComplete="current-password"
        pending={isLoading}
        error={message}
        onSubmit={handleSubmit}
      />
    </AuthLayout>
  )
}
