import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { CalendarHeader } from '../components/Calendar/CalendarHeader';
import { CalendarGrid } from '../components/Calendar/CalendarGrid';
import { fetchMonthlyLogs } from '../api';

export function CalendarPage() {
  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth() + 1);

  const { data: logs = [], isLoading } = useQuery({
    queryKey: ['calendar', year, month],
    queryFn: () => fetchMonthlyLogs(year, month),
  });

  const handlePrev = () => {
    if (month === 1) {
      setYear(y => y - 1);
      setMonth(12);
    } else {
      setMonth(m => m - 1);
    }
  };

  const handleNext = () => {
    if (month === 12) {
      setYear(y => y + 1);
      setMonth(1);
    } else {
      setMonth(m => m + 1);
    }
  };

  // Stats for the month
  const swimDays = logs.filter(l => l.swim).length;
  const studyDays = logs.filter(l => l.study).length;
  const diaryDays = logs.filter(l => l.diaryId !== null).length;
  const totalCommits = logs.reduce((sum, l) => sum + l.commitCount, 0);

  return (
    <div className="p-6 max-w-5xl mx-auto">
      {/* Page title */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold" style={{ color: '#e6edf3' }}>Life Log</h1>
        <p className="mt-1 text-sm" style={{ color: '#8b949e' }}>
          技術活動と生活習慣の記録
        </p>
      </div>

      {/* Stats bar */}
      <div className="grid grid-cols-4 gap-3 mb-6">
        {[
          { label: '🏊 水泳', value: swimDays, unit: '日' },
          { label: '📚 勉強', value: studyDays, unit: '日' },
          { label: '💻 Commits', value: totalCommits, unit: '' },
          { label: '📝 日記', value: diaryDays, unit: '日' },
        ].map(({ label, value, unit }) => (
          <div
            key={label}
            className="rounded-lg p-3"
            style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
          >
            <div className="text-xs mb-1" style={{ color: '#8b949e' }}>{label}</div>
            <div className="text-xl font-semibold" style={{ color: '#e6edf3' }}>
              {value}
              <span className="text-sm font-normal ml-0.5" style={{ color: '#8b949e' }}>{unit}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Calendar */}
      <div
        className="rounded-xl p-5"
        style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
      >
        <CalendarHeader year={year} month={month} onPrev={handlePrev} onNext={handleNext} />
        {isLoading ? (
          <div className="flex items-center justify-center h-64">
            <div className="text-sm" style={{ color: '#8b949e' }}>Loading...</div>
          </div>
        ) : (
          <CalendarGrid year={year} month={month} logs={logs} />
        )}
      </div>

      {/* Legend */}
      <div className="mt-4 flex items-center gap-4 flex-wrap">
        {[
          { stamp: '🏊', label: '水泳' },
          { stamp: '📚', label: '資格勉強' },
          { stamp: '💻', label: 'GitHub commit' },
          { stamp: '📝', label: '日記' },
        ].map(({ stamp, label }) => (
          <div key={stamp} className="flex items-center gap-1.5 text-xs" style={{ color: '#8b949e' }}>
            <span>{stamp}</span>
            <span>{label}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
