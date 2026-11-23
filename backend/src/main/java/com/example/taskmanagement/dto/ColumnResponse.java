package com.example.taskmanagement.dto;

import com.example.taskmanagement.entity.BoardColumn;

/** 列を API で返すときの形。Entity をそのまま返さず、返したい項目だけを持つ。 */
public record ColumnResponse(Long id, String name, Integer displayOrder) {

	public static ColumnResponse from(BoardColumn column) {
		return new ColumnResponse(column.getId(), column.getName(), column.getDisplayOrder());
	}
}
