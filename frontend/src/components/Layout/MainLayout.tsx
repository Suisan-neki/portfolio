import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';

export function MainLayout() {
  return (
    <div className="flex min-h-screen" style={{ backgroundColor: '#0d1117' }}>
      <Sidebar />
      <main className="flex-1 ml-16 min-h-screen overflow-auto">
        <Outlet />
      </main>
    </div>
  );
}
