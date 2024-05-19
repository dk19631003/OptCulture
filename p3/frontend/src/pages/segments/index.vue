<script setup lang="ts">
import { VDataTableServer } from "vuetify/labs/VDataTable";
import { reactive } from "vue";
import { ref } from "vue";
import axios from "@axios";
import router from '@/router';


const segmentsPerPage = ref(10);
const totalSegments = ref(0);
const loading = ref(false);
const state = reactive({
  createSegment: false,
});

interface segment {
  segmentName: ''
}

let userSegments = ref([]);
const segmentList = ref<segment[]>([]);
const pageCount = ref(0);
const pageNumber = ref(1);
const searchSegmentWithName = ref('');

loading.value = true;

// Loads all User-Segments
function loadAllSegments() {
  axios.get('/api/segment/fetch-all').then(response => {
    userSegments.value = response.data;
    console.log("RESPONSE :: ", response.data);
    segmentList.value = userSegments.value.slice(0, segmentsPerPage.value);
    totalSegments.value = userSegments.value.length;
    pageCount.value = Math.ceil(totalSegments.value / segmentsPerPage.value)
    loading.value = false;
  });
}
loadAllSegments();

function openEditedSegment(editedSegmentId) {
  if (userSegments.value.length > 0) {
    let editedSegment = userSegments.value.find(segment => segment.segRuleId == editedSegmentId);
    localStorage.setItem('EditedSegment' + editedSegmentId, JSON.stringify(editedSegment));
    console.log("EDITED SEGMENT >> ", editedSegment);
  }
  router.push(`/segments/edit/${editedSegmentId}`);
}

// Filters Segments By Segment-Name
function filterSegments() {
  const lowerCaseSegment = searchSegmentWithName.value.toLowerCase();
  segmentList.value = userSegments.value.filter(segment => (segment.segRuleName.toLowerCase().includes(lowerCaseSegment)))
  pageCount.value = Math.ceil(segmentList.value.length / segmentsPerPage.value)
  if (segmentsPerPage.value == -1) segmentsPerPage.value = totalSegments.value
  const startIndex = (pageNumber.value - 1) * segmentsPerPage.value;
  const endIndex = startIndex + segmentsPerPage.value;
  segmentList.value = segmentList.value.slice(startIndex, endIndex);
}

watch([segmentsPerPage], () => {
  pageNumber.value = 1;
  filterSegments();
})
watch(pageNumber, () => {
  filterSegments();
})

function searchSegment() {
  pageNumber.value = 1;
  filterSegments();
}


const expandedRows = ref(Array(userSegments.length).fill(false));

const table_headers = [
  { title: "Segment Name", key: "segRuleName", sortable: false, width: '30%' },
  // { title: "Type", key: "type", sortable: false, width: '19%' },
  { title: "Contacts", key: "totSize", sortable: false, width: '30%' },
  { title: "Last Interaction", key: "modifiedDate", sortable: false, width: '30%' },
  // { title: "ROI", key: "roi", sortable: false, width: '19%' },
  { title: "Actions", key: "actions", sortable: false, width: '10%' },
];
/*Fetching segments*/
function showCreateSegment() {
  state.createSegment = !state.createSegment;
}

const toggleRow = (index: number) => {
  expandedRows.value[index] = !expandedRows.value[index];
};

