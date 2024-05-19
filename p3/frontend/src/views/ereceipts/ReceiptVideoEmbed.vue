<script setup lang="ts">
import { ref, computed } from 'vue';
import { useSharedStore } from '@/store';
interface Props {
  data?: EreceiptType
  itemData: {
    "id": 0, "name": "", "configuration": {
    }
  },
}

const props = withDefaults(defineProps<Props>(), {
  itemData: {
    "id": 0, "name": "", "configuration": {}
  },
});
const modifiedSrc = computed(() => {
  // Get the src data from the Pinia store
  const store = useSharedStore();
  return props?.itemData?.configuration?.url || store?.ReceiptConfiguration?.ReceiptVideoEmbed?.url || 'https://www.youtube.com/embed/N7q7DixNLv0?si=sf3vUdcDPLiK__wI&amp;controls=0'
});
</script>

<template>
  <v-card class="my-2 text-center" elevation="0">
    <v-card-text>
      <p class="d-none d-sm-flex">
        <iframe width="400" height="225px" :src="modifiedSrc ? modifiedSrc : ''" frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; " allowfullscreen>
        </iframe>
      </p>
      <p class="d-flex d-sm-none">
        <iframe class="w-100 h-100" :src="modifiedSrc ? modifiedSrc : ''" frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; " allowfullscreen>
        </iframe>
      </p>
    </v-card-text>
  </v-card>
</template>