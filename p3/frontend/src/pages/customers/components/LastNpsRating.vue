<script setup lang="ts">
const ratingBar = ref(0)
const barColor = ref('#EA5455')
interface Props {
    rating: number
}
const props = defineProps<Props>()
ratingBar.value = props.rating * 10;

if (Number(props.rating) > 7)
    barColor.value = '#28C76F'
else if (Number(props.rating) >= 5 && Number(props.rating) <= 7)
    barColor.value = '#FFC107'

</script>

<template>
    <VCard height="auto" >
        <template #title>
            <div class=" d-flex justify-space-between text-subtitle1 font-weight-medium">
                <p>Last NPS Rating</p>
                <p v-if="props.rating > 0">{{ props.rating }}/10</p>
            </div>
        </template>
        <VCardText>
            <div class="demo-space-y" v-if="props.rating > 0">
                <div class="zindex">
                    <VProgressLinear v-model="ratingBar" :color="barColor" height="12" elevation="10">
                        <template #default="{ value }">
                            <!-- <span>{{ props.rating }}</span> -->
                        </template>
                    </VProgressLinear>
                </div>
                <div class="d-flex justify-space-between mt-n6">
                    <div v-for="i in 11" :key="i">
                        <div>╷</div>
                        <div>{{ i - 1 }}</div>
                    </div>
                </div>
            </div>
            <div v-else>
                <p class="font-weight-medium"> The customer has never rated us on NPS </p>
            </div>
        </VCardText>
    </VCard>
</template>
