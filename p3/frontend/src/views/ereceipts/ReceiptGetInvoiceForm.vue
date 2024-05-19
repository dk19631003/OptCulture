<script setup lang="ts">
import { EreceiptType } from '@/@fake-db/types';


interface Props {
  data?: EreceiptType
}

const props = withDefaults(defineProps<Props>(), {
  data: {
    receipt: {
      "downloadReceiptLink": "NA"
    },
  }
});

</script>
<template>
  <VCard class="my-2 text-center" v-if="data.receipt.downloadReceiptLink" elevation="0">
    <VCardTitle>
      <VBtn width="100%" class="wr-text-h2" :href="data.receipt.downloadReceiptLink">
        Download Invoice
      </VBtn>
    </VCardTitle>

    <!-- <VCardSubtitle class="d-flex gap-4 align-center justify-center wr-text-h2">
      Or send the invoice to your email
    </VCardSubtitle>
    <VCardText class="d-flex align-center justify-center">
      <AppTextField clearable type="text" class="mr-4 textfield-demo-icon-slot" placeholder="Enter Email Address">
      </AppTextField>
      <VBtn variant="tonal">
        <VIcon icon="tabler-send" />
        <span class="ms-3 wr-text-h2">Send</span>
      </VBtn> 
    </VCardText>-->
  </VCard>
</template>
