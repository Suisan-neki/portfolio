# デプロイ・運用

## 全体の自動化

main ブランチに push すると GitHub Actions が自動で動く：

```
git push origin main
    │
    ▼
GitHub Actions (.github/workflows/deploy.yml)
    ├── フロントエンドビルド (./gradlew wasmJsBrowserDistribution)
    │       └── GitHub Pages にデプロイ
    └── バックエンドデプロイ (railway up)
            └── Railway が再起動
```

---

## 各サービスの役割

### GitHub Pages（フロントエンド）
- URL: `https://suisan-neki.github.io/portfolio/`
- 静的ファイルをホスティングするだけ。サーバーは動いていない
- 無料・無制限

### Railway（バックエンド）
- Kotlin + Ktor サーバーが常時起動している
- `railway.json` に書いたコマンドでビルド・起動する
- 月$5のクレジットが無料でつく（小規模なら実質無料）

### Neon（データベース）
- PostgreSQL のクラウドサービス
- データはここに永続保存される
- デプロイしてもデータは消えない
- 無料枠: 500MB、月10億リクエスト

---

## 環境変数の設定場所

コードに直接書かない。Railway の Variables に設定する。

```
Railway > サービス > Variables タブ

DATABASE_URL  = postgresql://... (Neon の接続文字列)
ADMIN_PASSWORD = Marin52291316!
ADMIN_TOKEN   = 任意の長いランダム文字列
GITHUB_TOKEN  = ghp_xxxx (GitHub PAT)
GITHUB_USERNAME = Suisan-neki
```

GitHub Actions 用は別で設定：
```
GitHub > Settings > Secrets and variables > Actions

Variables:
  BACKEND_URL = https://xxx.up.railway.app  (Railway の URL)

Secrets:
  RAILWAY_TOKEN = xxx  (Railway の Project Token)
```

---

## ローカルで動かす方法

### バックエンドだけ動かす

```bash
cd backend

# 環境変数を設定して起動
DATABASE_URL="jdbc:postgresql://localhost:5432/lifelog" \
ADMIN_PASSWORD="test" \
ADMIN_TOKEN="test-token" \
./gradlew run
```

`http://localhost:8080/api/projects` にアクセスして確認。

### フロントエンドをブラウザで確認

```bash
cd frontend

# バックエンドを先に起動した状態で
./gradlew wasmJsBrowserDevelopmentRun
```

自動でブラウザが開く。API_BASE_URL が空なので `/api/...` への相対パスでアクセスされ、
バックエンドの8080番に繋がる。

---

## よくある問題

### Railway でヘルスチェックが失敗する
- Variables に `DATABASE_URL` が設定されているか確認
- Deploy Logs でエラーを確認

### GitHub Pages が表示されない
- Settings > Pages > Source が **GitHub Actions** になっているか確認
- Actions タブでワークフローが成功しているか確認

### 管理画面にログインできない
- Railway Variables の `ADMIN_PASSWORD` と入力したパスワードが一致しているか確認
