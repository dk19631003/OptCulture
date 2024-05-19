<script setup lang="ts">
const version = __APP_VERSION__
</script>
<template>
  <div class="h-100 d-flex align-center justify-space-between">
    <!-- 👉 Footer: left content -->
    <span class="d-flex align-center">
      &copy;
      {{ new Date().getFullYear() }} OptCulture.
    </span>
    <VSpacer />

    <!-- 👉 Footer: right content -->
    <VSpacer />
    <span>
      Version {{ version }}
    </span>
    <!-- <VSpacer />
    <span class="d-md-flex gap-x-4 text-primary d-none">
      <a href="#" target="noopener noreferrer">Terms of use.</a>
    </span> -->
  </div>
</template>
