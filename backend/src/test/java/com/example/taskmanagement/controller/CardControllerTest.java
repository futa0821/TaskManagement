package com.example.taskmanagement.controller;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 読み取り API のテスト。MockMvc で「HTTP の問い合わせ」を擬似的に送り、応答を確認する。
 *
 * - 事前に `docker compose up -d` で DB を起動しておく必要がある。
 * - 起動時に data.sql が実行され、列3件・カード10枚のテストデータが入る前提で確認している。
 */
@SpringBootTest
@AutoConfigureMockMvc
class CardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void 列の一覧が表示順で取れる() throws Exception {
		mockMvc.perform(get("/api/columns"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(3))
				.andExpect(jsonPath("$[0].name").value("未着手"))
				.andExpect(jsonPath("$[1].name").value("作業中"))
				.andExpect(jsonPath("$[2].name").value("完了"));
	}

	@Test
	void カードの一覧が列の順に取れる() throws Exception {
		mockMvc.perform(get("/api/cards"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(10))
				// 並びは「列の表示順 → position」。先頭は未着手の1番目、最後は完了の最後
				.andExpect(jsonPath("$[0].title").value("要件定義書を見直す"))
				.andExpect(jsonPath("$[9].title").value("GitHub の運用ルールを決める"));
	}

	@Test
	void columnIdで列を絞り込める() throws Exception {
		mockMvc.perform(get("/api/cards").param("columnId", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(3))
				.andExpect(jsonPath("$[*].columnName", everyItem(is("作業中"))));
	}

	@Test
	void IDを指定して1件取れる() throws Exception {
		mockMvc.perform(get("/api/cards/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").value("要件定義書を見直す"))
				.andExpect(jsonPath("$.priority").value("高"));
	}

	@Test
	void 存在しないIDだと404になる() throws Exception {
		mockMvc.perform(get("/api/cards/9999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}
}
