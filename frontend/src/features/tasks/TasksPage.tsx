import { useMeQuery } from '../../api/generated'
import { AppHeader } from '../../components/AppHeader'

export function TasksPage() {
  const { data: user } = useMeQuery()

  return (
    <div className="min-h-screen bg-bg">
      <AppHeader />
      <main className="mx-auto max-w-270 px-6 py-8">
        <h1 className="text-[28px] font-bold tracking-tight">My tasks</h1>
        {user && <p className="mt-2 text-sm text-muted">Signed in as {user.username}</p>}
      </main>
    </div>
  )
}
