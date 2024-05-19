<script setup lang="ts">
import { VDataTableServer } from 'vuetify/labs/VDataTable'
import { ref } from 'vue'
import axios from '@axios'
import router from '@/router'
import { dateFormater } from '@/@core/utils/localeFormatter'
import Emailbee from "./components/Emailbee.vue";


const searchCriteria = ref()
const searchValue = ref()
const templateName = ref('')
const templatePerPage = ref(10)
const totalTemplate = ref(0)
const templatesList = ref([])
const displayTemplates = ref([])
const loading = ref(false)
const templateBeeLoad = ref(false)
const channelType = ref("Email");

// Headers
const table_headers = [
    { title: 'NAME', key: 'templateName' },
    { title: 'Editor Type', key: 'editorType' },
    { title: 'Created Date', key: 'createdDate' },
    { title: 'Modified Date', key: 'modifiedDate' },
    { title: 'Actions', key: 'actions', sortable: false },
]
const search_filter = [
    { title: 'Template Name', value: 'NAME' },
]

// 👉 Fetching templates
async function loadTemplates({ page, sortBy }) {
  loading.value = true;
  if (templatePerPage.value.toString() === '-1') templatePerPage.value = totalTemplate.value;
   
    const resp = await axios.get('/api/campaigns/approved-templates',{
            params: {
                'pageNumber': page - 1,
                'pageSize':templatePerPage.value,
                'templateName': searchValue.value,
                'channelType':channelType.value
            }
        }
    ).then((resp) => {
        templatesList.value = resp.data.content;
        totalTemplate.value = resp.data.totalElements;;
        loading.value = false;
    }).catch((resp) => console.log("Error",resp.message))

    displayTemplates.value = templatesList.value;
    if (sortBy[0]?.key === 'templateName') {
        displayTemplates.value = displayTemplates.value.sort((a, b) => {
            if (sortBy[0]?.order === 'asc') {
                if (a.templateName != null && b.templateName != null)
                    return a.templateName.localeCompare(b.templateName) //write for lastname
                return a.templateName == null ? -1 : 1;
            }

            else {
                if (a.templateName != null && b.templateName != null)
                    return b.templateName.localeCompare(a.templateName)
                return a.templateName == null ? 1 : -1;
            }
        })
    }
   
}

function setTemplatetoLocStrge(item: any) {
    localStorage.removeItem('mytemplate');
    localStorage.setItem('mytemplate', JSON.stringify(item.raw));
    // isEditContactClicked.value = !isEditContactClicked.value
}

function toggleTemplateLoad(){
    templateBeeLoad.value = !templateBeeLoad.value;
}

</script>
<template>
      <div>
        <section>
          <VRow>
             <VCol cols="12">
                <VCard title="Templates">
                     <!-- 👉 Filters -->
                      <VCardText>
                            <VRow>
                                <!-- <VCol cols="12" lg="3" md="4" sm="6">
                                    <AppSelect v-if="!templateBeeLoad" class="ml-4" placeholder="Select criteria" :items="search_filter" v-model="searchCriteria">
                                    </AppSelect>
                                </VCol> -->
                                <VCol cols="12" lg="6" md="4" sm="5">
                                    <AppTextField  v-if="!templateBeeLoad" class="ml-2" placeholder="Enter template name" v-model="searchValue"></AppTextField>
                                </VCol>
                                <VCol cols="12" lg="2" md="4" sm="6">
                                    <VBtn class="ml-2" v-if="!templateBeeLoad" @click="loadTemplates({ page: 1, sortBy: {} })">Search</VBtn>
                                </VCol>
                                <VCol cols="12" lg="4" md="4" sm="6" class="d-flex justify-end pr-5 mb-5">
                                    <VBtn v-if="!templateBeeLoad" prepend-icon="tabler-plus" class="ml-2" @click="toggleTemplateLoad">Create Template</VBtn>
                                    <VBtn v-else @click="toggleTemplateLoad">Back</VBtn>
                                </VCol>
                            </VRow>
                        </VCardText>
                    
                        <VRow> 
                            <VCol cols="12" lg="12" md="12" sm="6" v-if="!templateBeeLoad" >
                                <VDataTableServer v-model:items-per-page="templatePerPage" :headers="table_headers" :items-length="totalTemplate" :items="templatesList"
                                            class="elevation-1" item-value="templateName" @update:options="loadTemplates" hover :loading="loading"
                                        >  
                                        <template v-slot:loading>
                                            <div class="text-center">
                                                Loading.... Please wait.
                                            </div>
                                        </template>

                                        <template #item.templateName="{ item }">
                                            <span class="text-subtitle-1 font-weight-medium contact-list-name" >{{ (item.raw.templateName) ? item.raw.templateName : '-' }}</span>
                                        </template>
                                        <template #item.editorType="{ item }">
                                            <span class="font-weight-regular">{{ item.raw.templateType ? item.raw.templateType : '-' }}</span>
                                        </template>
                                        <template #item.createdDate="{ item }">
                                            {{ dateFormater(item.raw.createdDate) }}
                                        </template>
                                        <template #item.modifiedDate="{ item }">
                                            {{ dateFormater(item.raw.modifiedDate) }}
                                        </template>
                                        <!-- Actions  -->
                                        <template #item.actions="{ item }">
                                            <RouterLink :to="{
                                                name:'emaildemo',
                                            }" class="font-weight-medium contact-list-icon ">
                                                <IconBtn @click="setTemplatetoLocStrge(item)">
                                                    <VIcon icon="tabler-mail" />
                                                </IconBtn>
                                            </RouterLink>

                                            <RouterLink :to="{
                                                name: 'emailBee-edit-id',
                                                params: {
                                                    id: item.raw.templateId,
                                                }
                                            }" class="font-weight-medium contact-list-icon ">
                                                <IconBtn @click="setTemplatetoLocStrge(item)">
                                                    <VIcon icon="tabler-edit" />
                                                </IconBtn>
                                            </RouterLink>
                                        </template>
                                </VDataTableServer>
                            </VCol>
                            <VCol cols="12" lg="12" md="12" sm="6" v-else>
                                <Emailbee  templateType="add_new_template" beeConfigId="emailbee-create-template" @toggleTemplateLoad="toggleTemplateLoad"/>
                            </VCol>
                        </VRow>
                </VCard>  
             </VCol>
          </VRow>
        </section>
      </div>
</template>      