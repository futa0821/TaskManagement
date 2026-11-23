package com.example.taskmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.taskmanagement.entity.Card;

/** カード(cards テーブル)の保存・取得を行う。基本の操作は JpaRepository が用意してくれる。 */
public interface CardRepository extends JpaRepository<Card, Long> {

	/** 全カード。並びは データ設計書 5.1 のとおり(列の表示順 → position 昇順 → id 降順)。 */
	@Query("select c from Card c order by c.column.displayOrder, c.position, c.id desc")
	List<Card> findAllInBoardOrder();

	/** 指定した列のカード。並びは position 昇順 → id 降順。 */
	@Query("select c from Card c where c.column.id = :columnId order by c.position, c.id desc")
	List<Card> findByColumnIdInOrder(Long columnId);
}
