import {
  createApi,
  fetchBaseQuery,
  type BaseQueryFn,
  type FetchArgs,
  type FetchBaseQueryError,
} from '@reduxjs/toolkit/query/react'
import type { RootState } from '../app/store'
import { loggedOut } from '../features/auth/authSlice'

const rawBaseQuery = fetchBaseQuery({
  baseUrl: window.location.origin,
  responseHandler: 'content-type',
  prepareHeaders: (headers, { getState }) => {
    const { token } = (getState() as RootState).auth
    if (token) {
      headers.set('Authorization', `Bearer ${token}`)
    }
    return headers
  },
})

const baseQueryWithAuth: BaseQueryFn<string | FetchArgs, unknown, FetchBaseQueryError> = async (
  args,
  api,
  extraOptions,
) => {
  const sentToken = (api.getState() as RootState).auth.token !== null
  const result = await rawBaseQuery(args, api, extraOptions)
  if (sentToken && result.error?.status === 401) {
    api.dispatch(loggedOut())
  }
  return result
}

export const baseApi = createApi({
  reducerPath: 'api',
  baseQuery: baseQueryWithAuth,
  tagTypes: ['Task'],
  endpoints: () => ({}),
})
