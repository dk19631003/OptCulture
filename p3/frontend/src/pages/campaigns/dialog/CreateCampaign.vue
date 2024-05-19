<script setup lang="ts">
import router from '@/router'

interface Emit {
    (e: 'update:isDialogVisible', value: boolean): void
}

interface Props {
    isDialogVisible: boolean,
}

const props = defineProps<Props>()
const emit = defineEmits<Emit>()
const dialogModelValueUpdate = (val: boolean) => {
    emit('update:isDialogVisible', val)
}
</script>
<template>
    <VDialog :width="$vuetify.display.smAndDown ? 'auto' : 780" :model-value="props.isDialogVisible"
        @update:model-value="dialogModelValueUpdate" :min-height="400">
        <!-- Dialog close btn -->
        <DialogCloseBtn @click="dialogModelValueUpdate(false)" />

        <VCard class="pa-5 pa-sm-8">
            <!-- 👉 Title -->
            <VCardItem class="text-center">
                <VCardTitle>
                    <div class="text-center text-body-2  text-wrap">
                        <span class="font-weight-medium text-h5 ma-3">Mode of
                            Communication</span>
                        <p>Choose an appropriate mode of communication to connect with your customers.</p>
                    </div>
                </VCardTitle>
            </VCardItem>
            <VRow>
                <VCol cols="12" lg="4" md="4" sm="4">
                    <VCard border flat @click="router.push('/campaigns/whatsapp')" class="pb-5" color="#98E5BA">
                        <VCardText class="text-center ">
                            <VIcon icon="tabler-brand-whatsapp" size="70"></VIcon>
                            <p class="text-h6 font-weight-medium">Whatsapp</p>
                            <p class="text-caption">The fastest way to get noticed and communicate</p>
                        </VCardText>
                    </VCard>
                </VCol>
                <VCol cols="12" lg="4" md="4" sm="4">
                    <VCard border flat @click="router.push('/campaigns/sms')" color="#F9D1A5">
                        <VCardText class="text-center">
                            <VIcon icon="tabler-message-2" size="70"></VIcon>
                            <p class="text-h6 font-weight-medium">SMS</p>
                            <p class="text-caption">Slide into your customer's inbox and grab more eyeballs</p>
                        </VCardText>
                    </VCard>
                </VCol>
                <VCol cols="12" lg="4" md="4" sm="4">
                    <VCard border flat @click="router.push('/campaigns/email')" color="#00CFE8">
                        <VCardText class="text-center">
                            <VIcon icon="tabler-mail" size="70"></VIcon>
                            <p class="text-h6 font-weight-medium">Email</p>
                            <p class="text-caption">Personalize your content land into your customer's mailbox </p>
                        </VCardText>
                    </VCard>
                </VCol>
            </VRow>
        </VCard>
    </VDialog>
</template>
