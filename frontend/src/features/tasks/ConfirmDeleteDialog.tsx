import { Dialog } from '../../components/Dialog'

interface ConfirmDeleteDialogProps {
  count: number
  pending: boolean
  onConfirm: () => void
  onCancel: () => void
}

export function ConfirmDeleteDialog({ count, pending, onConfirm, onCancel }: ConfirmDeleteDialogProps) {
  const noun = count === 1 ? 'task' : 'tasks'

  return (
    <Dialog title={`Delete ${count} ${noun}?`} onClose={onCancel}>
      <p className="text-sm text-muted">The selected {noun} will be removed from your list.</p>
      <div className="flex items-center justify-end gap-3">
        <button
          type="button"
          onClick={onCancel}
          className="h-12 cursor-pointer rounded-[10px] border border-border-strong px-5 text-[15px] font-semibold"
        >
          Cancel
        </button>
        <button
          type="button"
          onClick={onConfirm}
          disabled={pending}
          className="h-12 cursor-pointer rounded-[10px] bg-danger px-6 text-[15px] font-semibold text-surface disabled:cursor-not-allowed disabled:opacity-60"
        >
          {pending ? 'Deleting...' : 'Delete'}
        </button>
      </div>
    </Dialog>
  )
}
