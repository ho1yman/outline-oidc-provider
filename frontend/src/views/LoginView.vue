<template>
  <div class="login-wrapper" :style="wrapperStyle">
    <div class="login-container">
      <div class="login-card">
        <div class="card-header">
          <img v-if="theme.logoUrl" :src="theme.logoUrl" class="logo-img" alt="logo" />
          <h1 class="title">{{ theme.title }}</h1>
          <p class="subtitle" v-if="realmName">租户：{{ realmName }}</p>
        </div>

        <div v-if="savedCredentials.length > 0" class="saved-accounts">
          <div
            v-for="(cred, idx) in savedCredentials"
            :key="idx"
            class="saved-account-item"
            :class="{ active: selectedSavedIdx === idx }"
            @click="selectSavedAccount(idx)"
          >
            <span class="avatar">{{ cred.username.charAt(0).toUpperCase() }}</span>
            <span class="saved-username">{{ cred.username }}</span>
            <span class="saved-realm">{{ cred.realm }}</span>
          </div>
        </div>

        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label for="username">用户名</label>
            <input
              id="username"
              v-model="form.username"
              type="text"
              placeholder="请输入用户名"
              autocomplete="username"
              required
              :disabled="loading"
            />
          </div>

          <div class="form-group">
            <label for="password">密码</label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              autocomplete="current-password"
              required
              :disabled="loading"
            />
          </div>

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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { login, getThemeConfig, type ThemeConfig } from '@/api/auth'

const route = useRoute()

interface SavedCredential {
  username: string
  password: string
  realm: string
  timestamp: number
}

interface LoginForm {
  username: string
  password: string
  rememberMe: boolean
  autoLogin: boolean
}

const form = reactive<LoginForm>({
  username: '',
  password: '',
  rememberMe: false,
  autoLogin: false
})

const loading = ref(false)
const errorMsg = ref('')
const savedCredentials = ref<SavedCredential[]>([])
const selectedSavedIdx = ref(-1)
const realmName = ref('')

const theme = reactive<ThemeConfig>({
  backgroundImage: '',
  backgroundColor: '#f0f2f5',
  logoUrl: '',
  title: '登录到 OutlineWiki',
  primaryColor: '#1890ff'
})

const wrapperStyle = computed(() => {
  const style: Record<string, string> = {
    background: theme.backgroundColor
  }
  if (theme.backgroundImage) {
    style.backgroundImage = `url('${theme.backgroundImage}')`
    style.backgroundSize = 'cover'
    style.backgroundPosition = 'center'
  }
  return style
})

onMounted(async () => {
  // Parse OIDC parameters from URL
  const q = route.query
  if (q.realm) realmName.value = q.realm as string

  // Load theme
  try {
    const config = await getThemeConfig()
    Object.assign(theme, config)
    if (config.title) document.title = config.title
  } catch {
    // Use defaults
  }

  // Load saved credentials
  loadSavedCredentials()

  // Check for auto-login
  if (savedCredentials.value.length > 0) {
    const autoLoginCred = savedCredentials.value.find(c => {
      const stored = localStorage.getItem(`oidc_auto_${c.realm}_${c.username}`)
      return stored === 'true'
    })
    if (autoLoginCred) {
      form.username = autoLoginCred.username
      form.password = autoLoginCred.password
      form.rememberMe = true
      form.autoLogin = true
      handleLogin()
    }
  }
})

function loadSavedCredentials() {
  try {
    const raw = localStorage.getItem('oidc_saved_credentials')
    if (raw) {
      savedCredentials.value = JSON.parse(raw)
    }
  } catch {
    savedCredentials.value = []
  }
}

function saveCredentials() {
  if (!form.rememberMe) return

  const existing = savedCredentials.value.findIndex(
    c => c.username === form.username && c.realm === realmName.value
  )
  const cred: SavedCredential = {
    username: form.username,
    password: form.password,
    realm: realmName.value,
    timestamp: Date.now()
  }

  if (existing >= 0) {
    savedCredentials.value[existing] = cred
  } else {
    savedCredentials.value.push(cred)
  }

  // Keep max 5 saved accounts
  if (savedCredentials.value.length > 5) {
    savedCredentials.value.sort((a, b) => b.timestamp - a.timestamp)
    savedCredentials.value = savedCredentials.value.slice(0, 5)
  }

  localStorage.setItem('oidc_saved_credentials', JSON.stringify(savedCredentials.value))

  // Auto login flag
  if (form.autoLogin) {
    localStorage.setItem(`oidc_auto_${realmName.value}_${form.username}`, 'true')
  }
}

