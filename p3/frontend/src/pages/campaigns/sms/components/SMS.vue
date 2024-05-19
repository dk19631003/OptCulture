<script setup lang="ts">

import { ref, watch } from 'vue';
import DiscountCodes from '@/pages/campaigns/sms/components/DiscountCodes.vue'
import { dateValidator, requiredValidator } from '@/@core/utils/validators';
import { URLShortner, isEmpty } from '@/@core/utils';
import axios from '@axios';
import router from '@/router';
import { mergeTagsMap, oldToNewMergeTags } from '@/@core/utils/index'
import { getUTCToLocalTime, getUTCTime } from '@/@core/utils/localeFormatter';
import { integerValidator } from '@/@core/utils/validators';
import Templates from './Templates.vue';
import rocket from '@/assets/images/rocket.png'
import SegmentsStep from '@/pages/campaigns/common/SegmentsStep.vue';
import { useSegmentsStore } from '@/pages/campaigns/common/segmentsStore'
import { useScheduleDataStore } from '@/pages/campaigns/common/scheduleDataStore';
import { useTemplateStore } from '@/pages/campaigns/common/templateStore';
import ScheduleStep from '@/pages/campaigns/common/ScheduleStep.vue';
import { fetchUserChannelsCount } from '@/pages/campaigns/common/userChannelCount'


interface Segment {
    segRuleName: string,
    description: string,
    segRuleId: number,
    totEmailSize: number,
    totMobileSize: number,
    totSize: number,
    type: string

}

interface Template {
    templateName: string,
    templateContent: string,
    templateRegId: number,
    headerText: string,
    senderId: string
}
const route = useRoute()
const scheduleType = ref('ONE TIME')
const msgLength = ref(0)
const campaignName = ref()
const isSegmentSelected = ref(false)
const isScheduled = ref(false)
const templateId = ref();
const isAllDetailsValid = ref(false)
const templateName = ref();
const isTemplateSelected = ref(false)
const isTemplateValid = ref(false)
const showTemplates = ref(false)
const msgContent = ref('')
const myTextarea = ref(null);
const testMobiles = ref()
const segmentPage = ref(-1)
const templatePage = ref(-1);
const mergeTag = ref()
const showDiscodes = ref(false)
const segmentName = ref('')
const discountCodes = ref<string[]>([])
const selectedSegmentIds = ref(new Set())
const selectedSegmentNames = ref(new Set())
const prevselectedSegmentList = ref<Segment[]>([])
const scheduleDate = ref()
const recuScheduleTime = ref()
const recuScheduleDate = ref('')
const frequencyType = ref() //monthly,weekly,..
const isRangeDateValid = ref(false)
const startDate = ref()
const lines = ref<[]>()
const endDate = ref()
const isOneTimeDateValid = ref(false)
const panelExpand = ref('one')
const mergeTagsList = ref<Object[]>([])
const segmentList = ref<Segment[]>([])
const channelSettingToast = ref(false)
const channelConfigCount = ref()
const segmentIds = ref('')
const componentColor = ref('#f39d40')
const channelType = ref('SMS')
const senderId = ref('')
const segmentStore = useSegmentsStore()
const scheduleStore = useScheduleDataStore()
const templateStore = useTemplateStore()
for (const [key, value] of mergeTagsMap.entries()) {
    const obj = { title: '', value: '' }
    obj.title = key
    obj.value = value.tag
    mergeTagsList.value.push(obj)
}

onMounted(() => {

    segmentStore.searchByName = ''
    segmentStore.segmentList.length = 0
    segmentStore.channelType = channelType.value
    segmentStore.fetchSegmentList('')
    scheduleStore.isSentOut = false
    scheduleStore.intializeData()
    templateStore.channelType = channelType.value
    templateStore.fetchTemplates('Search')
})

function setDiscountCodes(value: Set<object>) { //receiving selected discount codes from popup
    discountCodes.value = [...value]
    // console.log(discountCodes.value)
    const cursSt = myTextarea.value.selectionStart;
    const curEnd = myTextarea.value.selectionEnd;
    if (discountCodes.value.length > 0) {
        msgContent.value = msgContent.value.substring(0, cursSt) + discountCodes.value[0].tag + msgContent.value.substring(curEnd);
    }
}

