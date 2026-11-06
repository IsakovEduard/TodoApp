import { useMemo } from 'react'
import { useNavigate, useParams } from 'react-router'
import { Dialog } from '../../components/Dialog'
import { TaskFormDialog } from './TaskFormDialog'
import { normalizeTask } from './taskModel'
import { useGetTasksByFilterQuery } from './tasksApi'

export function TaskDialogRoute() {
  const { taskId } = useParams()
  const navigate = useNavigate()
  const { data, isLoading } = useGetTasksByFilterQuery({})
  const task = useMemo(
    () => (data ?? []).map(normalizeTask).find((candidate) => candidate.id === taskId),
    [data, taskId],
  )
  const close = () => navigate('/tasks')

  if (taskId === undefined) {
    return <TaskFormDialog onClose={close} />
  }
  if (isLoading) {
    return null
  }
  if (!task) {
    return (
      <Dialog title="Task not found" onClose={close}>
        <p className="text-sm text-muted">This task no longer exists or was deleted.</p>
        <div className="flex justify-end">
          <button
            type="button"
            onClick={close}
            className="h-12 cursor-pointer rounded-[10px] border border-border-strong px-5 text-[15px] font-semibold"
          >
            Back to tasks
          </button>
        </div>
      </Dialog>
    )
  }
  return <TaskFormDialog key={task.id} task={task} onClose={close} />
}
