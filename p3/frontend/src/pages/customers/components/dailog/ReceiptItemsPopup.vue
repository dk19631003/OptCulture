<script setup lang="ts">
import { isEmpty } from '@/@core/utils'
import ReceiptItemsList from '@/views/ereceipts/ReceiptItemsList.vue'
import type { EreceiptType } from '@/@fake-db/types'
interface Emit {
    (e: 'update:isDialogVisible', value: boolean): void
}

interface Props {
    isDialogVisible: boolean,
    data?: EreceiptType
}


const props = defineProps<Props>()
const emit = defineEmits<Emit>()

const dialogModelValueUpdate = (val: boolean) => {
    emit('update:isDialogVisible', val)
}
function loadReceipt() {
    const params = {

    }
}
</script>
<template>
    <VDialog :width="$vuetify.display.smAndDown ? 'auto' : 400" :model-value="props.isDialogVisible"
        @update:model-value="dialogModelValueUpdate" :min-height="400">
        <!-- Dialog close btn -->
        <DialogCloseBtn @click="dialogModelValueUpdate(false)" />
        <ReceiptItemsList :data="props.data" />
    </VDialog>
</template>
