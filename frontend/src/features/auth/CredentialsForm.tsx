import { useId, useState, type FormEvent } from 'react'
import type { V1Credentials } from '../../api/generated'

interface CredentialsFormProps {
  submitLabel: string
  pendingLabel: string
  passwordAutoComplete: 'current-password' | 'new-password'
  pending: boolean
  error: string | null
  onSubmit: (credentials: V1Credentials) => void
}

const inputClass =
  'h-12 w-full rounded-[10px] border border-border-strong bg-surface px-3.5 text-base text-ink placeholder:text-muted aria-[invalid=true]:border-danger'

export function CredentialsForm({
  submitLabel,
  pendingLabel,
  passwordAutoComplete,
  pending,
  error,
  onSubmit,
}: CredentialsFormProps) {
  const usernameId = useId()
  const passwordId = useId()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [submitted, setSubmitted] = useState(false)

  const usernameMissing = submitted && username.trim() === ''
  const passwordMissing = submitted && password.trim() === ''

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitted(true)
    if (username.trim() === '' || password.trim() === '') {
      return
    }
    onSubmit({ username, password })
  }

  return (
    <form onSubmit={handleSubmit} noValidate className="flex flex-col gap-5">
      {error && (
        <p role="alert" className="rounded-[10px] bg-danger-soft px-3.5 py-3 text-sm font-medium text-danger">
          {error}
        </p>
      )}

      <div className="flex flex-col gap-1.5">
        <label htmlFor={usernameId} className="text-sm font-semibold">
          Username
        </label>
        <input
          id={usernameId}
          type="text"
          autoComplete="username"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
          aria-invalid={usernameMissing}
          aria-describedby={usernameMissing ? `${usernameId}-error` : undefined}
          className={inputClass}
        />
        {usernameMissing && (
          <p id={`${usernameId}-error`} className="text-sm text-danger">
            Username is required
          </p>
        )}
      </div>

      <div className="flex flex-col gap-1.5">
        <label htmlFor={passwordId} className="text-sm font-semibold">
          Password
        </label>
        <input
          id={passwordId}
          type="password"
          autoComplete={passwordAutoComplete}
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          aria-invalid={passwordMissing}
          aria-describedby={passwordMissing ? `${passwordId}-error` : undefined}
          className={inputClass}
        />
        {passwordMissing && (
          <p id={`${passwordId}-error`} className="text-sm text-danger">
            Password is required
          </p>
        )}
      </div>

      <button
        type="submit"
        disabled={pending}
        className="h-12 cursor-pointer rounded-[10px] bg-accent text-base font-semibold text-accent-ink disabled:cursor-not-allowed disabled:opacity-60"
      >
        {pending ? pendingLabel : submitLabel}
      </button>
    </form>
  )
}
