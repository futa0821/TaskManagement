package com.example.taskmanagement.exception;

/** 指定したIDのカードが存在しないときに投げる例外。API では 404 に変換される。 */
public class CardNotFoundException extends RuntimeException {

	public CardNotFoundException(Long id) {
		super("カードが見つかりません(id=" + id + ")");
	}
}
