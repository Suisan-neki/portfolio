import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Settings, LogIn, RefreshCw, Plus, Trash2 } from 'lucide-react';
import {
  adminLogin,
  syncGitHub,
  fetchProjects,
  createProject,
  deleteProject,
  fetchGoals,
  createGoal,
  deleteGoal,
  updateGoalProgress,
  fetchRecords,
  createRecord,
  deleteRecord,
} from '../api';
import type { RecordCategory } from '../types';

// ── Shared input style ─────────────────────────────────────────
const inputStyle = {
  backgroundColor: '#0d1117',
  border: '1px solid #30363d',
  color: '#e6edf3',
} as const;

const sectionCardStyle = {
  backgroundColor: '#161b22',
  border: '1px solid #21262d',
} as const;

export function AdminPage() {
  const [password, setPassword] = useState('');
  const [isLoggedIn, setIsLoggedIn] = useState(() => !!localStorage.getItem('admin_token'));
  const [loginError, setLoginError] = useState('');
  const queryClient = useQueryClient();

  // Login
  const loginMutation = useMutation({
    mutationFn: adminLogin,
    onSuccess: ({ token }) => {
      localStorage.setItem('admin_token', token);
      setIsLoggedIn(true);
      setLoginError('');
    },
    onError: () => setLoginError('パスワードが違います'),
  });

  // GitHub sync
  const syncMutation = useMutation({
    mutationFn: syncGitHub,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['calendar'] });
    },
  });

  // ── Projects ──────────────────────────────────────────────────
  const { data: projects = [] } = useQuery({
    queryKey: ['projects'],
    queryFn: fetchProjects,
    enabled: isLoggedIn,
  });
  const [newProject, setNewProject] = useState({
    title: '', description: '', tags: '', githubUrl: '', demoUrl: '',
  });
  const createProjectMutation = useMutation({
    mutationFn: () => createProject({
      title: newProject.title,
      description: newProject.description,
      tags: newProject.tags.split(',').map(t => t.trim()).filter(Boolean),
      githubUrl: newProject.githubUrl || undefined,
      demoUrl: newProject.demoUrl || undefined,
    }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['projects'] });
      setNewProject({ title: '', description: '', tags: '', githubUrl: '', demoUrl: '' });
    },
  });
  const deleteProjectMutation = useMutation({
    mutationFn: deleteProject,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['projects'] }),
  });

  // ── Goals ─────────────────────────────────────────────────────
  const { data: goals = [] } = useQuery({
    queryKey: ['goals'],
    queryFn: fetchGoals,
    enabled: isLoggedIn,
  });
  const [newGoal, setNewGoal] = useState({ title: '', description: '', target: '10' });
  const createGoalMutation = useMutation({
    mutationFn: () => createGoal({
      title: newGoal.title,
      description: newGoal.description,
      target: parseInt(newGoal.target) || 10,
    }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['goals'] });
      setNewGoal({ title: '', description: '', target: '10' });
    },
  });
  const deleteGoalMutation = useMutation({
    mutationFn: deleteGoal,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['goals'] }),
  });
  const updateProgressMutation = useMutation({
    mutationFn: ({ id, progress }: { id: number; progress: number }) =>
      updateGoalProgress(id, { progress }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['goals'] }),
  });

  // ── Records ───────────────────────────────────────────────────
  const { data: records = [] } = useQuery({
    queryKey: ['records'],
    queryFn: () => fetchRecords(),
    enabled: isLoggedIn,
  });
  const [newRecord, setNewRecord] = useState({
    category: 'event' as RecordCategory,
    title: '',
    url: '',
    date: new Date().toISOString().slice(0, 10),
    note: '',
  });
  const createRecordMutation = useMutation({
    mutationFn: () => createRecord({
      category: newRecord.category,
      title: newRecord.title,
      url: newRecord.url || undefined,
      date: newRecord.date,
      note: newRecord.note,
    }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['records'] });
      setNewRecord({
        category: 'event',
        title: '',
        url: '',
        date: new Date().toISOString().slice(0, 10),
        note: '',
      });
    },
  });
  const deleteRecordMutation = useMutation({
    mutationFn: deleteRecord,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['records'] }),
  });

  // ── Login screen ──────────────────────────────────────────────
  if (!isLoggedIn) {
    return (
      <div className="p-6 max-w-sm mx-auto mt-20">
        <div className="flex items-center gap-3 mb-8">
          <Settings size={24} style={{ color: '#58a6ff' }} />
          <h1 className="text-2xl font-bold" style={{ color: '#e6edf3' }}>Admin</h1>
        </div>
        <div className="rounded-xl p-6" style={sectionCardStyle}>
          <h2 className="text-sm font-medium mb-4" style={{ color: '#8b949e' }}>ログイン</h2>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && loginMutation.mutate(password)}
            placeholder="パスワード"
            className="w-full px-3 py-2 rounded-md text-sm mb-3 outline-none"
            style={inputStyle}
          />
          {loginError && (
            <p className="text-xs mb-3" style={{ color: '#f78166' }}>{loginError}</p>
          )}
          <button
            onClick={() => loginMutation.mutate(password)}
            disabled={loginMutation.isPending}
            className="w-full flex items-center justify-center gap-2 px-4 py-2 rounded-md text-sm font-medium"
            style={{ backgroundColor: '#238636', color: '#fff' }}
          >
            <LogIn size={14} />
            {loginMutation.isPending ? 'ログイン中...' : 'ログイン'}
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-3xl mx-auto">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold" style={{ color: '#e6edf3' }}>Admin</h1>
          <p className="mt-1 text-sm" style={{ color: '#8b949e' }}>管理画面</p>
        </div>
        <button
          onClick={() => { localStorage.removeItem('admin_token'); setIsLoggedIn(false); }}
          className="text-xs px-3 py-1.5 rounded-md"
          style={{ backgroundColor: '#21262d', color: '#8b949e', border: '1px solid #30363d' }}
        >
          ログアウト
        </button>
      </div>

      {/* GitHub Sync */}
      <section className="mb-8">
        <h2 className="text-base font-semibold mb-3" style={{ color: '#e6edf3' }}>GitHub 同期</h2>
        <div className="rounded-xl p-4 flex items-center justify-between" style={sectionCardStyle}>
          <p className="text-sm" style={{ color: '#8b949e' }}>
            GitHub APIからコミット数を取得してカレンダーに反映します
          </p>
          <button
            onClick={() => syncMutation.mutate()}
            disabled={syncMutation.isPending}
            className="flex items-center gap-2 px-4 py-2 rounded-md text-sm font-medium shrink-0 ml-4"
            style={{ backgroundColor: '#21262d', color: '#58a6ff', border: '1px solid #30363d' }}
          >
            <RefreshCw size={14} className={syncMutation.isPending ? 'animate-spin' : ''} />
            {syncMutation.isPending ? '同期中...' : '同期する'}
          </button>
        </div>
        {syncMutation.isSuccess && (
          <p className="text-xs mt-2" style={{ color: '#3fb950' }}>
            ✓ {syncMutation.data.synced} 件のコミットを同期しました
          </p>
        )}
      </section>

      {/* Records */}
      <section className="mb-8">
        <h2 className="text-base font-semibold mb-3" style={{ color: '#e6edf3' }}>Records</h2>
        <div className="rounded-xl p-4 mb-3" style={sectionCardStyle}>
          <div className="grid grid-cols-2 gap-2 mb-2">
            <select
              value={newRecord.category}
              onChange={(e) => setNewRecord(r => ({ ...r, category: e.target.value as RecordCategory }))}
              className="px-3 py-2 rounded-md text-sm outline-none"
              style={inputStyle}
            >
              <option value="event">イベント</option>
              <option value="conference">カンファレンス</option>
              <option value="book">技術書</option>
            </select>
            <input
              type="date"
              value={newRecord.date}
              onChange={(e) => setNewRecord(r => ({ ...r, date: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none"
              style={inputStyle}
            />
            <input
              placeholder="タイトル"
              value={newRecord.title}
              onChange={(e) => setNewRecord(r => ({ ...r, title: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none col-span-2"
              style={inputStyle}
            />
            <input
              placeholder="URL（任意）"
              value={newRecord.url}
              onChange={(e) => setNewRecord(r => ({ ...r, url: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none col-span-2"
              style={inputStyle}
            />
            <input
              placeholder="一言メモ（任意）"
              value={newRecord.note}
              onChange={(e) => setNewRecord(r => ({ ...r, note: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none col-span-2"
              style={inputStyle}
            />
          </div>
          <button
            onClick={() => createRecordMutation.mutate()}
            disabled={!newRecord.title || !newRecord.date || createRecordMutation.isPending}
            className="flex items-center gap-2 px-3 py-1.5 rounded-md text-sm"
            style={{ backgroundColor: '#238636', color: '#fff' }}
          >
            <Plus size={14} />
            追加
          </button>
        </div>
        <div className="flex flex-col gap-2">
          {records.map((r) => {
            const catLabel = r.category === 'event' ? 'イベント'
              : r.category === 'conference' ? 'カンファレンス' : '技術書';
            const catColor = r.category === 'event' ? '#58a6ff'
              : r.category === 'conference' ? '#bc8cff' : '#3fb950';
            return (
              <div
                key={r.id}
                className="flex items-center justify-between px-4 py-3 rounded-lg gap-4"
                style={sectionCardStyle}
              >
                <div className="flex items-center gap-3 min-w-0">
                  <span
                    className="text-xs px-2 py-0.5 rounded shrink-0"
                    style={{ backgroundColor: `${catColor}18`, color: catColor }}
                  >
                    {catLabel}
                  </span>
                  <div className="min-w-0">
                    <span className="text-sm font-medium truncate block" style={{ color: '#e6edf3' }}>
                      {r.title}
                    </span>
                    <span className="text-xs" style={{ color: '#8b949e' }}>{r.date}</span>
                  </div>
                </div>
                <button
                  onClick={() => deleteRecordMutation.mutate(r.id)}
                  className="shrink-0"
                >
                  <Trash2 size={14} style={{ color: '#8b949e' }} />
                </button>
              </div>
            );
          })}
        </div>
      </section>

      {/* Projects */}
      <section className="mb-8">
        <h2 className="text-base font-semibold mb-3" style={{ color: '#e6edf3' }}>Projects</h2>
        <div className="rounded-xl p-4 mb-3" style={sectionCardStyle}>
          <div className="grid grid-cols-2 gap-2 mb-2">
            <input
              placeholder="タイトル"
              value={newProject.title}
              onChange={(e) => setNewProject(p => ({ ...p, title: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none col-span-2"
              style={inputStyle}
            />
            <input
              placeholder="説明"
              value={newProject.description}
              onChange={(e) => setNewProject(p => ({ ...p, description: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none col-span-2"
              style={inputStyle}
            />
            <input
              placeholder="タグ（カンマ区切り）"
              value={newProject.tags}
              onChange={(e) => setNewProject(p => ({ ...p, tags: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none"
              style={inputStyle}
            />
            <input
              placeholder="GitHub URL"
              value={newProject.githubUrl}
              onChange={(e) => setNewProject(p => ({ ...p, githubUrl: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none"
              style={inputStyle}
            />
            <input
              placeholder="Demo URL"
              value={newProject.demoUrl}
              onChange={(e) => setNewProject(p => ({ ...p, demoUrl: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none col-span-2"
              style={inputStyle}
            />
          </div>
          <button
            onClick={() => createProjectMutation.mutate()}
            disabled={!newProject.title || createProjectMutation.isPending}
            className="flex items-center gap-2 px-3 py-1.5 rounded-md text-sm"
            style={{ backgroundColor: '#238636', color: '#fff' }}
          >
            <Plus size={14} />
            追加
          </button>
        </div>
        <div className="flex flex-col gap-2">
          {projects.map((p) => (
            <div
              key={p.id}
              className="flex items-center justify-between px-4 py-3 rounded-lg"
              style={sectionCardStyle}
            >
              <div>
                <span className="text-sm font-medium" style={{ color: '#e6edf3' }}>{p.title}</span>
                <div className="flex gap-1 mt-1">
                  {p.tags.map(t => (
                    <span key={t} className="text-xs px-1.5 py-0.5 rounded"
                      style={{ backgroundColor: '#21262d', color: '#8b949e' }}>{t}</span>
                  ))}
                </div>
              </div>
              <button onClick={() => deleteProjectMutation.mutate(p.id)}>
                <Trash2 size={14} style={{ color: '#8b949e' }} />
              </button>
            </div>
          ))}
        </div>
      </section>

      {/* Goals */}
      <section>
        <h2 className="text-base font-semibold mb-3" style={{ color: '#e6edf3' }}>Goals</h2>
        <div className="rounded-xl p-4 mb-3" style={sectionCardStyle}>
          <div className="grid grid-cols-2 gap-2 mb-2">
            <input
              placeholder="タイトル"
              value={newGoal.title}
              onChange={(e) => setNewGoal(g => ({ ...g, title: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none"
              style={inputStyle}
            />
            <input
              placeholder="目標値"
              type="number"
              value={newGoal.target}
              onChange={(e) => setNewGoal(g => ({ ...g, target: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none"
              style={inputStyle}
            />
            <input
              placeholder="説明"
              value={newGoal.description}
              onChange={(e) => setNewGoal(g => ({ ...g, description: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none col-span-2"
              style={inputStyle}
            />
          </div>
          <button
            onClick={() => createGoalMutation.mutate()}
            disabled={!newGoal.title || createGoalMutation.isPending}
            className="flex items-center gap-2 px-3 py-1.5 rounded-md text-sm"
            style={{ backgroundColor: '#238636', color: '#fff' }}
          >
            <Plus size={14} />
            追加
          </button>
        </div>
        <div className="flex flex-col gap-2">
          {goals.map((g) => (
            <div
              key={g.id}
              className="flex items-center justify-between px-4 py-3 rounded-lg gap-4"
              style={sectionCardStyle}
            >
              <span className="text-sm font-medium" style={{ color: '#e6edf3' }}>{g.title}</span>
              <div className="flex items-center gap-2">
                <input
                  type="number"
                  defaultValue={g.progress}
                  onBlur={(e) => updateProgressMutation.mutate({
                    id: g.id,
                    progress: parseInt(e.target.value) || 0,
                  })}
                  className="w-16 px-2 py-1 rounded text-sm text-center outline-none"
                  style={inputStyle}
                />
                <span className="text-xs" style={{ color: '#8b949e' }}>/ {g.target}</span>
                <button onClick={() => deleteGoalMutation.mutate(g.id)}>
                  <Trash2 size={14} style={{ color: '#8b949e' }} />
                </button>
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
