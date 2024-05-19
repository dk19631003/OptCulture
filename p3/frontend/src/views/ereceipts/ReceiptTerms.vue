<script setup lang="ts">
import type { EreceiptType } from '@/@fake-db/types'

interface Props {
  data?: EreceiptType
}

const props = withDefaults(defineProps<Props>(), {
  data: {
    termsAndConditions: {
      "content": "N/A"
    }
  }
});
let show1: false;
const termsConditions = props.data?.termsAndConditions.content;
</script>

<template>
  <VCard class="my-2" v-if="data.termsAndConditions" elevation="0">
    <div>
      <div class="d-flex justify-space-between justify-center px-4 mt-3">
        <p class="wr-text-h1">Terms & Conditions</p>
        <VIcon size="30" :icon="props.data.show1 ? 'mdi-chevron-up' : 'mdi-chevron-down'" class="flex-end"
          @click="props.data.show1 = !(props.data.show1)">
        </VIcon>
      </div>
      <div v-show=props.data.show1>
        <p v-html="termsConditions" class="ml-8 pr-2"></p>
      </div>
      <!-- <VExpansionPanels>
        <VExpansionPanel class="text-center" title="Terms and Conditions" :text="data.termsAndConditions.content">
        </VExpansionPanel>
      </VExpansionPanels> -->
    </div>
  </VCard>
</template>
