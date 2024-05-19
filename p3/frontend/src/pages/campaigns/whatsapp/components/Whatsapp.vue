<script setup lang="ts">

import { ref, watch } from 'vue';
import { dateValidator, requiredValidator } from '@/@core/utils/validators';
import { URLShortner, isEmpty, mergeTagsMap } from '@/@core/utils';
import axios from '@axios';
import router from '@/router';
import { getUTCToLocalTime, getUTCTime } from '@/@core/utils/localeFormatter';
import { integerValidator } from '@/@core/utils/validators';
import Templates from '@/pages/campaigns/sms/components/Templates.vue'
import rocket from '@/assets/images/rocket.png'
import VariableDailog from '@/pages/campaigns/dialog/VariableDailog.vue';
import Widget from './Widget.vue'
import FileManager from './FileManager.vue'
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
    templateId: number,
    templateName: string,
    templateContent: string,
    templateRegId: number,
    headerText: string,
    senderId: string,
    msgType: string,
    footer: string
}

interface Variable {
    mergeTag: string,
    previewPlacholder: string,
    sampleText: string
}
const route = useRoute()
const scheduleType = ref('ONE TIME')
const msgLength = ref(0)
const isScheduled = ref(false)
const campaignName = ref()
const isSegmentSelected = ref(false)
const templateId = ref();
const isAllDetailsValid = ref(false)
const isTemplateSelected = ref(false)
const showTemplates = ref(false)
const msgContent = ref('')
const originalMsgcontent = ref('');
const testMobiles = ref()
const segmentPage = ref(-1)
const templatePage = ref(-1);
const segments = ref<string[]>([])
const segmentName = ref('')
const templateName = ref()
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
const channelSettingToast = ref(false)
const channelConfigCount = ref()
const panelExpand = ref('one')
const segmentList = ref<Segment[]>([])
const templateList = ref<Template[]>([])
const contactsCount = ref(0)
const segmentIds = ref('')
const componentColor = ref('#66C871')
const isSegmentLastPage = ref(false)
const channelType = ref('WhatsApp')
const longURL = ref('')
const messageType = ref('')
const varMapping = ref<string[]>([]) // mapping array for varible and placeholder for rendering msg content
const placeholderMappings = ref<string[]>([])  // mapping array for varible and placeholder for db
const sampleTextMap = ref<string[]>([]) // mapping array for preview template
const footer = ref('')
const selectedVariable = ref()
const headerText = ref('')
const showVariableSelection = ref(false);
const templateText = ref('')

const userData = JSON.parse(localStorage.getItem('userData') || 'null')
const extractedName = ref('')
const showFileManager = ref(false)
const showPreview = ref(false)
const segmentStore = useSegmentsStore()
const scheduleStore = useScheduleDataStore()
const templateStore = useTemplateStore()
if (userData)
    extractedName.value = userData.userName.split('__')[0];

function selectTemplate(item: Template) {
    if (item) {
        msgContent.value = item.templateContent;
        templateId.value = item.templateId;
        templateStore.templateId = item.templateId
        originalMsgcontent.value = item.templateContent
        messageType.value = item.msgType;
        headerText.value = item.headerText;
        footer.value = item.footer;
        isTemplateSelected.value = true;
        templateName.value = item.templateName
    }
}
const propsForPreview = computed(() => {
    // console.log(headerText.value)
    return {
        header: headerText.value,
        msgContent: templateText.value,
        msgType: messageType.value,
        footer: footer.value,
        companyLogo: '',
        companyName: extractedName.value
    }
})


