<template>
  <div class="form-load-error">
    <div class="error-content">
      <IconSupport class="error-icon" />
      <p class="error-message">{{ errorMessage }}</p>
      <button 
        class="retry-button" 
        @click="$emit('retry')"
        :disabled="isLoading"
      >
        {{ isLoading ? '加载中...' : '点击重试' }}
      </button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    errorMessage: {
      type: String,
      required: true,
      default: '表单加载失败，请重试'
    },
    isLoading: {
      type: Boolean,
      required: false,
      default: false
    }
  }
}
</script>

<style scoped>
.form-load-error {
  padding: 2rem;
  text-align: center;
}

.error-icon {
  width: 64px;
  height: 64px;
  color: #ff4d4f;
  margin-bottom: 1rem;
}

.error-message {
  font-size: 1.2rem;
  color: #ff4d4f;
  margin-bottom: 1.5rem;
}

.retry-button {
  padding: 0.6rem 1.5rem;
  background-color: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.retry-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>