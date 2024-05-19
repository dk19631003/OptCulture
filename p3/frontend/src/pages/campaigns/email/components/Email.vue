<script setup lang="ts">
import { ref, watch } from "vue";
import { dateValidator, requiredValidator } from "@/@core/utils/validators";
import { isEmpty } from "@/@core/utils";
import axios from "@axios";
import router from "@/router";
import { getUTCTime, getUTCToLocalTime } from "@/@core/utils/localeFormatter";
import rocket from "@/assets/images/rocket.png";
import Templates from "./Templates.vue";
import Emailbee from "@/pages/emailBee/components/Emailbee.vue";
import { VsAccordion, VsAccordionItem } from '@vuesimple/vs-accordion';
import SegmentsStep from '@/pages/campaigns/common/SegmentsStep.vue';
import { useSegmentsStore } from '@/pages/campaigns/common/segmentsStore'
import { useScheduleDataStore } from '@/pages/campaigns/common/scheduleDataStore';
import { useTemplateStore } from '@/pages/campaigns/common/templateStore';
import ScheduleStep from '@/pages/campaigns/common/ScheduleStep.vue';
import { oldToNewMergeTags } from '@/@core/utils/index'
import { fetchUserChannelsCount } from '@/pages/campaigns/common/userChannelCount'
import { themeConfig } from '@themeConfig'

interface Segment {
  segRuleName: string;
  description: string;
  segRuleId: number;
  totEmailSize: number;
  totMobileSize: number;
  totSize: number;
  type: string;
}

interface Template {
  templateId: number;
  templateName: string;
  templateContent: string;
  templateRegId: number;
  senderId: string;
  msgType: string;
  jsonContent: string;
}
const route = useRoute()
const scheduleType = ref("ONE TIME");
const isScheduled = ref(false);
const campaignName = ref();
const subject = ref("");
const fromName = ref("");
const emailFrom = ref('Select from email address');
const emailTo = ref('Select reply email address');
const emailFromDomain = ref('Select domain')
const isSegmentSelected = ref(false);
const templateId = ref();
const isAllDetailsValid = ref(false);
const isTemplateSelected = ref(false);
const showTemplates = ref(false);
const msgContent = ref("");
const originalMsgcontent = ref("");
const segmentPage = ref(-1);
const templatePage = ref(-1);
const segments = ref<string[]>([]);
const segmentName = ref("");
const selectedSegmentIds = ref(new Set());
const selectedSegmentNames = ref(new Set());
const prevselectedSegmentList = ref<Segment[]>([]);
const scheduleDate = ref();
const recuScheduleTime = ref();
const recuScheduleDate = ref("");
const frequencyType = ref(); //monthly,weekly,..
const isRangeDateValid = ref(false);
const startDate = ref();
const endDate = ref();
const isOneTimeDateValid = ref(false);
const panelExpand = ref(0);
const step1 = ref(false);
const step2 = ref(true);
const step3 = ref(true);
const segmentList = ref<Segment[]>([]);
const segmentIds = ref("");
const channelType = ref("Email");
const messageType = ref("");
const varMapping = ref<string[]>([]); // mapping array for varible and placeholder for rendering msg content
const sampleTextMap = ref<string[]>([]); // mapping array for preview template
const templateText = ref("");
const userData = JSON.parse(localStorage.getItem("userData") || "null");
const extractedName = ref("");
const jsonContent = ref("");
const isTemplateChange = ref(false);
const saveContentPopup = ref(false);
const addEmailbeeVisible = ref(false)
const templateName = ref()
const segmentStore = useSegmentsStore()
const scheduleStore = useScheduleDataStore()
const templateStore = useTemplateStore()
const isTemplateValid = ref(false)
const fromEmailList = ref<object[]>([])
const replyToEmailList = ref<object[]>([])
const channelSettingToast = ref(false)
const channelConfigCount = ref()

if (userData) extractedName.value = userData.userName.split("__")[0];

