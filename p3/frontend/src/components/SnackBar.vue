<template>
    <v-snackbar v-model="snackbar.isVisible" multi-line dark :color="snackbar.color" location="top end" timeout="2000"
        v-bind="$attrs" v-on="$listeners">
        <v-layout align-center pr-4>
            <v-icon class="pr-3" dark large :icon="snackbar.icon" size="50"></v-icon>
            <v-layout column>
                <div class="snackbar-content pt-4">{{ snackbar.content }}</div>
            </v-layout>
        </v-layout>
        <template #action="{ attrs }">
            <v-btn text v-bind="attrs" @click="closeSnackbar">Close</v-btn>
        </template>
    </v-snackbar>
</template>

<script setup>
import { useSharedStore } from '@/store';
const store = useSharedStore();
const snackbar = computed(() => store.snackbar);
const closeSnackbar = () => {
    store.setSnackbar({
        content: '',
        color: '',
        icon: '',
        isVisible: false
    });
};
</script>
<style scoped>
.snackbar-content {
    font-size: 1rem;
    font-weight: 500;
}
</style>