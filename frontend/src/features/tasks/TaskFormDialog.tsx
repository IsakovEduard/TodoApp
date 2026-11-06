import { useId, useState, type FormEvent } from 'react'
import type { FetchBaseQueryError } from '@reduxjs/toolkit/query'
import { getErrorMessage } from '../../api/errors'
import type { V1Status, V1Urgency } from '../../api/generated'
import { Dialog } from '../../components/Dialog'
import {
  changedCharacteristics,
  STATUSES,
  toFormValues,
  URGENCIES,
  type Task,
  type TaskFormValues,
} from './taskModel'
import { useCreateTaskMutation, useUpdateTaskCharacteristicsMutation } from './tasksApi'

interface TaskFormDialogProps {
  task?: Task
  onClose: () => void
}

const fieldClass =
  'w-full rounded-[10px] border border-border-strong bg-surface px-3.5 text-base text-ink placeholder:text-muted aria-[invalid=true]:border-danger'

export function TaskFormDialog({ task, onClose }: TaskFormDialogProps) {
  const ids = useId()
  const [values, setValues] = useState<TaskFormValues>(() => toFormValues(task))
  const [submitted, setSubmitted] = useState(false)
  const [serverError, setServerError] = useState<string | null>(null)
  const [createTask, { isLoading: creating }] = useCreateTaskMutation()
  const [updateTask, { isLoading: updating }] = useUpdateTaskCharacteristicsMutation()
  const editing = task !== undefined
  const pending = creating || updating

  const titleMissing = submitted && values.title.trim() === ''
  const descriptionMissing = submitted && values.description.trim() === ''

  function set<K extends keyof TaskFormValues>(key: K, value: TaskFormValues[K]) {
    setValues((current) => ({ ...current, [key]: value }))
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitted(true)
    setServerError(null)
    if (values.title.trim() === '' || values.description.trim() === '') {
      return
    }

    try {
      if (task) {
        const characteristics = changedCharacteristics(task, values)
        if (characteristics.length > 0) {
          await updateTask({
            v1UpdateCharacteristicsInput: { taskId: task.id, characteristics },
          }).unwrap()
        }
      } else {
        await createTask({
          v1CreateTaskInput: {
            title: values.title.trim(),
            description: values.description.trim(),
            urgency: values.urgency,
            dueDate: values.dueDate === '' ? undefined : values.dueDate,
          },
        }).unwrap()
      }
      onClose()
    } catch (error) {
      setServerError(getErrorMessage(error as FetchBaseQueryError))
    }
  }

  return (
    <Dialog title={editing ? 'Edit task' : 'New task'} onClose={onClose}>
      <form onSubmit={handleSubmit} noValidate className="flex flex-1 flex-col gap-5">
        {serverError && (
          <p role="alert" className="rounded-[10px] bg-danger-soft px-3.5 py-3 text-sm font-medium text-danger">
            {serverError}
          </p>
        )}

        <div className="flex flex-col gap-1.5">
          <label htmlFor={`${ids}-title`} className="text-sm font-semibold">
            Title
          </label>
          <input
            id={`${ids}-title`}
            type="text"
            value={values.title}
            onChange={(event) => set('title', event.target.value)}
            aria-invalid={titleMissing}
            aria-describedby={titleMissing ? `${ids}-title-error` : undefined}
            placeholder="What needs to be done?"
            className={`${fieldClass} h-12`}
          />
          {titleMissing && (
            <p id={`${ids}-title-error`} className="text-sm text-danger">
              Title is required
            </p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <label htmlFor={`${ids}-description`} className="text-sm font-semibold">
            Description
          </label>
          <textarea
            id={`${ids}-description`}
            rows={3}
            value={values.description}
            onChange={(event) => set('description', event.target.value)}
            aria-invalid={descriptionMissing}
            aria-describedby={descriptionMissing ? `${ids}-description-error` : undefined}
            placeholder="Add details"
            className={`${fieldClass} resize-y py-3`}
          />
          {descriptionMissing && (
            <p id={`${ids}-description-error`} className="text-sm text-danger">
              Description is required
            </p>
          )}
        </div>

        <fieldset className="m-0 flex flex-col gap-1.5 border-0 p-0">
          <legend className="mb-1.5 p-0 text-sm font-semibold">Urgency</legend>
          <div className="grid grid-cols-3 gap-2">
            {URGENCIES.map((option) => {
              const selected = values.urgency === option.value
              return (
                <label
                  key={option.value}
                  className={`relative flex h-12 cursor-pointer items-center justify-center rounded-[10px] border text-sm ${
                    selected
                      ? 'border-2 border-accent bg-accent-soft font-bold text-accent-soft-ink'
                      : 'border-border-strong font-medium'
                  } has-focus-visible:outline-2 has-focus-visible:outline-accent`}
                >
                  <input
                    type="radio"
                    name={`${ids}-urgency`}
                    value={option.value}
                    checked={selected}
                    onChange={() => set('urgency', option.value as V1Urgency)}
                    className="absolute size-px opacity-0"
                  />
                  {option.label}
                </label>
              )
            })}
          </div>
        </fieldset>

        <div className="flex flex-col gap-1.5">
          <label htmlFor={`${ids}-due`} className="text-sm font-semibold">
            Due date and time
          </label>
          <input
            id={`${ids}-due`}
            type="datetime-local"
            value={values.dueDate}
            onChange={(event) => set('dueDate', event.target.value)}
            aria-describedby={task?.dueDate ? `${ids}-due-hint` : undefined}
            className={`${fieldClass} h-12`}
          />
          {task?.dueDate && (
            <p id={`${ids}-due-hint`} className="text-sm text-muted">
              A due date can be changed but not removed.
            </p>
          )}
        </div>

        {editing && (
          <div className="flex flex-col gap-1.5">
            <label htmlFor={`${ids}-status`} className="text-sm font-semibold">
              Status
            </label>
            <select
              id={`${ids}-status`}
              value={values.status}
              onChange={(event) => set('status', event.target.value as V1Status)}
              className={`${fieldClass} h-12`}
            >
              {STATUSES.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>
        )}

        <div className="mt-auto flex items-center justify-end gap-3 pt-1">
          <button
            type="button"
            onClick={onClose}
            className="h-12 cursor-pointer rounded-[10px] border border-border-strong px-5 text-[15px] font-semibold"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={pending}
            className="h-12 cursor-pointer rounded-[10px] bg-accent px-6 text-[15px] font-semibold text-accent-ink disabled:cursor-not-allowed disabled:opacity-60"
          >
            {pending ? 'Saving...' : editing ? 'Save changes' : 'Create task'}
          </button>
        </div>
      </form>
    </Dialog>
  )
}
