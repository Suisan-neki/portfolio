import { useQuery } from '@tanstack/react-query';
import { Github, ExternalLink, Activity } from 'lucide-react';
import { fetchActivities } from '../api';
import type { ExternalActivity } from '../types';

function sourceIcon(source: ExternalActivity['source']) {
  switch (source) {
    case 'github': return <Github size={14} />;
    case 'qiita': return <span className="text-xs font-bold">Q</span>;
    default: return <ExternalLink size={14} />;
  }
}

function sourceColor(source: ExternalActivity['source']): string {
  switch (source) {
    case 'github': return '#e6edf3';
    case 'qiita': return '#55c500';
    default: return '#8b949e';
  }
}

function sourceLabel(source: ExternalActivity['source']): string {
  switch (source) {
    case 'github': return 'GitHub';
    case 'qiita': return 'Qiita';
    default: return 'RSS';
  }
}

export function ActivityPage() {
  const { data: activities = [], isLoading } = useQuery({
    queryKey: ['activities'],
    queryFn: fetchActivities,
  });

  const grouped = activities.reduce<Record<string, ExternalActivity[]>>((acc, act) => {
    if (!acc[act.source]) acc[act.source] = [];
    acc[act.source].push(act);
    return acc;
  }, {});

  return (
    <div className="p-6 max-w-3xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold" style={{ color: '#e6edf3' }}>Activity</h1>
        <p className="mt-1 text-sm" style={{ color: '#8b949e' }}>
          GitHub・Qiita・外部活動
        </p>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center h-48">
          <div className="text-sm" style={{ color: '#8b949e' }}>Loading...</div>
        </div>
      ) : activities.length === 0 ? (
        <div
          className="rounded-xl p-12 text-center"
          style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
        >
          <Activity size={32} className="mx-auto mb-3" style={{ color: '#8b949e' }} />
          <p className="text-sm" style={{ color: '#8b949e' }}>アクティビティはまだありません</p>
        </div>
      ) : (
        <div className="flex flex-col gap-6">
          {(['github', 'qiita', 'rss'] as const).map((source) => {
            const items = grouped[source];
            if (!items || items.length === 0) return null;
            return (
              <div key={source}>
                <div className="flex items-center gap-2 mb-3">
                  <span style={{ color: sourceColor(source) }}>{sourceIcon(source)}</span>
                  <h2 className="text-sm font-semibold" style={{ color: sourceColor(source) }}>
                    {sourceLabel(source)}
                  </h2>
                  <span
                    className="text-xs px-1.5 py-0.5 rounded"
                    style={{ backgroundColor: '#21262d', color: '#8b949e' }}
                  >
                    {items.length}
                  </span>
                </div>
                <div
                  className="rounded-xl overflow-hidden"
                  style={{ border: '1px solid #21262d' }}
                >
                  {items.map((act, i) => (
                    <a
                      key={act.id}
                      href={act.url}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="flex items-center justify-between px-4 py-3 transition-colors group"
                      style={{
                        backgroundColor: '#161b22',
                        borderBottom: i < items.length - 1 ? '1px solid #21262d' : 'none',
                      }}
                      onMouseEnter={(e) => {
                        (e.currentTarget as HTMLAnchorElement).style.backgroundColor = '#1c2128';
                      }}
                      onMouseLeave={(e) => {
                        (e.currentTarget as HTMLAnchorElement).style.backgroundColor = '#161b22';
                      }}
                    >
                      <span className="text-sm" style={{ color: '#c9d1d9' }}>
                        {act.title}
                      </span>
                      <ExternalLink
                        size={12}
                        className="shrink-0 ml-2 opacity-0 group-hover:opacity-100 transition-opacity"
                        style={{ color: '#8b949e' }}
                      />
                    </a>
                  ))}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
