import { Link } from 'react-router'
import { StatusBadge, UrgencyBadge } from './TaskBadges'
import { formatDue, formatDueDate, isClosed, isOverdue, type Task } from './taskModel'

export const ROW_GRID = 'md:grid-cols-[48px_minmax(0,1fr)_110px_130px_170px_52px]'

interface TaskRowProps {
  task: Task
  selected: boolean
  onToggle: (id: string) => void
}

export function TaskRow({ task, selected, onToggle }: TaskRowProps) {
  const overdue = isOverdue(task)
  const closed = isClosed(task)

  return (
    <li
      className={`grid grid-cols-[44px_minmax(0,1fr)] items-start gap-y-2 border-b border-border p-3 last:border-b-0 md:items-center ${ROW_GRID} ${
        selected ? 'bg-accent-soft' : ''
      }`}
    >
      <span className="row-span-3 flex justify-center pt-1 md:row-span-1 md:pt-0">
        <input
          type="checkbox"
          checked={selected}
          onChange={() => onToggle(task.id)}
          aria-label={`Select ${task.title}`}
          className="size-5 cursor-pointer accent-accent"
        />
      </span>

      <span className="min-w-0">
        <Link
          to={`/tasks/${task.id}`}
          className={`block wrap-break-word text-[15px] font-semibold ${closed ? 'text-muted line-through' : ''}`}
        >
          {task.title}
        </Link>
        {task.description && (
          <span className="mt-0.5 block wrap-break-word text-[13px] text-muted">{task.description}</span>
        )}
      </span>

      <span className="col-start-2 flex flex-wrap gap-1.5 md:contents">
        <span className="md:flex">
          <UrgencyBadge urgency={task.urgency} />
        </span>
        <span className="md:flex">
          <StatusBadge status={task.status} />
        </span>
      </span>

      <span className="col-start-2 text-sm md:col-auto">
        {task.dueDate === null ? (
          <span className="text-muted">No due date</span>
        ) : overdue ? (
          <span className="inline-flex items-center gap-1.5 font-semibold text-danger">
            <svg
              width="16"
              height="16"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2.2"
              strokeLinecap="round"
              strokeLinejoin="round"
              aria-hidden="true"
            >
              <circle cx="12" cy="12" r="9" />
              <path d="M12 7v5M12 16h.01" />
            </svg>
            Overdue - {formatDueDate(task.dueDate)}
          </span>
        ) : (
          <span className={closed ? 'text-muted' : ''}>{formatDue(task.dueDate)}</span>
        )}
      </span>

      <span className="hidden justify-end md:flex">
        <Link
          to={`/tasks/${task.id}`}
          aria-label={`Edit ${task.title}`}
          className="inline-flex size-11 items-center justify-center rounded-[10px] text-muted hover:text-ink"
        >
          <svg
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            aria-hidden="true"
          >
            <path d="M12 20h9" />
            <path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z" />
          </svg>
        </Link>
      </span>
    </li>
  )
}
