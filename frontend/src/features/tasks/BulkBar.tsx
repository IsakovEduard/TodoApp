interface BulkBarProps {
  count: number
  pending: boolean
  onComplete: () => void
  onDelete: () => void
  onClear: () => void
}

const buttonClass = 'h-11 cursor-pointer rounded-[10px] border bg-surface px-4 text-sm font-semibold disabled:opacity-60'

export function BulkBar({ count, pending, onComplete, onDelete, onClear }: BulkBarProps) {
  return (
    <div
      role="region"
      aria-label="Bulk actions"
      className="flex flex-wrap items-center gap-3 rounded-xl border border-accent-soft-ink/30 bg-accent-soft py-2 pr-2 pl-4"
    >
      <span className="grow text-sm font-semibold text-accent-soft-ink">{count} selected</span>
      <button
        type="button"
        onClick={onComplete}
        disabled={pending}
        className={`${buttonClass} border-accent text-accent-soft-ink`}
      >
        Mark completed
      </button>
      <button type="button" onClick={onDelete} disabled={pending} className={`${buttonClass} border-danger text-danger`}>
        Delete
      </button>
      <button type="button" onClick={onClear} className={`${buttonClass} border-border-strong`}>
        Clear
      </button>
    </div>
  )
}
