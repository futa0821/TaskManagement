package com.example.taskmanagement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskmanagement.dto.CardResponse;
import com.example.taskmanagement.dto.ColumnResponse;
import com.example.taskmanagement.service.CardService;

/** カード・列の取得 API(READ のみ)。URL と処理を結びつける窓口。 */
@RestController
@RequestMapping("/api")
public class CardController {

	private final CardService cardService;

	public CardController(CardService cardService) {
		this.cardService = cardService;
	}

	@GetMapping("/columns")
	public List<ColumnResponse> getColumns() {
		return cardService.findColumns();
	}

	@GetMapping("/cards")
	public List<CardResponse> getCards(@RequestParam(required = false) Long columnId) {
		return cardService.findCards(columnId);
	}

	@GetMapping("/cards/{id}")
	public CardResponse getCard(@PathVariable Long id) {
		return cardService.findCard(id);
	}
}
