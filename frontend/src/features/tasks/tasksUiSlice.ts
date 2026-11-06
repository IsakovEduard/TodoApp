import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import { loggedOut } from '../auth/authSlice'
import type { SortKey, StatusFilter, UrgencyFilter } from './taskModel'

export interface TasksUiState {
  status: StatusFilter
  urgency: UrgencyFilter
  sortKey: SortKey
  selectedIds: string[]
}

const initialState: TasksUiState = {
  status: 'ALL',
  urgency: 'ALL',
  sortKey: 'dueDate',
  selectedIds: [],
}

const tasksUiSlice = createSlice({
  name: 'tasksUi',
  initialState,
  reducers: {
    statusFilterChanged(state, action: PayloadAction<StatusFilter>) {
      state.status = action.payload
    },
    urgencyFilterChanged(state, action: PayloadAction<UrgencyFilter>) {
      state.urgency = action.payload
    },
    sortKeyChanged(state, action: PayloadAction<SortKey>) {
      state.sortKey = action.payload
    },
    filtersReset(state) {
      state.status = 'ALL'
      state.urgency = 'ALL'
    },
    selectionToggled(state, action: PayloadAction<string>) {
      const id = action.payload
      state.selectedIds = state.selectedIds.includes(id)
        ? state.selectedIds.filter((selected) => selected !== id)
        : [...state.selectedIds, id]
    },
    selectionSet(state, action: PayloadAction<string[]>) {
      state.selectedIds = action.payload
    },
    selectionCleared(state) {
      state.selectedIds = []
    },
  },
  extraReducers: (builder) => {
    builder.addCase(loggedOut, () => initialState)
  },
})

export const {
  statusFilterChanged,
  urgencyFilterChanged,
  sortKeyChanged,
  filtersReset,
  selectionToggled,
  selectionSet,
  selectionCleared,
} = tasksUiSlice.actions
export default tasksUiSlice.reducer
