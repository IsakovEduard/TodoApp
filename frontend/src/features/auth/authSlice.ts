import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import { readStored } from '../../app/storage'

export const TOKEN_KEY = 'todo.token'

export interface AuthState {
  token: string | null
}

const authSlice = createSlice({
  name: 'auth',
  initialState: (): AuthState => ({ token: readStored(TOKEN_KEY) }),
  reducers: {
    tokenReceived(state, action: PayloadAction<string>) {
      state.token = action.payload
    },
    loggedOut(state) {
      state.token = null
    },
  },
})

export const { tokenReceived, loggedOut } = authSlice.actions
export default authSlice.reducer
