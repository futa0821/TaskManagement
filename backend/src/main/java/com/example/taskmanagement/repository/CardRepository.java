package com.example.taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskmanagement.entity.Card;

/** カード(cards テーブル)の保存・取得を行う。基本の操作は JpaRepository が用意してくれる。 */
public interface CardRepository extends JpaRepository<Card, Long> {
}
