import type { DailyLog, DayActivity, ActivityStamp } from '../types';

/**
 * DailyLog から ActivityStamp の配列を生成する
 */
export function getStamps(log: DailyLog): ActivityStamp[] {
  const stamps: ActivityStamp[] = [];
  if (log.swim) stamps.push('🏊');
  if (log.study) stamps.push('📚');
  if (log.commitCount > 0) stamps.push('💻');
  if (log.diaryId !== null) stamps.push('📝');
  return stamps;
}

/**
 * 月の全日付を生成する（カレンダーグリッド用）
 */
export function getCalendarDays(year: number, month: number): (Date | null)[] {
  const firstDay = new Date(year, month - 1, 1);
  const lastDay = new Date(year, month, 0);
  const startDow = firstDay.getDay(); // 0=Sun

  const days: (Date | null)[] = [];

  // 先頭の空白
  for (let i = 0; i < startDow; i++) {
    days.push(null);
  }

  // 実際の日付
  for (let d = 1; d <= lastDay.getDate(); d++) {
    days.push(new Date(year, month - 1, d));
  }

  return days;
}

/**
 * DailyLog[] を date をキーにした Map に変換する
 */
export function logsToMap(logs: DailyLog[]): Map<string, DailyLog> {
  const map = new Map<string, DailyLog>();
  for (const log of logs) {
    map.set(log.date, log);
  }
  return map;
}

/**
 * Date を YYYY-MM-DD 形式の文字列に変換する
 */
export function formatDate(date: Date): string {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

/**
 * YYYY-MM-DD 形式の文字列から Date を生成する
 */
export function parseDate(dateStr: string): Date {
  const [y, m, d] = dateStr.split('-').map(Number);
  return new Date(y, m - 1, d);
}

/**
 * 今日の日付を YYYY-MM-DD 形式で返す
 */
export function today(): string {
  return formatDate(new Date());
}

/**
 * DailyLog[] から DayActivity[] を生成する
 */
export function toDayActivities(logs: DailyLog[]): DayActivity[] {
  return logs.map((log) => ({
    date: log.date,
    stamps: getStamps(log),
    commitCount: log.commitCount,
    hasDiary: log.diaryId !== null,
  }));
}

/**
 * commit count に応じた GitHub contribution graph 風の色を返す
 */
export function commitColor(count: number): string {
  if (count === 0) return '#161b22';
  if (count <= 2) return '#0e4429';
  if (count <= 5) return '#006d32';
  if (count <= 10) return '#26a641';
  return '#39d353';
}
