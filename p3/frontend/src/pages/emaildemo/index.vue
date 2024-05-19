<template>
    <VCard class="pa-4">
        <VCardTitle class="text-h6 text-center">Email Settings</VCardTitle>
        <VCardText>
            <VRow>
                <VCol cols="12" lg="4" md="4" sm="6">
                    <p class="mt-2  text-subtitle-1 font-weight-medium">Campaign Name
                    </p>
                </VCol>
                <VCol lg="8" md="8" sm="6">
                    <AppTextField placeholder="Campaign Name" v-model="campaignName">
                    </AppTextField>
                </VCol>
            </VRow>
            <VRow>
                <VCol cols="12" lg="4" md="4" sm="6">
                    <p class="mt-2  text-subtitle-1 font-weight-medium">From Name
                    </p>
                </VCol>
                <VCol lg="8" md="8" sm="6">
                    <AppTextField placeholder="From Name" v-model="fromName">
                    </AppTextField>
                </VCol>
            </VRow>
            <VRow>
                <VCol cols="12" lg="4" md="4" sm="6">
                    <p class="mt-2  text-subtitle-1 font-weight-medium">From Email
                    </p>
                </VCol>
                <VCol lg="8" md="8" sm="6">
                    <AppTextField placeholder="from email" v-model="fromEmail">
                    </AppTextField>
                </VCol>
            </VRow>

            <VRow>
                <VCol cols="12" lg="4" md="4" sm="6">
                    <p class="mt-2  text-subtitle-1 font-weight-medium"> Reply To Email
                    </p>
                </VCol>
                <VCol lg="8" md="8" sm="6">
                    <AppTextField placeholder="reply to" v-model="replyToEmail">
                    </AppTextField>
                </VCol>
            </VRow>
            <VRow>
                <VCol cols="12" lg="4" md="4" sm="6">
                    <p class="mt-2  text-subtitle-1 font-weight-medium">Subject
                    </p>
                </VCol>
                <VCol lg="8" md="8" sm="6">
                    <AppTextField placeholder="Subject text" v-model="subject">
                    </AppTextField>
                </VCol>
            </VRow>
            <VRow>
                <VCol cols="12" lg="4" md="4" sm="6">
                    <p class="mt-2 text-subtitle-1 font-weight-medium">Time</p>
                </VCol>
                <VCol lg="8" md="8" sm="6">
                    <AppDateTimePicker v-model="time" placeholder="Select date and time"
                        :config="{ enableTime: true, dateFormat: 'Y-m-d H:i:ss' }" />
                </VCol>
            </VRow>
            <VRow>
                <VCol cols="12" lg="4" md="4" sm="6">
                    <p class="mt-2  text-subtitle-1 font-weight-medium">Template Name
                    </p>
                </VCol>
                <VCol lg="8" md="8" sm="6">
                    <AppTextField placeholder="template Name" v-model="name" disabled>
                    </AppTextField>
                </VCol>
            </VRow>
            <VRo>
                <div class="text-center pa-2">
                    <VBtn @click="saveEmail">Schedule</VBtn>
                </div>
            </VRo>
        </VCardText>

    </VCard>

</template>

<script setup lang="ts">

import axios from '@axios';
import { getUTCToLocalTime, getUTCTime } from '@/@core/utils/localeFormatter';

const scheduleType = ref('ONE TIME')
const channelType = ref('Email')
const fromName = ref('')
const fromEmail = ref('')
const campaignName = ref('')
const replyToEmail = ref('')
const subject = ref('')
const template = JSON.parse(localStorage.getItem("mytemplate") || 'null')
const time = ref()

const name = ref("")
if (template)
    name.value = template.templateName
async function saveEmail() {
    // Prepare the email object
    const emailData = {
        campaignName: campaignName.value,
        fromName: fromName.value,
        fromEmail: fromEmail.value,
        replyEmail: replyToEmail.value,
        subject: subject.value,
        messageContent: template.content,
        channelType: channelType.value,
        createdDate: new Date().toISOString(),
        scheduleDate: getUTCTime(time.value),
        scheduleType: scheduleType.value,
    }
    try {
        console.log(emailData)
        //   return;
        const resp = await axios.post('/api/campaigns/schedule', emailData)
        console.log(resp)
    } catch (err) {

    }
}

</script>

<style scoped>
/* Add your styles here */
</style>
