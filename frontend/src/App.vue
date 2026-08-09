<script setup>
// JavaScript (Vue 的邏輯與變數)
import { ref, onMounted } from 'vue'; // 修正：v 改為小寫
import axios from 'axios'; // 修正：avios 改為 axios

// 準備一個響應式變數，用來裝後端回傳的筆記陣列 (初始值為空陣列)
const notes = ref([]);

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

// Vue 生命週期：當網頁元件一載入 (Mounted) 時，就自動執行撈取函式
onMounted(() => {
  fetchNotes();
});
</script>

<template>
  <!-- HTML (畫面結構) -->
  <div>
    <h1>我的筆記系統</h1>
    <hr>
    <h3>從後端撈回來的原始資料：</h3>

    <!-- 修正：boarder 改為 border -->
    <pre style="background: #f4f4f4; padding: 15px; border-radius: 8px;">
      {{ notes }}
    </pre>
  </div>
</template>

<style scoped>
/* CSS (專屬這個元件的樣式) */
</style> 