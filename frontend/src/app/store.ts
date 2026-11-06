import { combineReducers, configureStore, createListenerMiddleware } from '@reduxjs/toolkit'
import { baseApi } from '../api/baseApi'
import authReducer, { loggedOut, TOKEN_KEY } from '../features/auth/authSlice'
import themeReducer, { THEME_KEY } from '../features/theme/themeSlice'
import { writeStored } from './storage'

const rootReducer = combineReducers({
  [baseApi.reducerPath]: baseApi.reducer,
  auth: authReducer,
  theme: themeReducer,
})

export function makeStore(preloadedState?: Partial<ReturnType<typeof rootReducer>>) {
  const listeners = createListenerMiddleware()
  listeners.startListening({
    actionCreator: loggedOut,
    effect: (_action, listenerApi) => {
      listenerApi.dispatch(baseApi.util.resetApiState())
    },
  })

  return configureStore({
    reducer: rootReducer,
    preloadedState,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware().prepend(listeners.middleware).concat(baseApi.middleware),
  })
}

export type AppStore = ReturnType<typeof makeStore>
export type RootState = ReturnType<AppStore['getState']>
export type AppDispatch = AppStore['dispatch']

export function persistStore(store: AppStore): () => void {
  return store.subscribe(() => {
    const { auth, theme } = store.getState()
    writeStored(TOKEN_KEY, auth.token)
    writeStored(THEME_KEY, theme.preference)
  })
}
