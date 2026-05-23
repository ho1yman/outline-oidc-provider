<template>
  <div class="preview-wrapper">
    <div class="preview-card">
      <h1>saved-account-item 预览</h1>
      <p class="desc">用于本地查看样式效果，不参与登录逻辑。</p>

      <div class="saved-accounts">
        <div class="saved-account-item active">
          <span class="avatar">O</span>
          <span class="saved-username">oxyless</span>
          <span class="saved-realm">outline</span>
        </div>
        <div class="saved-account-item">
          <span class="avatar">A</span>
          <span class="saved-username">admin</span>
          <span class="saved-realm">docs</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.preview-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: #efebf6;
}

.preview-card {
  width: 100%;
  max-width: 440px;
  padding: 28px;
  border-radius: 18px;
  background: #f7f3fb;
  box-shadow: 0 6px 20px rgba(29, 27, 32, 0.12);
}

h1 {
  margin: 0 0 8px;
  font-size: 1.2rem;
  color: #241d33;
}

.desc {
  margin: 0 0 16px;
  font-size: 0.9rem;
  color: #5b5468;
}

.saved-accounts {
  margin-bottom: 20px;
  border: 1px solid transparent;
  border-radius: 16px;
  overflow: hidden;
  background: color-mix(in srgb, #f3edf7 92%, white 8%);
}

.saved-account-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  cursor: pointer;
  transition: background 0.2s ease, transform 0.2s ease;
  border: 1px solid color-mix(in srgb, #79747e 10%, transparent);
  background: color-mix(in srgb, #f3edf7 90%, white 10%);
}

.saved-account-item:hover,
.saved-account-item.active {
  background: color-mix(in srgb, #6750a4 20%, white 80%);
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: color-mix(in srgb, #6750a4 18%, white 82%);
  color: #6750a4;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 15px;
  flex-shrink: 0;
}

.saved-username {
  font-weight: 500;
  color: #5f4b8b;
  flex: 1;
}

.saved-realm {
  font-size: 12px;
  color: #5f4b8b;
}
</style>
