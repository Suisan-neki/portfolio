import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { ExternalLink, Calendar, BookOpen, Users, Presentation } from 'lucide-react';
import { fetchRecords } from '../api';
import type { Record as LifeRecord, RecordCategory } from '../types';

type FilterCategory = 'all' | RecordCategory;

const CATEGORIES: { key: FilterCategory; label: string; icon: React.ReactNode; color: string }[] = [
  { key: 'all', label: 'すべて', icon: null, color: '#8b949e' },
  { key: 'event', label: 'イベント', icon: <Users size={14} />, color: '#58a6ff' },
  { key: 'conference', label: 'カンファレンス', icon: <Presentation size={14} />, color: '#bc8cff' },
  { key: 'book', label: '技術書', icon: <BookOpen size={14} />, color: '#3fb950' },
];

function categoryConfig(category: RecordCategory) {
  switch (category) {
    case 'event':
      return { label: 'イベント', color: '#58a6ff', bg: 'rgba(88,166,255,0.1)', icon: <Users size={12} /> };
    case 'conference':
      return { label: 'カンファレンス', color: '#bc8cff', bg: 'rgba(188,140,255,0.1)', icon: <Presentation size={12} /> };
    case 'book':
      return { label: '技術書', color: '#3fb950', bg: 'rgba(63,185,80,0.1)', icon: <BookOpen size={12} /> };
  }
}

function groupByYear(records: LifeRecord[]): [string, LifeRecord[]][] {
  const map = new Map<string, LifeRecord[]>();
  for (const r of records) {
    const year = r.date.slice(0, 4);
    if (!map.has(year)) map.set(year, []);
    map.get(year)!.push(r);
  }
  return Array.from(map.entries()).sort((a, b) => b[0].localeCompare(a[0]));
}

function formatDisplayDate(dateStr: string): string {
  const [y, m, d] = dateStr.split('-');
  return `${y}/${m}/${d}`;
}

export function RecordsPage() {
  const [filter, setFilter] = useState<FilterCategory>('all');

  const { data: records = [], isLoading } = useQuery<LifeRecord[]>({
    queryKey: ['records'],
    queryFn: () => fetchRecords(),
  });

  const filtered: LifeRecord[] = filter === 'all'
    ? records
    : records.filter(r => r.category === filter);

  const grouped = groupByYear(filtered);

  const eventCount = records.filter(r => r.category === 'event').length;
  const conferenceCount = records.filter(r => r.category === 'conference').length;
  const bookCount = records.filter(r => r.category === 'book').length;

  return (
    <div className="p-6 max-w-3xl mx-auto">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-3xl font-bold" style={{ color: '#e6edf3' }}>Records</h1>
        <p className="mt-1 text-sm" style={{ color: '#8b949e' }}>
          参加イベント・カンファレンス・読んだ技術書
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-3 gap-3 mb-6">
        {[
          { label: 'イベント', value: eventCount, color: '#58a6ff', icon: <Users size={14} /> },
          { label: 'カンファレンス', value: conferenceCount, color: '#bc8cff', icon: <Presentation size={14} /> },
          { label: '技術書', value: bookCount, color: '#3fb950', icon: <BookOpen size={14} /> },
        ].map(({ label, value, color, icon }) => (
          <div
            key={label}
            className="rounded-lg p-3 flex items-center gap-3"
            style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
          >
            <div
              className="w-8 h-8 rounded-md flex items-center justify-center shrink-0"
              style={{ backgroundColor: `${color}18`, color }}
            >
              {icon}
            </div>
            <div>
              <div className="text-xl font-semibold leading-none" style={{ color: '#e6edf3' }}>{value}</div>
              <div className="text-xs mt-0.5" style={{ color: '#8b949e' }}>{label}</div>
            </div>
          </div>
        ))}
      </div>

      {/* Filter tabs */}
      <div
        className="flex items-center gap-1 mb-6 p-1 rounded-lg w-fit"
        style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
      >
        {CATEGORIES.map(({ key, label, icon, color }) => (
          <button
            key={key}
            onClick={() => setFilter(key)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-md text-sm transition-all"
            style={{
              backgroundColor: filter === key ? '#21262d' : 'transparent',
              color: filter === key ? color : '#8b949e',
              fontWeight: filter === key ? '500' : '400',
            }}
          >
            {icon && <span style={{ color }}>{icon}</span>}
            {label}
          </button>
        ))}
      </div>

      {/* Timeline */}
      {isLoading ? (
        <div className="flex items-center justify-center h-48">
          <div className="text-sm" style={{ color: '#8b949e' }}>Loading...</div>
        </div>
      ) : filtered.length === 0 ? (
        <div
          className="rounded-xl p-12 text-center"
          style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
        >
          <div className="text-4xl mb-3">📚</div>
          <p className="text-sm" style={{ color: '#8b949e' }}>記録はまだありません</p>
        </div>
      ) : (
        <div className="flex flex-col gap-8">
          {grouped.map(([year, items]) => (
            <div key={year}>
              {/* Year label */}
              <div className="flex items-center gap-3 mb-4">
                <span className="text-lg font-bold" style={{ color: '#e6edf3' }}>{year}</span>
                <div className="flex-1 h-px" style={{ backgroundColor: '#21262d' }} />
                <span className="text-xs" style={{ color: '#8b949e' }}>{items.length} 件</span>
              </div>

              {/* Items */}
              <div className="flex flex-col gap-2">
                {items.map((record) => {
                  const cfg = categoryConfig(record.category);
                  return (
                    <div
                      key={record.id}
                      className="rounded-xl p-4 flex items-start gap-4 transition-colors"
                      style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
                      onMouseEnter={(e) => {
                        (e.currentTarget as HTMLDivElement).style.borderColor = '#30363d';
                      }}
                      onMouseLeave={(e) => {
                        (e.currentTarget as HTMLDivElement).style.borderColor = '#21262d';
                      }}
                    >
                      {/* Category badge */}
                      <div
                        className="flex items-center gap-1 px-2 py-1 rounded-md text-xs font-medium shrink-0 mt-0.5"
                        style={{ backgroundColor: cfg.bg, color: cfg.color }}
                      >
                        {cfg.icon}
                        <span>{cfg.label}</span>
                      </div>

                      {/* Content */}
                      <div className="flex-1 min-w-0">
                        <div className="flex items-start justify-between gap-2">
                          <div className="flex-1 min-w-0">
                            {record.url ? (
                              <a
                                href={record.url}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="text-sm font-medium leading-snug hover:underline flex items-center gap-1 group"
                                style={{ color: '#e6edf3' }}
                              >
                                <span className="truncate">{record.title}</span>
                                <ExternalLink
                                  size={11}
                                  className="shrink-0 opacity-0 group-hover:opacity-100 transition-opacity"
                                  style={{ color: '#8b949e' }}
                                />
                              </a>
                            ) : (
                              <span className="text-sm font-medium leading-snug" style={{ color: '#e6edf3' }}>
                                {record.title}
                              </span>
                            )}
                            {record.note && (
                              <p className="text-xs mt-1 leading-relaxed" style={{ color: '#8b949e' }}>
                                {record.note}
                              </p>
                            )}
                          </div>
                          {/* Date */}
                          <div className="flex items-center gap-1 shrink-0 text-xs" style={{ color: '#8b949e' }}>
                            <Calendar size={11} />
                            <span>{formatDisplayDate(record.date)}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
