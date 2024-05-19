<script setup lang="ts">
import { VDataTableServer } from 'vuetify/labs/VDataTable';
import { ref } from 'vue'
import { isEmpty } from "@/@core/utils";
import axios from '@axios';
const itemsPerPage = ref(10)
const totalUserChannelsCount = ref(0)
const userChannels = ref<UserChannelSetting[]>([])
const successOrFailed = ref(false);
const isNotificationEnabled = ref(false);
const notificationColor = ref('error');
const notificationMessage = ref();
const createUserChannel = ref(false)
const selectedChannel = ref()
const channelAccountId = ref()
const senderId = ref()
const channelAccounts = ref([])
const ucsId = ref()
const showId = ref()
const editId = ref()

interface UserChannelSetting {
    accountType: string,
    apiKey: string,
    channelAccountName: string,
    senderId: string,
    userChannelSettingId: number,
    channelType: string,
    channelAccountId: number
}

// Table Headers
const table_headers = [
    { title: 'Gateway', key: 'gatewayName', sortable: false },
    { title: 'Channel Type', key: 'channelType', sortable: false },
    { title: 'Message Type', key: 'accountType', sortable: false },
    { title: 'Account Name', key: 'channelAccountName', sortable: false },
    { title: 'SenderId', key: 'senderId', sortable: false },
    { title: 'API KEY', key: 'apiKey', sortable: false },
    { title: 'Actions', key: 'actions', sortable: false },
]

//For loading the list
function loadUserChannels({ page }) {

    if (itemsPerPage.value.toString() === '-1') itemsPerPage.value = totalUserChannelsCount.value;
    const params = {
        "pageNumber": (page - 1),
        "pageSize": itemsPerPage.value,
    };

    const resp = axios.get('/api/user/user-channel-settings', {
        params: params
    })
        .then((resp) => {
            console.log(resp)
            userChannels.value = resp.data.body.content;
            totalUserChannelsCount.value = resp.data.body.totalElements;
        }).catch(
            (resp) => console.log(resp.message))
    getChannelAccounts();

}
async function deleteUserChannel(ucsId) {
    console.log("ChannelId" + ucsId);
    try {
        await axios.delete(`/api/user/user-channel-settings/${ucsId}`);
        successOrFailed.value = true
        notificationColor.value = 'success'
        notificationMessage.value = 'User Channel Deleted Successfully...!'
    } catch (error) {
        console.error('Failed to delete user channel:', error);
        successOrFailed.value = false;
        notificationColor.value = 'error'
        notificationMessage.value = 'Failed to Delete UserChannel'
    }
    isNotificationEnabled.value = true;
    // Refresh the user channels after successful deletion
    loadUserChannels({ page: 1 });
}
const channelOptions = ref([
    { value: 'Email', title: 'Email' },
    { value: 'SMS', title: 'SMS' },
    { value: 'WhatsApp', title: 'WhatsApp' }
]);

async function saveUserChannel() {
    createUserChannel.value = false;
    const reqBody = {
        ucsId: ucsId.value,
        selectedChannel: selectedChannel.value,
        channelAccountId: channelAccountId.value,
        senderId: senderId.value,
    }
    try {
        console.log(reqBody)
        const resp = await axios.post('/api/user/add-user-channel', reqBody)
        successOrFailed.value = true
        notificationColor.value = 'success'
        if (ucsId.value)
            notificationMessage.value = 'Changes Saved Successfully...!'
        else
            notificationMessage.value = 'UserChannelSetting Saved Successfully...!'
    } catch (err) {
        console.log("Failed to Save User Channels:" + err)
        successOrFailed.value = false;
        notificationColor.value = 'error'
        if (ucsId.value)
            notificationMessage.value = 'Failed to Save Changes'
        else
            notificationMessage.value = 'Failed to Save UserChannelSetting'
    }
    isNotificationEnabled.value = true;
    // Refresh the user channels after successful deletion
    loadUserChannels({ page: 1 });
}

//To get Channel Accounts 
async function getChannelAccounts() {
    try {
        const resp = await axios.get('/api/user/fetch-channel-accounts')
        channelAccounts.value = resp.data.body;
        console.log()
    } catch (err) {
        console.log("Failed to get Channel Accounts:" + err)
    }
}
watch([selectedChannel, editId], () => {
    if (!isEmpty(channelAccountId.value && showId.value))
        showId.value = '';
    else
        channelAccountId.value = 'Select Account Name'
})
// Filter of Channel Accounts based on Channel Type
const channelAccountsList = computed(() => {
    const channelAccountsfiltered = channelAccounts.value.filter(e => e.channelType == selectedChannel.value)
    let filteredList = []
    channelAccountsfiltered.forEach(e => {
        const obj = { title: '', value: '' }
        obj.title = e.accountName
        obj.value = e.id
        filteredList.push(obj)
    })
    return filteredList;
})

