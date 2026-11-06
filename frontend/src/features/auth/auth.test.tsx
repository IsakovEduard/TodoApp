import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { http, HttpResponse } from 'msw'
import { server } from '../../test/server'
import { renderApp } from '../../test/renderApp'

function mockMe(username = 'alice') {
  server.use(http.get('/auth/me', () => HttpResponse.json({ username })))
}

async function fillCredentials(username: string, password: string) {
  if (username) {
    await userEvent.type(screen.getByLabelText('Username'), username)
  }
  if (password) {
    await userEvent.type(screen.getByLabelText('Password'), password)
  }
}

describe('login', () => {
  it('logs in, stores the token and shows the tasks page', async () => {
    mockMe()
    server.use(http.post('/auth/login', () => HttpResponse.json({ token: 'jwt-1' })))
    const store = renderApp('/login')

    await fillCredentials('alice', 'secret')
    await userEvent.click(screen.getByRole('button', { name: 'Log in' }))

    expect(await screen.findByText('Signed in as alice')).toBeInTheDocument()
    expect(store.getState().auth.token).toBe('jwt-1')
    expect(localStorage.getItem('todo.token')).toBe('jwt-1')
  })

  it('shows a friendly message for wrong credentials', async () => {
    server.use(http.post('/auth/login', () => new HttpResponse(null, { status: 401 })))
    renderApp('/login')

    await fillCredentials('alice', 'wrong')
    await userEvent.click(screen.getByRole('button', { name: 'Log in' }))

    expect(await screen.findByRole('alert')).toHaveTextContent('Invalid username or password.')
  })

  it('asks for missing fields without calling the server', async () => {
    renderApp('/login')

    await userEvent.click(screen.getByRole('button', { name: 'Log in' }))

    expect(screen.getByText('Username is required')).toBeInTheDocument()
    expect(screen.getByText('Password is required')).toBeInTheDocument()
  })

  it('reports an unreachable server', async () => {
    server.use(http.post('/auth/login', () => HttpResponse.error()))
    renderApp('/login')

    await fillCredentials('alice', 'secret')
    await userEvent.click(screen.getByRole('button', { name: 'Log in' }))

    expect(await screen.findByRole('alert')).toHaveTextContent('Cannot reach the server')
  })

  it('sends a signed-in user straight to the tasks page', async () => {
    mockMe()
    renderApp('/login', 'existing-token')

    expect(await screen.findByText('Signed in as alice')).toBeInTheDocument()
  })
})

describe('registration', () => {
  it('registers, logs in and shows the tasks page', async () => {
    mockMe('bob')
    server.use(
      http.post('/auth/register', () => HttpResponse.text('User registered')),
      http.post('/auth/login', () => HttpResponse.json({ token: 'jwt-2' })),
    )
    const store = renderApp('/register')

    await fillCredentials('bob', 'secret')
    await userEvent.click(screen.getByRole('button', { name: 'Create account' }))

    expect(await screen.findByText('Signed in as bob')).toBeInTheDocument()
    expect(store.getState().auth.token).toBe('jwt-2')
  })

  it('shows the server message when the username is taken', async () => {
    server.use(
      http.post('/auth/register', () => HttpResponse.text('Username already exists', { status: 400 })),
    )
    renderApp('/register')

    await fillCredentials('bob', 'secret')
    await userEvent.click(screen.getByRole('button', { name: 'Create account' }))

    expect(await screen.findByRole('alert')).toHaveTextContent('Username already exists')
  })
})

describe('session handling', () => {
  it('redirects to login when not signed in', async () => {
    renderApp('/tasks')

    expect(await screen.findByRole('button', { name: 'Log in' })).toBeInTheDocument()
  })

  it('returns to login and drops the token when the server rejects it', async () => {
    server.use(http.get('/auth/me', () => new HttpResponse(null, { status: 401 })))
    const store = renderApp('/tasks', 'expired-token')

    expect(await screen.findByRole('button', { name: 'Log in' })).toBeInTheDocument()
    expect(store.getState().auth.token).toBeNull()
    expect(localStorage.getItem('todo.token')).toBeNull()
  })

  it('logs out and clears the stored token', async () => {
    mockMe()
    const store = renderApp('/tasks', 'valid-token')
    await screen.findByText('Signed in as alice')

    await userEvent.click(screen.getByRole('button', { name: 'Log out' }))

    await waitFor(() => expect(store.getState().auth.token).toBeNull())
    expect(await screen.findByRole('button', { name: 'Log in' })).toBeInTheDocument()
    expect(localStorage.getItem('todo.token')).toBeNull()
  })
})
