# アーキテクチャ全体図

## 構成

```
ブラウザ
  │
  │  https://suisan-neki.github.io/portfolio/
  ▼
GitHub Pages（フロントエンド）
  Kotlin + Compose Multiplatform (wasmJs)
  ※ ビルドすると HTML + JS + Wasm の静的ファイルになる
  │
  │  API リクエスト (HTTP/JSON)
  │  例: GET https://xxx.railway.app/api/projects
  ▼
Railway（バックエンド）
  Kotlin + Ktor (REST API サーバー)
  │
  │  SQL
  ▼
Neon（データベース）
  PostgreSQL
```

## なぜ2つに分かれているか

- **フロントエンド** = ブラウザで動くプログラム。静的ファイル（HTML/JS/Wasm）なので、
  サーバーは「ファイルを返すだけ」でいい → GitHub Pages で無料ホスティング
- **バックエンド** = データの保存・取得・認証などを担当するサーバー。
  常時起動して HTTP リクエストを処理する必要がある → Railway で常時起動

## リクエストの流れ（例: プロジェクト一覧を表示する）

```
1. ブラウザが GitHub Pages のページを開く
2. Compose Web (wasmJs) が起動
3. ProjectsPage が apiGetProjects() を呼ぶ
4. Ktor クライアントが GET /api/projects をバックエンドに送信
5. Ktor サーバーが受け取る
6. ProjectService.getAllProjects() でDBに問い合わせ
7. Neon (PostgreSQL) から SELECT して返す
8. JSON にして返す
9. フロントエンドが受け取って画面に描画
```
