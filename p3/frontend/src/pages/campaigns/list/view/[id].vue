<script setup lang="ts">
import { VDataTableServer } from 'vuetify/labs/VDataTable'
import axios from '@axios';
import { ref } from 'vue';
import VueApexCharts from 'vue3-apexcharts'
import { useTheme } from 'vuetify'
import { dateFormater } from '@/@core/utils/localeFormatter'
import { getUTCToLocalTime, getUTCTime } from '@/@core/utils/localeFormatter';

const route = useRoute();

const campaignName = ref('');
const campaignSentDate = ref(null);
const totalCount = ref(0);
const communicationType = ref('');
const sentTemplate = ref('');
const segmentNames = ref([]);
const serverContacts = ref([]);
const contactList = ref([]);
const totalEvents = ref(0);
const totalPages = ref(0);
const searchRecipient = ref('');
const itemsPerPage = ref(10);
const pageNumber = ref(1);
const loading = ref(false);
const dataAvailable = ref(true);

let avatarSize = 60;
let avatarColor = 'primary';
const selectedEventStatus = ref('Delivered');
const availableEventStatus = ref(['']);
const eventsCountMap = ref(new Map<string, string>());
const listOfHourlyEventCounts = ref([]);



function loadCampaignReport() {
    dataAvailable.value = false;
    axios.get("/api/campaign-report/" + route.params.id).then((response) => {
        campaignName.value = response.data.campaignName;
        campaignSentDate.value = response.data.campaignSentDate;
        totalCount.value = response.data.totalCount;
        communicationType.value = response.data.communicationType;
        sentTemplate.value = response.data.sentTemplate;
        segmentNames.value = response.data.segmentNames;
        eventsCountMap.value = response.data.eventCountMap;
        availableEventStatus.value = Object.keys(eventsCountMap.value);
        availableEventStatus.value.unshift('All');
        listOfHourlyEventCounts.value = response.data.listOfhourlyEventCounts;
        console.log("reports >>>> ", response.data);
        dataAvailable.value = true;
    });
}
loadCampaignReport();


loadRecipientReports();

function loadRecipientReports() {
    loading.value = true;
    const params = {
        "crId": route.params.id,
        "recipient": searchRecipient.value,
        "status": selectedEventStatus.value
    }

    axios.get("/api/campaign-report/search", { params: params }).then((response) => {
        console.log("recipient >>>> ", response.data);
        serverContacts.value = response.data;
        contactList.value = serverContacts.value.slice(0, itemsPerPage.value);
        totalEvents.value = serverContacts.value.length;
        totalPages.value = Math.ceil(totalEvents.value / itemsPerPage.value);
        pageNumber.value = 1;
        loading.value = false;
    });
}


watch(pageNumber, () => {
    const startIndex = (pageNumber.value - 1) * itemsPerPage.value;
    const endIndex = startIndex + itemsPerPage.value;
    contactList.value = serverContacts.value.slice(startIndex, endIndex);
})




const segmentNameList = computed(() => {
    return segmentNames.value.join(', ');
});

// BAR-CHART
const vuetifyTheme = useTheme()

const currentTab = ref<number>(0)
const refVueApexChart = ref()



const chartConfigs = computed(() => {

    return [
        {
            title: 'Campaign Report',
            icon: 'tabler-send',
            chartOptions: {
                chart: {
                    type: 'bar',
                    height: 350
                },
                plotOptions: {
                    bar: {
                        horizontal: false,
                        columnWidth: '65%',
                        endingShape: 'rounded'
                    },
                },
                dataLabels: {
                    enabled: false,
                    textAnchor: 'start',
                    distributed: true
                },
                stroke: {
                    show: true,
                    width: 2,
                    colors: ['transparent']
                },
                xaxis: {
                    categories: [1, 2, 3, 4, 5, 6, 7, 8, 9], //eventHours.value
                },
                fill: {
                    opacity: 1
                },
                tooltip: {
                    y: {
                        formatter: function (val) {
                            return val;
                        }
                    }
                }
            },
            series: listOfHourlyEventCounts.value,
        },
    ]
});

// REPORT TABLE
const table_headers = [
    { title: "Recipient", key: "recipient", sortable: false },
    { title: "Status", key: "eventType", sortable: false },
    { title: "Date", key: "eventDate", sortable: false },
    // { title: "Actions", key: "actions", sortable: false },
];


const openDialogBox = ref(false);
function viewTemplate() {
    openDialogBox.value = true;
}

function getFirstWord(str: string) {
    return str.split(' ')[0];
}

function getPlaceHolder(commType: string) {
    return communicationType.value.startsWith('Email') ? "Search by Email Id" : "Search by Phone Number";
}

</script>