async function scheduleCampaign(saveType: string) {
    if (isScheduled.value) return; //already clicked
    isScheduled.value = true;
    const reqBody = {
        commId: route.params.id,
        campaignName: campaignName.value,
        segmentIds: 'Segment:' + segmentStore.selectedIds, // segemnt ids joining by ','
        createdDate: new Date().toISOString(),
        scheduleDate: getUTCTime(scheduleStore.scheduleDate),
        scheduleType: scheduleStore.scheduleType,
        frequencyType: scheduleStore.frequencyType,
        startDate: getUTCTime(scheduleStore.startDate),
        endDate: getUTCTime(scheduleStore.endDate),
        channelType: channelType.value,
        templateId: templateId.value,
        messageContent: originalMsgcontent.value,
        mediaUrl: messageType.value != 'TEXT' ? headerText.value : null, // only if any media type need to store
        placeholderMappings: placeholderMappings.value.join('||'),
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
async function fetchCampaign(campaignId: number) {
    if (isEmpty(campaignId)) return;
    try {
        const resp = await axios.get('/api/campaigns/campaign-id', {
            params: {
                "campaignId": campaignId
            }
        })
        console.log(resp)
        campaignName.value = resp.data.campaignName;
        templateId.value = resp.data.templateId;
        templateStore.templateId = templateId.value
        templateName.value = resp.data.templateName
        headerText.value = resp.data.headerText;
        footer.value = resp.data.footer
        messageType.value = resp.data.msgType
        // templateName.value = resp.data.templateName;

        originalMsgcontent.value = resp.data.msgContent;
        msgContent.value = resp.data.msgContent
        segmentIds.value = resp.data.segmentLists.replace('Segment:', '') //for showing prevoius selected segments
        segmentStore.prevSelectedIds = new Set(segmentIds.value.split(','))
        scheduleStore.scheduleDate = getUTCToLocalTime(resp.data.scheduleDate) //utc time to local time
        isTemplateSelected.value = true;
        scheduleStore.scheduleType = resp.data.scheduleType
        scheduleStore.frequencyType = resp.data.frequencyType
        scheduleStore.startDate = getUTCToLocalTime(resp.data.startDate)
        scheduleStore.endDate = getUTCToLocalTime(resp.data.endDate)
        scheduleStore.recuScheduleDate = scheduleStore.startDate?.substring(0, 10) + ' to ' + scheduleStore.endDate?.substring(0, 10)
        scheduleStore.isSentOut = resp.data.status == 1 ? true : false
        scheduleStore.recuScheduleTime = isEmpty(scheduleStore.scheduleDate) ? scheduleStore.startDate.substring(10) : scheduleStore.scheduleDate.substring(10)
        // showPrevSegmentSelection(segmentIds.value);
        getVaribleMappings(resp.data.placeholderMap)
    } catch (err) {
    }
}
function getVaribleMappings(placeholders: string) { //in edit mode
    placeholderMappings.value = placeholders.split("||", -1);
    placeholderMappings.value.forEach((e, index) => {
        if (!isEmpty(e)) {
            const match = '{{' + (index + 1) + '}}'
            templateText.value = templateText.value.replace(match, e);
        }
    })
    // console.log(placeholderMappings.value);
}

const validHeader = computed(() => {
    if (messageType.value == 'TEXT') return true;
    if (messageType.value == 'IMAGE') {
        const regex = /\.(jpg|jpeg|png)$/i
        return regex.test(headerText.value) ? true : 'please enter valid image url'
    }
    else if (messageType.value == 'VIDEO') {
        return headerText.value?.toLowerCase().endsWith('.mp4') ? true : 'please enter valid video url'
    }
    else if (messageType.value == 'DOCUMENT') {
        return headerText.value?.toLowerCase().endsWith('.pdf') ? true : 'please enter valid pdf url'
    }
})
async function shortenURL() { //getting msgcontent after shortening urls
    const text = await URLShortner(longURL.value);
    longURL.value = text
}


const step1Completed = computed(() => { //checking step1 validated
    return !(isEmpty(campaignName.value) || !(validHeader.value == true ? true : false) || !(isTemplateSelected.value)) //if camp name is empty or header not a valid one
})
const step2Completed = computed(() => {
    return segmentStore.isSegmentSelected
})

onMounted(() => {
    segmentStore.searchByName = ''
    segmentStore.segmentList.length = 0
    segmentStore.channelType = channelType.value
    scheduleStore.isSentOut = false
    segmentStore.fetchSegmentList('')
    scheduleStore.intializeData()
    templateStore.channelType = channelType.value
    templateStore.fetchTemplates('Search')
})

fetchCampaign(route.params.id)

watch(templateId, (newId, oldId) => {
    // console.log(oldId, newId)
    const regex1 = /{{.*?}}/g;
    const matches = msgContent.value.match(regex1);
    templateText.value = msgContent.value
    if (matches) {
        varMapping.value.length = matches.length;
        sampleTextMap.value.length = matches.length
        if (route.params.id && isEmpty(oldId)) //in edit mode we have items in it.
        {
            for (let i = 0; i < matches.length; i++) {
                const variableNum = Number(matches[i][2]) //sequnce of variable that cliked.
                const tag = placeholderMappings.value[variableNum - 1]
                const previewText = ref('')
                const sampleText = ref('')
                if (!tag.includes('${')) {
                    previewText.value = '<span id="' + (variableNum) + '"> ' + tag + '</span>' //for identifying unique variable
                    sampleText.value = '<span id="' + (variableNum) + '"> ' + tag + '</span>'

                }
                else if (tag.includes('coupon')) {
                    previewText.value = '<span id="' + (variableNum) + '">Coupon</span>' //for identifying unique variable
                    sampleText.value = '<span id="' + (variableNum) + '"> ' + 'AFAZEDNG' + '</span>'

                }
                else {
                    mergeTagsMap.forEach((value, key) => {

                        if (value.tag === tag) {
                            previewText.value = '<span id="' + (variableNum) + '"> ' + key + '</span>' //for identifying unique variable
                            sampleText.value = '<span id="' + (variableNum) + '"> ' + value.preview + '</span>'
                        }

                    })
                }
                varMapping.value[variableNum - 1] = previewText.value
                sampleTextMap.value[variableNum - 1] = sampleText.value
                msgContent.value = msgContent.value.replace(matches[i], previewText.value)
                templateText.value = templateText.value.replace(matches[i], sampleText.value);
            }

        }
        else {
            placeholderMappings.value.length = matches.length
            for (let i = 0; i < matches.length; i++) {
                const variableNum = Number(matches[i][2]) //sequnce of variable that cliked.
                const variable = '<span id="' + (variableNum) + '">{{' + (variableNum) + '}}</span>' //for identifying unique variable
                varMapping.value[variableNum - 1] = variable
                placeholderMappings.value[variableNum - 1] = matches[i]; // {{1}},{{2}}, etc.
                sampleTextMap.value[variableNum - 1] = variable //for sample texts in preview
                msgContent.value = msgContent.value.replace(matches[i], variable)
                templateText.value = templateText.value.replace(matches[i], variable);
            }
        }
    }
    const regex2 = /(<span.*?<\/span>)|(\{\{.*?\}\})|(\$.*?\})|(\#.*?\#)|(\$.*?\})/g
    let lastIndex = 0
    segments.value.length = 0
    let match
    while ((match = regex2.exec(msgContent.value)) !== null) {
        if (match.index > lastIndex) {
            segments.value.push(msgContent.value.slice(lastIndex, match.index))
        }
        segments.value.push(match[0])
        lastIndex = regex2.lastIndex
    }
    if (lastIndex < msgContent.value.length) {
        segments.value.push(msgContent.value.slice(lastIndex))
    }

}
)

function copyText() {
    navigator.clipboard.writeText(longURL.value)
}
watch(msgContent, () => {
    if (!msgContent.value.includes('<span')) return //note if same template clicked again no change
    const regex = /(<span.*?<\/span>)|(\{\{.*?\}\})|(\$.*?\})|(\#.*?\#)|(\$.*?\})/g
    let lastIndex = 0
    segments.value.length = 0
    let match
    while ((match = regex.exec(msgContent.value)) !== null) {
        if (match.index > lastIndex) {
            segments.value.push(msgContent.value.slice(lastIndex, match.index))
        }
        segments.value.push(match[0])
        lastIndex = regex.lastIndex
    }
    if (lastIndex < msgContent.value.length) {
        segments.value.push(msgContent.value.slice(lastIndex))
    }

})

function isPlaceholder(segment: string) {

    return (
        // segment.startsWith('{') && segment.endsWith('}') ||
        segment.startsWith('$') ||
        segment.startsWith('#') ||
        segment.startsWith('<span') && segment.endsWith('</span>')
    )
}

function openSelectionModal(match: string) {
    const parts = match.split('<span id="') //to fetch index from id="1"
    selectedVariable.value = Number(parts[1].substring(0, parts[1].indexOf('"')))

    showVariableSelection.value = true
}
function replaceVariable(placeholder: Variable) {

    const oldPreviewtag = varMapping.value[selectedVariable.value - 1]
    const newPreviewtag = '<span id="' + selectedVariable.value + '">' + placeholder.previewPlacholder + '</span>'
    const oldSampleText = sampleTextMap.value[selectedVariable.value - 1]
    const newSampleText = '<span id="' + selectedVariable.value + '">' + placeholder.sampleText + '</span>'
    //update in array for tracking
    varMapping.value[selectedVariable.value - 1] = newPreviewtag //replace the old selection 
    placeholderMappings.value[selectedVariable.value - 1] = placeholder.mergeTag
    sampleTextMap.value[selectedVariable.value - 1] = newSampleText
    //update template content
    templateText.value = templateText.value.replace(oldSampleText, newSampleText)
    msgContent.value = msgContent.value.replace(oldPreviewtag, newPreviewtag)

}

const dialogVisibleUpdate = (val: boolean) => {
    console.log(val)
    showFileManager.value = val;
}
function setImageUrl(url: string) {
    headerText.value = url;
    dialogVisibleUpdate(false)
}

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
                            <p v-show="!step1Completed" class="ma-0">
                                <VIcon icon="tabler-info-circle" color="error" size="30"></VIcon>
                            </p>
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
                        <V-Col cols="12" lg="3" sm="3" md="3"></V-Col>
                        <VCol cols="12" lg="1" sm="4" md="3">
                            <div class="d-flex justify-end mx-1 mt-1" v-show="!step1Completed">
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
                            <VCol cols="12" lg="2" md="2" sm="4">
                                <p class="mt-2  text-subtitle-1 font-weight-medium">Campaign Name

                                </p>
                            </VCol>
                            <VCol lg="6" md="5" sm="8">
                                <AppTextField placeholder="campaign name" v-model="campaignName"
                                    :rules="[requiredValidator]">
                                </AppTextField>
                            </VCol>
                        </VRow>
                        <VRow>
                            <VCol cols="12" lg="2" md="2" sm="4">
                                <p class=" text-subtitle-1 font-weight-medium"> Templates</p>
                            </VCol>
                            <VCol cols="12" lg="9" md="10" sm="8" class="mt-n3">
                                <VBtn v-for="item in templateStore.templates" :key="item" class="mr-3 mb-1 text-none"
                                    variant="outlined"
                                    :color="templateId == item.templateId ? componentColor : 'disabled'"
                                    @click="selectTemplate(item)">{{ item.templateName }}</VBtn>
                                <VBtn size="40" color="#97999D" class="mt-n1" @click="showTemplates = !showTemplates">
                                    <VIcon icon=" tabler-chevron-right"></VIcon>
                                </VBtn>
                            </VCol>
                        </VRow>
                        <VRow v-if="messageType !== 'TEXT'">
                            <VCol cols="12" lg="2" md="2" sm="4">
                                <p class="mt-2 text-subtitle-1 font-weight-medium">Header </p>
                            </VCol>
                            <VCol cols="12" lg="6" md="5" sm="8">
                                <AppTextField :placeholder="'enter ' + messageType.toLowerCase() + ' url'"
                                    v-model="headerText" :disabled="messageType == 'TEXT'">
                                    <template #append-inner v-if="messageType == 'IMAGE'">
                                        <VIcon icon="tabler-photo-up" :color="componentColor"
                                            @click="showFileManager = !showFileManager"></VIcon>
                                    </template>
                                </AppTextField>
                                <p class="text-caption text-red ml-1" v-show="validHeader !== true">{{ validHeader
                                    }}
                                </p>
                            </VCol>

                        </VRow>

                        <VRow>
                            <VCol cols="12" lg="8" md="7" sm="12">
                                <VRow>
                                    <VCol cols="12" lg="3" md="3" sm="4">
                                        <p class="mt-2 text-subtitle-1 font-weight-medium">Body </p>
                                    </VCol>
                                    <VCol cols="12" lg="9" md="8" sm="7"
                                        v-show="!showPreview || $vuetify.display.mdAndUp">
                                        <div class="textarea border-md  pa-3 rounded-lg text-subtitle-1 font-weight-medium"
                                            :style="{ minHeight: '400px', width: 'auto' }">
                                            <p class="preserve-whitespace">
                                                <span v-for="(segment, index) in segments" :key="index">
                                                    <div v-if="isPlaceholder(segment)" class="chip-container">
                                                        <VBtn :color="componentColor" variant="flat" size="small"
                                                            class="text-none" @click="openSelectionModal(segment)"
                                                            v-html="segment" height="23">

                                                        </VBtn>
                                                    </div>
                                                    <span v-else>{{ segment }}</span>
                                                </span>
                                            </p>
                                        </div>
                                    </VCol>
                                    <VCol cols="12" lg="9" md="8" sm="7"
                                        v-show="showPreview && $vuetify.display.smAndDown">
                                        <div>
                                            <Widget v-show="msgContent" :data="propsForPreview" class="device-width" />
                                        </div>
                                    </VCol>

                                    <VCol cols="12" lg="1" md="1" sm="1"
                                        v-show="$vuetify.display.smAndDown && msgContent">
                                        <VBtn @click="showPreview = !showPreview" :color="componentColor">{{ showPreview
            ?
            'Editor' : 'Preview' }}
                                        </VBtn>
                                    </VCol>
                                </VRow>
                                <VRow>
                                    <VCol cols="12" lg="3" md="3" sm="4">
                                        <p class="mt-2 text-subtitle-1 font-weight-medium">URL Shortner </p>
                                    </VCol>
                                    <VCol cols="12" lg="9" md="8" sm="8">
                                        <AppTextField placeholder="enter url" v-model="longURL">
                                        </AppTextField>
                                        <div class="text-right">
                                            <VBtn @click="shortenURL()" size="40" class="ma-2">
                                                <VIcon icon="tabler-arrow-right"></VIcon>
                                            </VBtn>
                                            <VBtn @click="copyText" size="40">
                                                <VIcon icon="tabler-copy"></VIcon>
                                            </VBtn>
                                        </div>
                                    </VCol>
                                </VRow>
                                <VRow>
                                    <VCol cols="12" md="3" sm="4" lg="3">
                                        <span class="text-subtilte-1 font-weight-medium">Send Test <VTooltip
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
                                    <VCol cols="12" lg="9" md="8" sm="8">
                                        <v-textarea rows="2" v-model="testMobiles" placeholder="Enter mobile numbers">
                                        </v-textarea>
                                        <div class="text-right">
                                            <VBtn color='#97999D' disabled class="ma-2">Send
                                            </VBtn>
                                        </div>
                                    </VCol>
                                </VRow>
                            </VCol>
                            <VCol cols="12" lg="4" md="5" sm="12" v-show="$vuetify.display.mdAndUp">
                                <div>
                                    <Widget v-show="msgContent" :data="propsForPreview" class="device-width" />
                                </div>
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
                <VExpansionPanelTitle disable-icon-rotate :color="componentColor" v-if="panelExpand == 'two'"
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
                <VExpansionPanelTitle :color="componentColor" v-else class="text-white">
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
                <VExpansionPanelTitle disable-icon-rotate :color="componentColor" v-if="panelExpand == 'three'"
                    class="text-white">
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
                <VExpansionPanelTitle :color="componentColor" v-else class="text-white">
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
                            <p class="heading-1">Status</p>
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
        <!-- </VCardText> -->
        <VDialog v-model:model-value="showFileManager" :width="$vuetify.display.smAndDown ? 'auto' : 1200">
            <DialogCloseBtn @click="dialogVisibleUpdate(false)" />
            <VCard>
                <VCardTitle>
                    <div class="text-center text-h6">Upload Images</div>
                </VCardTitle>
                <VCardText>
                    <FileManager :nameValue="userData.userName" @imageurl="setImageUrl" />
                </VCardText>
            </VCard>
        </VDialog>

        <VariableDailog v-model:is-dialog-visible="showVariableSelection" :channelType="channelType"
            @submitPlaceholder="replaceVariable" />
        <Templates v-model:is-dialog-visible="showTemplates" @submitTemplate="selectTemplate"
            :channelType="channelType" />

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

.preserve-whitespace {
    white-space: pre-wrap;
}

.chip-container {
    position: relative;
    display: inline-block;
}

.chip {
    background-color: #4444ff;
    cursor: pointer;
}
.device-width {
    max-width: 310px;
    min-height: 530px;
    max-height: 600px;
}
</style>
