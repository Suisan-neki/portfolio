// ============================================================
// Core Data Types for LifeLog Portfolio
// ============================================================

export interface DailyLog {
  date: string; // YYYY-MM-DD
  swim: boolean;
  study: boolean;
  commitCount: number;
  diaryId: number | null;
}

export interface Diary {
  id: number;
  date: string;
  title: string;
  contentMarkdown: string;
  createdAt: string;
}

export interface Project {
  id: number;
  title: string;
  description: string;
  tags: string[];
  githubUrl: string | null;
  demoUrl: string | null;
  createdAt: string;
}

export interface Goal {
  id: number;
  title: string;
  description: string;
  target: number;
  progress: number;
  createdAt: string;
}

export interface ExternalActivity {
  id: number;
  source: 'github' | 'qiita' | 'rss';
  title: string;
  url: string;
  createdAt: string;
}

export interface CalendarMonth {
  year: number;
  month: number; // 1-12
  logs: DailyLog[];
}

// Stamp type for calendar display
export type ActivityStamp = '🏊' | '📚' | '💻' | '📝';

export interface DayActivity {
  date: string;
  stamps: ActivityStamp[];
  commitCount: number;
  hasDiary: boolean;
}

// Admin types
export interface CreateDiaryRequest {
  date: string;
  title: string;
  contentMarkdown: string;
}

export interface UpdateRoutineRequest {
  date: string;
  swim: boolean;
  study: boolean;
}

export interface CreateProjectRequest {
  title: string;
  description: string;
  tags: string[];
  githubUrl?: string;
  demoUrl?: string;
}

export interface CreateGoalRequest {
  title: string;
  description: string;
  target: number;
}

export interface UpdateGoalProgressRequest {
  progress: number;
}

// Records
export type RecordCategory = 'event' | 'conference' | 'book';

export interface Record {
  id: number;
  category: RecordCategory;
  title: string;
  url: string | null;
  date: string; // YYYY-MM-DD
  note: string;
  createdAt: string;
}

export interface CreateRecordRequest {
  category: RecordCategory;
  title: string;
  url?: string;
  date: string;
  note?: string;
}
