import { loggedOut } from '../auth/authSlice'
import reducer, {
  filtersReset,
  selectionCleared,
  selectionSet,
  selectionToggled,
  sortKeyChanged,
  statusFilterChanged,
  urgencyFilterChanged,
} from './tasksUiSlice'

const initial = reducer(undefined, { type: 'init' })

describe('tasksUiSlice', () => {
  it('starts unfiltered, sorted by due date, with nothing selected', () => {
    expect(initial).toEqual({ status: 'ALL', urgency: 'ALL', sortKey: 'dueDate', selectedIds: [] })
  })

  it('stores filters and sort order', () => {
    let state = reducer(initial, statusFilterChanged('COMPLETED'))
    state = reducer(state, urgencyFilterChanged('HIGH'))
    state = reducer(state, sortKeyChanged('urgency'))

    expect(state).toMatchObject({ status: 'COMPLETED', urgency: 'HIGH', sortKey: 'urgency' })
  })

  it('resets filters but keeps the sort order', () => {
    let state = reducer(initial, statusFilterChanged('COMPLETED'))
    state = reducer(state, sortKeyChanged('createdAt'))

    expect(reducer(state, filtersReset())).toMatchObject({ status: 'ALL', urgency: 'ALL', sortKey: 'createdAt' })
  })

  it('toggles, sets and clears the selection', () => {
    let state = reducer(initial, selectionToggled('1'))
    state = reducer(state, selectionToggled('2'))
    expect(state.selectedIds).toEqual(['1', '2'])

    state = reducer(state, selectionToggled('1'))
    expect(state.selectedIds).toEqual(['2'])

    state = reducer(state, selectionSet(['3', '4']))
    expect(state.selectedIds).toEqual(['3', '4'])

    expect(reducer(state, selectionCleared()).selectedIds).toEqual([])
  })

  it('forgets everything on logout so the next user starts clean', () => {
    const state = reducer(reducer(initial, statusFilterChanged('COMPLETED')), selectionSet(['1']))

    expect(reducer(state, loggedOut())).toEqual(initial)
  })
})
