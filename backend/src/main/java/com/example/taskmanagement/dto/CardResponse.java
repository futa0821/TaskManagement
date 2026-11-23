package com.example.taskmanagement.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.taskmanagement.entity.Card;

/** カードを API で返すときの形。列は、中身ではなく列ID(columnId)と列名(columnName)だけを返す。 */
public record CardResponse(
		Long id,
		Long columnId,
		String columnName,
		String title,
		String description,
		String priority,
		LocalDate dueDate,
		Integer position,
		LocalDateTime placedAt,
		LocalDateTime createdAt,
		LocalDateTime updatedAt) {

	/** 呼ぶ側は、セッションが開いている間(Service の中)で呼ぶこと。column の中身を読むため。 */
	public static CardResponse from(Card card) {
		return new CardResponse(
				card.getId(),
				card.getColumn().getId(),
				card.getColumn().getName(),
				card.getTitle(),
				card.getDescription(),
				card.getPriority(),
				card.getDueDate(),
				card.getPosition(),
				card.getPlacedAt(),
				card.getCreatedAt(),
				card.getUpdatedAt());
	}
}
