<script setup>
// JavaScript (Vue 的邏輯與變數)
import { ref, onMounted } from 'vue'; 
import axios from 'axios';

// 準備一個響應式變數，用來裝後端回傳的筆記陣列 (初始值為空陣列)
const notes = ref([]);
// 控制彈窗
const isModalOpen = ref(false);
const selectedNote = ref({});

// 控制「新增筆記」彈窗的變數與表單資料
const isAddModalOpen = ref(false); // 控制新增彈窗是否開啟
const newNote = ref({ title: '', type: '', content: '' }); 

// 送出新增筆記的函式
const submitNewNote = async () => {
  // 簡單的防呆檢查
  if (!newNote.value.title || !newNote.value.content) {
    alert('標題與內容不能為空哦！');
    return;
  }
  
  try {
    await axios.post('http://localhost:8080/api/notes', newNote.value);
    // 新增成功後，重新撈取一次最新資料來更新畫面
    await fetchNotes();
    
    // 關閉彈窗並把表單清空，迎接下一次新增
    isAddModalOpen.value = false;
    newNote.value = { title: '', type: '', content: '' };
  } catch (error) {
    console.error('新增失敗：', error);
    alert('新增筆記失敗，請檢查後端狀態！');
  }
};

// 深色模式變數
const isDarkMode = ref(localStorage.getItem('theme') === 'dark');

// 換主題的動作
const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value; 
  localStorage.setItem('theme', isDarkMode.value ? 'dark' : 'light');
  
  // 同步切換最底層 body 的 class
  if (isDarkMode.value) {
    document.body.classList.add('dark-mode-body');
  } else {
    document.body.classList.remove('dark-mode-body');
  }
};


// 建立一個撈取資料的非同步函式 (async/await)
const fetchNotes = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/notes');
    // Axios 會把後端吐回來的 JSON 放在 response.data 裡面
    notes.value = response.data;
    console.log('成功從 Spring Boot 撈到資料：', notes.value);
  } catch (error) {
    console.error('API 呼叫失敗：', error);
  }
};

// 打開彈窗的動作
const openModal = (note) => {
  selectedNote.value = note; // 把點擊的那包筆記塞進變數裡
  isModalOpen.value = true;  // 把背景變暗、顯示彈窗
};
// 關閉彈窗的動作
const closeModal = () => {
  isModalOpen.value = false;
};

// Vue 生命週期：當網頁元件一載入 (Mounted) 時，就自動執行撈取函式
onMounted(() => {
  fetchNotes();
  // 重新整理網頁時，如果記憶是深色，也要把 body 變黑
  if (isDarkMode.value) {
    document.body.classList.add('dark-mode-body');
  }
});
</script>

<template>
  <div class="container" :class="{ 'dark-mode': isDarkMode }">
    <!-- 切換按鈕 -->
    <button class="theme-toggle-btn" @click="toggleTheme">
      {{ isDarkMode ? '☀️ 淺色模式' : '🌙 深色模式' }}
    </button>
    <h1 class="page-title">我的筆記</h1>
    
    <!-- 觸發新增彈窗的按鈕 -->
    <div class="action-bar">
      <button class="add-btn" @click="isAddModalOpen = true">➕ 新增筆記</button>
    </div>
    <hr class="divider">

    <!-- 筆記卡片網格區塊 -->
    <div class="notes-grid">
      <!-- 🌟 Vue 核心魔法：v-for 迴圈 -->
      <div v-for="note in notes" :key="note.id" class="note-card" @click="openModal(note)">
        <div class="card-header">
          <span class="note-type">{{ note.type }}</span>
        </div>
        <h3 class="note-title">{{ note.title }}</h3>
        <p class="note-content">{{ note.content }}</p>
      </div>
    </div>

    <!-- ========================================== -->
    <!-- 1. 懸浮彈窗 (Modal) 區塊 (用來查看筆記)      -->
    <!-- ========================================== -->
    <div v-if="isModalOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <!-- 關閉按鈕 -->
        <button class="close-btn" @click="closeModal">✕</button>
        
        <!-- 彈窗內的筆記資料 -->
        <div class="modal-header">
          <span class="note-type">{{ selectedNote.type }}</span>
        </div>
        <h2 class="modal-title">{{ selectedNote.title }}</h2>
        <div class="modal-body">
          <!-- 顯示完整內容 -->
          {{ selectedNote.content }}
        </div>
      </div>
    </div>

    <!-- ========================================== -->
    <!-- 2. 填寫新筆記的專屬彈窗                      -->
    <!-- ========================================== -->
    <div v-if="isAddModalOpen" class="modal-overlay" @click.self="isAddModalOpen = false">
      <div class="modal-content">
        <button class="close-btn" @click="isAddModalOpen = false">✕</button>
        <h2 class="modal-title">新增筆記</h2>
        
        <div class="form-group">
          <label>分類標籤</label>
          <input v-model="newNote.type" type="text" placeholder="例如：日常、工作、學習" class="form-input">
        </div>
        
        <div class="form-group">
          <label>筆記標題 *</label>
          <input v-model="newNote.title" type="text" placeholder="請輸入標題" class="form-input">
        </div>
        
        <div class="form-group">
          <label>筆記內容 *</label>
          <textarea v-model="newNote.content" rows="5" placeholder="請輸入筆記內容..." class="form-input"></textarea>
        </div>
        
        <button class="submit-btn" @click="submitNewNote">儲存筆記</button>
      </div>
    </div>

  </div>