function selectTemplate(item: Template) {
    if (item) {
        msgContent.value = item.templateContent;
        templateId.value = item.templateRegId;
        templateStore.templateId = templateId.value
        templateName.value = item.templateName
        senderId.value = item.senderId;
        isTemplateSelected.value = true;
    }
}
async function scheduleCampaign(saveType: string) {
    if (isScheduled.value) return; //already clicked
    isScheduled.value = true;
    const reqBody = {
        commId: route.params.id,
        senderId: senderId.value,
        campaignName: campaignName.value,
        segmentIds: 'Segment:' + segmentStore.selectedIds, // segemnt ids joined by ','
        createdDate: new Date().toISOString(),
        scheduleDate: getUTCTime(scheduleStore.scheduleDate),
        scheduleType: scheduleStore.scheduleType,
        frequencyType: scheduleStore.frequencyType,
        startDate: getUTCTime(scheduleStore.startDate),
        endDate: getUTCTime(scheduleStore.endDate),
        channelType: channelType.value,
        templateId: templateId.value,
        messageContent: msgContent.value,
        status: saveType //draft or active 
    }
    try {
        console.log(reqBody)
        const resp = await axios.post('/api/campaigns/schedule', reqBody)
        // console.log(resp)
        router.push('/campaigns/list') //redirect to list view
    } catch (err) {

    }
}


function expandPanel(open: string) {
    panelExpand.value = open
}


async function fetchSMSCampaign(smsCampaignId: string) {
    if (isEmpty(smsCampaignId)) return;
    try {
        const resp = await axios.get('/api/campaigns/campaign-id', {
            params: {
                "campaignId": smsCampaignId
            }
        })
        console.log(resp)
        campaignName.value = resp.data.campaignName;
        templateId.value = resp.data.templateId;
        templateStore.templateId = templateId.value
        templateName.value = resp.data.templateName;
        msgContent.value = resp.data.msgContent;
        senderId.value = resp.data.senderId
        segmentIds.value = resp.data.segmentLists.replace('Segment:', '') //for showing prevoius selected segments
        segmentStore.prevSelectedIds = new Set(segmentIds.value.split(','))
        isTemplateSelected.value = true;
        scheduleStore.scheduleDate = getUTCToLocalTime(resp.data.scheduleDate) //utc time to local time
        scheduleStore.scheduleType = resp.data.scheduleType
        scheduleStore.frequencyType = resp.data.frequencyType
        scheduleStore.isSentOut = resp.data.status == 1 ? true : false
        scheduleStore.startDate = getUTCToLocalTime(resp.data.startDate)
        scheduleStore.endDate = getUTCToLocalTime(resp.data.endDate)
        scheduleStore.recuScheduleDate = scheduleStore.startDate?.substring(0, 10) + ' to ' + scheduleStore.endDate?.substring(0, 10)
        scheduleStore.recuScheduleTime = isEmpty(scheduleStore.scheduleDate) ? scheduleStore.startDate.substring(10) : scheduleStore.scheduleDate.substring(10)

    } catch (err) {
    }
}
async function shortenURL() { //getting msgcontent after shortening urls
    const text = await URLShortner(msgContent.value);
    msgContent.value = text
}

watch([templateName, templateId], () => {
    isTemplateValid.value = (!isEmpty(templateName.value) && !isEmpty(templateId.value) && integerValidator(templateId.value) === true)
    console.log(isTemplateValid.value)
})

fetchSMSCampaign(route.params.id)

watch(mergeTag, () => { //appending mergetags to msgcontent
    const cursSt = myTextarea.value.selectionStart;
    const curEnd = myTextarea.value.selectionEnd;
    if (!isEmpty(mergeTag.value)) {
        msgContent.value = msgContent.value.substring(0, cursSt) + mergeTag.value + msgContent.value.substring(curEnd);
        // mergeTag=ref()
    }

})
watch(msgContent, () => {
    const [text, length] = oldToNewMergeTags(msgContent.value)
    msgContent.value = text;
    msgLength.value = Number(length);
    // console.log(msgContent.value);
    lines.value = msgContent.value.split('\n');
    // console.log(lines.value)

}
)
const creditsRequired = computed(() => {
    return msgLength.value + '/' + (Math.ceil(msgLength.value / 160) ?? '1')
    // console.log(creditsRequired);
})
const step1Completed = computed(() => { //checking step1 validated
    return !(isEmpty(campaignName.value)) && (isTemplateSelected.value) && isTemplateValid.value && !isEmpty(msgContent.value) //if camp name is empty or template validations
})
const step2Completed = computed(() => {
    return segmentStore.isSegmentSelected
})

const camapaignData = computed(() => {
    return {
        step1Completed: step1Completed.value,
        step2Completed: step2Completed.value,
        channelType: channelType.value
    }
})

