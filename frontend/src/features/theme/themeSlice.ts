import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import { readStored } from '../../app/storage'

export const THEME_KEY = 'todo.theme'

export type ThemePreference = 'light' | 'dark' | 'system'

export interface ThemeState {
  preference: ThemePreference
}

function isThemePreference(value: string | null): value is ThemePreference {
  return value === 'light' || value === 'dark' || value === 'system'
}

const themeSlice = createSlice({
  name: 'theme',
  initialState: (): ThemeState => {
    const stored = readStored(THEME_KEY)
    return { preference: isThemePreference(stored) ? stored : 'system' }
  },
  reducers: {
    themeChanged(state, action: PayloadAction<ThemePreference>) {
      state.preference = action.payload
    },
  },
})

export const { themeChanged } = themeSlice.actions
export default themeSlice.reducer
