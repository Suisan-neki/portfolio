import { ChevronLeft, ChevronRight } from 'lucide-react';

interface CalendarHeaderProps {
  year: number;
  month: number;
  onPrev: () => void;
  onNext: () => void;
}

const MONTH_NAMES = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December',
];

export function CalendarHeader({ year, month, onPrev, onNext }: CalendarHeaderProps) {
  return (
    <div className="flex items-center justify-between mb-6">
      <div>
        <h2 className="text-2xl font-semibold" style={{ color: '#e6edf3' }}>
          {MONTH_NAMES[month - 1]}
        </h2>
        <p className="text-sm mt-0.5" style={{ color: '#8b949e' }}>{year}</p>
      </div>
      <div className="flex items-center gap-2">
        <button
          onClick={onPrev}
          className="w-8 h-8 rounded-md flex items-center justify-center transition-colors"
          style={{ backgroundColor: '#21262d', color: '#8b949e' }}
          onMouseEnter={(e) => {
            (e.currentTarget as HTMLButtonElement).style.backgroundColor = '#30363d';
            (e.currentTarget as HTMLButtonElement).style.color = '#e6edf3';
          }}
          onMouseLeave={(e) => {
            (e.currentTarget as HTMLButtonElement).style.backgroundColor = '#21262d';
            (e.currentTarget as HTMLButtonElement).style.color = '#8b949e';
          }}
        >
          <ChevronLeft size={16} />
        </button>
        <button
          onClick={onNext}
          className="w-8 h-8 rounded-md flex items-center justify-center transition-colors"
          style={{ backgroundColor: '#21262d', color: '#8b949e' }}
          onMouseEnter={(e) => {
            (e.currentTarget as HTMLButtonElement).style.backgroundColor = '#30363d';
            (e.currentTarget as HTMLButtonElement).style.color = '#e6edf3';
          }}
          onMouseLeave={(e) => {
            (e.currentTarget as HTMLButtonElement).style.backgroundColor = '#21262d';
            (e.currentTarget as HTMLButtonElement).style.color = '#8b949e';
          }}
        >
          <ChevronRight size={16} />
        </button>
      </div>
    </div>
  );
}
