package com.example.taskmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Controller で起きた例外を、API の応答(ステータスコードと JSON)に変換する場所。 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CardNotFoundException.class)
	public ProblemDetail handleCardNotFound(CardNotFoundException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
	}
}