function selectTemplate(item: Template) {
  if (item) {
    const previousTemplateId = templateId.value;
    msgContent.value = item.templateContent;
    templateId.value = item.templateId;
    templateStore.templateId = item.templateId
    originalMsgcontent.value = item.templateContent;
    messageType.value = item.msgType;
    jsonContent.value = item.jsonContent;
    isTemplateSelected.value = true;
    templateName.value = item.templateName
    const currentTemplateId = templateId.value;
    const templateChanged = previousTemplateId !== currentTemplateId;
    if (templateChanged) {
      isTemplateChange.value = true;
    } else {
      isTemplateChange.value = false;
    }
    //expandPanel(1);
  }
}

async function scheduleCampaign(saveType: string) {
  if (isScheduled.value) return; //already clicked
  isScheduled.value = true;
  const reqBody = {
    commId: route.params.id,
    campaignName: campaignName.value,
    channelType: channelType.value,
    subject: subject.value,
    fromName: fromName.value,
    fromEmail: emailFrom.value,
    replyEmail: emailTo.value,
    segmentIds: 'Segment:' + segmentStore.selectedIds, // segemnt ids joining by ','
    scheduleDate: getUTCTime(scheduleStore.scheduleDate),
    createdDate: new Date().toISOString(),
    scheduleType: scheduleStore.scheduleType,
    frequencyType: scheduleStore.frequencyType,
    startDate: getUTCTime(scheduleStore.startDate),
    endDate: getUTCTime(scheduleStore.endDate),
    templateId: templateId.value,
    messageContent: originalMsgcontent.value,
    jsonContent: jsonContent.value,
    status: saveType
  };
  try {
    const resp = await axios.post("/api/campaigns/schedule", reqBody);
    router.push("/campaigns/list");
  } catch (err) { }
}
async function getUserEmailDomains() {
  try {
    const resp = await axios.get('/api/campaigns/approved-emails')
    console.log(resp.data)
    resp.data.fromEmails.forEach(email => {
      fromEmailList.value.push({ 'title': email, 'value': email })
    })
    resp.data.replyToEmails.forEach(email => {
      replyToEmailList.value.push({ 'title': email, 'value': email })
    })
  } catch (err) {
    console.log(err)
  }
}
function updateJsonContent(jsonData: string) {
  jsonContent.value = jsonData;
}


function validateDate(val: string) {
  if (isEmpty(val)) {
    isOneTimeDateValid.value = false;
    return "Date required for scheduling the campaign";
  } else if (scheduleType.value == "ONE TIME") {
    //onetime
    isOneTimeDateValid.value = dateValidator(val);
    if (isOneTimeDateValid.value) {
      // scheduleDate.value=val;
      return true;
    }
    return "Please select a future date and time";
  } else {
    //recurring type has two dates

    const dates = recuScheduleDate.value.split("to");
    if (dates.length < 2 && dates.length > 0) {
      isRangeDateValid.value = false;
      return "Please select schedule ending date also";
    } else {
      const date1 = dates[0].trim();
      const date2 = dates[1].trim();

      if (date1 == date2) return "Starting and ending dates should not be same";
      if (isEmpty(recuScheduleTime.value)) return "Please select schedule time";
      startDate.value = date1 + " " + recuScheduleTime.value;
      endDate.value = date2 + " " + recuScheduleTime.value;
      const valid1 = dateValidator(startDate.value);
      const valid2 = dateValidator(endDate.value);
      isRangeDateValid.value = valid1 && valid2;
      if (!isRangeDateValid.value) {
        return "Please select a future dates and time";
      }
      return true;
    }
  }
}

function expandPanel(open: number) {
  // console.log(open)
  if (open == 1) {
    step1.value = true;
  }
  if (open == 3) {
    step3.value = true;
  }
  panelExpand.value = -1;
  setTimeout(() => {
    panelExpand.value = open;
  }, 100)
}

function changeAccordian(e) {
  if (e.visible && e.index == 0) {
    step1.value = false;
  } else {
    step1.value = true;
  }

  if (e.visible && e.index == 1) {
    step2.value = false;
  } else {
    step2.value = true;
  }

  if (e.visible && e.index == 2) {
    step3.value = false;
  } else {
    step3.value = true;
  }
}

