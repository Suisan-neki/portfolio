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
} from '../api';

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

  // Projects
  const { data: projects = [] } = useQuery({
    queryKey: ['projects'],
    queryFn: fetchProjects,
    enabled: isLoggedIn,
  });
  const [newProject, setNewProject] = useState({ title: '', description: '', tags: '', githubUrl: '', demoUrl: '' });
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

  // Goals
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

  if (!isLoggedIn) {
    return (
      <div className="p-6 max-w-sm mx-auto mt-20">
        <div className="flex items-center gap-3 mb-8">
          <Settings size={24} style={{ color: '#58a6ff' }} />
          <h1 className="text-2xl font-bold" style={{ color: '#e6edf3' }}>Admin</h1>
        </div>
        <div
          className="rounded-xl p-6"
          style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
        >
          <h2 className="text-sm font-medium mb-4" style={{ color: '#8b949e' }}>ログイン</h2>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && loginMutation.mutate(password)}
            placeholder="パスワード"
            className="w-full px-3 py-2 rounded-md text-sm mb-3 outline-none"
            style={{
              backgroundColor: '#0d1117',
              border: '1px solid #30363d',
              color: '#e6edf3',
            }}
          />
          {loginError && (
            <p className="text-xs mb-3" style={{ color: '#f78166' }}>{loginError}</p>
          )}
          <button
            onClick={() => loginMutation.mutate(password)}
            disabled={loginMutation.isPending}
            className="w-full flex items-center justify-center gap-2 px-4 py-2 rounded-md text-sm font-medium transition-opacity"
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
        <div
          className="rounded-xl p-4 flex items-center justify-between"
          style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
        >
          <p className="text-sm" style={{ color: '#8b949e' }}>
            GitHub APIからコミット数を取得してカレンダーに反映します
          </p>
          <button
            onClick={() => syncMutation.mutate()}
            disabled={syncMutation.isPending}
            className="flex items-center gap-2 px-4 py-2 rounded-md text-sm font-medium"
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

      {/* Projects */}
      <section className="mb-8">
        <h2 className="text-base font-semibold mb-3" style={{ color: '#e6edf3' }}>Projects</h2>
        <div
          className="rounded-xl p-4 mb-3"
          style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
        >
          <div className="grid grid-cols-2 gap-2 mb-2">
            <input
              placeholder="タイトル"
              value={newProject.title}
              onChange={(e) => setNewProject(p => ({ ...p, title: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none col-span-2"
              style={{ backgroundColor: '#0d1117', border: '1px solid #30363d', color: '#e6edf3' }}
            />
            <input
              placeholder="説明"
              value={newProject.description}
              onChange={(e) => setNewProject(p => ({ ...p, description: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none col-span-2"
              style={{ backgroundColor: '#0d1117', border: '1px solid #30363d', color: '#e6edf3' }}
            />
            <input
              placeholder="タグ（カンマ区切り）"
              value={newProject.tags}
              onChange={(e) => setNewProject(p => ({ ...p, tags: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none"
              style={{ backgroundColor: '#0d1117', border: '1px solid #30363d', color: '#e6edf3' }}
            />
            <input
              placeholder="GitHub URL"
              value={newProject.githubUrl}
              onChange={(e) => setNewProject(p => ({ ...p, githubUrl: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none"
              style={{ backgroundColor: '#0d1117', border: '1px solid #30363d', color: '#e6edf3' }}
            />
            <input
              placeholder="Demo URL"
              value={newProject.demoUrl}
              onChange={(e) => setNewProject(p => ({ ...p, demoUrl: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none"
              style={{ backgroundColor: '#0d1117', border: '1px solid #30363d', color: '#e6edf3' }}
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
              style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
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
        <div
          className="rounded-xl p-4 mb-3"
          style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
        >
          <div className="grid grid-cols-2 gap-2 mb-2">
            <input
              placeholder="タイトル"
              value={newGoal.title}
              onChange={(e) => setNewGoal(g => ({ ...g, title: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none"
              style={{ backgroundColor: '#0d1117', border: '1px solid #30363d', color: '#e6edf3' }}
            />
            <input
              placeholder="目標値"
              type="number"
              value={newGoal.target}
              onChange={(e) => setNewGoal(g => ({ ...g, target: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none"
              style={{ backgroundColor: '#0d1117', border: '1px solid #30363d', color: '#e6edf3' }}
            />
            <input
              placeholder="説明"
              value={newGoal.description}
              onChange={(e) => setNewGoal(g => ({ ...g, description: e.target.value }))}
              className="px-3 py-2 rounded-md text-sm outline-none col-span-2"
              style={{ backgroundColor: '#0d1117', border: '1px solid #30363d', color: '#e6edf3' }}
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
              style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
            >
              <span className="text-sm font-medium" style={{ color: '#e6edf3' }}>{g.title}</span>
              <div className="flex items-center gap-2">
                <input
                  type="number"
                  defaultValue={g.progress}
                  onBlur={(e) => updateProgressMutation.mutate({ id: g.id, progress: parseInt(e.target.value) || 0 })}
                  className="w-16 px-2 py-1 rounded text-sm text-center outline-none"
                  style={{ backgroundColor: '#0d1117', border: '1px solid #30363d', color: '#e6edf3' }}
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
