import type { V1Characteristic, V1Status, V1Task, V1Urgency } from '../../api/generated'

export interface Task {
  id: string
  title: string
  description: string
  urgency: V1Urgency
  status: V1Status
  dueDate: string | null
  createdAt: string | null
}

export type StatusFilter = V1Status | 'ALL'
export type UrgencyFilter = V1Urgency | 'ALL'
export type SortKey = 'dueDate' | 'urgency' | 'createdAt'

export interface TaskFilters {
  status: StatusFilter
  urgency: UrgencyFilter
}

export const URGENCIES: { value: V1Urgency; label: string }[] = [
  { value: 'LOW', label: 'Low' },
  { value: 'NORMAL', label: 'Normal' },
  { value: 'HIGH', label: 'High' },
]

export const STATUSES: { value: V1Status; label: string }[] = [
  { value: 'IN_PROGRESS', label: 'In progress' },
  { value: 'SCHEDULED', label: 'Scheduled' },
  { value: 'COMPLETED', label: 'Completed' },
  { value: 'CANCELLED', label: 'Cancelled' },
]

export const SORT_OPTIONS: { value: SortKey; label: string }[] = [
  { value: 'dueDate', label: 'Due date' },
  { value: 'urgency', label: 'Urgency' },
  { value: 'createdAt', label: 'Newest first' },
]

const URGENCY_RANK: Record<V1Urgency, number> = { HIGH: 0, NORMAL: 1, LOW: 2 }

export function labelOf<T extends string>(options: { value: T; label: string }[], value: T): string {
  return options.find((option) => option.value === value)?.label ?? value
}

export function normalizeTask(task: V1Task): Task {
  return {
    id: task.id ?? '',
    title: task.title ?? '',
    description: task.description ?? '',
    urgency: task.urgency ?? 'NORMAL',
    status: task.status ?? 'IN_PROGRESS',
    dueDate: task.dueDate ?? null,
    createdAt: task.createdAt ?? null,
  }
}

export function isClosed(task: Task): boolean {
  return task.status === 'COMPLETED' || task.status === 'CANCELLED'
}

export function isOverdue(task: Task, now: Date = new Date()): boolean {
  return task.dueDate !== null && !isClosed(task) && new Date(task.dueDate) < now
}

export function filterTasks(tasks: Task[], filters: TaskFilters): Task[] {
  return tasks.filter(
    (task) =>
      (filters.status === 'ALL' || task.status === filters.status) &&
      (filters.urgency === 'ALL' || task.urgency === filters.urgency),
  )
}

function timeOf(value: string | null, missing: number): number {
  return value === null ? missing : new Date(value).getTime()
}

export function sortTasks(tasks: Task[], sortKey: SortKey): Task[] {
  const sorted = [...tasks]
  switch (sortKey) {
    case 'dueDate':
      return sorted.sort((a, b) => timeOf(a.dueDate, Infinity) - timeOf(b.dueDate, Infinity))
    case 'urgency':
      return sorted.sort((a, b) => URGENCY_RANK[a.urgency] - URGENCY_RANK[b.urgency])
    case 'createdAt':
      return sorted.sort((a, b) => timeOf(b.createdAt, -Infinity) - timeOf(a.createdAt, -Infinity))
  }
}

export function formatDue(dueDate: string): string {
  return new Date(dueDate).toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  })
}

export function formatDueDate(dueDate: string): string {
  return new Date(dueDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}

export function toInputValue(dueDate: string | null): string {
  return dueDate ? dueDate.slice(0, 16) : ''
}

export interface TaskFormValues {
  title: string
  description: string
  urgency: V1Urgency
  dueDate: string
  status: V1Status
}

export function toFormValues(task?: Task): TaskFormValues {
  return {
    title: task?.title ?? '',
    description: task?.description ?? '',
    urgency: task?.urgency ?? 'NORMAL',
    dueDate: toInputValue(task?.dueDate ?? null),
    status: task?.status ?? 'IN_PROGRESS',
  }
}

/**
 * Builds the characteristics to send for an update: only fields that changed.
 * An emptied due date is sent as an empty value, which makes the API clear it.
 */
export function changedCharacteristics(original: Task, values: TaskFormValues): V1Characteristic[] {
  const changes: V1Characteristic[] = []
  if (values.title.trim() !== original.title) {
    changes.push({ name: 'TITLE', value: values.title.trim() })
  }
  if (values.description.trim() !== original.description) {
    changes.push({ name: 'DESCRIPTION', value: values.description.trim() })
  }
  if (values.urgency !== original.urgency) {
    changes.push({ name: 'URGENCY', value: values.urgency })
  }
  if (values.status !== original.status) {
    changes.push({ name: 'STATUS', value: values.status })
  }
  if (values.dueDate !== toInputValue(original.dueDate)) {
    changes.push({ name: 'DUE_DATE', value: values.dueDate })
  }
  return changes
}