</script>
<template>
  <div>
    <v-btn class="font-weight-medium text-none mt-5" color="#685DD8" size="x-large" variant="flat" width="262"
      height="60" @click="$router.push('/segments/edit/new')"> <!--showCreateSegment-->
      <v-icon icon="tabler-plus" color="white"></v-icon>
      <span class="text-white">Create a Segment</span>
    </v-btn> <br><br>

    <section>

      <v-row v-if="state.createSegment">
        <!-- AI Suggested Segments -->
        <v-col cols="12" lg="5" md="5" sm="12">
          <v-card>
            <v-card-title class="text-h6 mx-2 my-5">AI Suggested Segments</v-card-title>
            <router-link to="/segments/edit/1">
              <v-card-text :height="68" :width="380">
                <div class="d-flex align-center">
                  <div class="d-inline-flex align-center" style="width: 95%;">
                    <div>
                      <v-progress-circular :size="54" :width="5" color="#C1BBFF" :model-value="72">
                        <b class="text-subtitle-2 text-grey-darken-1">72%</b>
                      </v-progress-circular>
                    </div>
                    <div class="ml-2">
                      <div class="font-weight-medium font-style-1">1 visit customers</div>
                      <div class="font-style-2 pr-3">
                        Customers who have transacted only once in their entire
                        lifetime
                      </div>
                    </div>
                  </div>
                  <div>
                    <v-icon icon="tabler-square-chevron-right-filled" size="30" color="#7367F0">
                    </v-icon>
                  </div>
                </div>
              </v-card-text>
            </router-link>
            <router-link to="/segments/edit/1">
              <v-card-text :height="68" :width="380">
                <div class="d-flex align-center">
                  <div class="d-inline-flex align-center" style="width: 95%;">
                    <div>
                      <v-progress-circular :size="54" :width="5" color="#28C76F" :model-value="48">
                        <b class="text-subtitle-2 text-grey-darken-1">48%</b>
                      </v-progress-circular>
                    </div>
                    <div class="ml-2">
                      <div class="font-weight-medium font-style-1">Last purchase > 90 days</div>
                      <div class="font-style-2 pr-3">
                        Customers who have not purchased in the last 90 days
                      </div>
                    </div>
                  </div>
                  <div>
                    <v-icon icon="tabler-square-chevron-right-filled" size="30" color="#7367F0">
                    </v-icon>
                  </div>
                </div>
              </v-card-text>
            </router-link>
            <router-link to="/segments/edit/1">
              <v-card-text :height="68" :width="380">
                <div class="d-flex align-center">
                  <div class="d-inline-flex align-center" style="width: 95%;">
                    <div>
                      <v-progress-circular :size="54" :width="5" color="#EA5455" :model-value="15">
                        <b class="text-subtitle-2 text-grey-darken-1">15%</b>
                      </v-progress-circular>
                    </div>
                    <div class="ml-2">
                      <div class="font-weight-medium font-style-1">At Risk</div>
                      <div class="font-style-2 pr-3">
                        Customers who have not acted on offers in the last 6
                        months
                      </div>
                    </div>
                  </div>
                  <div>
                    <v-icon icon="tabler-square-chevron-right-filled" size="30" color="#7367F0">
                    </v-icon>
                  </div>
                </div>
              </v-card-text>
            </router-link>

            <router-link to="/segments/edit/1">
              <v-card-text :height="68" :width="380">
                <div class="d-flex align-center">
                  <div class="d-inline-flex align-center" style="width: 95%;">
                    <div>
                      <v-progress-circular :size="54" :width="5" color="#00CFE8" :model-value="14">
                        <b class="text-subtitle-2 text-grey-darken-1">14%</b>
                      </v-progress-circular>
                    </div>
                    <div class="ml-2">
                      <div class="font-weight-medium font-style-1">Highest performing store</div>
                      <div class="font-style-2 pr-3">
                        This store has performed 40 times better than the stores
                        in this region. Run an offer
                      </div>
                    </div>
                  </div>
                  <div>
                    <v-icon icon="tabler-square-chevron-right-filled" size="30" color="#7367F0">
                    </v-icon>
                  </div>
                </div>
              </v-card-text>
            </router-link>
          </v-card>

        </v-col>
        <!-- OR -->
        <v-col cols="12" lg="1" md="1" sm="12" class="centered-text text-h4">OR</v-col>
        <!--Pre-defined segments -->
        <v-col cols="12" lg="6" md="6" sm="12">
          <span class="text-h6 mb-2 pl-0">Pre-defined segments</span>
          <!-- <v-row class="flex-child text-subtitle-2">
          <v-col class="d-flex" cols="12"> -->
          <v-row>
            <!-- <v-col class="pl-0" :style="{ marginLeft: '-50px' }" cols="12" lg="12" md="12" sm="12"> -->
            <v-col class="pl-0" cols="12" lg="12" md="12" sm="12">

              <v-slide-group class="pa-4" selected-class="block-active">
                <template v-slot:prev="{ prev }">
                  <VBtn size="28" variant="outline" rounded="0">
                    <v-icon icon="tabler-square-chevron-left-filled" size="30" color="#A8AAAE"></v-icon>
                  </VBtn>
                </template>
                <template v-slot:next="{ next }">
                  <VBtn size="28" variant="outline" rounded="0">
                    <v-icon icon="tabler-square-chevron-right-filled" size="30" color="#A8AAAE"></v-icon>
                  </VBtn>
                </template>
                <v-slide-group-item v-slot="{ isSelected, toggle, selectedClass }">
                  <v-card color="grey-lighten-6" :class="['mx-2 my-4', selectedClass]" height="210" width="150"
                    @click="toggle">
                    <div class="mb-2 text-center">
                      <v-icon class="mt-5 rounded-lg pa-3 block-icon-bg" size="48" icon="tabler-alert-triangle" />
                    </div>
                    <div>
                      <v-scale-transition>
                        <router-link to="/segments/edit/1">
                          <div class="text-center">

                            <div class="font-weight-medium font-style-1">
                              At Risk
                            </div>
                            <div class="font-style-2 pa-3">
                              Customers who have been idle for more than
                              6 months
                            </div>
                          </div>
                        </router-link>
                      </v-scale-transition>
                    </div>
                  </v-card>
                </v-slide-group-item>
                <v-slide-group-item v-slot="{ isSelected, toggle, selectedClass }">
                  <v-card color="grey-lighten-6" :class="['mx-2 my-4', selectedClass]" height="210" width="150"
                    @click="toggle">
                    <div class="mb-2 text-center">
                      <v-icon class="mt-5 rounded-lg pa-3 block-icon-bg" size="48" icon="tabler-lollipop" />
                    </div>
                    <div>
                      <v-scale-transition>
                        <router-link to="/segments/edit/1">
                          <div class="text-center">
                            <div class="font-weight-medium font-style-1">
                              Promoters
                            </div>
                            <div class="font-style-2 pa-3">
                              Customers who have been idle for more than
                              6 months
                            </div>
                          </div>
                        </router-link>
                      </v-scale-transition>
                    </div>
                  </v-card>
                </v-slide-group-item>
                <v-slide-group-item v-slot="{ isSelected, toggle, selectedClass }">
                  <v-card color="grey-lighten-6" :class="['mx-2 my-4', selectedClass]" height="210" width="150"
                    @click="toggle">
                    <div class="mb-2 text-center">
                      <v-icon class="mt-5 rounded-lg pa-3 block-icon-bg" size="48" icon="tabler-sun-high" />
                    </div>
                    <div>
                      <v-scale-transition>
                        <router-link to="/segments/edit/1">
                          <div class="text-center">
                            <div class="font-weight-medium font-style-1">
                              Win back
                            </div>
                            <div class="font-style-2 pa-3">
                              Customers who have been idle for more than
                              6 months
                            </div>
                          </div>
                        </router-link>
                      </v-scale-transition>
                    </div>
                  </v-card>
                </v-slide-group-item>
                <v-slide-group-item v-slot="{ isSelected, toggle, selectedClass }">
                  <v-card color="grey-lighten-6" :class="['mx-2 my-4', selectedClass]" height="210" width="150"
                    @click="toggle">
                    <div class="d-flex fill-height align-center justify-center">
                      <v-scale-transition>
                        <router-link to="/segments/edit/1">
                          <div class="text-center"></div>
                        </router-link>
                      </v-scale-transition>
                    </div>
                  </v-card>
                </v-slide-group-item>
              </v-slide-group>

            </v-col>
          </v-row>
          <!-- </v-col>
        </v-row> -->
          <v-row>
            <v-col cols="12">
              <router-link to="/segments/edit/new">
                <v-card class="text-center pt-4">
                  <svg class="pa-2 rounded-circle" style="background: #ff9f4366" xmlns="http://www.w3.org/2000/svg"
                    width="42" height="42" viewBox="0 0 25 24" fill="none">
                    <path
                      d="M10.5 3.19999C6.17001 4.20378 3.22126 8.21822 3.55844 12.6502C3.89562 17.0822 7.41776 20.6044 11.8498 20.9415C16.2818 21.2787 20.2962 18.33 21.3 14C21.3 13.4477 20.8523 13 20.3 13H13.5C12.3954 13 11.5 12.1046 11.5 11V3.99999C11.4749 3.76068 11.355 3.54138 11.1671 3.39107C10.9792 3.24075 10.739 3.17194 10.5 3.19999"
                      stroke="#FF9F43" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                    <path d="M15.5 3.5C18.0718 4.40556 20.0944 6.42819 21 9H16.5C15.9477 9 15.5 8.55228 15.5 8V3.5"
                      stroke="#FF9F43" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                  </svg>
                  <v-card-title class="text-h6">Create a segment from scratch</v-card-title>
                  <v-card-text>If you have the exact audience you want to target for
                    your next campaign here is what you need to
                    do!</v-card-text>
                </v-card>
              </router-link>
            </v-col>
          </v-row>
        </v-col>
      </v-row>

      <!-- Existing Segments -->
      <v-row>
        <v-col cols="12" lg="12" md="12" sm="12">
          <v-card class="pa-5">
            <span style="font-size: 22px; color: #4b465c; font-weight: 500; line-height: 30px; word-wrap: break-word">
              Existing Segments
            </span>
            <v-row class="mt-2 mb-3">
              <v-col cols="12" lg="10" md="10" sm="10">
                <AppTextField v-model="searchSegmentWithName" placeholder="Enter the name of your segment" />
              </v-col>
              <v-col cols="12" lg="2" md="2" sm="2">
                <v-btn height="38" width="100%" color="#7367F0" @click="searchSegment">
                  <span class="text-white">Search</span>
                </v-btn>
              </v-col>
            </v-row>

            <!-- segments table data -->
            <v-data-table-server :headers="table_headers" v-model:items-per-page="segmentsPerPage"
              :items-length="totalSegments" :items="segmentList" class="elevation-1" item-value="name"
              @update:options="filterSegments" hover :loading="loading">
              <template v-slot:loading>
                <div class="text-center">
                  Loading.... Please wait.
                </div>
              </template>
              <template #item="{ item, index }">
                <tr @click="toggleRow(index)">
                  <td class="text-subtitle-3 font-weight-regular">
                    <div>
                      {{ item.raw.segRuleName }}
                    </div>
                    <!-- <span class="text-body-2 text-disabled">{{
                    item.raw.segmentPeriod
                  }}</span> -->
                  </td>
                  <!-- <td>
                    <div class="text-subtitle-3 font-weight-regular">
                       {{ item.raw.type }} 
                      ---
                    </div>
                     <span class="text-body-2 text-disabled">{{
                    item.raw.typeText
                  }}</span>
                  <v-progress-linear :model-value="parseFloat(item.raw.type)" color="#7367F0"></v-progress-linear> 
                  </td> -->
                  <td>
                    <div class="text-subtitle-3 font-weight-regular">
                      {{ item.raw.totSize }}
                    </div>
                    <!-- <span v-if="item.raw.totSize" class="text-body-2 text-disabled">18th November 2023</span>
                  <v-btn v-if="!item.raw.totSize" prepend-icon="tabler-send" variant="outlined" color="indigo"
                    class="font-weight-regular">
                    Send Campaign
                  </v-btn> -->
                  </td>
                  <td>
                    <div class="text-subtitle-3">
                      {{ new Date(item.raw.modifiedDate).toLocaleString() }}
                    </div>
                    <!-- <router-link to="/segments/edit/1">
                    <v-btn v-if="!item.raw.modifiedDate" prepend-icon="tabler-edit" variant="outlined" color="indigo"
                      class="font-weight-regular">
                      Edit Segment
                    </v-btn>
                  </router-link> -->
                  </td>
                  <!-- <td>
                    <div class="text-subtitle-3" v-if="item.raw.roi">{{ item.raw.roi }}</div>
                    <div class="text-subtitle-3" v-else>---</div>
                    <div class="text-subtitle-1">{{ item.raw.roi_campaigns }}</div>
                  <v-btn v-if="!item.raw.roi" prepend-icon="tabler-database-export" variant="outlined" color="indigo"
                    class="font-weight-regular">
                    Export Segment
                  </v-btn>
                  </td> -->
                  <td>
                    <!-- hide expanded rows -->
                    <!-- <div class="pa-5">
                      <svg cursor="pointer" :class="{ 'rotate-icon': expandedRows[index] }"
                        xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <path d="M6 9L12 15L18 9" stroke="#4B465C" stroke-width="1.5" stroke-linecap="round"
                          stroke-linejoin="round" />
                        <path d="M6 9L12 15L18 9" stroke="white" stroke-opacity="0.2" stroke-width="1.5"
                          stroke-linecap="round" stroke-linejoin="round" :style="{ cursor: 'pointer' }" />
                      </svg>
                    </div> -->
                    <!-- <div>
                      <v-btn prepend-icon="tabler-edit" variant="outlined" color="indigo" class="font-weight-regular"
                        @click="openEditedSegment(item.value.segRuleId)">
                        <Edit Segment 
                      </v-btn>
                    </div> -->

                    <VTooltip location="bottom">
                      <template #activator="{ props }">
                        <VIcon icon="tabler-edit" variant="outlined" color="primary" class="font-weight-regular ml-6"
                          v-bind="props" @click="openEditedSegment(item.value.segRuleId)">
                        </VIcon>
                      </template>
                      Edit
                    </VTooltip>
                  </td>
                </tr>
                <!-- <tr v-if="expandedRows[index]">
                  <td>
                    - <div class="text-subtitle-3 font-weight-regular">
                      Current CTR
                    </div>
                 <span class="text-body-2 text-disabled"></span>
                </td>
                <td>
                   <div class="text-subtitle-3 font-weight-regular">
                      80%
                    </div> 
                   <span class="text-body-2 text-disabled"></span> 
                 <v-progress-linear :model-value="parseFloat('80%')" color="#7367F0"></v-progress-linear> 
                </td>
                 <td>
                    <v-btn prepend-icon="tabler-send" variant="outlined" color="indigo" class="font-weight-regular"
                      :disabled=true>
                      Send Campaign
                    </v-btn>
                  </td>
                <td></td>
                  <td></td> 
                  <td>
                     <div class="text-subtitle-3">

                    </div>
                    <div class="text-subtitle-3">

                    </div> 
                   
                  </td>
                  <td></td>
                   <td>
                    <div class="text-subtitle-3"></div>
                    <div class="text-subtitle-3"></div>
                    <v-btn prepend-icon="tabler-database-export" variant="outlined" color="indigo"
                      class="font-weight-regular" :disabled=true>
                      Export Segment
                    </v-btn>
                  </td> 

                </tr> -->
              </template>
              <template #bottom>
                <VDivider />

                <VRow class="text-center">
                  <VCol cols="12" lg="5" md="5" sm="5">
                    <div class="d-flex justify-space-around">
                      <span class="mx-3 mt-4">Items per Page</span>
                      <AppSelect class="mt-2" v-model="segmentsPerPage" :items="[
        { value: 10, title: '10' },
        { value: 25, title: '25' },
        { value: 50, title: '50' },
        { value: 100, title: '100' },
        { value: -1, title: 'All' },
      ]" style="width: 6.25rem;" />
                    </div>
                  </VCol>
                  <VSpacer />
                  <VCol>
                    <!-- <VPagination class="mt-1" v-model="pageNumber" :length="PageCount"
                    :total-visible="$vuetify.display.xs ? 1 : Math.ceil(totalTimelineItems / itemsPerPage)">
                  </VPagination> -->
                    <VPagination :length="pageCount" v-model="pageNumber"
                      :total-visible="$vuetify.display.xs ? 1 : pageCount > 2 ? 3 : pageCount">
                    </VPagination>
                  </VCol>
                </VRow>
              </template>
            </v-data-table-server>
          </v-card>
        </v-col>
      </v-row>
    </section>
  </div>
</template>

<style scoped>
a {
  color: inherit;
}

.block-icon-bg {
  background-color: #a8aaae29;
}

.block-active {
  background-color: #7367f0 !important;
  color: rgb(255 255 255 / 80%) !important;
}

.block-active .block-title {
  color: rgb(255 255 255 / 100%) !important;
}

.block-active .block-desc {
  color: rgb(255 255 255 / 100%) !important;
  opacity: 0.7;
}

.rotate-icon {
  transform: rotate(180deg);
  transition: transform 0.3s ease;
}

.centered-text {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5D596C;
  font-size: 32px;
  font-weight: 500;
  line-height: 44px;
  word-wrap: break-word
}

.font-style-1 {
  font-size: 15px;
  font-weight: 500;
  line-height: 21px;
  word-wrap: break-word
}

.font-style-2 {
  font-size: 13px;
  font-weight: 400;
  line-height: 20px;
  word-wrap: break-word
}
</style>
