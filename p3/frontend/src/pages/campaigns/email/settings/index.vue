<script setup lang="ts">
import { ref } from 'vue'
import axios from '@axios';
import {useSharedStore} from '@/store'
import router from '@/router';
const userData = JSON.parse(localStorage.getItem('userData') || 'null')
const userAddress = ref('');
const store = useSharedStore();
const footerAddressType = ref('USER')
const customAddress = ref('')

if (userData) {
    customAddress.value = userData.completeAddress
    userAddress.value = userData.completeAddress
}
const settingsId = ref()
const isSettingsSaved=ref(false)
const enableTrackingLinks = ref(false)
const includeLinks = ref(false)
const storeAdress = ref()
const userStoresList = ref<object[]>([])
const webLinkAlignment = ref('CENTER')
const webLinkDisplayText = ref('Click here')
const webLinkText = ref('Having Trouble viewing this email?')
const permissionEnabled = ref('OFF')
const permissionText = ref('You are receiving this email because you had opted in on our website.')
const personalizeToFlag = ref(false)
const personalizeToField = ref('FIRST_NAME')
const BeforeaddressText = ref('')
const personalizeFieldList = [
    {
        'title': 'FIRST NAME', 'value': 'FIRST_NAME'
    },
    {
        'title': 'LAST NAME', 'value': 'LAST_NAME'
    },
    {
        'title': 'FULL NAME', 'value': 'FULL_NAME'
    }]

async function loadEmailSettings() {
    try {
        const resp = await axios.get('/api/campaigns/email-settings');
        console.log(resp.data)
        settingsId.value =resp.data.settingId
        enableTrackingLinks.value = resp.data.enableAnalytics
        includeLinks.value = resp.data.webLinkFlag
        footerAddressType.value=resp.data.addressTypeFlag
        storeAdress.value = resp.data.addressTypeFlag=='STORE'?resp.data.addressTypeValue:storeAdress.value
        customAddress.value=resp.data.addressTypeFlag=='CUSTOM'?resp.data.addressTypeValue:customAddress.value
        webLinkAlignment.value = resp.data.alignment
        webLinkDisplayText.value = resp.data.webLinkURLText
        webLinkText.value = resp.data.webLinkText
        permissionEnabled.value = resp.data.permissionReminderFlag==true?'ON':'OFF'
        permissionText.value = resp.data.premissionReminderText
        personalizeToFlag.value = resp.data.personalizedToFlag
        personalizeToField.value = resp.data.personalizedToValue
        BeforeaddressText.value =resp.data.includeBeforeAddress
    } catch (err) {
        console.log(err)
    }
}
async function loadUserStoresList() {
    try {
        const resp = await axios.get('/api/user/store-list')
        resp.data.forEach(store => {
            let address = store.address.split(';=;').join(',')
            if (address.substring(address.length - 1, address.length) == ',') {
                address = address.substring(0, address.length - 1) //remove extra ',' at end
            }
            storeAdress.value = address
            userStoresList.value.push({ 'title': store.storeName, 'value': address })
        });
    } catch (err) {
        console.log(err)
    }
}
const addressTypeValue = computed(() => {
    let address;
    switch (footerAddressType.value) {
        case 'USER': return userAddress.value
        case 'CONTACT': return "${homeStore.addressStr ! 'Not Available'}"
        case 'STORE': return storeAdress.value
        case 'CUSTOM': return customAddress.value
    }
})

