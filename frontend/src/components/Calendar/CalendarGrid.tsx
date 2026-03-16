import { CalendarCell } from './CalendarCell';
import type { DailyLog } from '../../types';
import { getCalendarDays, logsToMap, formatDate, today } from '../../utils/calendar';

interface CalendarGridProps {
  year: number;
  month: number;
  logs: DailyLog[];
}

const DOW_LABELS = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

export function CalendarGrid({ year, month, logs }: CalendarGridProps) {
  const days = getCalendarDays(year, month);
  const logMap = logsToMap(logs);
  const todayStr = today();

  return (
    <div>
      {/* Day of week header */}
      <div className="grid grid-cols-7 gap-1 mb-1">
        {DOW_LABELS.map((dow) => (
          <div
            key={dow}
            className="text-center text-xs font-medium py-1"
            style={{ color: '#8b949e' }}
          >
            {dow}
          </div>
        ))}
      </div>

      {/* Calendar cells */}
      <div className="grid grid-cols-7 gap-1">
        {days.map((date, i) => {
          if (!date) {
            return <div key={`empty-${i}`} className="min-h-[80px]" />;
          }
          const dateStr = formatDate(date);
          const log = logMap.get(dateStr);
          return (
            <CalendarCell
              key={dateStr}
              date={date}
              log={log}
              isToday={dateStr === todayStr}
            />
          );
        })}
      </div>
    </div>
  );
}
