import reducer, { loggedOut, tokenReceived, TOKEN_KEY } from './authSlice'

describe('authSlice', () => {
  it('starts without a token when nothing is stored', () => {
    expect(reducer(undefined, { type: 'init' })).toEqual({ token: null })
  })

  it('restores the token from storage', () => {
    localStorage.setItem(TOKEN_KEY, 'stored-token')

    expect(reducer(undefined, { type: 'init' })).toEqual({ token: 'stored-token' })
  })

  it('keeps the received token', () => {
    expect(reducer({ token: null }, tokenReceived('abc'))).toEqual({ token: 'abc' })
  })

  it('clears the token on logout', () => {
    expect(reducer({ token: 'abc' }, loggedOut())).toEqual({ token: null })
  })
})