</template>

<style scoped>

/* 切換深淺色過渡動畫 */
* {
  transition: background-color 0.3s ease, color 0.3s ease, border-color 0.3s ease;
}

/* 容器設定 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;  
  min-height: 100vh;
}

/* 切換主題按鈕設計 */
.theme-toggle-btn {
  position: absolute;
  top: 25px;
  right: 20px;
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid #d1d5db;
  background: transparent;
  color: #4b5563;
  font-weight: bold;
  cursor: pointer;
}

.page-title {
  color: #1f2937;
  text-align: center;
  margin-bottom: 20px;
  font-size: 2rem;
  font-weight: 800;
}

.divider {
  border: 0;
  height: 2px;
  background: #e5e7eb;
  margin-bottom: 40px;
  width: 100px;
  margin-left: auto;
  margin-right: auto;
}

/* 網格排版：卡片自動並排，當螢幕變小會自動折行 (響應式設計) */
.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

/* 單張卡片的設計 */
.note-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  padding: 24px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border: 1px solid #f3f4f6;
  cursor: pointer;
}

/* 互動特效：滑鼠游標移過去時，卡片會輕微往上浮起 */
.note-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.card-header {
  margin-bottom: 16px;
}

/* 分類標籤美化 */
.note-type {
  background-color: #e0f2fe;
  color: #0369a1;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 700;
  letter-spacing: 0.05em;
}

.note-title {
  margin: 0 0 12px 0;
  font-size: 1.25rem;
  color: #111827;
  font-weight: 700;
}

.note-content {
  color: #4b5563;
  line-height: 1.6;
  margin: 0;
  /* 文字若太多會自動顯示刪節號 (...)，控制卡片高度一致 */
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 彈窗 (Modal) CSS 樣式 */

/* 黑色半透明背景，蓋住整個螢幕 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000; /* 確保它在最上層 */
}

/* 懸浮在中間的白色大卡片 */
.modal-content {
  background: white;
  width: 90%;
  max-width: 600px;
  max-height: 80vh; /* 最高不超過螢幕的 80% */
  border-radius: 16px;
  padding: 32px;
  position: relative;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  overflow-y: auto; /* 如果文字真的超級多，卡片內部會出現捲軸 */
}

/* 右上角的關閉按鈕 */
.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #9ca3af;
  cursor: pointer;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #ef4444; /* 滑鼠移上去變紅色 */
}

.modal-header {
  margin-bottom: 20px;
}

.modal-title {
  font-size: 1.75rem;
  color: #111827;
  margin-bottom: 20px;
}

.modal-body {
  color: #374151;
  line-height: 1.8;
  font-size: 1.1rem;
  white-space: pre-wrap; /* 保留換行符號，讓排版不跑掉 */
}

/* 表單與按鈕樣式 */
.action-bar {
  text-align: center;
  margin-bottom: 20px;
}

.add-btn {
  background-color: #0284c7;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.1s;
}

.add-btn:hover {
  background-color: #0369a1;
  transform: scale(1.02);
}

.form-group {
  margin-bottom: 20px;
  text-align: left;
}

.form-group label {
  display: block;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 1rem;
  font-family: inherit;
  box-sizing: border-box; /* 確保 padding 不會讓寬度爆掉 */
  transition: border-color 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #0284c7;
  box-shadow: 0 0 0 3px rgba(2, 132, 199, 0.2);
}

.submit-btn {
  width: 100%;
  background-color: #10b981; /* 綠色儲存按鈕 */
  color: white;
  border: none;
  padding: 14px;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  margin-top: 10px;
  transition: background-color 0.2s;
}

.submit-btn:hover {
  background-color: #059669;
}

/* 深色模式 (Dark Mode) 覆寫樣式 */

.dark-mode .page-title { color: #f3f4f6; }
.dark-mode .divider { background: #374151; }

.dark-mode .theme-toggle-btn {
  border-color: #4b5563;
  color: #d1d5db;
}
.dark-mode .theme-toggle-btn:hover {
  background-color: #374151;
}

.dark-mode .note-card, 
.dark-mode .modal-content {
  background: #1f2937; /* 卡片深色背景 */
  border-color: #374151;
}

.dark-mode .note-title, 
.dark-mode .modal-title {
  color: #f9fafb; /* 標題白字 */
}

.dark-mode .note-content, 
.dark-mode .modal-body {
  color: #9ca3af; /* 內文淺灰字 */
}

.dark-mode .note-type {
  background-color: #075985; /* 標籤深藍背景 */
  color: #bae6fd; /* 標籤淺藍字 */
}

.dark-mode .form-group label {
  color: #d1d5db;
}

.dark-mode .form-input {
  background-color: #374151;
  border-color: #4b5563;
  color: #f9fafb;
}

/* 讓 Vue 直接控制整個網頁最底層 (body) 的顏色 */
:global(body) {
  margin: 0;
  background-color: #f9fafb; /* 淺色底 */
  transition: background-color 0.3s ease;
}

:global(body.dark-mode-body) {
  background-color: #111827; /* 深色底 */
}

</style>