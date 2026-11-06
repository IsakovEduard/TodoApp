import type { SerializedError } from '@reduxjs/toolkit'
import type { FetchBaseQueryError } from '@reduxjs/toolkit/query'

const GENERIC_MESSAGE = 'Something went wrong. Please try again.'
const NETWORK_MESSAGE = 'Cannot reach the server. Check your connection and try again.'

export function getErrorStatus(error: FetchBaseQueryError | SerializedError | undefined): number | null {
  return error && 'status' in error && typeof error.status === 'number' ? error.status : null
}

export function getErrorMessage(error: FetchBaseQueryError | SerializedError | undefined): string {
  if (!error) {
    return GENERIC_MESSAGE
  }
  if ('status' in error) {
    if (error.status === 'FETCH_ERROR') {
      return NETWORK_MESSAGE
    }
    const { data } = error
    if (typeof data === 'string' && data.length > 0) {
      return data
    }
    if (data && typeof data === 'object' && 'message' in data && typeof data.message === 'string') {
      return data.message
    }
  }
  return GENERIC_MESSAGE
}
