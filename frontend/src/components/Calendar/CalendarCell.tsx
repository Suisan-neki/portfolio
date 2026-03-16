import { useNavigate } from 'react-router-dom';
import type { DailyLog } from '../../types';
import { getStamps, formatDate, commitColor } from '../../utils/calendar';

interface CalendarCellProps {
  date: Date;
  log?: DailyLog;
  isToday: boolean;
}

export function CalendarCell({ date, log, isToday }: CalendarCellProps) {
  const navigate = useNavigate();
  const dateStr = formatDate(date);
  const stamps = log ? getStamps(log) : [];
  const hasDiary = log?.diaryId !== null && log?.diaryId !== undefined;
  const commitCount = log?.commitCount ?? 0;

  const handleClick = () => {
    navigate(`/diary/${dateStr}`);
  };

  return (
    <div
      onClick={handleClick}
      className="relative rounded-lg p-2 cursor-pointer transition-all duration-150 select-none min-h-[80px] flex flex-col"
      style={{
        backgroundColor: isToday ? '#1c2128' : '#161b22',
        border: isToday ? '1px solid #58a6ff' : '1px solid #21262d',
      }}
      onMouseEnter={(e) => {
        (e.currentTarget as HTMLDivElement).style.backgroundColor = '#21262d';
        (e.currentTarget as HTMLDivElement).style.borderColor = '#30363d';
      }}
      onMouseLeave={(e) => {
        (e.currentTarget as HTMLDivElement).style.backgroundColor = isToday ? '#1c2128' : '#161b22';
        (e.currentTarget as HTMLDivElement).style.borderColor = isToday ? '#58a6ff' : '#21262d';
      }}
    >
      {/* Date number */}
      <div className="flex items-center justify-between mb-1">
        <span
          className="text-sm font-medium leading-none"
          style={{ color: isToday ? '#58a6ff' : '#8b949e' }}
        >
          {date.getDate()}
        </span>
        {/* Commit indicator */}
        {commitCount > 0 && (
          <div
            className="w-2 h-2 rounded-full"
            style={{ backgroundColor: commitColor(commitCount) }}
            title={`${commitCount} commits`}
          />
        )}
      </div>

      {/* Stamps */}
      {stamps.length > 0 && (
        <div className="flex flex-wrap gap-0.5 mt-auto">
          {stamps.map((stamp, i) => (
            <span key={i} className="text-sm leading-none" title={stampLabel(stamp)}>
              {stamp}
            </span>
          ))}
        </div>
      )}

      {/* Diary indicator dot */}
      {hasDiary && (
        <div
          className="absolute bottom-1.5 right-1.5 w-1.5 h-1.5 rounded-full"
          style={{ backgroundColor: '#bc8cff' }}
          title="日記あり"
        />
      )}
    </div>
  );
}

function stampLabel(stamp: string): string {
  switch (stamp) {
    case '🏊': return '水泳';
    case '📚': return '資格勉強';
    case '💻': return 'GitHub commit';
    case '📝': return '日記';
    default: return '';
  }
}
