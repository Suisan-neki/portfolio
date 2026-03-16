import { useQuery } from '@tanstack/react-query';
import { Target } from 'lucide-react';
import { fetchGoals } from '../api';

export function GoalsPage() {
  const { data: goals = [], isLoading } = useQuery({
    queryKey: ['goals'],
    queryFn: fetchGoals,
  });

  return (
    <div className="p-6 max-w-3xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold" style={{ color: '#e6edf3' }}>Goals</h1>
        <p className="mt-1 text-sm" style={{ color: '#8b949e' }}>
          目標と進捗
        </p>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center h-48">
          <div className="text-sm" style={{ color: '#8b949e' }}>Loading...</div>
        </div>
      ) : goals.length === 0 ? (
        <div
          className="rounded-xl p-12 text-center"
          style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
        >
          <Target size={32} className="mx-auto mb-3" style={{ color: '#8b949e' }} />
          <p className="text-sm" style={{ color: '#8b949e' }}>目標はまだありません</p>
        </div>
      ) : (
        <div className="flex flex-col gap-4">
          {goals.map((goal) => {
            const pct = goal.target > 0 ? Math.min(100, Math.round((goal.progress / goal.target) * 100)) : 0;
            return (
              <div
                key={goal.id}
                className="rounded-xl p-5"
                style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
              >
                <div className="flex items-start justify-between gap-4 mb-3">
                  <div>
                    <h3 className="text-base font-semibold" style={{ color: '#e6edf3' }}>
                      {goal.title}
                    </h3>
                    {goal.description && (
                      <p className="text-sm mt-0.5" style={{ color: '#8b949e' }}>
                        {goal.description}
                      </p>
                    )}
                  </div>
                  <span
                    className="text-sm font-semibold shrink-0"
                    style={{ color: pct >= 100 ? '#3fb950' : '#58a6ff' }}
                  >
                    {pct}%
                  </span>
                </div>

                {/* Progress bar */}
                <div
                  className="h-1.5 rounded-full overflow-hidden"
                  style={{ backgroundColor: '#21262d' }}
                >
                  <div
                    className="h-full rounded-full transition-all duration-500"
                    style={{
                      width: `${pct}%`,
                      backgroundColor: pct >= 100 ? '#3fb950' : '#58a6ff',
                    }}
                  />
                </div>

                <div className="flex items-center justify-between mt-2">
                  <span className="text-xs" style={{ color: '#8b949e' }}>
                    {goal.progress} / {goal.target}
                  </span>
                  {pct >= 100 && (
                    <span className="text-xs" style={{ color: '#3fb950' }}>✓ 達成</span>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
