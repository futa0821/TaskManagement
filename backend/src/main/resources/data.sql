-- テストデータ。アプリ起動のたびに自動で実行される(application.yml の spring.sql.init.mode: always)。
-- 列は データ設計書 4.1 の初期データ。カードは 優先度・期限あり/なし・説明あり/なし・期限切れ を混ぜている。
-- 起動のたびに実行されるので、先に全部消して、何回起動しても同じ状態になるようにする(開発用)。
-- RESTART IDENTITY: 自動採番の番号も 1 に戻す。
TRUNCATE TABLE cards, columns RESTART IDENTITY;

INSERT INTO columns (name, display_order) VALUES ('未着手', 1), ('作業中', 2), ('完了', 3);

INSERT INTO cards (column_id, title, description, priority, due_date, position, placed_at, created_at, updated_at) VALUES
((SELECT id FROM columns WHERE name = '未着手'), '要件定義書を見直す', '機能要件の抜け漏れを確認する', '高', CURRENT_DATE + 3, 1, NOW(), NOW(), NOW()),
((SELECT id FROM columns WHERE name = '未着手'), '買い物に行く', NULL, '中', NULL, 2, NOW(), NOW(), NOW()),
((SELECT id FROM columns WHERE name = '未着手'), '請求書を送る', '期限切れのサンプル', '高', CURRENT_DATE - 2, 3, NOW(), NOW(), NOW()),
((SELECT id FROM columns WHERE name = '未着手'), '部屋の掃除', NULL, '低', CURRENT_DATE + 14, 4, NOW(), NOW(), NOW()),
((SELECT id FROM columns WHERE name = '作業中'), 'API を実装する', E'GET の API を作る。\n画面は不要。', '高', CURRENT_DATE + 1, 1, NOW(), NOW(), NOW()),
((SELECT id FROM columns WHERE name = '作業中'), 'README を書く', NULL, '中', CURRENT_DATE, 2, NOW(), NOW(), NOW()),
((SELECT id FROM columns WHERE name = '作業中'), '英語の勉強', '毎日30分', '低', NULL, 3, NOW(), NOW(), NOW()),
((SELECT id FROM columns WHERE name = '完了'), 'DB を Docker で用意する', 'PostgreSQL 17', '高', CURRENT_DATE - 7, 1, NOW(), NOW(), NOW()),
((SELECT id FROM columns WHERE name = '完了'), 'Spring Boot のひな型を作る', NULL, '中', CURRENT_DATE - 10, 2, NOW(), NOW(), NOW()),
((SELECT id FROM columns WHERE name = '完了'), 'GitHub の運用ルールを決める', 'CLAUDE.md に記載', '中', NULL, 3, NOW(), NOW(), NOW());
