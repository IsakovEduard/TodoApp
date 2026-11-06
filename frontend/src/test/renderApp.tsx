import { render } from '@testing-library/react'
import { onTestFinished } from 'vitest'
import { Provider } from 'react-redux'
import { MemoryRouter } from 'react-router'
import { App } from '../App'
import { makeStore, persistStore } from '../app/store'

export function renderApp(route = '/login', token: string | null = null) {
  if (token) {
    localStorage.setItem('todo.token', token)
  }
  const store = makeStore()
  onTestFinished(persistStore(store))
  render(
    <Provider store={store}>
      <MemoryRouter initialEntries={[route]}>
        <App />
      </MemoryRouter>
    </Provider>,
  )
  return store
}
