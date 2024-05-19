
  
<script setup lang="ts">
import { ref } from 'vue';
import axios from '@axios';
import { isEmpty } from '@/@core/utils';
import { integerValidator, requiredValidator } from '@/@core/utils/validators';
interface Props {
  isAddTemplateVisible: boolean,
  channelType: string
}
interface Emit {
  (e: 'update:isAddTemplateVisible', val: boolean): void
}
interface Template {
  templateName: string,
  templateRegId: number,
  templateContent: string,
  headerText: string,
  footer: string,
  msgType: string,
}

const props = defineProps<Props>()
const emit = defineEmits<Emit>()
const templateName=ref('')
const templateRegId=ref('')
const templateContent=ref('')
const headerText=ref('')
const footer=ref('')
const msgType=ref('TEXT')
const isTemplateValid=computed(()=>{
   const isValid= (!isEmpty(templateName.value) && !isEmpty(templateContent.value)
    && !isEmpty(msgType.value)  && !isEmpty(templateRegId.value))
    console.log(isValid)
    return isValid ;
})
const dialogVisibleUpdate = (val: boolean) => {
  file = null
templateName.value=''
templateRegId.value=''
templateContent.value=''
headerText.value=''
footer.value=''
msgType.value='TEXT'
  emit('update:isAddTemplateVisible', val)
}
let file;
const color = props.channelType=='SMS'?'#f39d40':'#66C871'
const isImportSuccess = ref('success');
const notifyUser = ref(false)
const responseMsg = ref('Error while uploading template !')
const handleFileUpload = (event) => {
  file = event.target.files[0];
  // Call the uploadFile method passing the selected file
};

const uploadFile = async () => {
  try {
    // Create FormData object to send file
    const formData = new FormData();
    formData.append('file', file);

    dialogVisibleUpdate(false)
    // Make POST request to backend API
    const response = await axios.post('/api/campaigns/template-import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    // console.log(response.data); 
    responseMsg.value = response.data;
    if (response.data.includes('success')) {
      isImportSuccess.value = 'success';
    }
    else {
      isImportSuccess.value = 'error'
    }
  } catch (error) {
    isImportSuccess.value = 'error'
    console.error('Error uploading file:', error);
  }
  notifyUser.value = true;
}

async function uploadTemplate() {
  const reqBody={
    templateName: templateName.value,
    templateRegId: templateRegId.value,
    templateContent: templateContent.value,
    headerText: headerText.value,
    footer: footer.value,
    msgType: msgType.value,
  }
  dialogVisibleUpdate(false)
  console.log(reqBody);
  try{
  const response=await axios.post('/api/campaigns/add-template',reqBody)
  console.log(response)
  responseMsg.value=response.data
  if (response.data.includes('success')) {
      isImportSuccess.value = 'success';
    }
    else {
      isImportSuccess.value = 'error'
    }
  } catch (error) {
    isImportSuccess.value = 'error'
    console.error('error while adding template', error);
  }
  notifyUser.value = true;
  dialogVisibleUpdate(false);
}
</script>
<template >
  <VSnackbar location="top" timeout="3000" v-model="notifyUser" :color="isImportSuccess" height="25">
    <div class="d-flex justify-center align-center">
      <div class="font-weight-medium">{{ responseMsg }}</div>
    </div>
  </VSnackbar>
  <div>
    <VDialog :model-value="props.isAddTemplateVisible" :width="$vuetify.display.smAndDown ? 'auto' : 620"
      @update:model-value="dialogVisibleUpdate">
      <!-- 👉 Dialog close btn -->
      <DialogCloseBtn @click="dialogVisibleUpdate(false)" />

      <VCard class="pa-4" v-if="props.channelType == 'SMS'">
        <VCardTitle class="text-h6 text-center">Add Templates</VCardTitle>
        <VCardText>
          <div class="text-center">
            <input type="file" @change="handleFileUpload" accept=".csv">
            <VBtn @click="uploadFile" :color="color">Upload</VBtn>
          </div>

        </VCardText>
      </VCard>
      <VCard v-else class="pa-4">
        <VCardTitle class="text-h6 text-center">Add Template</VCardTitle>
        <VCardText>

          <VRow>
            <VCol cols="12" lg="4" md="4" sm="6">
              <p class="mt-2  text-subtitle-1 font-weight-medium">Template Name
              </p>
            </VCol>
            <VCol lg="8" md="8" sm="6">
              <AppTextField placeholder="template name" v-model="templateName" :rules="[requiredValidator]">
              </AppTextField>
              <p class="text-caption ma-1" ><VIcon icon="tabler-info-circle" class="mr-1" color="primary" ></VIcon>Please provide approved template name here</p>
            </VCol>
          </VRow>
          <VRow>
            <VCol cols="12" lg="4" md="4" sm="6">
              <p class="mt-2  text-subtitle-1 font-weight-medium">MessageType
              </p>
            </VCol>
            <VCol lg="8" md="8" sm="6">
              <div class="d-flex justify">
                <VRadioGroup v-model="msgType" inline :color="color" :rules="[requiredValidator]">
                  <VRadio value="TEXT" label="Text" true-icon="tabler-circle-check-filled" false-icon="tabler-circle">
                  </VRadio>
                  <VRadio value="DOCUMENT" label="Document" true-icon="tabler-circle-check-filled" false-icon="tabler-circle">
                  </VRadio>
                  <VRadio value="VIDEO" label="Video" true-icon="tabler-circle-check-filled" false-icon="tabler-circle">
                  </VRadio>
                 
                  <VRadio value="IMAGE" label="Image" true-icon="tabler-circle-check-filled" false-icon="tabler-circle">
                  </VRadio>
                </VRadioGroup>
              </div>
            </VCol>
          </VRow>
          <VRow v-show="msgType=='TEXT'">
            <VCol cols="12" lg="4" md="4" sm="6">
              <p class="mt-2  text-subtitle-1 font-weight-medium"> Header Text
              </p>
            </VCol>
            <VCol lg="8" md="8" sm="6">
              <AppTextField placeholder="header text" v-model="headerText">
              </AppTextField>
            </VCol>
          </VRow>
          <VRow>
            <VCol cols="12" lg="4" md="4" sm="6">
              <p class="mt-2  text-subtitle-1 font-weight-medium">Body
              </p>
            </VCol>
            <VCol lg="8" md="8" sm="6">
              <AppTextarea placeholder="message content" rows="5" :rules="[requiredValidator]" v-model="templateContent">
              </AppTextarea>
            </VCol>
          </VRow>
          <VRow>
            <VCol cols="12" lg="4" md="4" sm="6">
              <p class="mt-2  text-subtitle-1 font-weight-medium">Footer
              </p>
            </VCol>
            <VCol lg="8" md="8" sm="6">
              <AppTextField placeholder="footer text" v-model="footer">
              </AppTextField>
            </VCol>
          </VRow>
          <VRow>
            <VCol cols="12" lg="4" md="4" sm="6">
              <p class="mt-2  text-subtitle-1 font-weight-medium">Template Reg. Id
              </p>
            </VCol>
            <VCol lg="8" md="8" sm="6">
              <AppTextField placeholder="template id" v-model="templateRegId" :rules="[requiredValidator]">
              </AppTextField>
            </VCol>
          </VRow>
          <VRo>
            <div class="text-center pa-2">
              <VBtn @click="uploadTemplate" :disabled="!isTemplateValid" :color="color">Add</VBtn>
            </div>
          </VRo>
        </VCardText>

      </VCard>
    </VDialog>
  </div></template>

  
