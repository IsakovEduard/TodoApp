import { fireEvent, screen, waitFor, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import type { V1CreateTaskInput, V1Task, V1UpdateCharacteristicsInput } from '../../api/generated'
import { renderApp } from '../../test/renderApp'
import { server } from '../../test/server'

const SAMPLE_TASKS: V1Task[] = [
  {
    id: '1',
    title: 'Renew passport',
    description: 'Book the appointment online',
    urgency: 'HIGH',
    status: 'IN_PROGRESS',
    dueDate: '2020-01-01T10:00:00',
    createdAt: '2019-12-01T10:00:00',
  },
  {
    id: '2',
    title: 'Prepare quarterly report',
    description: 'Draft the slides',
    urgency: 'HIGH',
    status: 'IN_PROGRESS',
    dueDate: '2099-11-09T17:00:00',
    createdAt: '2026-11-01T10:00:00',
  },
  {
    id: '3',
    title: 'Book dentist appointment',
    description: 'Six-month check-up',
    urgency: 'NORMAL',
    status: 'SCHEDULED',
    dueDate: '2099-11-12T10:30:00',
    createdAt: '2026-11-02T10:00:00',
  },
  {
    id: '4',
    title: 'Send invoice to client',
    description: 'For October',
    urgency: 'NORMAL',
    status: 'COMPLETED',
    dueDate: '2020-11-03T12:00:00',
    createdAt: '2020-10-01T10:00:00',
  },
]

const FIELD_BY_NAME: Record<string, keyof V1Task> = {
  TITLE: 'title',
  DESCRIPTION: 'description',
  URGENCY: 'urgency',
  STATUS: 'status',
  DUE_DATE: 'dueDate',
}

function useFakeApi(initial: V1Task[] = SAMPLE_TASKS) {
  const api = {
    tasks: initial.map((task) => ({ ...task })),
    created: [] as V1CreateTaskInput[],
    updates: [] as V1UpdateCharacteristicsInput[],
    deletes: [] as string[][],
  }
  let nextId = 100

  server.use(
    http.get('/auth/me', () => HttpResponse.json({ username: 'alice' })),
    http.get('/task-management', () => HttpResponse.json(api.tasks)),
    http.post('/task-management', async ({ request }) => {
      const body = (await request.json()) as V1CreateTaskInput
      api.created.push(body)
      const task: V1Task = { ...body, id: String(nextId++), status: 'IN_PROGRESS', createdAt: '2026-11-06T09:00:00' }
      api.tasks.push(task)
      return HttpResponse.text(task.id!, { status: 201 })
    }),
    http.put('/task-management', async ({ request }) => {
      const body = (await request.json()) as V1UpdateCharacteristicsInput
      api.updates.push(body)
      const task = api.tasks.find((candidate) => candidate.id === body.taskId)!
      const changes = Object.fromEntries(
        (body.characteristics ?? []).map(({ name, value }) => [FIELD_BY_NAME[name!], value]),
      )
      Object.assign(task, changes)
      return HttpResponse.json([task])
    }),
    http.delete('/task-management', async ({ request }) => {
      const body = (await request.json()) as { taskIds: string[] }
      api.deletes.push(body.taskIds)
      api.tasks = api.tasks.filter((task) => !body.taskIds.includes(task.id!))
      return HttpResponse.json(body.taskIds.length)
    }),
  )
  return api
}

function rowTitles() {
  return screen.getAllByRole('listitem').map((row) => within(row).getAllByRole('link')[0].textContent)
}

describe('task list', () => {
  it('shows tasks sorted by due date with a summary', async () => {
    useFakeApi()
    renderApp('/tasks', 'token')

    expect(await screen.findByText('4 tasks, 1 overdue')).toBeInTheDocument()
    expect(rowTitles()).toEqual([
      'Renew passport',
      'Send invoice to client',
      'Prepare quarterly report',
      'Book dentist appointment',
    ])
    expect(screen.getByText(/Overdue - Jan 1/)).toBeInTheDocument()
  })

  it('shows a loading state first', async () => {
    useFakeApi()
    renderApp('/tasks', 'token')

    expect(screen.getByRole('status')).toHaveTextContent('Loading tasks')
    await screen.findByText('4 tasks, 1 overdue')
  })

  it('filters by status', async () => {
    useFakeApi()
    renderApp('/tasks', 'token')
    await screen.findByText('4 tasks, 1 overdue')

    await userEvent.click(screen.getByRole('button', { name: 'Completed' }))

    expect(rowTitles()).toEqual(['Send invoice to client'])
  })

  it('explains when filters match nothing and can clear them', async () => {
    useFakeApi()
    renderApp('/tasks', 'token')
    await screen.findByText('4 tasks, 1 overdue')

    await userEvent.selectOptions(screen.getByLabelText('Urgency'), 'LOW')
    expect(screen.getByText('No tasks match these filters')).toBeInTheDocument()

    await userEvent.click(screen.getByRole('button', { name: 'Clear filters' }))
    expect(screen.getAllByRole('listitem')).toHaveLength(4)
  })

  it('sorts by urgency', async () => {
    useFakeApi()
    renderApp('/tasks', 'token')
    await screen.findByText('4 tasks, 1 overdue')

    await userEvent.selectOptions(screen.getByLabelText('Sort by'), 'urgency')

    expect(rowTitles().slice(0, 2)).toEqual(['Renew passport', 'Prepare quarterly report'])
  })

  it('invites the user to create a first task when the list is empty', async () => {
    useFakeApi([])
    renderApp('/tasks', 'token')

    expect(await screen.findByText('No tasks yet')).toBeInTheDocument()
    expect(screen.getAllByRole('link', { name: 'New task' }).length).toBeGreaterThan(0)
  })

  it('offers a retry when loading fails', async () => {
    useFakeApi([])
    server.use(http.get('/task-management', () => new HttpResponse(null, { status: 500 }), { once: true }))
    renderApp('/tasks', 'token')

    expect(await screen.findByText('Could not load your tasks')).toBeInTheDocument()
    await userEvent.click(screen.getByRole('button', { name: 'Try again' }))

    expect(await screen.findByText('No tasks yet')).toBeInTheDocument()
  })
})

describe('creating a task', () => {
  it('validates, sends the task and shows it in the list', async () => {
    const api = useFakeApi()
    renderApp('/tasks', 'token')
    await screen.findByText('4 tasks, 1 overdue')

    await userEvent.click(screen.getAllByRole('link', { name: 'New task' })[0])
    const dialog = await screen.findByRole('dialog', { name: 'New task' })

    await userEvent.click(within(dialog).getByRole('button', { name: 'Create task' }))
    expect(within(dialog).getByText('Title is required')).toBeInTheDocument()
    expect(within(dialog).getByText('Description is required')).toBeInTheDocument()
    expect(api.created).toHaveLength(0)

    await userEvent.type(within(dialog).getByLabelText('Title'), 'Buy milk')
    await userEvent.type(within(dialog).getByLabelText('Description'), 'Two liters')
    await userEvent.click(within(dialog).getByLabelText('High'))
    fireEvent.change(within(dialog).getByLabelText('Due date and time'), { target: { value: '2099-12-01T09:30' } })
    await userEvent.click(within(dialog).getByRole('button', { name: 'Create task' }))

    expect(await screen.findByText('5 tasks, 1 overdue')).toBeInTheDocument()
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument()
    expect(api.created).toEqual([
      { title: 'Buy milk', description: 'Two liters', urgency: 'HIGH', dueDate: '2099-12-01T09:30' },
    ])
    expect(rowTitles()).toContain('Buy milk')
  })

  it('shows the server message and keeps the dialog open on failure', async () => {
    useFakeApi()
    server.use(
      http.post('/task-management', () =>
        HttpResponse.json({ message: 'Title can not be empty.' }, { status: 400 }),
      ),
    )
    renderApp('/tasks/new', 'token')
    const dialog = await screen.findByRole('dialog', { name: 'New task' })

    await userEvent.type(within(dialog).getByLabelText('Title'), 'x')
    await userEvent.type(within(dialog).getByLabelText('Description'), 'y')
    await userEvent.click(within(dialog).getByRole('button', { name: 'Create task' }))

    expect(await within(dialog).findByRole('alert')).toHaveTextContent('Title can not be empty.')
    expect(screen.getByRole('dialog', { name: 'New task' })).toBeInTheDocument()
  })

  it('closes without saving on cancel', async () => {
    const api = useFakeApi()
    renderApp('/tasks/new', 'token')
    const dialog = await screen.findByRole('dialog', { name: 'New task' })

    await userEvent.click(within(dialog).getByRole('button', { name: 'Cancel' }))

    await waitFor(() => expect(screen.queryByRole('dialog')).not.toBeInTheDocument())
    expect(api.created).toHaveLength(0)
  })
})

describe('editing a task', () => {
  it('sends only the changed fields and refreshes the list', async () => {
    const api = useFakeApi()
    renderApp('/tasks', 'token')
    await screen.findByText('4 tasks, 1 overdue')

    await userEvent.click(screen.getByRole('link', { name: 'Prepare quarterly report' }))
    const dialog = await screen.findByRole('dialog', { name: 'Edit task' })
    const title = within(dialog).getByLabelText('Title')
    expect(title).toHaveValue('Prepare quarterly report')

    await userEvent.clear(title)
    await userEvent.type(title, 'Prepare Q4 report')
    await userEvent.selectOptions(within(dialog).getByLabelText('Status'), 'COMPLETED')
    await userEvent.click(within(dialog).getByRole('button', { name: 'Save changes' }))

    await waitFor(() => expect(screen.queryByRole('dialog')).not.toBeInTheDocument())
    expect(api.updates).toEqual([
      {
        taskId: '2',
        characteristics: [
          { name: 'TITLE', value: 'Prepare Q4 report' },
          { name: 'STATUS', value: 'COMPLETED' },
        ],
      },
    ])
    expect(await screen.findByRole('link', { name: 'Prepare Q4 report' })).toBeInTheDocument()
  })

  it('does not call the server when nothing changed', async () => {
    const api = useFakeApi()
    renderApp('/tasks/3', 'token')
    const dialog = await screen.findByRole('dialog', { name: 'Edit task' })

    await userEvent.click(within(dialog).getByRole('button', { name: 'Save changes' }))

    await waitFor(() => expect(screen.queryByRole('dialog')).not.toBeInTheDocument())
    expect(api.updates).toHaveLength(0)
  })

  it('explains that an existing due date cannot be removed', async () => {
    useFakeApi()
    renderApp('/tasks/3', 'token')
    const dialog = await screen.findByRole('dialog', { name: 'Edit task' })

    expect(within(dialog).getByText('A due date can be changed but not removed.')).toBeInTheDocument()
    expect(within(dialog).getByLabelText('Due date and time')).toHaveAccessibleDescription(
      'A due date can be changed but not removed.',
    )
  })

  it('does not show that hint for a task without a due date', async () => {
    useFakeApi([{ ...SAMPLE_TASKS[0], dueDate: undefined }])
    renderApp('/tasks/1', 'token')
    const dialog = await screen.findByRole('dialog', { name: 'Edit task' })

    expect(within(dialog).queryByText(/cannot be removed|not removed/)).not.toBeInTheDocument()
  })

  it('says so when the task does not exist', async () => {
    useFakeApi()
    renderApp('/tasks/999', 'token')

    expect(await screen.findByRole('dialog', { name: 'Task not found' })).toBeInTheDocument()
  })
})

describe('bulk actions', () => {
  it('selects all visible tasks', async () => {
    useFakeApi()
    renderApp('/tasks', 'token')
    await screen.findByText('4 tasks, 1 overdue')

    await userEvent.click(screen.getByLabelText('Select all tasks'))

    expect(screen.getByText('4 selected')).toBeInTheDocument()
  })

  it('marks the selected tasks completed', async () => {
    const api = useFakeApi()
    renderApp('/tasks', 'token')
    await screen.findByText('4 tasks, 1 overdue')

    await userEvent.click(screen.getByLabelText('Select Prepare quarterly report'))
    await userEvent.click(screen.getByLabelText('Select Book dentist appointment'))
    expect(screen.getByText('2 selected')).toBeInTheDocument()
    await userEvent.click(screen.getByRole('button', { name: 'Mark completed' }))

    await waitFor(() => expect(screen.queryByText('2 selected')).not.toBeInTheDocument())
    expect(api.updates.map((update) => update.taskId).sort()).toEqual(['2', '3'])
    expect(api.updates.every((update) => update.characteristics?.[0].value === 'COMPLETED')).toBe(true)
  })

  it('deletes the selected tasks after confirmation', async () => {
    const api = useFakeApi()
    renderApp('/tasks', 'token')
    await screen.findByText('4 tasks, 1 overdue')

    await userEvent.click(screen.getByLabelText('Select Renew passport'))
    await userEvent.click(screen.getByRole('button', { name: 'Delete' }))
    const dialog = await screen.findByRole('dialog', { name: 'Delete 1 task?' })
    await userEvent.click(within(dialog).getByRole('button', { name: 'Delete' }))

    await waitFor(() => expect(api.deletes).toEqual([['1']]))
    expect(await screen.findByText('3 tasks')).toBeInTheDocument()
    expect(rowTitles()).not.toContain('Renew passport')
    expect(screen.queryByText('1 selected')).not.toBeInTheDocument()
  })

  it('keeps the tasks when the deletion is cancelled', async () => {
    const api = useFakeApi()
    renderApp('/tasks', 'token')
    await screen.findByText('4 tasks, 1 overdue')

    await userEvent.click(screen.getByLabelText('Select Renew passport'))
    await userEvent.click(screen.getByRole('button', { name: 'Delete' }))
    const dialog = await screen.findByRole('dialog', { name: 'Delete 1 task?' })
    await userEvent.click(within(dialog).getByRole('button', { name: 'Cancel' }))

    await waitFor(() => expect(screen.queryByRole('dialog')).not.toBeInTheDocument())
    expect(api.deletes).toHaveLength(0)
    expect(screen.getAllByRole('listitem')).toHaveLength(4)
  })

  it('shows an error when deleting fails', async () => {
    useFakeApi()
    server.use(http.delete('/task-management', () => new HttpResponse(null, { status: 500 })))
    renderApp('/tasks', 'token')
    await screen.findByText('4 tasks, 1 overdue')

    await userEvent.click(screen.getByLabelText('Select Renew passport'))
    await userEvent.click(screen.getByRole('button', { name: 'Delete' }))
    const dialog = await screen.findByRole('dialog', { name: 'Delete 1 task?' })
    await userEvent.click(within(dialog).getByRole('button', { name: 'Delete' }))

    expect(await screen.findByRole('alert')).toBeInTheDocument()
    expect(screen.getAllByRole('listitem')).toHaveLength(4)
  })
})
