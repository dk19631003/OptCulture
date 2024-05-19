<script setup lang="ts">
import axios from "@axios";
import Emailbee from "@/pages/emailBee/components/Emailbee.vue";
import { isEmpty } from "@/@core/utils";
interface Props {
    isDialogVisible: boolean;
    channelType: string
}
interface Emit {
    (e: 'update:isDialogVisible', val: boolean): void
    (e: 'submitTemplate', value: any): void
    (e: 'isTemplateAddVisible', value: boolean): void
}

interface Template {
    templateId: number,
    templateName: string,
    templateContent: string,
    templateRegId: number,
    headerText: string,
    senderId: string,
    msgType: string,
    jsonContent: string
}

const templateList = ref<Template[]>([]);
const pageNumber = ref(-1);
const selectedTemplate = ref<Template>()
const isTemplateLastPage = ref(false);
const templateName = ref("");
const isAddTemplateVisible = ref(false);
let isPopupOpen = true;
const props = defineProps<Props>()
const color = '#00CFE8'
const emit = defineEmits<Emit>()
const loadingTemplates = ref(false)

const dialogVisibleUpdate = (val: boolean) => {
    emit('submitTemplate', selectedTemplate.value ? selectedTemplate.value : '')
    selectedTemplate.value = ''
    templateList.value.length = 0
    isPopupOpen = true
    templateName.value = '';
    pageNumber.value = -1
    console.log('dialogVisibleUpdate', val)
    emit('update:isDialogVisible', val)
}

const addTemplateVisibleUpdate = (val: boolean) => {
    console.log('addTemplateVisibleUpdate', val)
    isAddTemplateVisible.value = val
    emit('isTemplateAddVisible', isAddTemplateVisible.value)
}

async function fetchTemplates(type: string) {
    try {
        loadingTemplates.value = true
        if (type == 'Search') pageNumber.value = 0;
        else pageNumber.value = pageNumber.value + 1;
        const resp = await axios.get('/api/campaigns/approved-templates', {
            params: {
                'pageNumber': pageNumber.value,
                'pageSize': 4,
                'templateName': templateName.value,
                'channelType': props.channelType
            }
        })
        console.log(resp);
        if (type == 'Search') templateList.value = resp.data.content
        else templateList.value = [...templateList.value, ...(resp).data.content] //adding to existing list
        loadingTemplates.value = false
        if (resp.data.last) //is this page is last
        {
            isTemplateLastPage.value = true;
        }
        else isTemplateLastPage.value = false;

    } catch (err) {

    }
}

function setTemplate(item: Template) {
    selectedTemplate.value = item;
    setTimeout(() => {
        dialogVisibleUpdate(false)
    }, 1500);
}
watchEffect(() => {
    if (props.isDialogVisible && isPopupOpen) {
        isPopupOpen = false;
        fetchTemplates("");
    }
});
</script>

<template>
    <div>
        <VDialog :model-value="props.isDialogVisible" :width="$vuetify.display.smAndDown ? 'auto' : 1100"
            @update:model-value="dialogVisibleUpdate">
            <DialogCloseBtn @click="dialogVisibleUpdate(false)" />
            <VCard>
                <VCardTitle>
                    <div class="text-center text-caption text-wrap">
                        <span class="font-weight-medium text-h6 ma-3">Select Pre Approved Templates</span>
                    </div>
                </VCardTitle>
                <VCardText>
                    <VRow class="d-flex justify-space-between w-100">
                        <VCol cols="12" md="5" lg="5" sm="5" xs="10">
                            <AppTextField placeholder="template name" height="20" style="{height: 60px;}"
                                v-model="templateName">
                            </AppTextField>
                        </VCol>
                        <VCol cols="12" md="2" lg="2" sm="2" xs="4">
                            <VBtn :color="color" @click="fetchTemplates('Search')">Search</VBtn>
                        </VCol>

                        <VCol cols="12" lg="4" md="4" sm="5" xs="7"
                            :class="{ 'd-flex': true, 'justify-end': !$vuetify.display.smAndDown }">
                            <VBtn prepend-icon="tabler-plus" :color="color" @click="addTemplateVisibleUpdate(true)"> Add
                                Templates</VBtn>
                        </VCol>
                    </VRow>
                </VCardText>
                <VCardText v-if="!loadingTemplates">
                    <VRow v-if="templateList.length > 0">
                        <VCol cols="12" lg="6" md="6" sm="12" v-for="(item, index) in templateList" :key="index">
                            <VCard flat border @click="setTemplate(item)"
                                :class="[selectedTemplate == item ? 'border-custom' : '']" height="150">
                                <VCardTitle>
                                    <div class="text-center">
                                        <p class="text-subtilte-1 font-weight-medium">{{ item.templateName }}</p>

                                    </div>
                                </VCardTitle>
                                <!-- <VCardText>
                                <div class="text-center text-wrap">
                                    <p class="text-subtilte-2 font-weight-light" v-html="item.templateContent"></p>
                                    
                                </div>
                            </VCardText> -->
                            </VCard>
                        </VCol>
                    </VRow>

                    <!-- <VRow > -->
                    <div class="text-center mt-3" v-else>
                        <p class="text-subtitle-1 font-weight-medium">No templates available </p>
                    </div>
                    <div class="d-flex justify-end mt-6">
                        <VBtn height="22" class="px-n1 " variant="tonal" @click="fetchTemplates"
                            :disabled="isTemplateLastPage">Load More
                        </VBtn>
                    </div>
                </VCardText>
                <VCardText v-else>
                    <div class="text-center mt-3">
                        <p class="text-subtitle-1 font-weight-medium">Loading templates .... </p>
                    </div>
                </VCardText>
                <VDialog :model-value="isAddTemplateVisible" :width="$vuetify.display.smAndDown ? 'auto' : 1200"
                    @update:model-value="addTemplateVisibleUpdate">
                    <!-- 👉 Dialog close btn -->
                    <DialogCloseBtn @click="addTemplateVisibleUpdate(false)" />
                    <VCard class="pa-4">
                        <VCardTitle class="text-h6 text-center">Add Template</VCardTitle>
                        <VCardText>
                            <Emailbee beeConfigId="bee-campain-email-model" templateType="add_campain_template"
                                v-model:isAddTemplateVisible="isAddTemplateVisible" />
                        </VCardText>
                    </VCard>
                </VDialog>
            </VCard>
        </VDialog>
    </div>
</template>

<style scoped>
.border-custom {
    border-color: rgb(var(--v-theme-primary)) !important;
}

.scroll {
    overflow-y: auto;
}

.custom-max-height {
    max-height: 280px;
    /* Max height for large breakpoints */
}
</style>
