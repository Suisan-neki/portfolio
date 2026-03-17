# バックエンド解説

## ディレクトリ構成

```
backend/src/main/kotlin/com/lifelog/
├── Application.kt        ← 起動ポイント
├── database/
│   ├── DatabaseFactory.kt  ← DB接続の初期化
│   └── Tables.kt           ← テーブル定義
├── models/
│   └── Models.kt           ← データの型定義
├── plugins/
│   ├── Auth.kt             ← 認証設定
│   ├── CORS.kt             ← CORS設定
│   ├── Routing.kt          ← URLとハンドラの紐付け
│   ├── Serialization.kt    ← JSON変換設定
│   └── StatusPages.kt      ← エラーレスポンス設定
└── services/
    ├── ProjectService.kt   ← プロジェクトのCRUD
    ├── DiaryService.kt     ← 日記のCRUD
    ├── GoalService.kt      ← 目標のCRUD
    ├── RecordService.kt    ← レコードのCRUD
    ├── RoutineService.kt   ← ルーティン更新
    ├── ActivityService.kt  ← 外部活動の取得
    └── GitHubService.kt    ← GitHub API連携
```

---

## Application.kt — 起動の流れ

```kotlin
fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init()    // 1. DBに接続してテーブルを作る
    configureSerialization()  // 2. JSON変換を有効化
    configureCORS()           // 3. フロントエンドからのリクエストを許可
    configureAuth()           // 4. 認証（Bearer トークン）を設定
    configureStatusPages()    // 5. エラー処理を設定
    configureRouting()        // 6. URLのルーティングを設定
}
```

`fun Application.module()` はKtorの書き方で、
「Applicationという既存の型にmoduleという関数を追加する（拡張関数）」という意味。

---

## DatabaseFactory.kt — DB接続

```kotlin
val rawUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/lifelog"
```

`System.getenv("DATABASE_URL")` で環境変数を読む。
`?: "..."` は「nullだったらこの値を使う」という意味（エルビス演算子）。

`DATABASE_URL` は `postgresql://...` と `jdbc:postgresql://...` の両方を受け付ける。
ユーザー名とパスワードが URL に埋め込まれていても、起動時に JDBC 用へ正規化して接続する。

`SchemaUtils.createMissingTablesAndColumns(...)` は
「テーブルが存在しなければ作る」を自動でやってくれる。マイグレーション不要。

---

## Tables.kt — テーブル定義

```kotlin
object Projects : IntIdTable("projects") {
    val title = varchar("title", 255)
    val description = text("description")
    val tags = text("tags")  // JSON文字列で保存
    val githubUrl = varchar("github_url", 500).nullable()
}
```

- `object` = シングルトン（インスタンスを1つだけ作るクラス）
- `IntIdTable` = 自動連番のIDカラム(id)を持つテーブル
- `.nullable()` = NULLを許可するカラム

---

## Models.kt — データの型定義

```kotlin
@Serializable
data class Project(
    val id: Int,
    val title: String,
    val description: String,
    val tags: List<String>,
    val githubUrl: String?,  // ? がついているとnull許容
    val demoUrl: String?,
    val createdAt: String
)
```

- `data class` = データを保持するためのクラス。equals/hashCode/toString が自動生成される
- `@Serializable` = JSONに変換できるようにするアノテーション
- `String?` = nullになりうるString（nullableな型）

---

## Routing.kt — URLの定義

```kotlin
route("/api") {
    get("/projects") {
        call.respond(ProjectService.getAllProjects())
    }

    authenticate("admin-auth") {
        post("/projects") {
            val req = call.receive<CreateProjectRequest>()
            val project = ProjectService.createProject(req)
            call.respond(HttpStatusCode.Created, project)
        }
    }
}
```

- `get("/projects")` → `GET /api/projects` にアクセスが来たら実行
- `call.respond(...)` → レスポンスを返す（自動でJSONに変換）
- `call.receive<T>()` → リクエストボディをTの型として受け取る
- `authenticate("admin-auth")` → このブロック内はログイン必須

---

## ProjectService.kt — DBとのやり取り

```kotlin
fun getAllProjects(): List<Project> = transaction {
    Projects.selectAll()
        .orderBy(Projects.createdAt, SortOrder.DESC)
        .map { row -> row.toProject() }
}
```

- `transaction { ... }` = このブロック内でDBのSQLを実行する
- `Projects.selectAll()` = `SELECT * FROM projects` と同じ
- `.map { row -> ... }` = 各行を `Project` データクラスに変換
- `= transaction { ... }` は `{ return transaction { ... } }` の省略形

```kotlin
fun createProject(req: CreateProjectRequest): Project = transaction {
    val id = Projects.insertAndGetId {
        it[title] = req.title  // it はInsertStatementを指す
        it[description] = req.description
    }.value
}
```

`insertAndGetId` = INSERT してIDを返す。`it[カラム] = 値` で各カラムに値をセット。

---

## 認証の仕組み

1. `POST /api/auth/login` にパスワードを送る
2. サーバーが環境変数 `ADMIN_PASSWORD` と照合
3. 一致したら `ADMIN_TOKEN`（環境変数）を返す
4. 以降はリクエストヘッダーに `Authorization: Bearer <token>` をつける
5. 認証が必要なエンドポイントはトークンを検証してから処理

---

## 環境変数一覧

| 変数名 | 説明 | 設定場所 |
|--------|------|---------|
| `DATABASE_URL` | Neon の接続文字列 | Railway Variables |
| `ADMIN_PASSWORD` | 管理画面のパスワード | Railway Variables |
| `ADMIN_TOKEN` | API認証トークン | Railway Variables |
| `GITHUB_TOKEN` | GitHub API用PAT | Railway Variables |
| `GITHUB_USERNAME` | GitHubユーザー名 | Railway Variables |
