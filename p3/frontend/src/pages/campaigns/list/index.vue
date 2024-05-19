<script setup lang="ts">
import { ref } from 'vue';
import { VDataTableServer } from 'vuetify/labs/VDataTable'
import axios from '@axios'
import router from '@/router';
import { isEmpty } from '@/@core/utils';
import { dateFormater, getUTCToLocalTime } from '@/@core/utils/localeFormatter';
interface Communication {
    scheduleDate: string,
    startDate: string,
    endDate: string,
    periodType: string,
    status: number,
    scheduleType: string
}

import CreateCampaign from '../dialog/CreateCampaign.vue'
const campaignPerPage = ref(10)
const totalCampaigns = ref(0)
const campaignList = ref([])
const loading = ref(false)
const expandedRows = ref(Array(campaignList.value.length).fill(false));
const createCampaign = ref(false)
const searchValue = ref()
const criteria = ref()
const table_headers = [
    { title: 'Name', key: 'campaignName' },
    { title: 'Channel', key: 'channelType' },
    { title: 'Campaign Type', key: 'campaignType' },
    { title: 'Schedule Type', key: 'periodType' },
    { title: 'Date', key: 'scheduleDate' },
    { title: 'status', key: 'status' },
    { title: 'Actions', key: 'actions', sortable: false },
]
const searchFilter = [
    { title: 'Campaign Name', value: 'NAME' },
]
const channelTypes = ref(new Set(['SMS', 'Email', 'Whatsapp']))
async function loadCampaigns({ page, sortBy }) {

    loading.value = true;
    if (campaignPerPage.value.toString() === '-1') campaignPerPage.value = totalCampaigns.value;
    try {
        const reqBody = {
            channelTypes: [...channelTypes.value],
            pageNumber: page - 1,
            pageSize: campaignPerPage.value,
            criteria: criteria.value,
            searchValue: searchValue.value
        }
        // console.log(reqBody)
        const resp = await axios.post('/api/campaigns/list', reqBody
        );
        console.log(resp);
        campaignList.value = resp.data.content;
        totalCampaigns.value = resp.data.totalElements;
        expandedRows.value.fill(false, 0)
        loading.value = false
    } catch (err) {

    }
}
function editCampaign(item: any) {
    router.push('/campaigns/' + item.raw.channelType.toLowerCase() + '/' + item.raw.commId) //redirect to respective campaign page
}
const toggleRow = (index: number) => {
    expandedRows.value[index] = !expandedRows.value[index];
};
function getScheduleDate(commObj: Communication) {
    if (commObj.scheduleType == 'RECURRING') { //recurring schedule 
        // if(!isEmpty(commObj.value.scheduleDate)) return dateFormater(getUTCToLocalTime(commObj.value.scheduleDate));
        //    else{
        const startDate = dateFormater(getUTCToLocalTime(commObj.startDate.substring(0, 10)));
        const endDate = dateFormater(getUTCToLocalTime(commObj.endDate.substring(0, 10)));
        // console.log(startDate?.substring(0,10) + ' to ' + endDate?.substring(0,10))
        return startDate?.substring(0, 10) + ' to ' + endDate?.substring(0, 10);
        //    }
    }
    else return dateFormater(getUTCToLocalTime(commObj.scheduleDate));
}
function getStatus(item: Communication) {
    switch (item.status) {
        case 0: return 'Scheduled';
        case 1: return 'Launched';
        case 2: return 'In Progress';
        case -1: return 'Draft';
        case 9: return 'Failed';
        default: return 'Not yet created'
    }
}
</script>
<template>
    <div>
        <VCard>
            <VRow class="mt-2 ">
                <!-- <Div class="d-flex "> -->
                <VCol cols="12" lg="3" md="4" sm="6">
                    <AppSelect class="ml-4" placeholder="Select criteria" :items="searchFilter" v-model="criteria">
                    </AppSelect>
                </VCol>
                <VCol cols="12" lg="3" md="4" sm="5">
                    <AppTextField class="ml-2" placeholder="Enter value" v-model="searchValue"></AppTextField>
                </VCol>
                <VCol cols="12" lg="2" md="4" sm="6">
                    <VBtn class="ml-2" @click="loadCampaigns({ page: 1, sortBy: {} })">Search</VBtn>
                </VCol>
                <VCol cols="12" lg="4" md="4" sm="6">
                    <VBtn prepend-icon="tabler-plus" class="ml-2" @click="createCampaign = !createCampaign">Create
                        Campaign
                    </VBtn>
                </VCol>
            </VRow>
            <div class="d-flex mx-4 mb-4 justify-start">

                <div class="ma-2">
                    <VIcon :icon="channelTypes.has('SMS') ? 'tabler-square-check-filled' : 'tabler-square'"
                        @click="channelTypes.has('SMS') ? channelTypes.delete('SMS') : channelTypes.add('SMS')"
                        color="primary">
                    </VIcon> SMS
                </div>
                <div class="ma-2">
                    <VIcon :icon="channelTypes.has('Whatsapp') ? 'tabler-square-check-filled' : 'tabler-square'"
                        @click="channelTypes.has('Whatsapp') ? channelTypes.delete('Whatsapp') : channelTypes.add('Whatsapp')"
                        color="green">
                    </VIcon> Whatsapp
                </div>
                <div class="ma-2">
                    <VIcon :icon="channelTypes.has('Email') ? 'tabler-square-check-filled' : 'tabler-square'"
                        @click="channelTypes.has('Email') ? channelTypes.delete('Email') : channelTypes.add('Email')"
                        color="orange"></VIcon> Email
                </div>
                <!-- <div class="ma-2">
                    <VIcon :icon="channelTypes.has('Push Notifications') ? 'tabler-square-check-filled' : 'tabler-square'"
                        @click="channelTypes.has('Push Notifications') ? channelTypes.delete('Push Notifications') : channelTypes.add('Push Notifications')"
                        color="red">
                    </VIcon> Push Notifications
                </div> -->
                <!-- <div class="ma-2 d-flex ">
                    <VIcon :icon="channelTypes.has('Special Day') ? 'tabler-square-check-filled' : 'tabler-square'"
                        @click="channelTypes.has('Special Day') ? channelTypes.delete('Special Day') : channelTypes.add('Special Day')"
                        color="secondary">
                    </VIcon>
                    <p class="ml-1 mt">Special Day</p>
                </div> -->

            </div>
            <!-- </VRow> -->
            <VDivider></VDivider>
            <VRow class=" text-center mr-2 my-1 ml-2">
                <VCol cols="3" lg="2" class="text-body-2 font-weight-medium text-left">
                    <div>NAME</div>
                </VCol>
                <VCol cols="3" lg="2" class="text-body-2 font-weight-medium text-left ">
                    <div>CHANNEL TYPE</div>
                </VCol>
                <VCol cols="3" lg="2" class="text-body-2 font-weight-medium text-left">
                    <div>CAMPAIGN TYPE</div>
                </VCol>
                <VCol cols="3" lg="2" class="text-body-2 font-weight-medium text-left">
                    <div>SCHEDULE TYPE</div>
                </VCol>
                <VCol cols="3" lg="2" class="text-body-2 font-weight-medium text-left">
                    <div>DATE</div>
                </VCol>
                <VCol cols="3" lg="1" class="text-body-2 font-weight-medium text-left">
                    <div>STATUS</div>
                </VCol>
                <VCol cols="3" lg="1" class="text-body-2 font-weight-medium text-left">
                    <div>ACTION</div>
                </VCol>
            </VRow>
            <VDivider></VDivider>
            <VDataTableServer v-model:items-per-page="campaignPerPage" :items-length="totalCampaigns"
                :items="campaignList" class="elevation-1" item-value="name" @update:options="loadCampaigns" hover
                :loading="loading">

                <template v-slot:loading>
                    <div class="text-center">
                        Loading.... Please wait.
                    </div>
                </template>

                <template #item="{ item, index }">
                    <VRow class="text-center align-center ml-1 px-2">
                        <VCol class="text-subtitle-3 font-weight-medium text-left " cols="6" lg="2">
                            <div class="ml-2">
                                {{ item.raw.campaignName }}
                            </div>
                        </VCol>
                        <VCol cols="6" lg="2">
                            <div class="text-subtitle-3 font-weight-medium text-left">
                                {{ item.raw.channelType }}
                            </div>
                        </VCol>

                        <VCol cols="6" lg="2">
                            <div class="text-subtitle-3 text-left">
                                {{ item.raw.campaignType }}
                            </div>

                        </VCol>
                        <VCol cols="6" lg="2">
                            <div class="text-subtitle-3 font-weight-regular text-left" v-if="item.raw.status != -1">
                                {{ item.raw.scheduleType == 'ONE TIME' ? 'ONE TIME' : item.raw.frequencyType }}
                            </div>
                            <div class="text-subtitle-3 text-left" v-else>{{ '--' }}</div>
                        </VCol>
                        <VCol cols="6" lg="2" v-if="item.raw">
                            <!-- <span v-show="false">{{ commObj=item.raw }}</span> -->
                            <div class="text-subtitle-3 text-left" v-if="item.raw.status != -1">{{
                        getScheduleDate(item.raw) }}</div>
                            <div class="text-subtitle-3 text-left" v-else>{{ '--' }}</div>
                        </VCol>
                        <VCol cols="3" lg="1">
                            <div class="text-subtitle-3 text-left" style="min-width: 80px;">{{ getStatus(item.raw)
                                }}</div>
                        </VCol>
                        <VCol cols="3" lg="1">
                            <!-- <VList>
                                <VListItem>
                                    <VIcon :icon="expandedRows[index] ? 'tabler-chevron-up' : 'tabler-chevron-down'">
                                    </VIcon>
                                </VListItem>
                            </VList> -->
                            <div class="text-body-1 d-flex justify-space-around py-4">
                                <!-- <VTooltip location="bottom" disabled>
                                    <template #activator="{ props }">
                                        <v-icon icon="tabler-eye" variant="outlined" 
                                            class="font-weight-regular" v-bind="props">
                                        </v-icon>
                                      
                                    </template>
