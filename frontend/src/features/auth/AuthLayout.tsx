import type { ReactNode } from 'react'
import { NavLink } from 'react-router'
import { Logo } from '../../components/Logo'
import { ThemeToggle } from '../theme/ThemeToggle'

const tabClass = ({ isActive }: { isActive: boolean }) =>
  `flex h-10 items-center justify-center rounded-[9px] text-sm ${
    isActive ? 'bg-surface font-semibold text-ink shadow-sm' : 'font-medium text-muted hover:text-ink'
  }`

export function AuthLayout({ children }: { children: ReactNode }) {
  return (
    <div className="flex min-h-screen flex-col px-6 py-4">
      <div className="flex justify-end">
        <ThemeToggle />
      </div>
      <main className="mx-auto flex w-full max-w-105 flex-1 flex-col justify-center gap-6 pb-12">
        <div className="flex items-center justify-center gap-3">
          <Logo size={32} />
          <span className="text-[26px] font-bold tracking-tight">Tasks</span>
        </div>
        <div className="flex flex-col gap-5 rounded-2xl border border-border bg-surface p-7">
          <nav aria-label="Account" className="grid grid-cols-2 gap-1 rounded-xl bg-surface-2 p-1">
            <NavLink to="/login" className={tabClass}>
              Log in
            </NavLink>
            <NavLink to="/register" className={tabClass}>
              Register
            </NavLink>
          </nav>
          {children}
        </div>
      </main>
    </div>
  )
}
