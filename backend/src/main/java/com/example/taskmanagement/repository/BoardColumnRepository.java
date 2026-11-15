package com.example.taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskmanagement.entity.BoardColumn;

/** 列(columns テーブル)の保存・取得を行う。基本の操作は JpaRepository が用意してくれる。 */
public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {
}
