<template>
    <div v-if="isLoading" style="z-index: 9999 !important;">
        <v-progress-linear color="primary" indeterminate height="8px"></v-progress-linear>
    </div>
</template>

<script setup>
import { useSharedStore } from '@/store';
const store = useSharedStore();
const isLoading = computed(() => store.Loading.isLoading);
</script>
