<script setup lang="ts">
import { EreceiptType } from '@/@fake-db/types'
const text = 'View your previous transactions with us'
interface Props {
  data?: EreceiptType
}
const props = defineProps<Props>()
const url = props.data?.branding.urlStr
</script>
<template>
  <VCard class="my-2 text-center" v-if="url">
    <VCardText class="px-1 px-sm-6">
      <VBtn append-icon="tabler-arrow-right" color="primary" :href="url">
        <span class="d-flex d-sm-none">See previous transactions</span>
        <span class="d-none d-sm-flex">{{ text }}</span>
      </VBtn>
    </VCardText>
  </VCard>
</template>
