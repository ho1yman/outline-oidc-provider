<template>
  <div class="login-wrapper">
    <div class="login-container">
      <div class="login-card">
        <div class="card-header" :class="headerToneClass">
          <h1 class="title">登录到 OutlineWiki</h1>
        </div>

        <div class="saved-accounts">
          <div
            class="saved-account-item"
            :class="{ active: selectedSavedIdx === 0 }"
            @click="selectSavedAccount(0)"
          >
            <span class="avatar">O</span>
            <span class="saved-username">oxyless</span>
            <span class="saved-realm">outline</span>
          </div>
          <div
            class="saved-account-item"
            :class="{ active: selectedSavedIdx === 1 }"
            @click="selectSavedAccount(1)"
          >
            <span class="avatar">A</span>
            <span class="saved-username">admin</span>
            <span class="saved-realm">docs</span>
          </div>
        </div>

        <form class="login-form" @submit.prevent>
          <div class="form-group">
            <input
              id="username"
              v-model="form.username"
              type="text"
              placeholder="请输入用户名"
              autocomplete="username"
            />
          </div>

          <div class="form-group">
            <div class="password-field">
              <input
                id="password"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                autocomplete="current-password"
              />
              <button
                type="button"
                class="password-toggle"
                :aria-label="showPassword ? '隐藏密码' : '显示密码'"
                :title="showPassword ? '隐藏密码' : '显示密码'"
                @mousedown.prevent="showPassword = true"
                @mouseup.prevent="showPassword = false"
                @mouseleave="showPassword = false"
                @touchstart.prevent="showPassword = true"
                @touchend="showPassword = false"
                @keydown.space.prevent="showPassword = true"
                @keyup.space.prevent="showPassword = false"
                @keydown.enter.prevent="showPassword = true"
                @keyup.enter.prevent="showPassword = false"
              >
                <svg
                  v-if="showPassword"
                  class="eye-icon"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                  aria-hidden="true"
                >
                  <path
                    d="M3 3L21 21M10.58 10.58C10.21 10.95 10 11.46 10 12C10 13.1 10.9 14 12 14C12.54 14 13.05 13.79 13.42 13.42M9.88 5.09C10.57 4.89 11.28 4.78 12 4.78C16.5 4.78 20.34 8.06 21.5 12C21.07 13.45 20.25 14.74 19.16 15.75M14.12 18.91C13.43 19.11 12.72 19.22 12 19.22C7.5 19.22 3.66 15.94 2.5 12C3.11 9.92 4.47 8.15 6.31 6.99"
                    stroke="currentColor"
                    stroke-width="1.8"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                </svg>
                <svg
                  v-else
                  class="eye-icon"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                  aria-hidden="true"
                >
                  <path
                    d="M2.5 12C3.66 8.06 7.5 4.78 12 4.78C16.5 4.78 20.34 8.06 21.5 12C20.34 15.94 16.5 19.22 12 19.22C7.5 19.22 3.66 15.94 2.5 12Z"
                    stroke="currentColor"
                    stroke-width="1.8"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  />
                  <circle
                    cx="12"
                    cy="12"
                    r="3"
                    stroke="currentColor"
                    stroke-width="1.8"
                  />
                </svg>
              </button>
            </div>
          </div>

          <button type="submit" class="login-btn">
            <span>登录</span>
          </button>

          <div class="form-options">
            <label class="remember-me">
              <input v-model="form.rememberMe" type="checkbox" />
              <span>记住我</span>
            </label>
            <label class="auto-login" v-if="form.rememberMe">
              <input v-model="form.autoLogin" type="checkbox" />
              <span>下次自动登录</span>
            </label>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'

const showPassword = ref(false)
const selectedSavedIdx = ref(0)

const form = reactive({
  username: 'oxyless',
  password: 'password123',
  rememberMe: true,
  autoLogin: false
})

const headerBgColor = ref('#f2f4f8')

