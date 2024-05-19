<script setup lang="ts">
import rocket from '@/assets/images/rocket.png'
import draftImg from '@/assets/images/device-floppy.png'
import { isEmpty } from '@/@core/utils'
import { dateValidator } from '@/@core/utils/validators'
import { useScheduleDataStore } from '../common/scheduleDataStore'
interface Props {
    camapaignData: {
        step1Completed: boolean,
        step2Completed: boolean,
        channelType: string
    }
}
interface Emit {
    (e: 'saveCampaign', val: string) // string may be instruction for schedule or draft 
}

const props = defineProps<Props>()
const emit = defineEmits<Emit>()
const componentColor = computed(() => {
    if (props.camapaignData?.channelType == 'SMS') {
        return '#f39d40'
    } else if (props.camapaignData?.channelType == 'WhatsApp') {
        return '#66C871'
    } else if (props.camapaignData?.channelType == 'Email') {
        return '#00CFE8'
    }
})

const scheduleStore = useScheduleDataStore()

function scheduleCampaign(saveType: string) {
    scheduleStore.status = saveType
    emit('saveCampaign', saveType)
}

function validateDate(val: string) {
    console.log(val)
    if (isEmpty(val)) {
        scheduleStore.isOneTimeDateValid = false;
        return 'Date required for scheduling the campaign'
    }

    else if (scheduleStore.scheduleType == 'ONE TIME') { //onetime
        scheduleStore.isOneTimeDateValid = dateValidator(val)
        if (scheduleStore.isOneTimeDateValid) {
            return true
        };
        return 'Please select a future date and time'
    }
    else { //recurring type has two dates

        const dates = scheduleStore.recuScheduleDate.split('to');
        if (dates.length < 2 && dates.length > 0) {
            scheduleStore.isRangeDateValid = false;
            return 'Please select schedule ending date also'
        }
        else {
            const date1 = dates[0].trim();
            const date2 = dates[1].trim();

            if (date1 == date2) return 'Starting and ending dates should not be same'
            if (isEmpty(scheduleStore.recuScheduleTime)) return 'Please select schedule time'
            scheduleStore.startDate = date1 + ' ' + scheduleStore.recuScheduleTime;
            scheduleStore.endDate = date2 + ' ' + scheduleStore.recuScheduleTime;
            const valid1 = dateValidator(scheduleStore.startDate);
            const valid2 = dateValidator(scheduleStore.endDate);
            scheduleStore.isRangeDateValid = valid1 && valid2
            if (!scheduleStore.isRangeDateValid) {
                return 'Please select a future dates and time'
            }
            return true;
        }
    }
}
scheduleStore.step3Completed = computed(() => {
    // console.log(props.camapaignData)
    return props.camapaignData?.step1Completed && props.camapaignData?.step2Completed &&
        ((scheduleStore.isOneTimeDateValid && scheduleStore.scheduleType == 'ONE TIME') || (scheduleStore.isRangeDateValid && scheduleStore.scheduleType == 'RECURRING'))
        && (!isEmpty(scheduleStore.frequencyType) || scheduleStore.scheduleType == 'ONE TIME')
})
const isdraftEnabled = computed(() => {
    return props.camapaignData?.step1Completed && props.camapaignData?.step2Completed
})
</script>
<template>
    <VCardText>
        <VRow>
            <VCol cols="12" lg="3" md="3" sm="4">
                <p class="mt-1 text-subtitle-1 font-weight-medium">Schedule</p>
            </VCol>
            <VCol cols="12" lg="4" md="4" sm="6">
                <v-radio-group v-model="scheduleStore.scheduleType" inline :color="componentColor"
                    class="font-weight-medium"
                    :disabled="scheduleStore.scheduleType == 'RECURRING' && scheduleStore.isSentOut">
                    <v-radio label="One time" value="ONE TIME" true-icon="tabler-circle-check-filled"
                        false-icon="tabler-circle" class="mr-7"></v-radio>
                    <v-radio label="Recurring" value="RECURRING" true-icon="tabler-circle-check-filled"
                        false-icon="tabler-circle"></v-radio>
                </v-radio-group>
            </VCol>
        </VRow>
        <VRow class="mb-2 " v-if="scheduleStore.scheduleType == 'RECURRING'">
            <VCol>
                <VBtn @click="scheduleStore.frequencyType = 'DAILY'" :color="componentColor"
                    :variant="scheduleStore.frequencyType == 'DAILY' ? 'flat' : 'outlined'"
                    :class="['mr-3', 'text-h6', 'font-weight-medium', scheduleStore.frequencyType == 'DAILY' ? 'text-white' : '']">
                    <p class="mt-0 mb-2">Daily</p>
                </VBtn>
                <VBtn @click="scheduleStore.frequencyType = 'WEEKLY'" :color="componentColor"
                    :variant="scheduleStore.frequencyType == 'WEEKLY' ? 'flat' : 'outlined'"
                    :class="['mr-3', 'text-h6', 'font-weight-medium', scheduleStore.frequencyType == 'WEEKLY' ? 'text-white' : '']">
                    <p class="mt-0 mb-3">Weekly</p>
                </VBtn>
                <VBtn @click="scheduleStore.frequencyType = 'MONTHLY'" :color="componentColor"
                    :variant="scheduleStore.frequencyType == 'MONTHLY' ? 'flat' : 'outlined'"
                    :class="['mr-3', 'text-h6', 'font-weight-medium', scheduleStore.frequencyType == 'MONTHLY' ? 'text-white' : '']">
                    <p class="mt-0 mb-2">Monthly</p>
                </VBtn>
                <VBtn @click="scheduleStore.frequencyType = 'YEARLY'" :color="componentColor"
                    :variant="scheduleStore.frequencyType == 'YEARLY' ? 'flat' : 'outlined'"
                    :class="['mr-3', 'text-h6', 'font-weight-medium', scheduleStore.frequencyType == 'YEARLY' ? 'text-white' : '']">
                    <p class="mt-0 mb-2">Yearly</p>
                </VBtn>
            </VCol>
        </VRow>
        <VRow v-if="scheduleStore.scheduleType == 'ONE TIME'">
            <VCol cols="12" lg="3" md="3" sm="4">
                <p class="font-weight-medium text-subtile-1 my-3">Date</p>
            </VCol>

            <VCol cols="12" lg="4" md="4" sm="5">
                <AppDateTimePicker v-model="scheduleStore.scheduleDate" placeholder="Select date and time"
                    :rules="[scheduleStore.validateDate]" :config="{ enableTime: true, dateFormat: 'Y-m-d H:i:ss' }" />
            </VCol>
        </VRow>
        <VRow v-else v-show="!isEmpty(scheduleStore.frequencyType)">
            <VCol cols="12" lg="3" md="3" sm="4">
                <p class="font-weight-medium text-subtile-1 my-3">Date</p>
            </VCol>
            <VCol cols="12" lg="5" md="5" sm="6">
                <div>
                    <AppDateTimePicker placeholder="Select a two dates for range" color="success"
                        v-model="scheduleStore.recuScheduleDate" :config="{ mode: 'range', dateFormat: 'Y-m-d' }" />
                </div>
                <VRow>
                    <p class="text-caption text-red mt-3 ml-4" v-show="!scheduleStore.isRangeDateValid">
                        {{ scheduleStore.recDateValidation }}</p>
                </VRow>
            </VCol>

        </VRow>

        <VRow v-if="scheduleStore.scheduleType == 'RECURRING'">
            <VCol cols="12" lg="3" md="3" sm="4">
                <p class="font-weight-medium text-subtile-1 my-3">Time</p>
            </VCol>
            <VCol cols="12" lg="2" md="3" sm="5">
                <AppDateTimePicker v-model="scheduleStore.recuScheduleTime" placeholder="Select time"
                    :config="{ enableTime: true, noCalendar: true, dateFormat: 'H:i:ss' }" />
            </VCol>
        </VRow>
        <div class="d-flex justify-end ma-2">
            <div class="d-flex justify-end text-center ma-2">
                <VCard flat border :width="$vuetify.display.xs ? 'auto' : 150" height="130"
                    @click="scheduleCampaign('Draft')" :color="isdraftEnabled ? componentColor : '#DBDADE'"
                    :disabled="!isdraftEnabled">
                    <VCardText class="text-center">
                        <VImg :src="draftImg" aspect-ratio="1" width="58" height="70" class="mx-auto ma-0 pa-0" />
                        <p class="text-body-2 ma-0 text-white"> Save as Draft</p>
                    </VCardText>
                </VCard>
            </div>
            <div class="d-flex justify-end ma-2">
                <VCard flat border color="#DBDADE" :width="$vuetify.display.xs ? 'auto' : 150" height="130"
                    @click="scheduleCampaign('Active')" :class="scheduleStore.step3Completed ? 'launch-campaign' : ''"
                    :disabled="!scheduleStore.step3Completed">
                    <VCardText class="text-center">
                        <VImg :src="rocket" aspect-ratio="1" width="58" class="mx-auto ma-0 pa-0" />
                        <p class="text-body-2 ma-0 text-white"> Launch Campaign</p>
                    </VCardText>
                </VCard>
            </div>
        </div>
    </VCardText>
</template>
<style scoped>
.launch-campaign {
    background: linear-gradient(130.92deg, #7C01CA -12.43%, #C50109 136.29%);
}
</style>
