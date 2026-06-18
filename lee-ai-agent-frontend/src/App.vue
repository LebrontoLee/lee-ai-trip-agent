<template>
  <div id="app-root">
    <router-view v-slot="{ Component, route }">
      <transition :name="route.meta.transition || 'fade'" mode="out-in">
        <component :is="Component" :key="route.path" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
</script>

<style scoped>
#app-root {
  width: 100%;
  min-height: 100vh;
}
</style>