function selectSavedAccount(idx: number) {
  const cred = savedCredentials.value[idx]
  form.username = cred.username
  form.password = cred.password
  form.rememberMe = true
  selectedSavedIdx.value = idx
}

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
      rememberMe: form.rememberMe
    })

    if (result.success) {
      saveCredentials()
      // Redirect to the OIDC callback
      window.location.href = result.redirectUrl
    }
  } catch (err: any) {
    const msg = err?.response?.data?.error || '登录失败，请检查登录用户名和密码是否正确！'
    errorMsg.value = msg
  } finally {
    loading.value = false
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
    radial-gradient(circle at 92% 90%, rgba(56, 142, 60, 0.1) 0, rgba(56, 142, 60, 0) 38%);
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
  --md-sys-color-error: #ba1a1a;
  --md-sys-elevation-1: 0 1px 2px rgba(29, 27, 32, 0.14), 0 1px 3px 1px rgba(29, 27, 32, 0.08);
  --md-sys-elevation-3: 0 4px 8px 3px rgba(29, 27, 32, 0.12), 0 1px 3px rgba(29, 27, 32, 0.14);
  background: color-mix(in srgb, var(--md-sys-color-surface) 88%, white 12%);
  border-radius: 28px;
  padding: 36px 28px 28px;
  box-shadow: var(--md-sys-elevation-3);
  border: 1px solid color-mix(in srgb, var(--md-sys-color-outline) 24%, transparent);
  backdrop-filter: blur(6px);
}

.card-header {
  text-align: center;
  margin-bottom: 26px;
}

.logo-img {
  max-height: 48px;
  margin-bottom: 14px;
}

.title {
  font-size: 1.65rem;
  line-height: 1.2;
  font-weight: 700;
  color: var(--md-sys-color-on-surface);
  letter-spacing: 0.01em;
  margin: 0 0 8px;
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
  margin-bottom: 20px;
  border: 1px solid color-mix(in srgb, var(--md-sys-color-outline) 36%, transparent);
  border-radius: 16px;
  overflow: hidden;
  background: var(--md-sys-color-surface-container);
}

.saved-account-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  cursor: pointer;
  transition: background 0.2s ease, transform 0.2s ease;
  border-bottom: 1px solid color-mix(in srgb, var(--md-sys-color-outline) 20%, transparent);
}

.saved-account-item:last-child {
  border-bottom: none;
}

.saved-account-item:hover,
.saved-account-item.active {
  background: color-mix(in srgb, var(--md-sys-color-primary) 16%, white 84%);
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
  color: var(--md-sys-color-on-surface);
  flex: 1;
}

.saved-realm {
  font-size: 12px;
  color: var(--md-sys-color-on-surface-variant);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--md-sys-color-on-surface-variant);
  padding-left: 4px;
}

.form-group input {
  min-height: 56px;
  padding: 12px 16px;
  border: 1px solid var(--md-sys-color-outline);
  border-radius: 16px;
  font-size: 15px;
  color: var(--md-sys-color-on-surface);
  background: color-mix(in srgb, var(--md-sys-color-surface-container) 72%, white 28%);
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
  outline: none;
}

.form-group input::placeholder {
  color: color-mix(in srgb, var(--md-sys-color-on-surface-variant) 78%, white 22%);
}

.form-group input:focus {
  border-color: var(--md-sys-color-primary);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--md-sys-color-primary) 24%, transparent);
  background: #fff;
}

.form-group input:disabled {
  background: color-mix(in srgb, var(--md-sys-color-surface-container) 76%, #f4f4f4 24%);
  cursor: not-allowed;
}

.form-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.remember-me,
.auto-login {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--md-sys-color-on-surface-variant);
  cursor: pointer;
  user-select: none;
}

.remember-me input,
.auto-login input {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.error-message {
  padding: 12px 14px;
  background: color-mix(in srgb, var(--md-sys-color-error) 9%, white 91%);
  border: 1px solid color-mix(in srgb, var(--md-sys-color-error) 36%, transparent);
  border-radius: 12px;
  color: var(--md-sys-color-error);
  font-size: 13px;
  text-align: center;
}

.login-btn {
  min-height: 48px;
  padding: 12px 18px;
  border: none;
  border-radius: 999px;
  color: var(--md-sys-color-on-primary);
  background: linear-gradient(
    160deg,
    color-mix(in srgb, var(--md-sys-color-primary) 92%, white 8%) 0%,
    color-mix(in srgb, var(--md-sys-color-primary) 76%, black 24%) 100%
  );
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.02em;
  cursor: pointer;
  box-shadow: var(--md-sys-elevation-1);
  transition: transform 0.15s ease, box-shadow 0.2s ease, filter 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: var(--md-sys-elevation-3);
  filter: saturate(1.06);
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
}
</style>
