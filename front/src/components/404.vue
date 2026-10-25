<template>
  <div class="not-found-container">
    <!-- 404 状态码 -->
    <h1 class="not-found-title">404</h1>
    <!-- 错误提示文案 -->
    <p class="not-found-message">抱歉，您访问的页面不存在。</p>
    <!-- 操作按钮区域 -->
    <div class="not-found-actions">
      <router-link to="/" class="not-found-link">返回首页</router-link>
    </div>
  </div>
</template>

<script>
// 404 页面组件
export default {

};
</script>

<style scoped>
.not-found-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  text-align: center;
}

.not-found-title {
  font-size: 6rem;
  color: #ff4d4f;
  margin: 0;
}

.not-found-message {
  font-size: 1.5rem;
  color: #666;
  margin: 1rem 0;
}

.not-found-actions {
  display: flex;
  gap: 1rem;
}

.not-found-link{
  padding: 0.5rem 1rem;
  font-size: 1rem;
  border: 2px solid #ff4d4f;
  border-radius: 5px;
  color: #ff4d4f;
  text-decoration: none;
  cursor: pointer;
  transition: background-color 0.3s, color 0.3s;
}

.not-found-link:hover{
  background-color: #ff4d4f;
  color: white;
}

.not-found-button {
  background: none;
}
</style>
