<script setup lang="ts">
import axios from '@axios'
import { VDataTableServer } from 'vuetify/labs/VDataTable'
import { useAppAbility } from '@/plugins/casl/useAppAbility'

interface Props {
    isDialogVisible: boolean
}
interface user {
    userName: ''
}
const props = defineProps<Props>()
interface Emit {
    (e: 'update:isDialogVisible', val: boolean): void
}
const emit = defineEmits<Emit>()
const dialogModelValueUpdate = (val: boolean) => {
    emit('update:isDialogVisible', val)
}
const route = useRoute()
const router = useRouter()

const ability = useAppAbility()

const usersPerPage = ref(10)
const totalUsers = ref(0)
const serverUsers = ref<user[]>([])
const userList = ref<user[]>([])
const searchQuery = ref('')
const isLogoutUserVisible = ref(false);
const pageCount = ref(0)
const pageNumber = ref(1)
const userName = ref('')
const table_headers = [
    { title: 'User Name', key: 'userName', sortable: false },
    { title: 'company Name', key: 'companyName', sortable: false },
    { title: 'Actions', key: 'actions', sortable: false }
]

function filterUsers() {
    const queryLower = searchQuery.value.toLowerCase()
    // filter users
    userList.value = serverUsers.value.filter(user => (user.userName.split('__')[0].toLowerCase().includes(queryLower)))
    pageCount.value = Math.ceil(userList.value.length / usersPerPage.value)
    if (usersPerPage.value == -1) usersPerPage.value = totalUsers.value
    const startIndex = (pageNumber.value - 1) * usersPerPage.value;
    const endIndex = startIndex + usersPerPage.value;
    userList.value = userList.value.slice(startIndex, endIndex);
}

watch([searchQuery, usersPerPage], () => {
    pageNumber.value = 1;
    filterUsers();
})
watch(pageNumber, () => {
    filterUsers();
})

function loadAllUsers() {
    const users=JSON.parse(localStorage.getItem('userList') || 'null')
    if(users) {
        serverUsers.value=users
    }else {
        axios.get('/api/user/fetch-all', {
        }).then((resp) => {
            console.log(resp)
            serverUsers.value = resp.data;
            localStorage.setItem("userList",JSON.stringify(resp.data))
        }).catch(() => {

        })
    }
    userList.value = serverUsers.value.slice(0, usersPerPage.value);
    totalUsers.value = serverUsers.value.length;
    pageCount.value = Math.ceil(totalUsers.value / usersPerPage.value)
}

function logoutCheck(username: string) {
    userName.value = username
    isLogoutUserVisible.value = true
}
function switchUser(userName: string) {
    isLogoutUserVisible.value = false;
    emit('update:isDialogVisible', false)

    const params = {
        "userName": userName
    }
    isLogoutUserVisible.value = true;
    axios.get('/api/login/switch-user',
        {
            params: params,
        }
    ).then(r => {
        const abilities = r.data.userAbilities;
        console.log(r.data.userAbilities)
        abilities.push({
            action: 'read',
            subject: 'Auth',
        })
        const userData = JSON.parse(localStorage.getItem('userData') || 'null')
        if (!localStorage.getItem('adminName'))
            localStorage.setItem('adminName', JSON.stringify(userData.userName.split('__')[0])) //storing admin name
        localStorage.removeItem('userData')

        // Remove "accessToken" from localStorage
        localStorage.removeItem('accessToken')
        const userdata = JSON.stringify(r.data.userData);
        const token = JSON.stringify(r.data.token);


        localStorage.setItem('userData', JSON.stringify(r.data.userData))
        localStorage.setItem('accessToken', JSON.stringify(r.data.token))

        // Redirect redirect to index route
        router.replace({ name: 'pages-misc-coming-soon' })
        setTimeout(()=>{
        window.location.reload()},200) //to set router in index route and reflect changes in user
      
    })
        .catch(e => {

        })
        removeSegmentLocalStorage();
}
// REMOVING SEGMENTS FROM LOCAL-STORAGE ON SWITCH-USER
function removeSegmentLocalStorage() {
  for (let i = 0; i < localStorage.length; i++) {
    const key = localStorage.key(i);
    if (key.startsWith('EditedSegment')) {
      localStorage.removeItem(key);
    }
  }
}
if (ability.can('View', 'OCAdmin')) //only admins can see userlist
    loadAllUsers();


</script>
<template>
    <VDialog :width="$vuetify.display.smAndDown ? 'auto' : 677" :model-value="props.isDialogVisible"
        @update:model-value="dialogModelValueUpdate" persistent>
        <!-- Dialog close btn -->
        <DialogCloseBtn @click="dialogModelValueUpdate(false)" />

        <VCard class="pa-sm-8 pa-5">
            <VCardItem class="text-center">
                <VCardTitle class="text-h5 mb-3">
                    Users List
                </VCardTitle>
            </VCardItem>
            <VCardText>
                <VRow class="d-flex justify-space-between">

                    <VCol cols="12" lg="7" md="12" sm="12">
                        <AppTextField v-model="searchQuery" placeholder="Search username" density="compact" class="ma-3"
                            autofocus />
                    </VCol>
                </VRow>
                <VDataTableServer v-model:items-per-page="usersPerPage" :headers="table_headers" :items-length="totalUsers"
                    :items="userList" class="elevation-1" item-value="name" @update:options="filterUsers" density="default">
                    <template #item.userName="{ item }">
                        <VListItem @click="logoutCheck(item.raw.userName)">
                            <span>{{ item.raw.userName.split('__')[0] }}</span>
                        </VListItem>
                    </template>
                    <template #item.actions="{ item }">
                        <!-- <VListItem > -->
                        <VIcon @click="logoutCheck(item.raw.userName)" icon="tabler-user-share"></VIcon>
                        <!-- </VListItem> -->
                    </template>
                    <template #bottom>
                        <VDivider />
                        <VRow>
                            <!-- <VCol cols="12" lg="5" md="5" sm="5">
                                <div class="d-flex justify-space-around">
                                    <span class="ml-2 mr-1">Items per Page</span>

                                    <AppSelect class="mt-1" v-model="usersPerPage" :items="[
                                        { value: 10, title: '10' },
                                        { value: 25, title: '25' },
                                        { value: 50, title: '50' },
                                        { value: 100, title: '100' },
                                        { value: -1, title: 'All' },
                                    ]" style="width: 6.25rem;" />
                                </div>
                            </VCol> -->
                            <VCol>
                                <VPagination :length="pageCount" v-model="pageNumber"
                                    :total-visible="$vuetify.display.xs ? 1 : pageCount > 2 ? 2 : pageCount">
                                </VPagination>
                            </VCol>
                        </VRow>
                    </template>
                </VDataTableServer>
            </VCardText>
        </VCard>
        <VDialog v-model="isLogoutUserVisible" width="450">
            <DialogCloseBtn @click="isLogoutUserVisible = false" />
            <VCard>
                <VCardItem class="text-center">
                    <VCardTitle class="text-h5 mb-3">
                        Account Login
                    </VCardTitle>
                </VCardItem>
                <VCardText>
                    <span class="text-body-1">You will be logged out of your account and will be logged into </span><span
                        class="font-weight-black">{{ userName.split('__')[0] }}</span><span>
                        user's account.</span>
                    <br>
                    <div class="text-center mt-5">
                        <VBtn @click="switchUser(userName)">OK</VBtn>
                    </div>
                </VCardText>
            </VCard>
        </VDialog>
    </VDialog>
</template>
