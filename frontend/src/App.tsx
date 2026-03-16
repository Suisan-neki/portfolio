import { Routes, Route } from 'react-router-dom'
import { MainLayout } from './components/Layout/MainLayout'
import { CalendarPage } from './pages/CalendarPage'
import { DiaryPage } from './pages/DiaryPage'
import { ProjectsPage } from './pages/ProjectsPage'
import { GoalsPage } from './pages/GoalsPage'
import { ActivityPage } from './pages/ActivityPage'
import { AdminPage } from './pages/AdminPage'

function App() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<CalendarPage />} />
        <Route path="/diary/:date" element={<DiaryPage />} />
        <Route path="/projects" element={<ProjectsPage />} />
        <Route path="/goals" element={<GoalsPage />} />
        <Route path="/activity" element={<ActivityPage />} />
        <Route path="/admin" element={<AdminPage />} />
        <Route path="/admin/diary/:date" element={<AdminPage />} />
      </Route>
    </Routes>
  )
}

export default App
