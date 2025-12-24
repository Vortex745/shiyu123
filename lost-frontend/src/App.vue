<template>
  <router-view v-slot="{ Component }">
    <Transition name="page-fade" mode="out-in">
      <component :is="Component" />
    </Transition>
  </router-view>
</template>

<script setup>
// App.vue 只需要负责渲染路由
</script>

<style>
/* 这里可以放一些全局重置样式，或者保持为空，主要样式在 style.css */
</style>