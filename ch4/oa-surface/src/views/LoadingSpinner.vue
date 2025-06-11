<template>
    <div 
      v-if="visible" 
      class="loading-spinner" 
      :style="{ background: background }"
    >
      <div class="spinner" :style="spinnerStyle"></div>
      <div v-if="text" class="loading-text">{{ text }}</div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'LoadingSpinner',
    props: {
      // 控制显示/隐藏
      visible: {
        type: Boolean,
        default: true
      },
      // 加载提示文本
      text: {
        type: String,
        default: '加载中...'
      },
      // 背景颜色（支持透明背景）
      background: {
        type: String,
        default: 'rgba(255, 255, 255, 0.8)'
      },
      // 尺寸（单位：px）
      size: {
        type: Number,
        default: 60
      },
      // 旋转器颜色
      color: {
        type: String,
        default: '#3498db'
      },
      // 文本颜色
      textColor: {
        type: String,
        default: '#333'
      }
    },
    computed: {
      spinnerStyle() {
        return {
          width: `${this.size}px`,
          height: `${this.size}px`,
          borderColor: `${this.color} transparent transparent transparent`
        };
      }
    }
  }
  </script>
  
  <style scoped>
  .loading-spinner {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    z-index: 9999;
  }
  
  .spinner {
    border: 4px solid;
    border-radius: 50%;
    animation: spin 1.2s linear infinite;
  }
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
  
  .loading-text {
    margin-top: 15px;
    font-size: 16px;
    color: v-bind(textColor);
  }
  </style>