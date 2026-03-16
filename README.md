# LifeLog Portfolio

> 技術活動 × 生活ログ × 継続性を一つのUIで可視化する、就活向け個人ポートフォリオWebアプリ

## コンセプト

一般的なポートフォリオは「作ったもの」と「技術スタック」しか見えない。  
このアプリは **技術ログ + 人生ログ** を組み合わせ、エンジニアとしての継続力・生活習慣・人となりを可視化する。

```
Portfolio = 技術ログ + 人生ログ
```

## 機能

| 機能 | 説明 |
|------|------|
| 📅 カレンダー | 月単位で活動スタンプ（🏊📚💻📝）を表示。日付クリックで日記へ |
| 📝 日記 | 日次の生活ログ（Markdown対応） |
| ✅ ルーティンログ | 水泳・資格勉強などの日次チェック |
| 💻 GitHub Activity | コミット数をカレンダーに自動反映 |
| 🚀 Projects | ポートフォリオ（技術スタック・GitHub・デモURL） |
| 🎯 Goals | 目標管理と進捗バー |
| 📚 Records | 参加イベント・カンファレンス・読んだ技術書の時系列記録 |
| 🌐 Activity | GitHub・Qiita・RSSの外部活動まとめ |

## 技術スタック

### Backend
- **Kotlin** + **Ktor** (REST API)
- **SQLite** + **Exposed** (ORM)
- GitHub GraphQL API 連携

### Frontend
- **React** + **TypeScript** + **Vite**
- **TailwindCSS** (スタイリング)
- **React Query** (データフェッチ)
- **React Router** (ルーティング)

### Infrastructure
- **Docker** + **Docker Compose**
- **nginx** (フロントエンドサーバー・リバースプロキシ)

## ディレクトリ構成

```
portfolio/
├── backend/          # Ktor バックエンド
│   └── src/main/kotlin/com/lifelog/
│       ├── plugins/  # Ktor プラグイン設定
│       ├── models/   # データモデル
│       ├── database/ # DB設定・テーブル定義
│       └── services/ # ビジネスロジック
├── frontend/         # React フロントエンド
│   └── src/
│       ├── components/  # UIコンポーネント
│       ├── pages/       # ページコンポーネント
│       ├── api/         # APIクライアント
│       └── types/       # 型定義
└── docker-compose.yml
```

## セットアップ

### Docker（推奨）

```bash
cp .env.example .env
# .env を編集してパスワードを設定

docker-compose up -d
```

ブラウザで `http://localhost` にアクセス。

### ローカル開発

**Backend:**
```bash
cd backend
ADMIN_PASSWORD=lifelog2024 ADMIN_TOKEN=lifelog-admin-token ./gradlew run
```

**Frontend:**
```bash
cd frontend
pnpm install
pnpm dev
```

## ページ構成

| パス | 説明 |
|------|------|
| `/` | カレンダー（メイン） |
| `/diary/:date` | 日記詳細 |
| `/projects` | プロジェクト一覧 |
| `/goals` | 目標管理 |
| `/records` | Records（イベント・カンファレンス・技術書） |
| `/activity` | 外部活動 |
| `/admin` | 管理画面 |

## カレンダースタンプ

| スタンプ | 意味 |
|---------|------|
| 🏊 | 水泳 |
| 📚 | 資格勉強 |
| 💻 | GitHub commit |
| 📝 | 日記あり |

## バックアップ

```bash
# SQLiteファイルをコピーするだけでOK
docker cp lifelog-backend:/data/database.db ./backup/database-$(date +%Y%m%d).db
```
