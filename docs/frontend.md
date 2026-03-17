# フロントエンド解説

## Compose Multiplatform (wasmJs) とは

Kotlin で書いた UI コードを **WebAssembly** にコンパイルしてブラウザで動かす仕組み。
Jetpack Compose (Android の UI フレームワーク) と同じ書き方でウェブが作れる。

```
Kotlin コード (.kt)
    ↓ gradle wasmJsBrowserDistribution でビルド
HTML + JS + .wasm ファイル
    ↓ GitHub Pages に配置
ブラウザで動く
```

Canvas にレンダリングするため、DOM ベースの HTML ではなく描画ベース。
SEO には弱いが、個人ポートフォリオなら問題ない。

---

## ディレクトリ構成

```
frontend/src/wasmJsMain/kotlin/com/lifelog/web/
├── main.kt               ← エントリーポイント
├── App.kt                ← 画面の切り替え（ルーティング）
├── Screen.kt             ← どの画面かを表す型
├── theme/
│   └── Theme.kt          ← 色の定義
├── components/
│   └── Layout.kt         ← サイドバー付き共通レイアウト
├── api/
│   ├── ApiClient.kt      ← バックエンドへのHTTPリクエスト
│   └── Models.kt         ← バックエンドと共通のデータ型
└── pages/
    ├── CalendarPage.kt
    ├── DiaryPage.kt
    ├── ProjectsPage.kt
    ├── GoalsPage.kt
    ├── ActivityPage.kt
    ├── RecordsPage.kt
    └── AdminPage.kt
```

---

## main.kt — エントリーポイント

```kotlin
fun main() {
    ComposeViewport(document.body!!) {
        App()
    }
}
```

`ComposeViewport` = `<body>` 全体を Compose のキャンバスにする。
`App()` を呼び出してアプリを開始する。

---

## Screen.kt — 画面の状態管理

```kotlin
sealed class Screen {
    data object Calendar  : Screen()
    data object Projects  : Screen()
    data class  Diary(val date: String) : Screen()
    // ...
}
```

`sealed class` = この型のサブクラスはここに列挙されたものだけ、という型。
React Router のような URL ベースのルーティングではなく、
**状態**として「今どの画面か」を管理する。

---

## App.kt — 画面の切り替え

```kotlin
var currentScreen by remember { mutableStateOf<Screen>(Screen.Calendar) }

when (val screen = currentScreen) {
    is Screen.Calendar -> CalendarPage(...)
    is Screen.Diary    -> DiaryPage(date = screen.date, ...)
    is Screen.Projects -> ProjectsPage()
    // ...
}
```

- `remember { mutableStateOf(...) }` = 再描画をまたいで値を保持する変数
- `by` = プロパティ委譲。`currentScreen.value` ではなく `currentScreen` と書ける
- `when` = Kotlin の switch 文。`is Screen.Diary` でパターンマッチングできる

---

## Compose の基本

### UI は関数で書く

```kotlin
@Composable
fun ProjectsPage() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Projects", color = TextPrimary, fontSize = 24.sp)
        // ...
    }
}
```

- `@Composable` = この関数は UI を描画する、という印
- `Column` = 縦に並べるコンテナ（HTML の `<div style="flex-direction: column">` に相当）
- `Modifier` = サイズ・パディング・背景色などのスタイルをチェーンで指定

### 状態が変わると自動で再描画

```kotlin
var projects by remember { mutableStateOf<List<Project>>(emptyList()) }

// ← projects が変わると自動的に画面が更新される
LaunchedEffect(Unit) {
    projects = apiGetProjects()  // APIで取得して状態を更新
}
```

`LaunchedEffect` = コンポーザブルが画面に表示されたタイミングで一度だけ実行される。
`Unit` はキーで、「起動時に1回だけ」という意味。

---

## ApiClient.kt — API リクエスト

```kotlin
val httpClient = HttpClient(Js) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

suspend fun apiGetProjects(): List<Project> =
    httpClient.get("$baseUrl/projects").body()
```

- `HttpClient(Js)` = ブラウザの Fetch API を使う Ktor クライアント
- `suspend fun` = 非同期関数。完了するまで待つが、スレッドをブロックしない
- `.body()` = レスポンスを指定した型（`List<Project>`）に自動デシリアライズ

---

## ビルドの仕組み

```
# 開発時 (ブラウザで確認)
cd frontend
./gradlew wasmJsBrowserDevelopmentRun

# 本番ビルド (GitHub Pages 用の静的ファイル生成)
./gradlew wasmJsBrowserDistribution
# → build/dist/wasmJs/productionExecutable/ に出力される
```

GitHub Actions が main ブランチへのプッシュ時に自動でビルド＆デプロイする。

---

## API_BASE_URL の注入

ローカルと本番でバックエンドの URL が違う問題を Gradle で解決している：

```kotlin
// build.gradle.kts
val apiBaseUrl = System.getenv("API_BASE_URL") ?: ""

// ビルド時に ApiConfig.kt を自動生成
file.writeText("""
    const val API_BASE_URL = "$apiBaseUrl"
""")
```

GitHub Actions が `API_BASE_URL=https://xxx.railway.app` を環境変数にセットして
ビルドすると、その URL がバイナリに焼き込まれる。
