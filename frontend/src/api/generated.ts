import { baseApi as api } from "./baseApi";
const injectedRtkApi = api.injectEndpoints({
  endpoints: (build) => ({
    register: build.mutation<RegisterApiResponse, RegisterApiArg>({
      query: (queryArg) => ({
        url: `/auth/register`,
        method: "POST",
        body: queryArg.v1Credentials,
      }),
    }),
    login: build.mutation<LoginApiResponse, LoginApiArg>({
      query: (queryArg) => ({
        url: `/auth/login`,
        method: "POST",
        body: queryArg.v1Credentials,
      }),
    }),
    me: build.query<MeApiResponse, MeApiArg>({
      query: () => ({ url: `/auth/me` }),
    }),
    getTasksByFilter: build.query<
      GetTasksByFilterApiResponse,
      GetTasksByFilterApiArg
    >({
      query: (queryArg) => ({
        url: `/task-management`,
        params: {
          taskId: queryArg.taskId,
          status: queryArg.status,
          urgency: queryArg.urgency,
        },
      }),
    }),
    createTask: build.mutation<CreateTaskApiResponse, CreateTaskApiArg>({
      query: (queryArg) => ({
        url: `/task-management`,
        method: "POST",
        body: queryArg.v1CreateTaskInput,
      }),
    }),
    deleteTask: build.mutation<DeleteTaskApiResponse, DeleteTaskApiArg>({
      query: (queryArg) => ({
        url: `/task-management`,
        method: "DELETE",
        body: queryArg.v1DeleteTaskInput,
      }),
    }),
    updateTaskCharacteristics: build.mutation<
      UpdateTaskCharacteristicsApiResponse,
      UpdateTaskCharacteristicsApiArg
    >({
      query: (queryArg) => ({
        url: `/task-management`,
        method: "PUT",
        body: queryArg.v1UpdateCharacteristicsInput,
      }),
    }),
    getTaskById: build.query<GetTaskByIdApiResponse, GetTaskByIdApiArg>({
      query: (queryArg) => ({ url: `/task-management/${queryArg.taskId}` }),
    }),
  }),
  overrideExisting: false,
});
export { injectedRtkApi as generatedApi };
export type RegisterApiResponse = /** status 200 User registered */ string;
export type RegisterApiArg = {
  v1Credentials: V1Credentials;
};
export type LoginApiResponse = /** status 200 Authenticated */ V1AuthToken;
export type LoginApiArg = {
  v1Credentials: V1Credentials;
};
export type MeApiResponse = /** status 200 Current user */ V1CurrentUser;
export type MeApiArg = void;
export type GetTasksByFilterApiResponse =
  /** status 200 List of tasks */ V1Task[];
export type GetTasksByFilterApiArg = {
  taskId?: string;
  status?: string;
  urgency?: string;
};
export type CreateTaskApiResponse =
  /** status 201 Created, returns the new task id */ string;
export type CreateTaskApiArg = {
  v1CreateTaskInput: V1CreateTaskInput;
};
export type DeleteTaskApiResponse = /** status 200 Removed task ids */ number;
export type DeleteTaskApiArg = {
  v1DeleteTaskInput: V1DeleteTaskInput;
};
export type UpdateTaskCharacteristicsApiResponse =
  /** status 200 Tasks after update characteristics */ V1Task[];
export type UpdateTaskCharacteristicsApiArg = {
  v1UpdateCharacteristicsInput: V1UpdateCharacteristicsInput;
};
export type GetTaskByIdApiResponse = /** status 200 The task */ V1Task;
export type GetTaskByIdApiArg = {
  taskId: string;
};
export type V1Error = {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
  fieldErrors?: {
    [key: string]: string;
  };
};
export type V1Credentials = {
  username: string;
  password: string;
};
export type V1AuthToken = {
  token: string;
};
export type V1CurrentUser = {
  username: string;
};
export type V1Urgency = "LOW" | "NORMAL" | "HIGH";
export type V1Status = "IN_PROGRESS" | "COMPLETED" | "CANCELLED" | "SCHEDULED";
export type V1ActivationStatus = "AC" | "DE";
export type V1Task = {
  id?: string;
  userId?: string;
  title?: string;
  description?: string;
  urgency?: V1Urgency;
  dueDate?: string;
  createdAt?: string;
  status?: V1Status;
  activationStatus?: V1ActivationStatus;
};
export type V1CreateTaskInput = {
  title?: string;
  description?: string;
  urgency?: V1Urgency;
  dueDate?: string;
};
export type V1DeleteTaskInput = {
  taskIds: string[];
};
export type V1Characteristic = {
  /** One of TITLE, DESCRIPTION, DUE_DATE, CREATED_AT, URGENCY, STATUS (case-insensitive). */
  name?: string;
  /** New value. DUE_DATE takes an ISO local date-time such as 2026-12-01T09:30, and an empty value clears it. URGENCY and STATUS must be one of their enum values. */
  value?: string;
};
export type V1UpdateCharacteristicsInput = {
  taskId?: string;
  characteristics?: V1Characteristic[];
};
export const {
  useRegisterMutation,
  useLoginMutation,
  useMeQuery,
  useGetTasksByFilterQuery,
  useCreateTaskMutation,
  useDeleteTaskMutation,
  useUpdateTaskCharacteristicsMutation,
  useGetTaskByIdQuery,
} = injectedRtkApi;
