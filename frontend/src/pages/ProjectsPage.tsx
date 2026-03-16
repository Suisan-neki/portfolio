import { useQuery } from '@tanstack/react-query';
import { Github, ExternalLink, Code2 } from 'lucide-react';
import { fetchProjects } from '../api';

export function ProjectsPage() {
  const { data: projects = [], isLoading } = useQuery({
    queryKey: ['projects'],
    queryFn: fetchProjects,
  });

  return (
    <div className="p-6 max-w-5xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold" style={{ color: '#e6edf3' }}>Projects</h1>
        <p className="mt-1 text-sm" style={{ color: '#8b949e' }}>
          制作物・OSS活動
        </p>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center h-48">
          <div className="text-sm" style={{ color: '#8b949e' }}>Loading...</div>
        </div>
      ) : projects.length === 0 ? (
        <div
          className="rounded-xl p-12 text-center"
          style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
        >
          <Code2 size={32} className="mx-auto mb-3" style={{ color: '#8b949e' }} />
          <p className="text-sm" style={{ color: '#8b949e' }}>プロジェクトはまだありません</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {projects.map((project) => (
            <div
              key={project.id}
              className="rounded-xl p-5 flex flex-col gap-3 transition-colors"
              style={{ backgroundColor: '#161b22', border: '1px solid #21262d' }}
              onMouseEnter={(e) => {
                (e.currentTarget as HTMLDivElement).style.borderColor = '#30363d';
              }}
              onMouseLeave={(e) => {
                (e.currentTarget as HTMLDivElement).style.borderColor = '#21262d';
              }}
            >
              {/* Header */}
              <div className="flex items-start justify-between gap-2">
                <h3 className="text-base font-semibold leading-tight" style={{ color: '#e6edf3' }}>
                  {project.title}
                </h3>
                <div className="flex items-center gap-2 shrink-0">
                  {project.githubUrl && (
                    <a
                      href={project.githubUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="transition-colors"
                      style={{ color: '#8b949e' }}
                      onMouseEnter={(e) => (e.currentTarget.style.color = '#e6edf3')}
                      onMouseLeave={(e) => (e.currentTarget.style.color = '#8b949e')}
                    >
                      <Github size={16} />
                    </a>
                  )}
                  {project.demoUrl && (
                    <a
                      href={project.demoUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="transition-colors"
                      style={{ color: '#8b949e' }}
                      onMouseEnter={(e) => (e.currentTarget.style.color = '#58a6ff')}
                      onMouseLeave={(e) => (e.currentTarget.style.color = '#8b949e')}
                    >
                      <ExternalLink size={16} />
                    </a>
                  )}
                </div>
              </div>

              {/* Description */}
              <p className="text-sm leading-relaxed flex-1" style={{ color: '#8b949e' }}>
                {project.description}
              </p>

              {/* Tags */}
              {project.tags.length > 0 && (
                <div className="flex flex-wrap gap-1.5">
                  {project.tags.map((tag) => (
                    <span
                      key={tag}
                      className="px-2 py-0.5 rounded text-xs"
                      style={{ backgroundColor: '#21262d', color: '#8b949e', border: '1px solid #30363d' }}
                    >
                      {tag}
                    </span>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
