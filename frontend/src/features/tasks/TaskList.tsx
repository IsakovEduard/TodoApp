import { ROW_GRID, TaskRow } from './TaskRow'
import type { Task } from './taskModel'

interface TaskListProps {
  tasks: Task[]
  selectedIds: string[]
  onToggle: (id: string) => void
  onSelectAll: (ids: string[]) => void
}

export function TaskList({ tasks, selectedIds, onToggle, onSelectAll }: TaskListProps) {
  const allSelected = tasks.length > 0 && tasks.every((task) => selectedIds.includes(task.id))

  return (
    <div className="overflow-hidden rounded-[14px] border border-border bg-surface">
      <div
        className={`grid grid-cols-[44px_minmax(0,1fr)] items-center gap-x-0 border-b border-border px-3 text-xs font-semibold tracking-wide text-muted uppercase md:h-11 ${ROW_GRID}`}
      >
        <span className="flex justify-center py-2 md:py-0">
          <input
            type="checkbox"
            checked={allSelected}
            onChange={() => onSelectAll(allSelected ? [] : tasks.map((task) => task.id))}
            aria-label="Select all tasks"
            className="size-5 cursor-pointer accent-accent"
          />
        </span>
        <span>
          <span className="md:hidden">Select all</span>
          <span className="hidden md:inline">Task</span>
        </span>
        <span className="hidden md:block">Urgency</span>
        <span className="hidden md:block">Status</span>
        <span className="hidden md:block">Due</span>
        <span className="hidden md:block" />
      </div>
      <ul>
        {tasks.map((task) => (
          <TaskRow key={task.id} task={task} selected={selectedIds.includes(task.id)} onToggle={onToggle} />
        ))}
      </ul>
    </div>
  )
}

export function TaskListSkeleton() {
  return (
    <div
      aria-busy="true"
      className="flex animate-pulse flex-col gap-px overflow-hidden rounded-[14px] border border-border bg-surface"
    >
      <span role="status" className="sr-only">
        Loading tasks
      </span>
      {[0, 1, 2].map((row) => (
        <div key={row} className="flex items-center gap-4 border-b border-border p-4 last:border-b-0">
          <div className="size-5 rounded bg-surface-2" />
          <div className="flex grow flex-col gap-2">
            <div className="h-4 w-1/3 rounded bg-surface-2" />
            <div className="h-3 w-1/2 rounded bg-surface-2" />
          </div>
        </div>
      ))}
    </div>
  )
}
