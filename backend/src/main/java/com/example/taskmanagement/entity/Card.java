package com.example.taskmanagement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * カード。データ設計書の CARDS テーブルに対応する。
 * 1つの列(BoardColumn)に属する。
 */
@Entity
@Table(name = "cards")
public class Card {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 外部キー column_id。カードは必ず1つの列に属する。
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "column_id", nullable = false)
	private BoardColumn column;

	@Column(nullable = false, length = 50)
	private String title;

	@Column(length = 500)
	private String description;

	// 高・中・低のいずれか(値のチェックはアプリ側で行う。初期値は「中」)
	@Column(nullable = false)
	private String priority = "中";

	@Column(name = "due_date")
	private LocalDate dueDate;

	// 列内の並び順。小さいほど上に表示する。
	@Column(nullable = false)
	private Integer position;

	@Column(name = "placed_at", nullable = false)
	private LocalDateTime placedAt;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	protected Card() {
		// JPA(Hibernate)がデータを読み込むときに使う
	}

	/** 新しいカードを作る。作成日時・更新日時・列に置かれた日時は、今の日時を入れる。 */
	public Card(BoardColumn column, String title, String description, String priority,
			LocalDate dueDate, Integer position) {
		LocalDateTime now = LocalDateTime.now();
		this.column = column;
		this.title = title;
		this.description = description;
		this.priority = priority;
		this.dueDate = dueDate;
		this.position = position;
		this.placedAt = now;
		this.createdAt = now;
		this.updatedAt = now;
	}

	public Long getId() {
		return id;
	}

	public BoardColumn getColumn() {
		return column;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getPriority() {
		return priority;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public Integer getPosition() {
		return position;
	}

	public LocalDateTime getPlacedAt() {
		return placedAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
