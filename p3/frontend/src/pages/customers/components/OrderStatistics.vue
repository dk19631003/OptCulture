<script setup lang="ts">
import { kFormatter } from '@/@core/utils/formatters'
import { getCurrencySymbol } from '@/@core/utils/localeFormatter'
interface Props {
    title: string
    color?: string
    icon: string
    stats: string
}

const props = withDefaults(defineProps<Props>(), {
    color: 'primary',
})
const currencySymbol = getCurrencySymbol()
</script>

<template>
    <VCard>
        <VCardText class="d-flex flex-column align-center justify-center">
            <VAvatar v-if="props.icon" size="42" variant="tonal" :color="props.color">
                <VIcon :icon="props.icon" />
            </VAvatar>

            <h5 class="text-h5 my-2">
                {{ currencySymbol + ' ' + kFormatter(Number(props.stats)) }}
            </h5>
            <span class="text-body-2 text-center">{{ props.title }}</span>
        </VCardText>
    </VCard>
</template>
