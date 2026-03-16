import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft, Calendar, Edit3 } from 'lucide-react';
import { fetchDiary } from '../api';
import { parseDate } from '../utils/calendar';

const MONTH_NAMES = [
  'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
  'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec',
];

export function DiaryPage() {
  const { date } = useParams<{ date: string }>();
  const navigate = useNavigate();

  const { data: diary, isLoading } = useQuery({
    queryKey: ['diary', date],
    queryFn: () => fetchDiary(date!),
    enabled: !!date,
  });

  const parsedDate = date ? parseDate(date) : null;
  const formattedDate = parsedDate
    ? `${parsedDate.getDate()} ${MONTH_NAMES[parsedDate.getMonth()]} ${parsedDate.getFullYear()}`
    : '';

  return (
    <div className="p-6 max-w-3xl mx-auto">
      {/* Back button */}
      <button
        onClick={() => navigate(-1)}
        className="flex items-center gap-2 text-sm mb-6 transition-colors"
        style={{ color: '#8b949e' }}
        onMouseEnter={(e) => (e.currentTarget.style.color = '#e6edf3')}
        onMouseLeave={(e) => (e.currentTarget.style.color = '#8b949e')}
      >
        <ArrowLeft size={16} />
        Back to Calendar
      </button>

      {/* Date header */}
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-3">
          <div
            className="w-10 h-10 rounded-lg flex items-center justify-center"
            style={{ backgroundColor: '#21262d' }}
          >
            <Calendar size={18} style={{ color: '#58a6ff' }} />
          </div>
          <div>
            <h1 className="text-xl font-semibold" style={{ color: '#e6edf3' }}>
              {formattedDate}
            </h1>
            <p className="text-xs" style={{ color: '#8b949e' }}>{date}</p>
          </div>
        </div>
        <button
          onClick={() => navigate(`/admin/diary/${date}`)}
          className="flex items-center gap-2 px-3 py-1.5 rounded-md text-sm transition-colors"
          style={{ backgroundColor: '#21262d', color: '#8b949e', border: '1px solid #30363d' }}
          onMouseEnter={(e) => {
            (e.currentTarget as HTMLButtonElement).style.backgroundColor = '#30363d';
            (e.currentTarget as HTMLButtonElement).style.color = '#e6edf3';
          }}
          onMouseLeave={(e) => {
            (e.currentTarget as HTMLButtonElement).style.backgroundColor = '#21262d';
            (e.currentTarget as HTMLButtonElement).style.color = '#8b949e';
          }}
        >
          <Edit3 size={14} />
          Edit
        </button>
      </div>

      {/* Content */}
      {isLoading ? (
        <div className="flex items-center justify-center h-48">
          <div className="text-sm" style={{ color: '#8b949e' }}>Loading...</div>
        </div>
      ) : diary ? (
        <div
          className="rounded-xl p-6"
          style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
        >
          <h2 className="text-lg font-semibold mb-4" style={{ color: '#e6edf3' }}>
            {diary.title}
          </h2>
          <div
            className="prose prose-invert max-w-none text-sm leading-relaxed whitespace-pre-wrap"
            style={{ color: '#c9d1d9' }}
          >
            {diary.contentMarkdown}
          </div>
        </div>
      ) : (
        <div
          className="rounded-xl p-12 text-center"
          style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
        >
          <div className="text-4xl mb-3">📝</div>
          <p className="text-sm" style={{ color: '#8b949e' }}>
            この日の日記はまだありません
          </p>
          <button
            onClick={() => navigate(`/admin/diary/${date}`)}
            className="mt-4 px-4 py-2 rounded-md text-sm transition-colors"
            style={{ backgroundColor: '#21262d', color: '#58a6ff', border: '1px solid #30363d' }}
          >
            日記を書く
          </button>
        </div>
      )}
    </div>
  );
}