const headerToneClass = computed(() => {
  const hex = headerBgColor.value.replace('#', '')
  const normalized = hex.length === 3
    ? hex.split('').map(ch => ch + ch).join('')
    : hex

  if (!/^[0-9a-fA-F]{6}$/.test(normalized)) {
    return 'is-dark'
  }

  const r = parseInt(normalized.slice(0, 2), 16)
  const g = parseInt(normalized.slice(2, 4), 16)
  const b = parseInt(normalized.slice(4, 6), 16)
  const luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255
  return luminance < 0.6 ? 'is-dark' : 'is-light'
})

function selectSavedAccount(idx: number) {
  selectedSavedIdx.value = idx
  if (idx === 0) {
    form.username = 'oxyless'
    form.password = 'password123'
  } else {
    form.username = 'admin'
    form.password = 'admin123'
  }
}
</script>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  transition: background 0.3s ease;
  background-image:
    radial-gradient(circle at 8% 10%, rgba(103, 80, 164, 0.14) 0, rgba(103, 80, 164, 0) 40%),
    radial-gradient(circle at 92% 90%, rgba(56, 142, 60, 0.1) 0, rgba(56, 142, 60, 0) 38%),
    url("http://minio.outline.xyz:10087/outline-bucket/public/ubuntu_by_arman1992.jpg");
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}

.login-container {
  width: 100%;
  max-width: 440px;
}

.login-card {
  --md-sys-color-primary: #6750a4;
  --md-sys-color-on-primary: #ffffff;
  --md-sys-color-surface: #fffbfe;
  --md-sys-color-surface-container: #f3edf7;
  --md-sys-color-on-surface: #1d1b20;
  --md-sys-color-on-surface-variant: #49454f;
  --md-sys-color-outline: #79747e;
  --md-sys-elevation-1: 0 1px 2px rgba(29, 27, 32, 0.14), 0 1px 3px 1px rgba(29, 27, 32, 0.08);
  --md-sys-elevation-3: 0 4px 8px 3px rgba(29, 27, 32, 0.12), 0 1px 3px rgba(29, 27, 32, 0.14);
  background:
    linear-gradient(
      165deg,
      rgba(255, 255, 255, 0.12) 0%,
      rgba(255, 255, 255, 0.07) 38%,
      rgba(255, 255, 255, 0.04) 100%
    );
  border-radius: 28px;
  padding: 36px 28px 28px;
  
  backdrop-filter: blur(6px) saturate(112%);
  -webkit-backdrop-filter: blur(6px) saturate(112%);
}

.card-header {
  text-align: center;
  margin-bottom: 10px;
  padding: 0;
  border-radius: 16px;
}

.card-header.is-dark {
  --header-text-main: #f5f8ff;
  --header-text-sub: rgba(242, 247, 255, 0.9);
}

.card-header.is-light {
  --header-text-main: #1b2330;
  --header-text-sub: rgba(34, 44, 58, 0.85);
}

.title {
  font-size: 1.65rem;
  line-height: 1.2;
  font-weight: 700;
  color: var(--header-text-main, var(--md-sys-color-on-surface));
  text-shadow: 0 1px 8px color-mix(in srgb, var(--header-text-main, #111) 18%, transparent);
  letter-spacing: 0.01em;
  margin: 0;
}

.subtitle {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 0.82rem;
  color: var(--md-sys-color-on-surface-variant);
  margin: 0;
  padding: 6px 10px;
  border-radius: 999px;
  background: var(--md-sys-color-surface-container);
}

.saved-accounts {
  margin-bottom: 10px;
  border: 1px solid transparent;
  border-radius: 16px;
  overflow: visible;
  background: transparent;
}

.saved-account-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  position: relative;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease, transform 0.2s ease;
  border: 1px solid transparent;
  background: rgba(255, 255, 255, 0.12);
}

.saved-account-item + .saved-account-item {
  margin-top: -1px;
}

.saved-account-item:last-child {
  border-bottom: none;
}

.saved-account-item:first-child {
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
}

.saved-account-item:last-child {
  border-bottom-left-radius: 16px;
  border-bottom-right-radius: 16px;
}

.saved-account-item:hover {
  background: rgba(255, 255, 255, 0.08);
}

.saved-account-item.active {
  border-color: transparent;
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--md-sys-color-primary) 24%, transparent);
  background: color-mix(in srgb, var(--md-sys-color-surface-container) 86%, white 14%);
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: color-mix(in srgb, var(--md-sys-color-primary) 18%, white 82%);
  color: var(--md-sys-color-primary);
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

