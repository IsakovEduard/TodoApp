import { useAppDispatch, useAppSelector } from '../../app/hooks'
import {
  labelOf,
  SORT_OPTIONS,
  STATUSES,
  URGENCIES,
  type SortKey,
  type StatusFilter,
  type UrgencyFilter,
} from './taskModel'
import { sortKeyChanged, statusFilterChanged, urgencyFilterChanged } from './tasksUiSlice'

const selectClass =
  'h-11 w-full rounded-[10px] border border-border-strong bg-surface px-3 text-sm text-ink sm:w-auto'
const selectLabelClass = 'flex flex-col gap-1 text-sm text-muted sm:flex-row sm:items-center sm:gap-2'

export function TaskFilters() {
  const dispatch = useAppDispatch()
  const { status, urgency, sortKey } = useAppSelector((state) => state.tasksUi)
  const statusOptions: { value: StatusFilter; label: string }[] = [{ value: 'ALL', label: 'All' }, ...STATUSES]

  return (
    <div className="flex flex-wrap items-center gap-3">
      <div role="group" aria-label="Filter by status" className="flex grow flex-wrap gap-2">
        {statusOptions.map((option) => {
          const selected = status === option.value
          return (
            <button
              key={option.value}
              type="button"
              aria-pressed={selected}
              onClick={() => dispatch(statusFilterChanged(option.value))}
              className={`h-11 cursor-pointer rounded-full border px-4 text-sm ${
                selected
                  ? 'border-accent bg-accent font-semibold text-accent-ink'
                  : 'border-border-strong bg-surface font-medium'
              }`}
            >
              {option.label}
            </button>
          )
        })}
      </div>

      <div className="grid w-full grid-cols-2 gap-3 sm:flex sm:w-auto sm:items-center">
        <label className={selectLabelClass}>
          Urgency
          <select
            value={urgency}
            onChange={(event) => dispatch(urgencyFilterChanged(event.target.value as UrgencyFilter))}
            className={selectClass}
          >
            <option value="ALL">All</option>
            {URGENCIES.map((option) => (
              <option key={option.value} value={option.value}>
                {labelOf(URGENCIES, option.value)}
              </option>
            ))}
          </select>
        </label>

        <label className={selectLabelClass}>
          Sort by
          <select
            value={sortKey}
            onChange={(event) => dispatch(sortKeyChanged(event.target.value as SortKey))}
            className={selectClass}
          >
            {SORT_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>
      </div>
    </div>
  )
}