async function fetchCampaign(campaignId: number) {
  if (isEmpty(campaignId)) return;
  try {
    const resp = await axios.get("/api/campaigns/campaign-id", {
      params: {
        "campaignId": campaignId,
      },
    });
    campaignName.value = resp.data.campaignName;
    subject.value = resp.data.subject;
    fromName.value = resp.data.fromName;
    emailFrom.value = resp.data.fromEmail;
    emailTo.value = resp.data.replyEmail;

    templateId.value = resp.data.templateId;
    templateStore.templateId = templateId.value
    messageType.value = resp.data.msgType;
    templateName.value = resp.data.templateName;

    originalMsgcontent.value = resp.data.msgContent;
    msgContent.value = resp.data.msgContent;
    segmentIds.value = resp.data.segmentLists.replace("Segment:", "") //for showing prevoius selected segments
    segmentStore.prevSelectedIds = new Set(segmentIds.value.split(','))
    scheduleStore.scheduleDate = getUTCToLocalTime(resp.data.scheduleDate) //utc time to local time
    isTemplateSelected.value = true;
    scheduleStore.isSentOut = resp.data.status == 1 ? true : false
    scheduleStore.scheduleType = resp.data.scheduleType
    scheduleStore.frequencyType = resp.data.frequencyType
    scheduleStore.startDate = getUTCToLocalTime(resp.data.startDate)
    scheduleStore.endDate = getUTCToLocalTime(resp.data.endDate)
    scheduleStore.recuScheduleDate = scheduleStore.startDate?.substring(0, 10) + ' to ' + scheduleStore.endDate?.substring(0, 10)
    scheduleStore.recuScheduleTime = isEmpty(scheduleStore.scheduleDate) ? scheduleStore.startDate.substring(10) : scheduleStore.scheduleDate.substring(10)
    jsonContent.value = resp.data.jsonContent;
  } catch (err) { }
}

function isValidTemplate(valid) {
  isTemplateValid.value = valid
}

function updateHtmlContent(htmlData) {
  if (htmlData) {
    originalMsgcontent.value = htmlData;
  }
}

watch(jsonContent, () => {
  const [text, length] = oldToNewMergeTags(jsonContent.value)
  jsonContent.value = text;
})
/* Next step open after Template Detail Save*/
function nextStepOpen() {
  saveContentPopup.value = false;
  expandPanel(1);
}

function isTemplateAddVisible(isOpen) {
  addEmailbeeVisible.value = isOpen;
}

/*Validation From Email*/
const validEmailFrom = computed(() => {
  const value = emailValidator(emailFrom.value);
  return value;
});

/*Validation ReplyTo email*/
const validEmailReply = computed(() => {
  const value = emailValidator(emailTo.value);
  return value;
});


const recDateValidation = computed(() => {
  return validateDate(recuScheduleDate.value);
});

const step1Completed = computed(() => { //checking step1 validated
  return !(isEmpty(campaignName.value) || isEmpty(subject.value) || isEmpty(fromName.value) || isEmpty(emailFrom.value) ||
    !(validEmailFrom.value == true ? true : false) || isEmpty(emailTo.value) || !(validEmailReply.value == true ? true : false) || !(isTemplateValid.value == true ? true : false)) //if camp name,subject,from name is empty or from email , reply email not a valid one
})

const step2Completed = computed(() => {
  return segmentStore.isSegmentSelected
})

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

const themeColor = computed(() => {
  if (themeConfig.app.theme.value == 'dark') {
    return '#2F3349'
  }
  return '#FFFFFF'
})
watch(
  [
    campaignName,
    subject,
    fromName,
    emailFrom,
    emailTo,
    jsonContent,
    isOneTimeDateValid,
    isRangeDateValid,
    scheduleType,
    isTemplateSelected,
    frequencyType,
    validEmailFrom,
    validEmailReply,
    originalMsgcontent,
    showTemplates,
    step1Completed,
    step2Completed,
    isTemplateChange,
    isTemplateValid
  ],
  () => {
    //for checking is campaign ready for schedule
    isAllDetailsValid.value =
      ((isOneTimeDateValid.value && scheduleType.value == "ONE TIME") ||
        (isRangeDateValid.value && scheduleType.value == "RECURRING")) &&
      isTemplateValid.value &&
      !isEmpty(originalMsgcontent.value.trim()) &&
      !isEmpty(campaignName.value.trim()) &&
      !isEmpty(subject.value.trim()) &&
      !isEmpty(fromName.value.trim()) &&
      step2Completed.value &&
      (!isEmpty(frequencyType.value) || scheduleType.value == "ONE TIME") &&
      !isEmpty(emailFrom.value) &&
      validEmailFrom.value == true &&
      !isEmpty(emailTo.value) &&
      validEmailReply.value == true;
  }
);


