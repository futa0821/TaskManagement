package com.example.taskmanagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import com.example.taskmanagement.entity.BoardColumn;
import com.example.taskmanagement.entity.Card;

/**
 * Repository 経由で、本物の PostgreSQL(docker compose で起動したもの)に
 * 保存・検索・削除できることを確認するテスト。
 *
 * - 事前に `docker compose up -d` で DB を起動しておく必要がある。
 * - @DataJpaTest は、テストごとに処理を取り消す(ロールバック)ので、DB にデータは残らない。
 */
@DataJpaTest
// 既定では、テスト用の簡易DB(H2)に差し替えられる。本物の PostgreSQL を使うため、差し替えを止める。
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoryDbTest {

	@Autowired
	private BoardColumnRepository columnRepository;

	@Autowired
	private CardRepository cardRepository;

	// DB には data.sql のテストデータが入っている。「空の DB」を前提にしたこのテストのために、
	// 各テストの最初に全部消す。テストは終わるとロールバックされるので、DB の中身は元に戻る。
	@BeforeEach
	void 前準備としてDBを空にする() {
		cardRepository.deleteAllInBatch(); // カードを先に消す(カードが列を参照しているため)
		columnRepository.deleteAllInBatch();
	}

	@Test
	void 列とカードを保存して検索できる() {
		BoardColumn todo = columnRepository.save(new BoardColumn("未着手", 1));
		cardRepository.save(new Card(todo, "買い物", "テスト用", "高", LocalDate.of(2026, 11, 20), 1));
		cardRepository.save(new Card(todo, "掃除", null, "中", null, 2));

		// 全件検索
		List<Card> cards = cardRepository.findAll();
		assertThat(cards).hasSize(2);

		// 1件検索(ID 指定)。列との関係(外部キー)も保存されている
		Card first = cardRepository.findById(cards.get(0).getId()).orElseThrow();
		assertThat(first.getTitle()).isEqualTo("買い物");
		assertThat(first.getColumn().getName()).isEqualTo("未着手");
		assertThat(first.getDueDate()).isEqualTo(LocalDate.of(2026, 11, 20));
		assertThat(first.getCreatedAt()).isNotNull();
	}

	@Test
	void カードを削除できる() {
		BoardColumn todo = columnRepository.save(new BoardColumn("未着手", 1));
		Card card = cardRepository.save(new Card(todo, "削除するカード", null, "中", null, 1));

		cardRepository.deleteById(card.getId());

		assertThat(cardRepository.findById(card.getId())).isEmpty();
		assertThat(cardRepository.count()).isZero();
	}

	@Test
	void タイトルが51文字だと保存できない() {
		BoardColumn todo = columnRepository.save(new BoardColumn("未着手", 1));

		assertThatThrownBy(() -> cardRepository.saveAndFlush(new Card(todo, "あ".repeat(51), null, "中", null, 1)))
				.isInstanceOf(DataIntegrityViolationException.class);
	}
}
