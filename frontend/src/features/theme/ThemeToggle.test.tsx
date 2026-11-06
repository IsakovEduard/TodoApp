import { screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import reducer, { themeChanged, THEME_KEY } from './themeSlice'
import { renderApp } from '../../test/renderApp'

describe('themeSlice', () => {
  it('defaults to following the system', () => {
    expect(reducer(undefined, { type: 'init' })).toEqual({ preference: 'system' })
  })

  it('restores a stored preference', () => {
    localStorage.setItem(THEME_KEY, 'dark')

    expect(reducer(undefined, { type: 'init' })).toEqual({ preference: 'dark' })
  })

  it('ignores an unknown stored value', () => {
    localStorage.setItem(THEME_KEY, 'sepia')

    expect(reducer(undefined, { type: 'init' })).toEqual({ preference: 'system' })
  })

  it('stores the chosen preference', () => {
    expect(reducer({ preference: 'system' }, themeChanged('light'))).toEqual({ preference: 'light' })
  })
})

describe('ThemeToggle', () => {
  it('switches to the dark palette and remembers it', async () => {
    renderApp('/login')

    await userEvent.click(screen.getByRole('button', { name: 'Dark theme' }))

    expect(document.documentElement).toHaveClass('dark')
    expect(screen.getByRole('button', { name: 'Dark theme' })).toHaveAttribute('aria-pressed', 'true')
    expect(localStorage.getItem(THEME_KEY)).toBe('dark')
  })

  it('switches back to the light palette', async () => {
    renderApp('/login')
    await userEvent.click(screen.getByRole('button', { name: 'Dark theme' }))

    await userEvent.click(screen.getByRole('button', { name: 'Light theme' }))

    expect(document.documentElement).not.toHaveClass('dark')
    expect(localStorage.getItem(THEME_KEY)).toBe('light')
  })
})
