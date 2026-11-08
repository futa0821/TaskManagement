# TaskManagement バックエンド

Spring Boot で作るバックエンドです。現在は、Spring Initializr で生成したひな型に、DB 関連の依存関係を追加した状態です。DB(PostgreSQL)にはまだ接続していません。

## 技術

| 項目 | 内容 |
|---|---|
| 言語 | Java 25 |
| フレームワーク | Spring Boot 4.1.2 |
| ビルドツール | Gradle 9.8.2(Gradle Wrapper を同梱。別途インストール不要) |
| 依存関係 | Spring Web、Spring Data JPA、Validation、PostgreSQL ドライバ |
| 設定ファイル | `src/main/resources/application.yml` |

## 事前準備

Java 25(JDK)をインストールしておく。複数の Java が入っている場合は、`JAVA_HOME` で Java 25 を指定する(そのターミナルを閉じるまで有効)。

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.5.8-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version   # 25 と表示されればOK
```

## 使い方

`backend/` フォルダで実行する。

| やりたいこと | コマンド |
|---|---|
| テストを実行する | `.\gradlew.bat test` |
| サーバーを起動する | `.\gradlew.bat bootRun` |
| サーバーを止める | ターミナルで `Ctrl + C` |

初回は Gradle 本体と部品のダウンロードのため、数分かかる。

## 動作確認

サーバーを起動し、ブラウザで <http://localhost:8080> を開く。
まだ画面や API を作っていないため、`Whitelabel Error Page`(404 Not Found)が表示される。これは **サーバーが起動してリクエストを受け付けている状態** で、正常。

## 注意:DB 関連の自動設定を一時的に除外している

JPA と PostgreSQL ドライバが入っているため、DB に接続できないとアプリ全体が起動に失敗する
(`localhost:5432 への接続が拒絶されました` というエラーになる)。

PostgreSQL を用意するまでの間、`application.yml` の `spring.autoconfigure.exclude` で、DB 関連の自動設定を使わないようにしている。

**PostgreSQL を用意したら:**

1. `application.yml` の `autoconfigure` ブロックを削除する
2. コメントアウトしている `datasource` と `jpa` の設定を有効にする(ユーザー名・パスワードは自分の環境に合わせる)

## フォルダ構成

```
backend/
├─ build.gradle        ビルドの設定(使う部品、Javaのバージョンなど)
├─ settings.gradle     プロジェクト名の設定
├─ gradlew(.bat)       Gradle Wrapper(Gradleを自動で用意して実行する)
├─ gradle/wrapper/     Gradle Wrapper の設定
└─ src/
   ├─ main/java/com/example/taskmanagement/TaskmanagementApplication.java   起動クラス
   ├─ main/resources/application.yml                                       アプリの設定
   └─ test/java/com/example/taskmanagement/TaskmanagementApplicationTests.java  起動テスト
```

## 今後の予定

- PostgreSQL の用意と接続(上の「注意」の手順で、除外を外す)
- テーブル(Entity)の作成
