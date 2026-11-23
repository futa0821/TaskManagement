# TaskManagement バックエンド

Spring Boot で作るバックエンドです。PostgreSQL(Docker)に接続し、カード・列を取得する読み取り用の REST API(GET)があります。画面はありません。

## 技術

| 項目 | 内容 |
|---|---|
| 言語 | Java 25 |
| フレームワーク | Spring Boot 4.1.2 |
| ビルドツール | Gradle 9.8.2(Gradle Wrapper を同梱。別途インストール不要) |
| 依存関係 | Spring Web、Spring Data JPA、Validation、PostgreSQL ドライバ |
| DB | PostgreSQL 17(リポジトリ直下の `docker-compose.yml` で起動) |
| 設定ファイル | `src/main/resources/application.yml` |

## 事前準備

1. Java 25(JDK)をインストールしておく。複数の Java が入っている場合は、`JAVA_HOME` で Java 25 を指定する(そのターミナルを閉じるまで有効)。

   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.5.8-hotspot"
   $env:Path = "$env:JAVA_HOME\bin;$env:Path"
   java -version   # 25 と表示されればOK
   ```

2. Docker Desktop を起動し、リポジトリ直下で DB を起動する。

   ```powershell
   docker compose up -d
   ```

## 使い方

`backend/` フォルダで実行する。

| やりたいこと | コマンド |
|---|---|
| テストを実行する | `.\gradlew.bat test` |
| サーバーを起動する | `.\gradlew.bat bootRun` |
| サーバーを止める | ターミナルで `Ctrl + C` |

初回は Gradle 本体と部品のダウンロードのため、数分かかる。

## API

| メソッドと URL | 内容 |
|---|---|
| `GET /api/columns` | 列の一覧(表示順) |
| `GET /api/cards` | カードの一覧(列の表示順 → `position` 昇順 → `id` 降順)。`?columnId=2` で列を絞り込める |
| `GET /api/cards/{id}` | カード1件。存在しない ID は 404 |

## 動作確認

1. DB を起動し(`docker compose up -d`)、サーバーを起動する(`.\gradlew.bat bootRun`)。
2. 別のターミナルで、次を実行する(ブラウザのアドレス欄に URL を入れても確認できる)。

   ```powershell
   curl.exe http://localhost:8080/api/columns
   curl.exe http://localhost:8080/api/cards
   curl.exe "http://localhost:8080/api/cards?columnId=2"
   curl.exe http://localhost:8080/api/cards/1
   curl.exe http://localhost:8080/api/cards/9999   # 404 になる
   ```

## テストデータ(`data.sql`)

- `src/main/resources/data.sql` に、列3件とカード10枚のテストデータがある。
- **アプリを起動するたびに自動で実行される**(`spring.sql.init.mode: always`)。
- 先頭で `TRUNCATE`(全データの削除)をしているため、**起動するたびに、DB の全データが消えてテストデータに戻る**。開発・学習用の設定で、本番では使わない。
- 自動テスト(`.\gradlew.bat test`)の実行時にも同じ処理が動くので、開発用 DB に作ったデータは消える。

## フォルダ構成

```
backend/
├─ build.gradle        ビルドの設定(使う部品、Javaのバージョンなど)
├─ settings.gradle     プロジェクト名の設定
├─ gradlew(.bat)       Gradle Wrapper(Gradleを自動で用意して実行する)
├─ gradle/wrapper/     Gradle Wrapper の設定
└─ src/
   ├─ main/java/com/example/taskmanagement/
   │  ├─ TaskmanagementApplication.java   起動クラス
   │  ├─ controller/   API の窓口(URL と処理を結びつける)
   │  ├─ service/      処理の手順(Entity から DTO への詰め替えもここ)
   │  ├─ repository/   DB から取り出す係
   │  ├─ entity/       DB のテーブルに対応するクラス
   │  ├─ dto/          API で返す形のクラス
   │  └─ exception/    例外と、404 などへの変換
   ├─ main/resources/
   │  ├─ application.yml   アプリの設定
   │  └─ data.sql          テストデータ
   └─ test/java/...         自動テスト(Repository の DB テスト、API のテスト)
```

## 今後の予定

- カードの追加・編集・削除(CREATE / UPDATE / DELETE)の API
