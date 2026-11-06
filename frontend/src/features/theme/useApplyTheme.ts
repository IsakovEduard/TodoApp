import { useEffect } from 'react'
import { useAppSelector } from '../../app/hooks'

const DARK_QUERY = '(prefers-color-scheme: dark)'

export function useApplyTheme(): void {
  const preference = useAppSelector((state) => state.theme.preference)

  useEffect(() => {
    const media = window.matchMedia(DARK_QUERY)
    const apply = () => {
      const dark = preference === 'dark' || (preference === 'system' && media.matches)
      document.documentElement.classList.toggle('dark', dark)
    }
    apply()
    if (preference !== 'system') {
      return
    }
    media.addEventListener('change', apply)
    return () => media.removeEventListener('change', apply)
  }, [preference])
}