Preview
</VTooltip>
<VTooltip location="bottom" disabled>
    <template #activator="{ props }">
                                        <v-icon icon="tabler-copy" variant="outlined" 
                                            class="font-weight-regular" v-bind="props">
                                        </v-icon>
                                    </template>
    Duplicate
</VTooltip> -->
                                <VTooltip location="bottom">
                                    <template #activator="{ props }">
                                        <VIcon icon="tabler-edit" variant="outlined" color="primary"
                                            class="font-weight-regular pl-1" v-bind="props" @click="editCampaign(item)">
                                        </VIcon>
                                    </template>
                                    Edit
                                </VTooltip>
                                <VTooltip location="bottom" :disabled="!item.raw.crId" v-if="item.raw.crId">
                                    <template #activator="{ props }">
                                        <VIcon icon="tabler-report" variant="outlined"
                                            :color="item.raw.crId ? 'primary' : ''" class="font-weight-regular pl-1"
                                            v-bind="props"
                                            @click="$router.push(`/campaigns/list/view/${item.raw.crId}`)">
                                        </VIcon>
                                    </template>
                                    Report
                                </VTooltip>
                                <div v-else class="pl-3"></div>
                            </div>
                        </VCol>
                    </VRow>
                    <VDivider />
                    <!-- <VRow v-if="expandedRows[index]" class="text-center align-center py-2">
                        <VCol cols="6" lg="3">
                            <div class="text-subtitle-3 font-weight-regular">
                            <span class="font-weight-medium">Segment-At Risk</span>
                         <p class="ma-0 text-caption font-weight-regular"> {{ item.raw.configured }} contacts</p> 
                            </div>
                        </VCol>
                        <VCol cols="6" lg="2">
                            <div class="text-subtitle-3 font-weight-regular">
                         <span class="font-weight-medium">Sent</span>
                        <p class="ma-0 text-caption font-weight-regular"> {{ item.raw.sent }} contacts</p>
                            </div>
                        </VCol>
                        <VCol cols="6" lg="2">
                            <div class="text-subtitle-3 font-weight-regular">
                            <span class="font-weight-medium">Delivered</span> -->
                    <!-- <p class="ma-0 text-caption font-weight-regular"> {{ item.raw.delivered }} contacts</p> 
                            </div>
                        </VCol>
                        <VCol cols="6" lg="3">
                            <div class="text-subtitle-3 font-weight-regular">
                             <span class="font-weight-medium">Clicks</span> -->
                    <!-- <p class="ma-0 text-caption font-weight-regular"> {{ item.raw.clicks }} contacts</p> 
                            </div>
                        </VCol>
                        <VCol cols="6" lg="1">

                        </VCol>

                    </VRow> -->
                    <VDivider />
                </template>
            </VDataTableServer>
        </VCard>
        <CreateCampaign v-model:isDialogVisible="createCampaign" />
    </div>
</template>
