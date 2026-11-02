// モック用:データはメモリ上のみ(再読み込みで初期状態に戻る)
const COLUMNS = [
  { id: 1, name: "未着手" },
  { id: 2, name: "作業中" },
  { id: 3, name: "完了" },
];

// 今日から offset 日ずらした日付を YYYY-MM-DD で返す(null は期限なし)
function dateFromToday(offset) {
  if (offset === null) return "";
  const d = new Date();
  d.setDate(d.getDate() + offset);
  return toYmd(d);
}
function toYmd(d) {
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${m}-${day}`;
}

// position は列内の並び順(小さいほど上)。サンプルは後から作ったものほど上に来るようにする
let seq = 0;
function newCard(columnId, title, description, priority, dueOffset) {
  seq += 1;
  return {
    id: seq, columnId, title, description, priority,
    dueDate: dateFromToday(dueOffset), position: -seq,
  };
}

let cards = [
  newCard(3, "掃除", "机の上を片付ける", "低", null),
  newCard(3, "請求書を送る", "", "中", -5),
  newCard(2, "レビュー", "先輩のコードをレビューする。\n観点:命名と例外処理", "中", 3),
  newCard(2, "テスト作成", "", "低", 2),
  newCard(1, "掃除の計画", "", "低", null),
  newCard(1, "期限切れのタスク", "昨日が期限だった例", "高", -1),
  newCard(1, "今日が期限のタスク", "当日は赤にならない例", "中", 0),
  newCard(1, "資料を作る", "来週の会議用の資料", "高", 5),
];

let editingId = null;  // null のとき追加モード
let deletingId = null;
let draggingId = null;

const PRIORITY_RANK = { "高": 0, "中": 1, "低": 2 };

const $ = (id) => document.getElementById(id);

function formatDue(ymd) {
  if (!ymd) return "なし";
  const [, m, d] = ymd.split("-");
  return `${Number(m)}/${Number(d)}`;
}
function isOverdue(ymd) {
  return ymd !== "" && ymd < toYmd(new Date()); // 昨日以前のみ
}

// 列の並び(position の小さい順。同じ値なら id の大きい順)
function savedOrder(columnId) {
  return cards
    .filter((c) => c.columnId === columnId)
    .sort((a, b) => a.position - b.position || b.id - a.id);
}

// 新しいカードを列の一番上に入れるための position
function topPosition(columnId) {
  const list = cards.filter((c) => c.columnId === columnId);
  return list.length === 0 ? 0 : Math.min(...list.map((c) => c.position)) - 1;
}

// 列のカードの position を、上から 1,2,3… に振り直す
function renumber(list) {
  list.forEach((c, i) => { c.position = i + 1; });
}

// 優先度順・期限順ボタン:押した列のカードを並べ替えて、その並びを保存する
// 同じ優先度・同じ期限の中は、今の並びのまま(安定ソート)
function sortColumn(columnId, mode) {
  const list = savedOrder(columnId);
  if (mode === "priority") {
    list.sort((a, b) => PRIORITY_RANK[a.priority] - PRIORITY_RANK[b.priority]);
  } else if (mode === "due") {
    const key = (c) => (c.dueDate === "" ? "9999-99-99" : c.dueDate);
    list.sort((a, b) => key(a).localeCompare(key(b)));
  }
  renumber(list);
  render();
}

// ドラッグしたカードを、列 columnId の targetId の前(after なら後ろ)に入れる
// targetId が null のときは、その列の一番下に入れる
function placeCard(id, columnId, targetId, after) {
  const moving = cards.find((x) => x.id === id);
  if (!moving) return;
  const list = savedOrder(columnId).filter((c) => c.id !== id);
  let index = list.length;
  if (targetId !== null) {
    index = list.findIndex((c) => c.id === targetId) + (after ? 1 : 0);
  }
  moving.columnId = columnId;
  list.splice(index, 0, moving);
  renumber(list);
  render();
}

function render() {
  const board = $("board");
  board.innerHTML = "";
  for (const col of COLUMNS) {
    const list = savedOrder(col.id);

    const colEl = document.createElement("section");
    colEl.className = "column";
    colEl.innerHTML = `<h2>${col.name} (${list.length})</h2>`;

    colEl.appendChild(toolbar(col.id));

    if (col.id === 1) {
      const add = document.createElement("button");
      add.className = "add-btn";
      add.textContent = "+ カードを追加";
      add.onclick = () => openCardModal(null);
      colEl.appendChild(add);
    }

    for (const c of list) colEl.appendChild(cardElement(c));

    // 列の余白(カードの上以外)に落としたら、その列の一番下に入る
    colEl.addEventListener("dragover", (e) => {
      if (draggingId === null) return;
      e.preventDefault();
      colEl.classList.add("drag-over");
    });
    colEl.addEventListener("dragleave", (e) => {
      if (!colEl.contains(e.relatedTarget)) colEl.classList.remove("drag-over");
    });
    colEl.addEventListener("drop", (e) => {
      if (draggingId === null) return;
      e.preventDefault();
      placeCard(draggingId, col.id, null, false);
    });
    board.appendChild(colEl);
  }
}

// 列見出しの下のボタン(優先度順・期限順)
function toolbar(columnId) {
  const bar = document.createElement("div");
  bar.className = "col-tools";
  for (const [mode, label] of [["priority", "優先度順"], ["due", "期限順"]]) {
    const b = document.createElement("button");
    b.textContent = label;
    b.className = "tool-btn";
    b.onclick = () => sortColumn(columnId, mode);
    bar.appendChild(b);
  }
  return bar;
}

function cardElement(c) {
  const el = document.createElement("div");
  el.className = `card p-${c.priority}`;
  el.draggable = true;

  const body = document.createElement("div");
  const title = document.createElement("div");
  title.className = "card-title";
  const pri = document.createElement("span");
  pri.className = `pri pri-${c.priority}`;
  pri.textContent = `【${c.priority}】`;
  title.append(pri, document.createTextNode(c.title));
  const due = document.createElement("div");
  due.className = "card-due" + (isOverdue(c.dueDate) ? " overdue" : "");
  due.textContent = `期限 ${formatDue(c.dueDate)}`;
  body.append(title, due);

  const del = document.createElement("button");
  del.className = "delete-btn";
  del.textContent = "🗑";
  del.title = "削除";
  del.onclick = (e) => {
    e.stopPropagation(); // カードのクリック(編集)を開かない
    openDeleteModal(c.id);
  };

  el.append(body, del);
  el.onclick = () => openCardModal(c.id);
  el.addEventListener("dragstart", () => {
    draggingId = c.id;
    setTimeout(() => el.classList.add("dragging"), 0);
  });
  el.addEventListener("dragend", () => {
    draggingId = null;
    render();
  });

  // カードの上半分に落とすと、そのカードの前に入る。下半分なら後ろに入る
  const isAfter = (e) => {
    const r = el.getBoundingClientRect();
    return e.clientY > r.top + r.height / 2;
  };
  el.addEventListener("dragover", (e) => {
    if (draggingId === null) return;
    e.preventDefault();
    e.stopPropagation();
    if (draggingId === c.id) return; // 自分自身の上には線を出さない
    el.classList.toggle("drop-after", isAfter(e));
    el.classList.toggle("drop-before", !isAfter(e));
  });
  el.addEventListener("dragleave", () => {
    el.classList.remove("drop-before", "drop-after");
  });
  el.addEventListener("drop", (e) => {
    if (draggingId === null) return;
    e.preventDefault();
    e.stopPropagation();
    if (draggingId === c.id) return; // 自分自身の上に落としたら、何も変わらない
    placeCard(draggingId, c.columnId, c.id, isAfter(e));
  });
  return el;
}

// S-02
function openCardModal(id) {
  editingId = id;
  const c = id === null ? null : cards.find((x) => x.id === id);
  $("card-modal-title").textContent = c ? "カードを編集" : "カードを追加";
  $("f-title").value = c ? c.title : "";
  $("f-desc").value = c ? c.description : "";
  $("f-priority").value = c ? c.priority : "中";
  $("f-due").value = c ? c.dueDate : "";
  $("title-error").hidden = true;
  $("card-modal").hidden = false;
  $("f-title").focus();
}
function closeCardModal() {
  $("card-modal").hidden = true;
}

$("card-form").addEventListener("submit", (e) => {
  e.preventDefault();
  const title = $("f-title").value.trim();
  if (!title) {
    $("title-error").hidden = false; // M-01
    return;
  }
  const values = {
    title,
    description: $("f-desc").value,
    priority: $("f-priority").value,
    dueDate: $("f-due").value,
  };
  if (editingId === null) {
    seq += 1;
    cards.push({ id: seq, columnId: 1, position: topPosition(1), ...values });
  } else {
    // 編集では列も position も変えない
    Object.assign(cards.find((x) => x.id === editingId), values);
  }
  closeCardModal();
  render();
});
$("card-cancel").onclick = closeCardModal;

// S-03
function openDeleteModal(id) {
  deletingId = id;
  $("delete-modal").hidden = false;
}
function closeDeleteModal() {
  deletingId = null;
  $("delete-modal").hidden = true;
}
$("delete-cancel").onclick = closeDeleteModal;
$("delete-ok").onclick = () => {
  cards = cards.filter((c) => c.id !== deletingId);
  closeDeleteModal();
  render();
};

render();