fetchCampaign(route.params.id)

watch(templateId, () => {
  const regex1 = /{{.*?}}/g;
  const matches = msgContent.value.match(regex1);
  templateText.value = msgContent.value;
  if (matches) {
    varMapping.value.length = matches.length;
    sampleTextMap.value.length = matches.length;
  }
  const regex2 =
    /(<span.*?<\/span>)|(\{\{.*?\}\})|(\$.*?\})|(\#.*?\#)|(\$.*?\})/g;
  let lastIndex = 0;
  segments.value.length = 0;
  let match;
  while ((match = regex2.exec(msgContent.value)) !== null) {
    if (match.index > lastIndex) {
      segments.value.push(msgContent.value.slice(lastIndex, match.index));
    }
    segments.value.push(match[0]);
    lastIndex = regex2.lastIndex;
  }
  if (lastIndex < msgContent.value.length) {
    segments.value.push(msgContent.value.slice(lastIndex));
  }
});

watch(msgContent, () => {
  if (!msgContent.value.includes("<span")) return; //note if same template clicked again no chenge
  const regex =
    /(<span.*?<\/span>)|(\{\{.*?\}\})|(\$.*?\})|(\#.*?\#)|(\$.*?\})/g;
  let lastIndex = 0;
  segments.value.length = 0;
  let match;
  while ((match = regex.exec(msgContent.value)) !== null) {
    if (match.index > lastIndex) {
      segments.value.push(msgContent.value.slice(lastIndex, match.index));
    }
    segments.value.push(match[0]);
    lastIndex = regex.lastIndex;
  }
  if (lastIndex < msgContent.value.length) {
    segments.value.push(msgContent.value.slice(lastIndex));
  }
});

watch(scheduleDate, () => {
  validateDate(scheduleDate.value);
});

function emailValidator(email: string) {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email) ? true : "please enter valid email";
}

