import type { V1Status, V1Urgency } from '../../api/generated'
import { labelOf, STATUSES, URGENCIES } from './taskModel'

const URGENCY_CLASS: Record<V1Urgency, string> = {
  HIGH: 'badge-high',
  NORMAL: 'badge-normal',
  LOW: 'badge-low',
}

const STATUS_CLASS: Record<V1Status, string> = {
  IN_PROGRESS: 'badge-in-progress',
  COMPLETED: 'badge-completed',
  CANCELLED: 'badge-cancelled',
  SCHEDULED: 'badge-scheduled',
}

export function UrgencyBadge({ urgency }: { urgency: V1Urgency }) {
  return <span className={`badge ${URGENCY_CLASS[urgency]}`}>{labelOf(URGENCIES, urgency)}</span>
}

export function StatusBadge({ status }: { status: V1Status }) {
  return <span className={`badge ${STATUS_CLASS[status]}`}>{labelOf(STATUSES, status)}</span>
}
