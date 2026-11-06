import { Navigate, useNavigate } from 'react-router'
import { getErrorMessage } from '../../api/errors'
import type { V1Credentials } from '../../api/generated'
import { useLoginMutation, useRegisterMutation } from '../../api/generated'
import { useAppDispatch, useAppSelector } from '../../app/hooks'
import { AuthLayout } from './AuthLayout'
import { CredentialsForm } from './CredentialsForm'
import { tokenReceived } from './authSlice'

export function RegisterPage() {
  const dispatch = useAppDispatch()
  const navigate = useNavigate()
  const hasToken = useAppSelector((state) => state.auth.token !== null)
  const [register, { isLoading: registering, error: registerError }] = useRegisterMutation()
  const [login, { isLoading: loggingIn, error: loginError }] = useLoginMutation()

  if (hasToken) {
    return <Navigate to="/tasks" replace />
  }

  async function handleSubmit(credentials: V1Credentials) {
    const registered = await register({ v1Credentials: credentials })
    if (registered.error) {
      return
    }
    const loggedIn = await login({ v1Credentials: credentials })
    if (loggedIn.data) {
      dispatch(tokenReceived(loggedIn.data.token))
      navigate('/tasks', { replace: true })
    }
  }

  const error = registerError ?? loginError

  return (
    <AuthLayout>
      <CredentialsForm
        submitLabel="Create account"
        pendingLabel="Creating account..."
        passwordAutoComplete="new-password"
        pending={registering || loggingIn}
        error={error ? getErrorMessage(error) : null}
        onSubmit={handleSubmit}
      />
    </AuthLayout>
  )
}
