import { useMeQuery } from '../api/generated'
import { useAppDispatch } from '../app/hooks'
import { loggedOut } from '../features/auth/authSlice'
import { ThemeToggle } from '../features/theme/ThemeToggle'
import { Logo } from './Logo'

export function AppHeader() {
  const dispatch = useAppDispatch()
  const { data: user } = useMeQuery()

  return (
    <header className="border-b border-border bg-surface">
      <div className="mx-auto flex min-h-16 max-w-270 flex-wrap items-center gap-3 px-6 py-2">
        <Logo />
        <span className="grow text-lg font-bold">Tasks</span>
        <ThemeToggle />
        {user && <span className="hidden text-sm text-muted sm:inline">{user.username}</span>}
        <button
          type="button"
          onClick={() => dispatch(loggedOut())}
          className="inline-flex h-11 cursor-pointer items-center gap-2 rounded-[10px] border border-border-strong bg-surface px-3.5 text-sm font-medium"
        >
          <svg
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            aria-hidden="true"
          >
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
            <path d="M16 17l5-5-5-5" />
            <path d="M21 12H9" />
          </svg>
          Log out
        </button>
      </div>
    </header>
  )
}
