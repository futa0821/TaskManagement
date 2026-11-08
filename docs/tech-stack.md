# 技術スタック:Trello風タスク管理アプリ

作成日:2026-10-24 / 更新日:2026-11-08 / 作成者:futa / 版数:0.4 / 状態:決定(課題の指定に沿って選定)
関連文書:[要件定義書](requirements.md)、[非機能要件](non-functional-requirements.md)、[データ設計書](data-design.md)

## 改訂履歴
| 版数 | 日付 | 内容 |
|---|---|---|
| 0.1 | 2026-10-24 | 候補の整理(未決定) |
| 0.2 | 2026-11-02 | 課題の指定(Java/Spring Boot、React、PostgreSQL、Next対象外)に沿って決定版に変更 |
| 0.3 | 2026-11-02 | 追加の指定を反映。ビルドツールを Maven から Gradle に変更し、Tailwind CSS と npm を追加 |
| 0.4 | 2026-11-08 | バックエンドの環境構築に合わせて、Java を 21 から 25(LTS)に、Spring Boot を 3.x系から 4.1系(4.1.2)に変更 |

## 1. 課題での指定
- バックエンド:Java と Spring Boot
- フロントエンド:React(Next.js は今回は対象外)
- データベース:PostgreSQL
- 上記以外のツールは、これらに合わせて選定する

追加で指定されたツール(2026-11-02):
- フロントエンド:React + Vite + TypeScript + Tailwind CSS + dnd-kit + npm
- バックエンド:Java + Spring Boot + Gradle + REST API
- データベース:PostgreSQL

## 2. 前提(これまでに決まっていること)
- 実際のDBを使って運用し、バックエンドを作る(2026-10-20 決定)
- 利用者は1人で、マルチユーザー対応・ログインはしない
- 画面は1ページだけ(NF-08)。Next.js が不要な点とも合う
- 対応ブラウザは Chrome の最新版(Windows)、画面幅は1280px以上(NF-05、NF-06)
- カードを列の間でドラッグして移動できること(F-04)、列内で並べ替えられること(F-10)
- ER図は Mermaid 記法で書く
- 想定カード枚数は200枚まで(NF-01)

## 3. 全体像
```
ブラウザ(Chrome)
  └ React画面  ──(REST API / JSON)──▶  Spring Boot サーバー  ──(SQL)──▶  PostgreSQL
   フロントエンド                          バックエンド                     データベース
```

## 4. 採用する技術
### 4.1 バックエンド
| 項目 | 採用 | 選定理由 |
|---|---|---|
| 言語 | Java 25(LTS) | 課題の指定。LTS(長期サポート版)で現場の標準 |
| フレームワーク | Spring Boot 4.1系(4.1.2) | 課題の指定(4系の最新)。構築時(2026-11-08)の最新の安定版 |
| ビルドツール | Gradle(指定) | 設定ファイル(build.gradle)で、ライブラリの取得・ビルド・テスト・起動を管理する。`./gradlew bootRun` でサーバーを起動できる |
| API方式 | REST API(指定) | URL と HTTPメソッド(GET/POST/PUT/DELETE)で操作を表し、データは JSON でやり取りする |
| DBアクセス | Spring Data JPA(Hibernate) | Spring標準。SQLを直接書かずに操作できる |
| 表の管理 | Flyway | 表の作成・変更をSQLファイルで履歴管理できる。データ設計書の物理設計をそのままSQL化できる |
| 入力チェック | Bean Validation | Spring Boot標準。タイトル50文字以内などをバックエンドでも検査する |
| API仕様書 | springdoc-openapi(Swagger UI) | API仕様書を自動生成でき、要件定義書に載せやすい |
| テスト | JUnit 5、Spring Boot Test | 標準 |

### 4.2 フロントエンド
| 項目 | 採用 | 選定理由 |
|---|---|---|
| ライブラリ | React | 指定。画面を部品(コンポーネント)に分けて作れる |
| 言語 | TypeScript(指定) | JavaScriptに型を足した言語。実行前に間違いに気づける。React現場の標準 |
| ビルドツール | Vite(指定) | 起動が速く、Next.js なしの React 単体で標準的。旧CRAは非推奨 |
| スタイル | Tailwind CSS(指定) | 小さなクラス名(例:`p-4` は余白、`text-red-500` は赤い文字)を組み合わせてデザインする。優先度の文字色(F-07)などに使う |
| パッケージ管理 | npm(指定) | ライブラリを `package.json` に記録し、`npm install` でまとめて導入する。Node.js(LTS版)が必要 |
| ドラッグ操作 | dnd-kit(指定) | 列間の移動・列内の並べ替え(F-04、F-10)を作りやすい |
| API通信 | 標準の fetch | まずは追加ライブラリなし。必要になれば TanStack Query を検討 |
| 品質チェック | Vitest、ESLint、Prettier | テスト、文法チェック、コード整形の標準的な道具 |
| 開発中の通信 | Vite の proxy 設定 | フロントとバックエンドのポートが違っても、CORS(別オリジンへの通信制限)の問題を避けられる |

### 4.3 データベース・開発環境
| 項目 | 採用 | 選定理由 |
|---|---|---|
| DB | PostgreSQL 16系 | 指定。実務で広く使われる |
| DBの起動 | Docker Compose | PostgreSQL のインストール作業を避け、`docker compose up` で同じ環境を再現できる |
| ソース管理 | Git | 既存。構成は backend/、frontend/、compose.yaml、docs/ |

## 5. 採用しなかった技術と理由
| 技術 | 理由 |
|---|---|
| Next.js | 課題で対象外。画面が1ページで、サーバー側描画も不要 |
| Maven | Gradle と役割は同じ。Gradle が指定されたため不採用 |
| MyBatis | SQLを自分で書く方式。今回は Spring 標準の JPA を採用 |
| SQLite、Node.js、Python | 課題の指定(PostgreSQL、Java)により不採用 |
| 素の HTML + JavaScript | 課題の指定(React)により、モック(mock/)のみで使用 |

## 6. 決めたあとの見直し項目
- 非機能要件:NF-04(データ容量)、NF-07(導入。Java、Node.js、Docker が必要になる)、NF-10〜14(localStorage前提からDB前提へ)
- 要件定義書:2章、7章、8.3
- データ設計書:9章の物理設計(データ型、SQL制約、インデックス、文字数検査をどちらで行うか)
  - 文字数検査の方針案:画面とバックエンドの両方(Bean Validation)で行い、DBでも長さ制限をかける
