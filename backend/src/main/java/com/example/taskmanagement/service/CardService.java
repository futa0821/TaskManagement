package com.example.taskmanagement.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taskmanagement.dto.CardResponse;
import com.example.taskmanagement.dto.ColumnResponse;
import com.example.taskmanagement.exception.CardNotFoundException;
import com.example.taskmanagement.repository.BoardColumnRepository;
import com.example.taskmanagement.repository.CardRepository;

/**
 * カード・列の「読み取り」の処理をまとめる層。Controller と Repository の間に入る。
 * Entity から DTO への詰め替えは、セッションが開いている(@Transactional の)この中で行う。
 */
@Service
@Transactional(readOnly = true)
public class CardService {

	private final CardRepository cardRepository;
	private final BoardColumnRepository columnRepository;

	public CardService(CardRepository cardRepository, BoardColumnRepository columnRepository) {
		this.cardRepository = cardRepository;
		this.columnRepository = columnRepository;
	}

	public List<ColumnResponse> findColumns() {
		return columnRepository.findAll(Sort.by("displayOrder")).stream()
				.map(ColumnResponse::from)
				.toList();
	}

	/** columnId が null なら全件、指定があればその列のカードだけ返す。 */
	public List<CardResponse> findCards(Long columnId) {
		var cards = (columnId == null)
				? cardRepository.findAllInBoardOrder()
				: cardRepository.findByColumnIdInOrder(columnId);
		return cards.stream().map(CardResponse::from).toList();
	}

	public CardResponse findCard(Long id) {
		return cardRepository.findById(id)
				.map(CardResponse::from)
				.orElseThrow(() -> new CardNotFoundException(id));
	}
}
