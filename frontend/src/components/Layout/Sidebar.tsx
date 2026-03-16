import { NavLink } from 'react-router-dom';
import {
  CalendarDays,
  Code2,
  Target,
  Activity,
  Settings,
  User,
  BookMarked,
} from 'lucide-react';

const navItems = [
  { to: '/', icon: CalendarDays, label: 'Calendar', exact: true },
  { to: '/projects', icon: Code2, label: 'Projects' },
  { to: '/goals', icon: Target, label: 'Goals' },
  { to: '/records', icon: BookMarked, label: 'Records' },
  { to: '/activity', icon: Activity, label: 'Activity' },
  { to: '/admin', icon: Settings, label: 'Admin' },
];

export function Sidebar() {
  return (
    <aside className="fixed left-0 top-0 h-full w-16 flex flex-col items-center py-4 gap-1 z-50"
      style={{ backgroundColor: '#161b22', borderRight: '1px solid #30363d' }}>
      {/* Logo */}
      <div className="mb-6 mt-2">
        <div className="w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold"
          style={{ backgroundColor: '#58a6ff', color: '#0d1117' }}>
          LL
        </div>
      </div>

      {/* Nav items */}
      <nav className="flex flex-col items-center gap-1 flex-1">
        {navItems.map(({ to, icon: Icon, label, exact }) => (
          <NavLink
            key={to}
            to={to}
            end={exact}
            title={label}
            className={({ isActive }) =>
              `w-10 h-10 rounded-lg flex items-center justify-center transition-colors group relative ${
                isActive
                  ? 'text-white'
                  : 'text-gray-500 hover:text-gray-300'
              }`
            }
            style={({ isActive }) => ({
              backgroundColor: isActive ? '#21262d' : 'transparent',
            })}
          >
            <Icon size={18} />
            {/* Tooltip */}
            <span className="absolute left-full ml-2 px-2 py-1 text-xs rounded whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none z-50"
              style={{ backgroundColor: '#21262d', color: '#e6edf3', border: '1px solid #30363d' }}>
              {label}
            </span>
          </NavLink>
        ))}
      </nav>

      {/* Profile icon at bottom */}
      <div className="mt-auto mb-2">
        <div className="w-8 h-8 rounded-full flex items-center justify-center"
          style={{ backgroundColor: '#21262d' }}>
          <User size={14} style={{ color: '#8b949e' }} />
        </div>
      </div>
    </aside>
  );
}