.login-form {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.form-group input {
  min-height: 56px;
  padding: 12px 16px;
  border: 1px solid transparent;
  border-radius: 16px;
  font-size: 15px;
  color: var(--md-sys-color-on-surface);
  background: rgba(255, 255, 255, 0.12);
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
  outline: none;
}

.form-group:first-child input {
  border-bottom-left-radius: 1.6px;
  border-bottom-right-radius: 1.6px;
}

.form-group input::placeholder {
  color: color-mix(in srgb, var(--md-sys-color-on-surface-variant) 78%, white 22%);
}

.form-group input:focus {
  border-color: transparent;
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--md-sys-color-primary) 24%, transparent);
  background: color-mix(in srgb, var(--md-sys-color-surface-container) 86%, white 14%);
}

.password-field {
  position: relative;
  display: flex;
  align-items: center;
  margin-top: -1px;
}

.password-field input {
  width: 100%;
  padding-right: 70px;
  border-radius: 1.6px;
}

.password-toggle {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  border: none;
  background: color-mix(in srgb, var(--md-sys-color-primary) 11%, rgba(255, 255, 255, 0) 89%);
  color: var(--md-sys-color-primary);
  width: 36px;
  height: 36px;
  padding: 0;
  border-radius: 50%;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s ease, transform 0.15s ease, opacity 0.2s ease;
}

.password-toggle:hover {
  background: color-mix(in srgb, var(--md-sys-color-primary) 18%, white 82%);
}

.password-toggle:active {
  transform: translateY(-50%) scale(0.96);
}

.eye-icon {
  width: 20px;
  height: 20px;
}

.form-options {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 8px;
  margin-bottom: 0;
}

.remember-me,
.auto-login {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #ffffff;
  cursor: pointer;
  user-select: none;
}

.remember-me span,
.auto-login span {
  text-shadow: 0 1px 6px rgba(119, 0, 146, 0.929);
}

.remember-me input,
.auto-login input {
  appearance: none;
  -webkit-appearance: none;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  border: 2px solid #7b57d1;
  background: #ffffff;
  display: inline-block;
  position: relative;
  cursor: pointer;
  transition: background 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}

.remember-me input:checked,
.auto-login input:checked {
  background: #2fbf5b;
  border-color: #7b57d1;
  box-shadow: inset 0 0 0 2px #2fbf5b;
}

.login-btn {
  min-height: 52px;
  padding: 12px 18px;
  border: none;
  border-radius: 14px;
  color: #5f4b8b;
  background: #b89df2;
  border: 1px solid rgba(255, 255, 255, 0.45);
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.02em;
  cursor: pointer;
  box-shadow: var(--md-sys-elevation-1);
  transition: transform 0.15s ease, box-shadow 0.2s ease, filter 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 0;
  margin-top: -1px;
  border-top-left-radius: 1.4px;
  border-top-right-radius: 1.4px;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(29, 27, 32, 0.16), 0 1px 2px rgba(29, 27, 32, 0.1);
  filter: brightness(1.03);
}

.login-btn:active {
  transform: translateY(0);
}

@media (max-width: 480px) {
  .login-card {
    border-radius: 24px;
    padding: 28px 18px 20px;
  }

  .title {
    font-size: 1.4rem;
  }

  .form-group input {
    min-height: 52px;
  }

  .login-btn {
    min-height: 50px;
    border-radius: 12px;
    border-top-left-radius: 1.2px;
    border-top-right-radius: 1.2px;
  }

  .login-form {
    gap: 0;
  }
}
</style>
