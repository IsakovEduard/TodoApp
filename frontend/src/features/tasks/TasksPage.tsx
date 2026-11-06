import { useMemo, useState } from 'react'
import type { FetchBaseQueryError } from '@reduxjs/toolkit/query'
import { Link, Outlet } from 'react-router'
import { getErrorMessage } from '../../api/errors'
import { useAppDispatch, useAppSelector } from '../../app/hooks'
import { AppHeader } from '../../components/AppHeader'
import { BulkBar } from './BulkBar'
import { ConfirmDeleteDialog } from './ConfirmDeleteDialog'
import { TaskFilters } from './TaskFilters'
import { TaskList, TaskListSkeleton } from './TaskList'
import { filterTasks, isOverdue, normalizeTask, sortTasks } from './taskModel'
import { useDeleteTaskMutation, useGetTasksByFilterQuery, useUpdateTaskCharacteristicsMutation } from './tasksApi'
import { filtersReset, selectionCleared, selectionSet, selectionToggled } from './tasksUiSlice'

function StateMessage({ title, children }: { title: string; children?: React.ReactNode }) {
  return (
    <div className="flex flex-col items-center gap-3 rounded-[14px] border border-border bg-surface px-6 py-12 text-center">
      <p className="text-base font-semibold">{title}</p>
      {children}
    </div>
  )
}

const primaryButton =
  'inline-flex h-11 cursor-pointer items-center justify-center gap-2 rounded-[10px] bg-accent px-4 text-[15px] font-semibold text-accent-ink'

export function TasksPage() {
  const dispatch = useAppDispatch()
  const { status, urgency, sortKey, selectedIds } = useAppSelector((state) => state.tasksUi)
  const { data, isLoading, isError, error, refetch } = useGetTasksByFilterQuery({})
  const [updateTask, { isLoading: updating }] = useUpdateTaskCharacteristicsMutation()
  const [deleteTask, { isLoading: deleting }] = useDeleteTaskMutation()
  const [confirmingDelete, setConfirmingDelete] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const tasks = useMemo(() => (data ?? []).map(normalizeTask), [data])
  const visible = useMemo(
    () => sortTasks(filterTasks(tasks, { status, urgency }), sortKey),
    [tasks, status, urgency, sortKey],
  )
  const selected = selectedIds.filter((id) => tasks.some((task) => task.id === id))
  const overdueCount = tasks.filter((task) => isOverdue(task)).length

  async function completeSelected() {
    setActionError(null)
    const toComplete = tasks.filter((task) => selected.includes(task.id) && task.status !== 'COMPLETED')
    try {
      await Promise.all(
        toComplete.map((task) =>
          updateTask({
            v1UpdateCharacteristicsInput: {
              taskId: task.id,
              characteristics: [{ name: 'STATUS', value: 'COMPLETED' }],
            },
          }).unwrap(),
        ),
      )
      dispatch(selectionCleared())
    } catch (failure) {
      setActionError(getErrorMessage(failure as FetchBaseQueryError))
    }
  }

  async function deleteSelected() {
    setActionError(null)
    try {
      await deleteTask({ v1DeleteTaskInput: { taskIds: selected } }).unwrap()
      dispatch(selectionCleared())
    } catch (failure) {
      setActionError(getErrorMessage(failure as FetchBaseQueryError))
    } finally {
      setConfirmingDelete(false)
    }
  }

  function renderContent() {
    if (isLoading) {
      return <TaskListSkeleton />
    }
    if (isError) {
      return (
        <StateMessage title="Could not load your tasks">
          <p className="text-sm text-muted">{getErrorMessage(error)}</p>
          <button type="button" onClick={refetch} className={primaryButton}>
            Try again
          </button>
        </StateMessage>
      )
    }
    if (tasks.length === 0) {
      return (
        <StateMessage title="No tasks yet">
          <p className="text-sm text-muted">Create your first task to get started.</p>
          <Link to="/tasks/new" className={primaryButton}>
            New task
          </Link>
        </StateMessage>
      )
    }
    if (visible.length === 0) {
      return (
        <StateMessage title="No tasks match these filters">
          <button
            type="button"
            onClick={() => dispatch(filtersReset())}
            className="h-11 cursor-pointer rounded-[10px] border border-border-strong px-4 text-sm font-semibold"
          >
            Clear filters
          </button>
        </StateMessage>
      )
    }
    return (
      <TaskList
        tasks={visible}
        selectedIds={selected}
        onToggle={(id) => dispatch(selectionToggled(id))}
        onSelectAll={(ids) => dispatch(selectionSet(ids))}
      />
    )
  }

  return (
    <div className="min-h-screen bg-bg">
      <AppHeader />
      <main className="mx-auto flex max-w-270 flex-col gap-5 px-6 pt-8 pb-12">
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <h1 className="text-[28px] font-bold tracking-tight">My tasks</h1>
            {!isLoading && !isError && (
              <p className="mt-1.5 text-sm text-muted">
                {tasks.length} {tasks.length === 1 ? 'task' : 'tasks'}
                {overdueCount > 0 && `, ${overdueCount} overdue`}
              </p>
            )}
          </div>
          <Link to="/tasks/new" className={`${primaryButton} h-11`}>
            <svg
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2.4"
              strokeLinecap="round"
              aria-hidden="true"
            >
              <path d="M12 5v14M5 12h14" />
            </svg>
            New task
          </Link>
        </div>

        {tasks.length > 0 && <TaskFilters />}

        {actionError && (
          <p role="alert" className="rounded-[10px] bg-danger-soft px-3.5 py-3 text-sm font-medium text-danger">
            {actionError}
          </p>
        )}

        {selected.length > 0 && (
          <BulkBar
            count={selected.length}
            pending={updating || deleting}
            onComplete={completeSelected}
            onDelete={() => setConfirmingDelete(true)}
            onClear={() => dispatch(selectionCleared())}
          />
        )}

        {renderContent()}
      </main>

      {confirmingDelete && (
        <ConfirmDeleteDialog
          count={selected.length}
          pending={deleting}
          onConfirm={deleteSelected}
          onCancel={() => setConfirmingDelete(false)}
        />
      )}
      <Outlet />
    </div>
  )
}