async function saveEmailSettings() {
    if(isSettingsSaved.value) return
    isSettingsSaved.value=true;
    try {
        const reqBody = {
            'settingId': settingsId.value,
            'addressTypeFlag': footerAddressType.value,
            'addressTypeValue': addressTypeValue.value,
            'includeBeforeAddress': BeforeaddressText.value,
            'enableAnalytics': enableTrackingLinks.value,
            'webLinkFlag': includeLinks.value,
            'webLinkText': webLinkText.value,
            'webLinkURLText': webLinkDisplayText.value,
            'alignment': webLinkAlignment.value,
            'permissionReminderFlag': permissionEnabled.value == 'ON' ? true : false,
            'premissionReminderText': permissionText.value,
            'personalizedToFlag': personalizeToFlag.value,
            'personalizedToValue': personalizeToField.value

        }
        const resp = await axios.post('/api/campaigns/save-email-settings', reqBody);
        console.log(resp.data)
        if (resp.data == 'Emails settings saved') {
         store.setSnackbar({
        content: "Settings saved successfully!",
        color: "green",
        isVisible: true,
        icon: 'tabler-circle-check'
      });
      setTimeout(()=>{
      router.replace({name:'campaigns-list'})
        },1500)
    } else {
      store.setSnackbar({
        content: "Something Went Wrong!",
        color: "error",
        isVisible: true,
        icon: 'tabler-exclamation-circle'
      });
    }
    } catch (err) {
        console.log(err)
        store.setSnackbar({
        content: "Something Went Wrong!",
        color: "error",
        isVisible: true,
        icon: 'tabler-exclamation-circle'
      });
    }
}
loadUserStoresList();
loadEmailSettings()
</script>
<template>
    <VCard>
        <VCardTitle>
            <div class="text-center font-weight-medium text-h5">
                <p>Email Settings</p>
            </div>
        </VCardTitle>
        <VCardText>
            <VRow>
                <VCol cols="12" lg="12" md="12" sm="12">
                    <p class="text-h6 ma-0">Sender Address</p>
                </VCol>
            </VRow>
            <VDivider />
            <VRow class="ml-2 mt-2">
                <VCol cols="12" lg="3" md="3" sm="12">
                    <p class="text-subtitle-1 ma- font-weight-medium"> Address in footer</p>
                </VCol>
                <VCol cols="12" lg="8" md="8" sm="12">
                    <v-radio-group v-model="footerAddressType" class="text-subtitle-1">
                        <v-radio true-icon="tabler-circle-check-filled" false-icon="tabler-circle" value="USER">
                            <template #label>
                                <VCol cols="12" lg="12" md="12" sm="12">
                                    <div>{{ userAddress }}</div>
                                </VCol>
                            </template></v-radio>
                        <v-radio true-icon="tabler-circle-check-filled" false-icon="tabler-circle" value="CONTACT">
                            <template #label>
                                <VCol cols="12" lg="12" md="12" sm="12">
                                    <div>Autofill with contact's homestore address</div>
                                </VCol>
                            </template></v-radio>
                        <v-radio true-icon="tabler-circle-check-filled" false-icon="tabler-circle" value="STORE">
                            <template #label>
                                <VCol cols="12" lg="3" md="3" sm="4">
                                    <div>Use store address</div>
                                </VCol>
                                <VCol cols="12" lg="4" md="4" sm="5">
                                    <AppSelect :items="userStoresList" v-model="storeAdress"
                                        v-show="footerAddressType == 'STORE'" />
                                </VCol>
                                <VCol cols="12" lg="5" md="5" sm="6" v-show="$vuetify.display.mdAndUp">
                                    <p v-show="footerAddressType == 'STORE'" class="mt-2">{{ storeAdress }}</p>
                                </VCol>
                            </template> </v-radio>
                        <v-radio true-icon="tabler-circle-check-filled" false-icon="tabler-circle" value="CUSTOM">
                            <template #label>
                                <VCol cols="12" lg="3" md="3" sm="4">
                                    <div>Custom address</div>
                                </VCol>
                                <VCol cols="12" lg="9" md="9" sm="8">
                                    <AppTextField v-show="footerAddressType == 'CUSTOM'" v-model="customAddress" />
                                </VCol>
                            </template>
                        </v-radio>
                        <VCol clos="12" lg="6" md="6" sm="9" class="ml-n3 pr-1">
                        </VCol>
                    </v-radio-group>
                </VCol>
            </VRow>
            <VRow class="ml-2 ">
                <VCol cols="12" lg="3" md="3" sm="5">
                    <p class="text-subtitle-1 ma- font-weight-medium"> Include before address</p>
                </VCol>
                <VCol cols="12" lg="6" md="5" sm="7">
                    <AppTextField placeholder="e.g. Ginesys" v-model="BeforeaddressText" />
                </VCol>
            </VRow>
            <VRow>
                <VCol cols="12" lg="12" md="12" sm="12">
                    <p class="text-h6 ma-0">Google Analytics Settings</p>
                </VCol>
            </VRow>
            <VDivider />
            <VRow class="ml-3 mt-2">
                <VCol cols="12" lg="3" md="3" sm="6">
                    <p class="text-subtitle-1 ma- font-weight-medium">
                        Enable tracking links</p>
                </VCol>
                <VCol cols="12" lg="9" md="9" sm="2">
                    <VIcon :icon="enableTrackingLinks ? 'tabler-square-check-filled' : 'tabler-square'"
                        @click="enableTrackingLinks = !enableTrackingLinks" size="25" color="primary"></VIcon>
                </VCol>
            </VRow>
            <VRow>
                <VCol cols="12" lg="12" md="12" sm="12">
                    <p class="text-h6 ma-0">Optional Settings</p>
                </VCol>
            </VRow>
            <VDivider />
            <!-- <VRow class="ml-3 mt-2">
                <VCol cols="12" lg="3" md="3" sm="6">
                    <span class="text-subtitle-1 mr-2 font-weight-medium">
                        View in browser</span>
                    <VTooltip location="right">
                        <template #activator="{ props }">
                            <v-icon icon="tabler-info-circle" variant="outlined" color="blue"
                                class="font-weight-regular mb-1" v-bind="props" size="25">
                            </v-icon>
                        </template>
                        Include a link to view a web page version of this email
                    </VTooltip>
                </VCol>
                <VCol cols="12" lg="9" md="9" sm="12">
                    <VIcon :icon="includeLinks ? 'tabler-square-check-filled' : 'tabler-square'"
                        @click="includeLinks = !includeLinks" size="25" color="primary"></VIcon>
                    <div v-show="includeLinks" class="mt-2">
                        <VRow>
                            <VCol cols="12" lg="2" md="2" sm="6" class="mt-2 text-subtitle-2 font-weight-medium">
                                Text
                            </VCol>
                            <VCol cols="12" lg="6" md="6" sm="6">
                                <AppTextField v-model="webLinkText" />
                            </VCol>
                        </VRow>
                        <VRow>
                            <VCol cols="12" lg="2" md="2" sm="6" class="mt-2 text-subtitle-2 font-weight-medium">
                                Link Text
                            </VCol>
                            <VCol cols="12" lg="6" md="6" sm="6">
                                <AppTextField v-model="webLinkDisplayText" />
                            </VCol>
                        </VRow>
                        <VRow>
                            <VCol cols="12" lg="2" md="2" sm="6" class="mt-2 text-subtitle-2 font-weight-medium">
                                Alignment
                            </VCol>
                            <VCol cols="12" lg="6" md="6" sm="6">
                                <v-radio-group v-model="webLinkAlignment" inline class="d-flex justify-space-between">
                                    <v-radio true-icon="tabler-circle-check-filled" false-icon="tabler-circle" label="Left"
                                        value="LEFT" />
                                    <v-radio true-icon="tabler-circle-check-filled" false-icon="tabler-circle"
                                        label="Center" value="CENTER" />
                                    <v-radio true-icon="tabler-circle-check-filled" false-icon="tabler-circle" label="Right"
                                        value="RIGHT" />
                                </v-radio-group>
                            </VCol>
                        </VRow>
                    </div>
                </VCol>
            </VRow> -->
            <VRow class="ml-3 mt-2">
                <VCol cols="12" lg="3" md="3" sm="6">
                    <p class="text-subtitle-1 ma- font-weight-medium">
                        Permission Reminder</p>
                </VCol>
                <VCol cols="12" lg="4" md="6" sm="6">
                    <v-radio-group v-model="permissionEnabled" inline class="d-flex justify-space-between">
                        <v-radio true-icon="tabler-circle-check-filled" false-icon="tabler-circle" label="ON" value="ON" />
                        <v-radio true-icon="tabler-circle-check-filled" false-icon="tabler-circle" label="OFF"
                            value="OFF" />
                    </v-radio-group>
                    <div v-show="permissionEnabled == 'ON'">
                        <AppTextarea rows="2" v-model="permissionText" />
                    </div>
                </VCol>
            </VRow>
            <VRow class="ml-3 mt-2">
                <VCol cols="12" lg="3" md="3" sm="6">
                    <p class="text-subtitle-1  font-weight-medium">
                        Personalize the "To:" Field</p>
                </VCol>
                <VCol cols="12" lg="9" md="9" sm="6">
                    <VIcon :icon="personalizeToFlag ? 'tabler-square-check-filled' : 'tabler-square'"
                        @click="personalizeToFlag = !personalizeToFlag" size="25" color="primary"></VIcon>
                    <div class="mt-2">

                    </div>
                </VCol>
            </VRow>
            <VRow v-show="personalizeToFlag" class="ml-3 mt-2">
                <VCol cols="12" lg="3" md="3" sm="12" class="mt-2 text-subtitle-2 font-weight-medium">
                    Select tag for recipient name
                </VCol>
                <VCol cols="12" lg="4" md="6" sm="12">
                    <AppSelect :items="personalizeFieldList" v-model="personalizeToField" />
                </VCol>
            </VRow>
            <!-- <VRow> -->
            <div class=" text-center mt-5">
                <VBtn @click="saveEmailSettings">Save</VBtn>
            </div>
            <!-- </VRow> -->
        </VCardText>
    </VCard>
</template>
