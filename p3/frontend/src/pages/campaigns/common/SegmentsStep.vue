<script setup lang="ts">
import { ref } from 'vue'
import axios from '@axios'
import { useSegmentsStore } from './segmentsStore'
import { isEmpty } from '@/@core/utils'
interface Props {
    campaignType: string
}
interface Emit {
    (e: 'goToNextStep', val: boolean)
}
interface Segment {
    segRuleName: string,
    description: string,
    segRuleId: number,
    totEmailSize: number,
    totMobileSize: number,
    totSize: number,
    type: string,
    selected: boolean
}
const props = defineProps<Props>()
const emit = defineEmits<Emit>()
const prevselectedSegmentList = ref<Segment[]>([])
const isSegmentLastPage = ref(false)
const segmentList = ref<Segment[]>([])
const segmentPage = ref(0)
const segmentName = ref('')

const segmentStore = useSegmentsStore()
const color = computed(() => {
    if (props.campaignType == 'SMS') {
        return '#f39d40'
    } else if (props.campaignType == 'WhatsApp') {
        return '#66C871'
    } else if (props.campaignType == 'Email') {
        return '#00CFE8'
    }
})

function gotoStep3() {
    emit('goToNextStep', true)
}
</script>
<template>
    <VCardTitle>
        <div class="d-flex justify-space-between">
            <div class="text-subtitle-1 font-weight-medium">{{ segmentStore.selectedSegments.length > 0 ?
                'Selected Segments' : '' }}</div>
            <div class="text-subtitle-1 font-weight-medium d-flex align-start">
            </div>
            <div class="text-subtitle-1 font-weight-medium d-flex justify-space-between"
                v-if="segmentStore.selectedSegments.length == 0">
                <div class="mr-3"> Total Count :{{ segmentStore.totalSelectedCount }}</div>
                <div>
                    <VIcon icon="tabler-refresh" color="primary" @click=""></VIcon>
                </div>
            </div>
        </div>
    </VCardTitle>
    <VCardText>
        <VRow class="scroll" :class="{ 'custom-max-height-1': true }" v-if="segmentStore.selectedSegments.length > 0">
            <VCol cols="12" lg="4" md="4" sm="6" v-for="(item, index) in segmentStore.selectedSegments" :key="index">
                <VCard flat border>
                    <div style="float:right;" class="pt-2 pr-2">
                        <VIcon size="22" color="primary"></VIcon>
                    </div><br>
                    <p></p>
                    <div class="text-center">
                        <p class="text-subtilte-1 font-weight-medium">{{ item.segRuleName }}</p>
                    </div>
                    <div class="text-center">
                        <p class="text-subtilte-2 font-weight-light">{{
                item.description && item.description.length > 35 ?
                    item.description.substring(0, 35) + '...'
                    : item.description
            }}&nbsp;
                        </p>
                    </div>
                </VCard>

            </VCol>
        </VRow>
        <VRow v-if="segmentStore.selectedSegments.length > 0" class="d-flex justify-space-between">
            <div class="text-subtitle-1 font-weight-medium">Select segments</div>
            <div class="text-subtitle-1 font-weight-medium d-flex align-start">
            </div>
            <div class="text-subtitle-1 font-weight-medium d-flex justify-space-between">
                <div class="mr-3"> Total Count :{{ segmentStore.totalSelectedCount }}</div>
                <div>
                </div>
            </div>

        </VRow>
        <VRow class="d-flex justify-space-between w-100">

            <VCol cols="12" md="5" lg="5" sm="5">
                <AppTextField placeholder="segment name" height="20" style="{height: 60px;}"
                    v-model="segmentStore.searchByName">
                </AppTextField>
            </VCol>
            <VCol cols="12" md="2" lg="2" sm="3">
                <VBtn :color="color" @click="segmentStore.fetchSegmentList('Search')">Search</VBtn>
            </VCol>
            <!-- <div :class="{ 'd-flex': true, 'justify-end': !$vuetify.display.smAndDown,'w-75':true }"> -->
            <VCol cols="12" lg="4" md="5" sm="8"
                :class="{ 'd-flex': true, 'justify-end': !$vuetify.display.smAndDown }">
                <VBtn prepend-icon="tabler-plus" :color="color"> Create Segment</VBtn>
            </VCol>
            <!-- </div> -->
        </VRow>
        <VRow class="scroll" :class="{ 'custom-max-height': true }">
            <VCol cols="12" lg="4" md="4" sm="6" v-for="(item, index) in segmentStore.segments" :key="index">
                <VCard flat border @click="item.selected = !item.selected"
                    :class="[item.selected ? 'border-custom' : '']">
                    <div style="float:right;" class="pt-2 pr-2">
                        <VIcon :icon="item.selected ? 'tabler-square-check-filled' : 'tabler-square'" size="22"
                            color="primary"></VIcon>
                    </div><br>
                    <p></p>
                    <div class="text-center">
                        <p class="text-subtilte-1 font-weight-medium">{{ item.segRuleName }}</p>
                    </div>
                    <div class="text-center">
                        <p class="text-subtilte-2 font-weight-light">{{
                item.description && item.description.length > 35 ?
                    item.description.substring(0, 35) + '...'
                    : item.description
            }}&nbsp;
                        </p>
                    </div>
                </VCard>

            </VCol>
        </VRow>
        <div class="d-flex justify-end mt-6">
            <!-- <VBtn>Back</VBtn> -->
            <VBtn height="22" class="px-n1 " variant="tonal" @click="segmentStore.fetchSegmentList('Load More')"
                :disabled="segmentStore.isSegmentLastPage">Load
                More
            </VBtn>
        </div>
    </VCardText>
    <VCardText>

        <div class="d-flex justify-end">
            <VBtn :color="color" @click="gotoStep3()" :disabled="segmentStore.selectedSegments.length == 0"
                class="text-white">Next
            </VBtn>
        </div>

    </VCardText>
</template>
<style scoped>
.scroll {
    overflow-y: auto;
}

.custom-max-height-1 {
    max-height: 150px;
}

.custom-max-height {
    max-height: 290px;
    /* Max height for large breakpoints */
}
</style>