// Editing existing User Channel Setting
function editUserChannel(id) {
    selectedChannel.value = '';
    channelAccountId.value = '';
    senderId.value = '';
    showId.value = id;
    editId.value = id

    const editedChannel = userChannels.value.find((channel) => channel.userChannelSettingId === id);

    if (editedChannel) {
        console.log(editedChannel)
        ucsId.value = editedChannel.userChannelSettingId || '';
        selectedChannel.value = editedChannel.channelType || '';
        senderId.value = editedChannel.senderId || '';
        channelAccountId.value = editedChannel.channelAccountId;
        createUserChannel.value = true;
    } else {
        console.error(`User channel with id ${id} not found.`);
    }
}
function addUser() {
    selectedChannel.value = 'Select Channel Type';
    channelAccountId.value = 'Select Account Name';
    senderId.value = '';
    createUserChannel.value = !createUserChannel.value;
}
</script>
<template>
    <div>
        <VCard class="my-2">
            <div class="my-2 mx-2">
                <VBtn size="large" @click="addUser()">
                    Add Channel
                </VBtn>
            </div>
            <VRow>
                <VSnackbar v-model="isNotificationEnabled" location="top end" timeout="2000" :color=notificationColor>
                    {{ notificationMessage }}
                </VSnackbar>
            </VRow>
            <VCardText>
                <VDataTableServer :headers="table_headers" v-model:items-per-page="itemsPerPage"
                    :items-length="totalUserChannelsCount" :items="userChannels" @update:options="loadUserChannels">

                    <template #item.accountType="{ item }">
                        <span class="font-weight-regular">{{ item.raw.accountType ? item.raw.accountType : '-' }}</span>
                    </template>
                    <template #item.senderId="{ item }">
                        <span class="font-weight-regular">{{ item.raw.senderId ? item.raw.senderId : '-' }}</span>
                    </template>
                    <template #item.apiKey="{ item }">
                        <span class="font-weight-regular">{{ item.raw.apiKey ? item.raw.apiKey : '-' }}</span>
                    </template>
                    <!--Actions-->
                    <template #item.actions="{ item }">
                        <IconBtn @click="editUserChannel(item.raw.userChannelSettingId)">
                            <VIcon icon="tabler-edit" />
                        </IconBtn>
                        <IconBtn @click="deleteUserChannel(item.raw.userChannelSettingId)">
                            <VIcon icon="tabler-trash" />
                        </IconBtn>
                    </template>
                </VDataTableServer>
            </VCardText>
            <VCardText v-show="createUserChannel">
                <VRow>
                    <VCol cols="12" lg="2" md="2" sm="4">
                        <p class="mt-2  text-subtitle-1 font-weight-medium">Channel Type</p>
                    </VCol>
                    <VCol lg="6" md="5" sm="8">
                        <AppSelect :items="channelOptions" placeholder="Select Channel Type"
                            v-model="selectedChannel" />

                    </VCol>
                </VRow>
                <VRow>
                    <VCol cols="12" lg="2" md="2" sm="4">
                        <p class="mt-2  text-subtitle-1 font-weight-medium">Account Name</p>
                    </VCol>
                    <VCol lg="6" md="5" sm="8">
                        <AppSelect :items="channelAccountsList" placeholder="Select Account Name"
                            v-model="channelAccountId" />

                    </VCol>
                </VRow>
                <VRow>
                    <VCol cols="12" lg="2" md="2" sm="4">
                        <p class="mt-2  text-subtitle-1 font-weight-medium">Sender Information</p>
                    </VCol>
                    <VCol lg="6" md="5" sm="8">
                        <AppTextField placeholder="Enter senderid" v-model="senderId">
                        </AppTextField>
                    </VCol>
                </VRow>
                <VRow>
                    <VCol>
                        <VBtn @click="saveUserChannel()">
                            Save
                        </VBtn>
                    </VCol>
                </VRow>
            </VCardText>

        </VCard>
    </div>
</template>
<style scoped>
.status-message {
    padding: 10px;
    margin-bottom: 10px;
    border: 1px solid #ccc;
}
</style>
