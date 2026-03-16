import { apiClient } from './client';
import type {
  DailyLog,
  Diary,
  Project,
  Goal,
  ExternalActivity,
  CreateDiaryRequest,
  UpdateRoutineRequest,
  CreateProjectRequest,
  CreateGoalRequest,
  UpdateGoalProgressRequest,
  Record,
  CreateRecordRequest,
} from '../types';

// ── Calendar / Daily Logs ──────────────────────────────────────

export const fetchMonthlyLogs = async (year: number, month: number): Promise<DailyLog[]> => {
  const { data } = await apiClient.get<DailyLog[]>(`/calendar/${year}/${month}`);
  return data;
};

// ── Diary ──────────────────────────────────────────────────────

export const fetchDiary = async (date: string): Promise<Diary | null> => {
  try {
    const { data } = await apiClient.get<Diary>(`/diary/${date}`);
    return data;
  } catch {
    return null;
  }
};

export const fetchDiaries = async (): Promise<Diary[]> => {
  const { data } = await apiClient.get<Diary[]>('/diary');
  return data;
};

export const createDiary = async (req: CreateDiaryRequest): Promise<Diary> => {
  const { data } = await apiClient.post<Diary>('/diary', req);
  return data;
};

export const updateDiary = async (id: number, req: Partial<CreateDiaryRequest>): Promise<Diary> => {
  const { data } = await apiClient.put<Diary>(`/diary/${id}`, req);
  return data;
};

export const deleteDiary = async (id: number): Promise<void> => {
  await apiClient.delete(`/diary/${id}`);
};

// ── Routines ───────────────────────────────────────────────────

export const updateRoutine = async (req: UpdateRoutineRequest): Promise<DailyLog> => {
  const { data } = await apiClient.put<DailyLog>('/routine', req);
  return data;
};

// ── Projects ───────────────────────────────────────────────────

export const fetchProjects = async (): Promise<Project[]> => {
  const { data } = await apiClient.get<Project[]>('/projects');
  return data;
};

export const createProject = async (req: CreateProjectRequest): Promise<Project> => {
  const { data } = await apiClient.post<Project>('/projects', req);
  return data;
};

export const updateProject = async (id: number, req: Partial<CreateProjectRequest>): Promise<Project> => {
  const { data } = await apiClient.put<Project>(`/projects/${id}`, req);
  return data;
};

export const deleteProject = async (id: number): Promise<void> => {
  await apiClient.delete(`/projects/${id}`);
};

// ── Goals ──────────────────────────────────────────────────────

export const fetchGoals = async (): Promise<Goal[]> => {
  const { data } = await apiClient.get<Goal[]>('/goals');
  return data;
};

export const createGoal = async (req: CreateGoalRequest): Promise<Goal> => {
  const { data } = await apiClient.post<Goal>('/goals', req);
  return data;
};

export const updateGoalProgress = async (id: number, req: UpdateGoalProgressRequest): Promise<Goal> => {
  const { data } = await apiClient.put<Goal>(`/goals/${id}/progress`, req);
  return data;
};

export const deleteGoal = async (id: number): Promise<void> => {
  await apiClient.delete(`/goals/${id}`);
};

// ── External Activity ──────────────────────────────────────────

export const fetchActivities = async (): Promise<ExternalActivity[]> => {
  const { data } = await apiClient.get<ExternalActivity[]>('/activity');
  return data;
};

export const syncGitHub = async (): Promise<{ synced: number }> => {
  const { data } = await apiClient.post<{ synced: number }>('/github/sync');
  return data;
};

// ── Auth ───────────────────────────────────────────────────────

export const adminLogin = async (password: string): Promise<{ token: string }> => {
  const { data } = await apiClient.post<{ token: string }>('/auth/login', { password });
  return data;
};

// ── Records ────────────────────────────────────────────────────

export const fetchRecords = async (category?: string): Promise<Record[]> => {
  const params = category ? { category } : {};
  const { data } = await apiClient.get<Record[]>('/records', { params });
  return data;
};

export const createRecord = async (req: CreateRecordRequest): Promise<Record> => {
  const { data } = await apiClient.post<Record>('/records', req);
  return data;
};

export const updateRecord = async (id: number, req: CreateRecordRequest): Promise<Record> => {
  const { data } = await apiClient.put<Record>(`/records/${id}`, req);
  return data;
};

export const deleteRecord = async (id: number): Promise<void> => {
  await apiClient.delete(`/records/${id}`);
};
