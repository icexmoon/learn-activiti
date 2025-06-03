<script setup>
import HelloWorld from './components/HelloWorld.vue'
import TheWelcome from './components/TheWelcome.vue'
import { inject } from 'vue'

const stagewiseConfig = inject('stagewiseConfig')
const isDev = import.meta.env.DEV
</script>

<template>
  <router-view />
  <StagewiseToolbar v-if="isDev" :config="stagewiseConfig" />
</template>

<style>
body {
  margin: 0;
  font-family: 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
  background: #f5f7fa;
}
</style>
