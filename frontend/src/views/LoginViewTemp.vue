<template>
  <div class="debug-wrapper">
    <video
      src="http://minio.outline.xyz:10087/outline-bucket/public/landing.mp4"
      autoplay loop muted playsinline
      class="bg-video"
    ></video>
    <div class="login-container">
      <div class="login-card">
        <div class="card-header">
          <h1 class="title">登录 OutlineWiki</h1>
        </div>

        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <input
              v-model="form.username"
              type="text"
              placeholder="请输入用户名"
              autocomplete="username"
              required
              maxlength="25"
              :disabled="loading"
            />
          </div>

          <div class="form-group">
            <div class="password-field">
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                autocomplete="current-password"
                required
                maxlength="25"
                :disabled="loading"
              />
              <button
                type="button"
                class="password-toggle"
                :disabled="loading"
                @mousedown.prevent="showPassword = true"
                @mouseup.prevent="showPassword = false"
                @mouseleave="showPassword = false"
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

          <div v-if="errorMsg" class="error-message">{{ errorMsg }}</div>

          <button type="submit" class="login-btn" :disabled="loading">
            <span v-if="loading" class="spinner"></span>
            <span v-else>登录</span>
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { login } from '@/api/auth'

const route = useRoute()

interface LoginForm {
  username: string
  password: string
}

const form = reactive<LoginForm>({
  username: '',
  password: ''
})

const loading = ref(false)
const errorMsg = ref('')
const showPassword = ref(false)

async function handleLogin() {
  errorMsg.value = ''
  loading.value = true

  try {
    const q = route.query
    const result = await login({
      username: form.username,
      password: form.password,
      realm: (q.realm as string) || 'outline',
      clientId: (q.client_id as string) || 'outline',
      redirectUri: (q.redirect_uri as string) || '',
      state: (q.state as string) || '',
      scope: (q.scope as string) || 'openid',
      responseType: (q.response_type as string) || 'code',
      nonce: (q.nonce as string) || '',
      rememberMe: false
    })

    if (result.success) {
      window.location.href = result.redirectUrl
    }
  } catch (err: any) {
    errorMsg.value = err?.response?.data?.error || '登录失败，请检查登录用户名和密码是否正确！'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.debug-wrapper {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  overflow: hidden;
}

.bg-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
  pointer-events: none;
}

.login-container {
  position: relative;
  z-index: 1;
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
  --md-sys-color-error: #ba1a1a;
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
}

.title {
  font-size: 1.65rem;
  line-height: 1.2;
  font-weight: 700;
  color: var(--md-sys-color-on-surface);
  text-shadow: 0 1px 8px color-mix(in srgb, var(--md-sys-color-on-surface) 18%, transparent);
  letter-spacing: 0.01em;
  margin: 0;
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

.form-group input:disabled {
  background: color-mix(in srgb, var(--md-sys-color-surface-container) 76%, #f4f4f4 24%);
  cursor: not-allowed;
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

.password-toggle:hover:not(:disabled) {
  background: color-mix(in srgb, var(--md-sys-color-primary) 18%, white 82%);
}

.password-toggle:active:not(:disabled) {
  transform: translateY(-50%) scale(0.96);
}

.password-toggle:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.eye-icon {
  width: 20px;
  height: 20px;
}

.error-message {
  padding: 12px 14px;
  background: color-mix(in srgb, var(--md-sys-color-error) 9%, white 91%);
  border: 1px solid color-mix(in srgb, var(--md-sys-color-error) 36%, transparent);
  border-radius: 1.6px;
  color: var(--md-sys-color-error);
  font-size: 13px;
  text-align: center;
  margin-top: 2px;
  margin-bottom: 4px;
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
  margin-top: -1px;
  border-top-left-radius: 1.4px;
  border-top-right-radius: 1.4px;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(29, 27, 32, 0.16), 0 1px 2px rgba(29, 27, 32, 0.1);
  filter: brightness(1.03);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
}

.login-btn:disabled {
  opacity: 0.68;
  cursor: not-allowed;
}

.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.35);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
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
}
</style>