<template>
    <div>
        <VDialog :width="$vuetify.display.smAndDown ? 'auto' : 900" :model-value="openDialogBox" persistent>
            <!-- Dialog close btn -->
            <DialogCloseBtn @click="openDialogBox = false" />

            <VCard class="pa-2" elevation="6">
                <VCardText v-if="communicationType.startsWith('Email')">
                    <span v-html="sentTemplate"></span>
                </VCardText>
                <VCardText v-else style="white-space: pre-wrap;" class="font-weight-medium">
                    <span> {{ sentTemplate }} </span>
                </VCardText>
            </VCard>
        </VDialog>
        <VRow>
            <VCol cols="12" lg="4" md="5" sm="12">
                <VCard style="height: 550px;">
                    <VCardText class="text-center pt-15">
                        <VAvatar :size="avatarSize" :color="avatarColor">
                            <slot name="avatarContent"></slot>
                            <span class="text-h4 font-weight-medium text-capitalize" style="color: whitesmoke;">
                                {{ campaignName ? campaignName.substring(0, 1) : '' }}
                            </span>
                        </VAvatar><br>

                        <span class="text-h5 mt-4 text-capitalize">{{ campaignName }}</span>
                        <br>
                        <span>{{ communicationType }}</span>
                        <br>
                    </VCardText>

                    <VDivider />

                    <VCardText v-if="dataAvailable" style="height: 350px; overflow-y: auto;">
                        <p class="text-sm text-uppercase text-disabled">Details</p>
                        <VList class="card-list mt-2">
                            <slot name="snapShot"></slot>
                        </VList>

                        <span style="font-weight: 600; padding-bottom: 10px; display: inline-block;">Date : </span>
                        {{ dateFormater(getUTCToLocalTime(campaignSentDate)) }}<br>
                        <span>
                            <span style="font-weight: 600;  display: inline-block;">Segment : </span>
                            {{ segmentNameList }}
                        </span> <br>
                        <span style="font-weight: 600; padding-bottom: 10px;  padding-top: 10px; display: inline-block;">
                            Total Count :
                        </span>
                        {{ totalCount }}<br>

                        <span v-for="(value, key) in Object.entries(eventsCountMap)" :key="key" class="py-2">
                            <span style="font-weight: 600; padding-bottom: 10px; display: inline-block;">{{ value[0] }} : &nbsp;</span>
                            <span>{{ value[1] }}<br></span>
                        </span>

                    </VCardText>

                    <!-- <VCardText class="d-flex justify-center">
                        <RouterLink :to="{ name: 'customers-edit-id', params: {} }">
                            <VBtn variant="elevated" class="me-4">Edit Campaign</VBtn>
                        </RouterLink>
                    </VCardText> -->
                </VCard>
            </VCol>
            <VCol cols="12" lg="8" md="7" sm="12">
                <VCard style="height: 550px;" title="Campaign Reports" subtitle="Overview of Your Campaign for the First 9 Hours">
                    <template #append>
                        <div class="mt-n4 me-n2">
                            <v-btn color="primary" @click="viewTemplate()">
                                View {{ getFirstWord(communicationType) }}
                            </v-btn>
                        </div>
                    </template>


                    <VCardText>
                        <VueApexCharts ref="refVueApexChart" :key="currentTab"
                            :options="chartConfigs[Number(currentTab)].chartOptions"
                            :series="chartConfigs[Number(currentTab)].series" height="320" class="mt-3" />
                        <div class="text-center">Hours</div>
                    </VCardText>
                </VCard>
            </VCol>
        </VRow>
        <VRow>
            <VCol cols="12" lg="12" md="12" sm="12">
                <VCard class="pa-5">
                    <span
                        style="font-size: 22px; color: #4b465c; font-weight: 500; line-height: 30px; word-wrap: break-word">
                        Recipient Activity Report
                    </span>
                    <v-row class="mt-2 mb-3">
                        <v-col cols="12" lg="4" md="4" sm="4">
                            <v-select v-model="selectedEventStatus"
                                :items="availableEventStatus"
                                placeholder="Select Status"></v-select>
                        </v-col>
                        <v-col cols="12" lg="6" md="6" sm="6">
                            <AppTextField v-model="searchRecipient" :placeholder="getPlaceHolder(communicationType)" />
                        </v-col>
                        <v-col cols="12" lg="2" md="2" sm="2">
                            <v-btn height="38" width="100%" color="#7367F0" @click="loadRecipientReports">
                                <span class="text-white">Search</span>
                            </v-btn>
                        </v-col>
                    </v-row>
                    <VDataTableServer v-model:items-per-page="itemsPerPage" :headers="table_headers"
                        :items-length="totalEvents" :items="contactList" class="elevation-1" item-value="name" hover
                        :loading="loading">
                        <template v-slot:loading>
                            <div class="text-center">
                                Loading.... Please wait.
                            </div>
                        </template>
                        <template #item.eventDate="{ item }">
                            {{ dateFormater(getUTCToLocalTime(item.raw.eventDate)) }}
                        </template>
                        <template #bottom>
                            <VDivider />
                            <VRow class="text-center">
                                <VSpacer />
                                <VCol>
                                    <VPagination class="mt-1" v-model="pageNumber" :length="totalPages"
                                        :total-visible="$vuetify.display.xs ? 1 : totalPages > 2 ? 3 : totalPages">
                                    </VPagination>
                                </VCol>
                            </VRow>
                        </template>
                    </VDataTableServer>
                </VCard>
            </VCol>
        </VRow>
    </div>
</template>

<style lang="scss" scoped>
// .scrollable-card {
//   overflow-y: auto;
// }
</style>
