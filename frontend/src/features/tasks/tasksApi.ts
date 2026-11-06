import { generatedApi } from '../../api/generated'

export const tasksApi = generatedApi.enhanceEndpoints({
  endpoints: {
    getTasksByFilter: { providesTags: ['Task'] },
    createTask: { invalidatesTags: ['Task'] },
    updateTaskCharacteristics: { invalidatesTags: ['Task'] },
    deleteTask: { invalidatesTags: ['Task'] },
  },
})

export const {
  useGetTasksByFilterQuery,
  useCreateTaskMutation,
  useUpdateTaskCharacteristicsMutation,
  useDeleteTaskMutation,
} = tasksApi