async function getUserChannelCount() {
    channelConfigCount.value = await fetchUserChannelsCount(channelType.value)
}
watch(channelConfigCount, () => {
    console.log(channelConfigCount.value)
    if (channelConfigCount.value == 0) { //show notififcation on user channel settings found for user
        channelSettingToast.value = true;
    }
})
getUserChannelCount()
</script>
<template>
    <div>
        <VSnackbar timeout="15000" v-model="channelSettingToast" location="top" color="error" variant="tonal"
            width="500px">
            <VIcon icon="tabler-info-circle" color="error" size="30"></VIcon>
            <span class="text-black ">{{ ' Channel Gateways are not configured' }}</span>
            <template #actions>
                <VBtn color="info" @click="router.push('/channels')">
                    Configure
                </VBtn>
            </template>
        </VSnackbar>
        <VExpansionPanels variant="accordion" v-model="panelExpand">
            <VExpansionPanel class="mb-2" value="one">
                <VExpansionPanelTitle :color="componentColor" class="text-white" v-if="panelExpand == 'one'">
                    <VRow class="d-flex justify-space-between align-center">
                        <VCol cols="12" lg="3" sm="3" md="3">
                            <p class="ma-0">Campaign Details</p>
                        </VCol>
                        <VCol cols="12" lg="1" sm="1" md="1">
                            <div v-show="!step1Completed">
                                <p class="ma-0">
                                    <VIcon icon="tabler-info-circle" color="error" size="30"></VIcon>
                                </p>
                            </div>
                        </VCol>
                    </VRow>
                </VExpansionPanelTitle>
                <VExpansionPanelTitle :color="componentColor" v-else class="text-white">
                    <VRow class=" justify-space-between">
                        <VCol cols="12" lg="4" sm="3" md="3">
                            <p class="heading-1">Campaign Name</p>
                            <p class="heading-2">{{ campaignName }}</p>
                        </VCol>
                        <VCol cols="12" lg="4" sm="3" md="3">
                            <p class="heading-1">Template Name</p>
                            <p class="heading-2">{{ templateName }}</p>
                        </VCol>
                        <VCol cols="12" lg="3" sm="3" md="3"></VCol>
                        <VCol cols="12" lg="1" sm="4" md="3">
                            <div class="d-flex justify-end mx-1 mt-1" v-if="!step1Completed">
                                <p class=" mt-3 mr-1">
                                    <VIcon icon="tabler-info-circle" color="error" size="30"></VIcon>
                                </p>
                                <!-- <p color="error" class="text-caption text-red mt-4">Information missing</p> -->
                            </div>
                        </VCol>
                    </VRow>
                </VExpansionPanelTitle>
                <VExpansionPanelText>
                    <VCardText>
                        <VRow>
                            <VCol cols="12" lg="3" md="3" sm="4">
                                <p class="mt-2  text-subtitle-1 font-weight-medium">Campaign Name
                                    <!-- <span
                                                class="text-red">*</span> -->
                                </p>
                            </VCol>
                            <VCol cols="12" lg="4" md="4" sm="4">
                                <AppTextField placeholder="campaign name" v-model="campaignName"
                                    :rules="[requiredValidator]">
                                </AppTextField>
                            </VCol>
                        </VRow>
                        <VRow>
                            <VCol cols="12" lg="3" md="6" sm="12">
                                <p class=" text-subtitle-1 font-weight-medium">Pre approved templates</p>
                            </VCol>
                            <VCol cols="12" lg="9" md="10" sm="12" class="mt-n3">
                                <VBtn v-for="item in templateStore.templates" :key="item" class="mr-3 mb-1"
                                    variant="outlined"
                                    :color="item.templateRegId == templateId ? componentColor : 'disabled'"
                                    @click="selectTemplate(item)">{{ item.templateName }}</VBtn>
                                <VBtn size="40" color="#97999D" class="mt-n1" @click="showTemplates = !showTemplates">
                                    <VIcon icon=" tabler-chevron-right"></VIcon>
                                </VBtn>
                            </VCol>
                        </VRow>
                        <VRow>
                            <VCol cols="12" lg="3" md="3" sm="4">
                                <p class="mt-2 text-subtitle-1 font-weight-medium">Template Name </p>
                            </VCol>
                            <VCol cols="12" lg="4" md="4" sm="6">
                                <AppTextField placeholder="template name" v-model="templateName"
                                    :rules="[requiredValidator]" :disabled="isTemplateSelected">
                                </AppTextField>
                            </VCol>
                        </VRow>
                        <VRow>
                            <VCol cols="12" lg="3" md="3" sm="4">
                                <p class="mt-2 text-subtitle-1 font-weight-medium">Template Id </p>
                            </VCol>
                            <VCol cols="12" lg="4" md="4" sm="6">
                                <AppTextField placeholder="template id" v-model="templateId"
                                    :rules="[integerValidator, requiredValidator]" :disabled="isTemplateSelected">
                                </AppTextField>
                            </VCol>
                        </VRow>
                        <VRow>
                            <VCol cols="12" lg="7" md="7" sm="12">
                                <div class="d-flex justify-end">
                                    <p>{{ creditsRequired }}
                                    </p>
                                </div>
                                <VTextarea class="mb-3" v-model="msgContent" ref="myTextarea"
                                    :rows="$vuetify.display.md ? 20 : 15">

                                </VTextarea>
                                <!-- <p>{{ msgContent }}</p> -->
                                <!-- <div  class="border-md pa-1 rounded-1" :style="{ height:300}">
                                            <p v-for="line in lines" class="ma-0">
                                                {{ line }}
                                            </p>
                                        </div> -->
                            </VCol>
                            <VCol cols="12" lg="5" md="5" sm="12">
                                <VRow :class="{ 'mt-7': !$vuetify.display.smAndDown }">

                                    <VCol cols="12" lg="4" md="12" sm="6">
                                        <p :class="{ 'mt-0': $vuetify.display.smAndDown }"><span
                                                class="text-subtitle-1 font-weight-medium">Merge tags</span> </p>
                                    </VCol>
                                    <VCol cols="12" lg="8" md="8" sm="6">
                                        <AppAutocomplete v-model="mergeTag" :items="mergeTagsList" bg-color="#f39d40"
                                            class="font-weight-medium" placeholder="Merge Tags" />
                                    </VCol>
                                </VRow>
                                <VRow>
                                    <VCol>
                                        <p class="mt-2  text-subtitle-1 font-weight-medium">Discount Codes </p>
                                    </VCol>
                                    <VCol cols="12" lg="8" md="12" sm="6">
                                        <VBtn color="#f39d40" @click="showDiscodes = !showDiscodes"
                                            :width="$vuetify.display.mdAndDown ? 'auto' : 300">
                                            Insert Discount
                                            Codes
                                        </VBtn>
                                    </VCol>
                                </VRow>
                                <VRow>
                                    <VCol cols="12" md="7" sm="4" lg="5">
                                        <span class="text-subtilte-1 font-weight-medium">Send Test SMS <VTooltip
                                                location="bottom">
                                                <template #activator="{ props }">
                                                    <v-icon icon="tabler-info-circle" variant="outlined" color="blue"
                                                        class="font-weight-regular mb-1" v-bind="props" size="25">
                                                    </v-icon>
                                                </template>
                                                <pre>You can send a test message
                                            to multiple mobile numbers
                                            separated by commas (,)</pre>
                                            </VTooltip>
                                        </span>
                                    </VCol>
                                    <VCol cols="12" lg="5" md="8" sm="6">
                                        <v-textarea rows="2" v-model="testMobiles">
                                        </v-textarea>
                                    </VCol>
                                    <VCol cols="12" lg="2" md="3" sm="2" class="text-center align-center">
                                        <VBtn color='#97999D' disabled>Send
                                        </VBtn>
                                    </VCol>
                                </VRow>
                                <VRow>
                                    <VCol cols="12" lg="6" md="6" sm="5">
                                        <VBtn color="#f39d40" @click="shortenURL()" :disabled="isEmpty(msgContent)">
                                            Use URL shortner
                                        </VBtn>
                                    </VCol>

                                </VRow>
                            </VCol>
                        </VRow>
                    </VCardText>
                    <VCardText>
                        <!-- <VRow class=" mt-n16"> -->
                        <div :class="{ 'text-center': true, 'd-flex': true, 'justify-end': true }">
                            <VBtn :color="componentColor" @click="expandPanel('two')" :disabled="!step1Completed"
                                class="text-white">
                                Next</VBtn>
                        </div>
                        <!-- </VRow> -->
                    </VCardText>
                </VExpansionPanelText>
            </VExpansionPanel>
            <VExpansionPanel class="mb-2" value="two">
                <VExpansionPanelTitle disable-icon-rotate color="#f39d40" v-if="panelExpand == 'two'"
                    class="text-white">
                    <VRow class="d-flex justify-space-between align-center">
                        <VCol cols="12" lg="3" sm="3" md="3">
                            <p class="ma-0">Segment Details</p>
                        </VCol>
                        <VCol cols="12" lg="1" sm="1" md="1">
                            <p v-if="!step2Completed" class="ma-0">
                                <VIcon icon="tabler-info-circle" color="error" size="30"></VIcon>
                            </p>
                        </VCol>
                    </VRow>
                </VExpansionPanelTitle>
                <VExpansionPanelTitle :color="componentColor" class="text-white" v-else>
                    <VRow class=" justify-space-between">
                        <VCol cols="12" lg="4" sm="3" md="3">
                            <p class="heading-1">Segment Name</p>
                            <p class="heading-2">{{ segmentStore.segmentNames }}</p>
                        </VCol>
                        <VCol cols="12" lg="4" sm="3" md="3">
                            <p class="heading-1">Segment Type</p>
                            <p class="heading-2">{{ segmentStore.segmentType
                                }}</p>
                        </VCol>
                        <VCol cols="12" lg="3" sm="3" md="3">
                            <p class="heading-1">Total Count</p>
                            <p class="heading-2">{{ segmentStore.totalSelectedCount }}</p>
                        </VCol>
                        <VCol cols="12" lg="1" sm="3" md="3">
                            <div class="d-flex justify-end mx-1 mt-1" v-if="!step2Completed">
                                <p class=" mt-3 mr-1">
                                    <VIcon icon="tabler-info-circle" color="error" size="30"></VIcon>
                                </p>
                                <!-- <p color="error" class="text-caption text-red mt-4">Information missing</p> -->
                            </div>
                        </VCol>
                    </VRow>
                </VExpansionPanelTitle>
                <VExpansionPanelText>
                    <VCardText>
                        <SegmentsStep :campaignType="channelType" @goToNextStep="expandPanel('three')"></SegmentsStep>
                    </VCardText>
                </VExpansionPanelText>
            </VExpansionPanel>
            <VExpansionPanel class="mb-2" value="three">
                <VExpansionPanelTitle disable-icon-rotate color="#f39d40" class="text-white"
                    v-if="panelExpand == 'three'">
                    <VRow class="d-flex justify-space-between align-center">
                        <VCol cols="12" lg="3" sm="3" md="3">
                            <p class="ma-0">Schedule Campaign</p>
                        </VCol>
                        <VCol cols="12" lg="1" sm="1" md="1">
                            <p v-if="!scheduleStore.step3Completed" class="ma-0">
                                <VIcon icon="tabler-info-circle" color="error" size="30"></VIcon>
                            </p>
                        </VCol>
                    </VRow>
                </VExpansionPanelTitle>
                <VExpansionPanelTitle :color="componentColor" class="text-white" v-else>
                    <VRow class=" justify-space-between">
                        <VCol cols="12" lg="4" sm="3" md="3">
                            <p class="heading-1">Schedule</p>
                            <p class="heading-2">{{ scheduleStore.scheduleType }}</p>
                        </VCol>
                        <VCol cols="12" lg="4" sm="3" md="3">
                            <p class="heading-1">Occurence</p>
                            <p class="heading-2">{{ scheduleStore.scheduleType == 'ONE TIME' ? 'ONE TIME' :
            scheduleStore.frequencyType
                                }}</p>
                        </VCol>
                        <VCol cols="12" lg="3" sm="3" md="3">
                            <p class="headong-1">Status</p>
                            <p class="heading-2">{{ isScheduled ? 'Scheduled' : 'Not Scheduled' }}</p>
                        </VCol>
                        <VCol cols="12" lg="1" sm="3" md="3">
                            <div class="d-flex justify-end mx-1 mt-1" v-show="!scheduleStore.step3Completed">
                                <p class=" mt-3 mr-1">
                                    <VIcon icon="tabler-info-circle" color="error" size="30"></VIcon>
                                </p>
                                <!-- <p color="error" class="text-caption text-red mt-4">Information missing</p> -->
                            </div>
                        </VCol>
                    </VRow>
                </VExpansionPanelTitle>
                <VExpansionPanelText>
                    <ScheduleStep :camapaign-data="camapaignData" @save-campaign="scheduleCampaign" />
                </VExpansionPanelText>
            </VExpansionPanel>
        </VExpansionPanels>
        <Templates v-model:is-dialog-visible="showTemplates" @submitTemplate="selectTemplate"
            :channelType="channelType" />
        <DiscountCodes v-model:is-dialog-visible="showDiscodes" @submitDiscount="setDiscountCodes"
            :color="componentColor" />

    </div>
</template>
<style scoped>
.scroll {
    overflow-y: auto;
}

.custom-max-height {
    max-height: 290px;
    /* Max height for large breakpoints */
}

.launch-campaign {
    background: linear-gradient(130.92deg, #7C01CA -12.43%, #C50109 136.29%);
}
</style>