const camapaignData = computed(() => {
  return {
    step1Completed: step1Completed.value,
    step2Completed: step2Completed.value,
    channelType: channelType.value
  }
})
getUserEmailDomains()

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
    <VSnackbar timeout="15000" v-model="channelSettingToast" location="top" color="error" variant="tonal" width="500px">
      <VIcon icon="tabler-info-circle" color="error" size="30"></VIcon>
      <span class="text-black ">{{ ' Channel Gateways are not configured' }}</span>
      <template #actions>
        <VBtn color="info" @click="router.push('/channels')">
          Configure
        </VBtn>
      </template>
    </VSnackbar>
    <VsAccordion :is-box="true" variant="accordion" :active="panelExpand" is-compact>
      <!-- Campaign Details -->
      <VsAccordionItem class="mb-2" @change.self="changeAccordian" :key="0">
        <template #accordion-trigger>
          <VRow class="d-flex justify-space-between align-center" v-if="!step1">
            <VCol cols="12" lg="3" sm="3" md="3">
              <p class="ma-0">Campaign Details</p>
            </VCol>

            <VCol cols="12" lg="1" sm="1" md="1">
              <p v-show="!step1Completed" class="ma-0">
                <VIcon icon="tabler-info-circle" color="error" size="30"></VIcon>
              </p>
            </VCol>
          </VRow>
          <VRow class=" justify-space-between" v-else>
            <VCol cols="12" lg="4" sm="3" md="3">
              <p class="heading-1">Campaign Name</p>
              <p class="heading-2">{{ campaignName }}</p>
            </VCol>
            <VCol cols="12" lg="4" sm="3" md="3">
              <p class="heading-1">Template Name</p>
              <p class="heading-2">{{ templateName }}</p>
            </VCol>
            <VCol cols="12" lg="3" sm="3" md="3"></VCol>
            <VCol cols="12" lg="1" sm="4" md="4">
              <div class="d-flex justify-end mx-1 mt-1" v-if="!step1Completed">
                <p class=" mt-3 mr-1">
                  <VIcon icon="tabler-info-circle" color="error" size="30"></VIcon>
                </p>
                <!-- <p color="error" class="text-caption text-red mt-4">Information missing</p> -->
              </div>
            </VCol>
          </VRow>
        </template>
        <!-- <VCard class="mt-5" elevation="0" z-index="0"> -->
        <!-- <VCardText background-color="#FFFFFF"> -->
        <div class="accordion-panel mt-2 " :style="{ backgroundColor: `${themeColor}` }">
          <VCardText>
            <VRow>
              <VCol cols="12" lg="3" md="2" sm="4">
                <p class="mt-2 text-subtitle-1 font-weight-medium">
                  Campaign Name
                </p>
              </VCol>
              <VCol lg="6" md="6" sm="8">
                <AppTextField placeholder="campaign name" v-model="campaignName" :rules="[requiredValidator]">
                </AppTextField>
              </VCol>
            </VRow>
            <VRow>
              <VCol cols="12" lg="3" md="2" sm="4">
                <p class="mt-2 text-subtitle-1 font-weight-medium">
                  From Name
                </p>
              </VCol>
              <VCol lg="6" md="6" sm="8">
                <AppTextField placeholder="from name" v-model="fromName" :rules="[requiredValidator]">
                </AppTextField>
              </VCol>
            </VRow>
            <VRow>
              <VCol cols="12" lg="3" md="2" sm="4">
                <p class="mt-2 text-subtitle-1 font-weight-medium">
                  From Email
                </p>
              </VCol>
              <VCol cols="12" lg="6" md="6" sm="8">
                <AppSelect :items="fromEmailList" v-model="emailFrom" placeholder="Select from email address" />
              </VCol>
            </VRow>
            <VRow>
              <VCol cols="12" lg="3" md="2" sm="4">
                <p class="mt-2 text-subtitle-1 font-weight-medium">
                  ReplyTo email
                </p>
              </VCol>
              <!-- <VCol cols="12" lg="3" md="3" sm="3">
                <AppTextField placeholder="reply to email" v-model="emailTo" :rules="[requiredValidator, emailValidator]">
                </AppTextField>
              </VCol> -->
              <VCol cols="12" lg="6" md="6" sm="8">
                <AppSelect :items="replyToEmailList" v-model="emailTo" />
              </VCol>

            </VRow>
            <VRow>
              <VCol cols="12" lg="3" md="2" sm="4">
                <p class="mt-2 text-subtitle-1 font-weight-medium">
                  Subject
                </p>
              </VCol>
              <VCol cols="12" lg="6" md="6" sm="8">
                <AppTextField placeholder="enter subject" v-model="subject" :rules="[requiredValidator]">
                </AppTextField>
              </VCol>
              <VCol> </VCol>
            </VRow>
            <VRow>
              <VCol cols="12" lg="3" md="3" sm="4">
                <p class="text-subtitle-1 font-weight-medium">Templates</p>
              </VCol>
              <VCol cols="12" lg="9" md="10" sm="8" class="">
                <VBtn v-for="item in templateStore.templates" :key="item" class="mr-3 mb-1 text-none" variant="outlined"
                  :color="templateId == item.templateId
      ? '#00CFE8'
      : 'disabled'
      " @click="selectTemplate(item)">{{ item.templateName }}</VBtn>
                <VBtn size="40" color="#97999D" class="mt-n1" @click="showTemplates = !showTemplates">
                  <VIcon icon=" tabler-chevron-right"></VIcon>
                </VBtn>
              </VCol>
            </VRow>
            <VRow>
              <VCol cols="12" lg="12" md="12" sm="12" class="scroll">
                <Emailbee templateType="selctedChampain" :templateJson="jsonContent"
                  v-model:isTemplateChange="isTemplateChange" :saveContentPopup="saveContentPopup"
                  beeConfigId="bee-campain-email-template" @updateJsonContent="updateJsonContent"
                  @updateHtmlContent="updateHtmlContent" @isValidTemplate="isValidTemplate"
                  @close-dialog="saveContentPopup = false" @nextStep="nextStepOpen" v-if="!addEmailbeeVisible" />
              </VCol>
            </VRow>
          </VCardText>
          <VCardText class="mt-10">
            <!-- <VRow class=" mt-n16"> -->
            <div :class="{
      'text-center': true,
      'd-flex': true,
      'justify-end': true,

    }">
              <VBtn color='email-clr' @click="saveContentPopup = true" :disabled="isEmpty(campaignName) ||
      isEmpty(subject) ||
      isEmpty(fromName) ||
      !(validEmailFrom == true ? true : false) ||
      !(validEmailReply == true ? true : false) ||
      !isTemplateValid
      " class="text-white">
                Next</VBtn>
            </div>
          </VCardText>
        </div>
      </VsAccordionItem>

      <!-- Segment Details -->
      <VsAccordionItem class="mb-2" @change.self="changeAccordian" :key="1">
        <template #accordion-trigger v-if="!step2">
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
        </template>
        <template #accordion-trigger v-else>
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
            <VCol cols="12" lg="1" sm="1" md="3">
              <p class="mt-3" v-if="!step2Completed">
                <VIcon icon="tabler-info-circle" color="error" size="30"></VIcon>
              </p>
            </VCol>
          </VRow>
        </template>

        <div class="accordion-panel mt-2" :style="{ backgroundColor: `${themeColor}` }">
          <SegmentsStep :campaignType="channelType" @goToNextStep="expandPanel(2)"></SegmentsStep>
        </div>
      </VsAccordionItem>

      <!-- Schedule Campaign -->
      <VsAccordionItem class="mb-2" @change.self="changeAccordian" :key="2">
        <template #accordion-trigger v-if="!step3">
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
        </template>
        <template #accordion-trigger v-else>
          <VRow class=" justify-space-between mb-n2">
            <VCol cols="12" lg="4" sm="3" md="3">
              <p class="heading-1">Schedule</p>
              <p class="heading-2">{{ scheduleStore.scheduleType }}</p>
            </VCol>
            <VCol cols="12" lg="4" sm="3" md="3">
              <p class="heading-1">Occurence</p>
              <p class="heading-2">{{ scheduleStore.scheduleType=='ONE TIME'?'ONE TIME':scheduleStore.frequencyType
                }}</p>
            </VCol>
            <VCol cols="12" lg="3" sm="3" md="3">
              <p class="heading-1">Status</p>
              <p class="heading-2">{{ isScheduled?'Scheduled':'Not Scheduled' }}</p>
            </VCol>
            <VCol cols="12" lg="1" sm="1" md="3">
              <p class="mt-3" v-show="!scheduleStore.step3Completed">
                <VIcon icon="tabler-info-circle" color="error" size="30"></VIcon>
              </p>
            </VCol>
          </VRow>
        </template>
        <div class="accordion-panel mt-2" :style="{ backgroundColor: `${themeColor}` }">
          <ScheduleStep :camapaign-data="camapaignData" @save-campaign="scheduleCampaign" />
        </div>

      </VsAccordionItem>
    </VsAccordion>
    <!-- </VCardText> -->
    <!-- </VCard> -->
    <Templates v-model:is-dialog-visible="showTemplates" @isTemplateAddVisible="isTemplateAddVisible"
      @submitTemplate="selectTemplate" :channelType="channelType" v-if="showTemplates" />
  </div>
</template>
<style scoped>
.scroll {
  overflow-x: auto;
}

.custom-max-height {
  max-height: 290px;
  /* Max height for large breakpoints */
}

.launch-campaign {
  background: linear-gradient(130.92deg, #7c01ca -12.43%, #c50109 136.29%);
}
</style>

<style>
.vs-accordion__trigger {
  background-color: #00CFE8;
  border-radius: 0.375rem 0.375rem 0 0;
}

.vs-accordion--box {
  border-radius: 0.375rem !important;
}

.vs-accordion__button,
.vs-accordion--icon {
  color: white !important;
  font-weight: 500 !important;
}

:root {
  --vs-accordion-border-color: rgb(47, 43, 61) !important;
  --vs-accordion-border-radius: 0.375 rem;
  --vs-accordion-padding: 20px;
  --vs-accordion-compact-padding: 10px
}
</style>
