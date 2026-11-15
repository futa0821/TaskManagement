package com.example.taskmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 列(未着手 / 作業中 / 完了)。データ設計書の COLUMNS テーブルに対応する。
 * クラス名は、JPA の @Column と区別するため BoardColumn にしている。
 */
@Entity
@Table(name = "columns")
public class BoardColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	protected BoardColumn() {
		// JPA(Hibernate)がデータを読み込むときに使う
	}

	public BoardColumn(String name, Integer displayOrder) {
		this.name = name;
		this.displayOrder = displayOrder;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}
}
