import {
  changedCharacteristics,
  filterTasks,
  formatDue,
  isOverdue,
  normalizeTask,
  sortTasks,
  toFormValues,
  toInputValue,
  type Task,
} from './taskModel'

function task(overrides: Partial<Task> = {}): Task {
  return {
    id: '1',
    title: 'Task',
    description: 'Details',
    urgency: 'NORMAL',
    status: 'IN_PROGRESS',
    dueDate: null,
    createdAt: null,
    ...overrides,
  }
}

describe('normalizeTask', () => {
  it('fills missing fields with safe defaults', () => {
    expect(normalizeTask({ id: '7' })).toEqual({
      id: '7',
      title: '',
      description: '',
      urgency: 'NORMAL',
      status: 'IN_PROGRESS',
      dueDate: null,
      createdAt: null,
    })
  })
})

describe('isOverdue', () => {
  const now = new Date('2026-11-06T12:00:00')

  it('is true for an open task past its due date', () => {
    expect(isOverdue(task({ dueDate: '2026-11-05T10:00:00' }), now)).toBe(true)
  })

  it('is false for a future due date or no due date', () => {
    expect(isOverdue(task({ dueDate: '2026-11-07T10:00:00' }), now)).toBe(false)
    expect(isOverdue(task({ dueDate: null }), now)).toBe(false)
  })

  it('is false for completed and cancelled tasks', () => {
    expect(isOverdue(task({ dueDate: '2026-11-05T10:00:00', status: 'COMPLETED' }), now)).toBe(false)
    expect(isOverdue(task({ dueDate: '2026-11-05T10:00:00', status: 'CANCELLED' }), now)).toBe(false)
  })
})

describe('filterTasks', () => {
  const tasks = [
    task({ id: '1', status: 'IN_PROGRESS', urgency: 'HIGH' }),
    task({ id: '2', status: 'COMPLETED', urgency: 'HIGH' }),
    task({ id: '3', status: 'IN_PROGRESS', urgency: 'LOW' }),
  ]

  it('keeps everything for ALL filters', () => {
    expect(filterTasks(tasks, { status: 'ALL', urgency: 'ALL' })).toHaveLength(3)
  })

  it('combines status and urgency', () => {
    const result = filterTasks(tasks, { status: 'IN_PROGRESS', urgency: 'HIGH' })

    expect(result.map((t) => t.id)).toEqual(['1'])
  })
})

describe('sortTasks', () => {
  it('orders by due date with undated tasks last', () => {
    const tasks = [
      task({ id: 'none' }),
      task({ id: 'late', dueDate: '2026-12-01T10:00:00' }),
      task({ id: 'soon', dueDate: '2026-11-08T10:00:00' }),
    ]

    expect(sortTasks(tasks, 'dueDate').map((t) => t.id)).toEqual(['soon', 'late', 'none'])
  })

  it('orders by urgency from high to low', () => {
    const tasks = [task({ id: 'l', urgency: 'LOW' }), task({ id: 'h', urgency: 'HIGH' }), task({ id: 'n' })]

    expect(sortTasks(tasks, 'urgency').map((t) => t.id)).toEqual(['h', 'n', 'l'])
  })

  it('orders by creation time, newest first', () => {
    const tasks = [
      task({ id: 'old', createdAt: '2026-11-01T10:00:00' }),
      task({ id: 'new', createdAt: '2026-11-05T10:00:00' }),
    ]

    expect(sortTasks(tasks, 'createdAt').map((t) => t.id)).toEqual(['new', 'old'])
  })

  it('does not change the input array', () => {
    const tasks = [task({ id: 'b', urgency: 'LOW' }), task({ id: 'a', urgency: 'HIGH' })]

    sortTasks(tasks, 'urgency')

    expect(tasks.map((t) => t.id)).toEqual(['b', 'a'])
  })
})

describe('date formatting', () => {
  it('formats a due date for display', () => {
    expect(formatDue('2026-11-09T17:00:00')).toBe('Nov 9, 5:00 PM')
  })

  it('trims seconds for the datetime input', () => {
    expect(toInputValue('2026-11-09T17:00:00')).toBe('2026-11-09T17:00')
    expect(toInputValue(null)).toBe('')
  })
})

describe('changedCharacteristics', () => {
  const original = task({ title: 'Old', description: 'Desc', urgency: 'LOW', dueDate: '2026-11-09T17:00:00' })

  it('returns nothing when the form is unchanged', () => {
    expect(changedCharacteristics(original, toFormValues(original))).toEqual([])
  })

  it('sends only the fields that changed, trimmed', () => {
    const values = { ...toFormValues(original), title: '  New title ', urgency: 'HIGH' as const, status: 'COMPLETED' as const }

    expect(changedCharacteristics(original, values)).toEqual([
      { name: 'TITLE', value: 'New title' },
      { name: 'URGENCY', value: 'HIGH' },
      { name: 'STATUS', value: 'COMPLETED' },
    ])
  })

  it('sends a changed due date', () => {
    const values = { ...toFormValues(original), dueDate: '2026-12-01T09:30' }

    expect(changedCharacteristics(original, values)).toEqual([{ name: 'DUE_DATE', value: '2026-12-01T09:30' }])
  })

  it('does not send an emptied due date because the API cannot clear it', () => {
    const values = { ...toFormValues(original), dueDate: '' }

    expect(changedCharacteristics(original, values)).toEqual([])
  })
})
