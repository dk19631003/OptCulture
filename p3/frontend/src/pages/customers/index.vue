<script setup lang="ts">
import { VDataTableServer } from 'vuetify/labs/VDataTable'
import type { ContactProperties } from '@/@fake-db/types'
import { ref } from 'vue'
import axios from '@axios'
import { useAppAbility } from '@/plugins/casl/useAppAbility'
import router from '@/router'
import { dateFormater, numberFormatter } from '@/@core/utils/localeFormatter'
import { emit } from 'process'

interface Contact {
    firstName: string,
    lastName: string,
    membershipNumber: string,
    lastInteraction: string,
    emailId: string,
    mobilePhone: string
}
const searchCriteria = ref()
const searchValue = ref('')
const firstName = ref('')
const lastName = ref('')
const contactPerPage = ref(10)
const totalContacts = ref(0)
const serverContacts = ref<Contact[]>([])
const contactsList = ref<Contact[]>([])
const currTierColor = ref('')
const userTotalContacts = ref('')
const notify = ref(false);
const ability = useAppAbility()
const loading = ref(false)

// Headers
const table_headers = [
    { title: 'NAME', key: 'firstName' },
    { title: 'EMAIL', key: 'emailId' },
    { title: 'mobile', key: 'mobilePhone' },
    { title: 'Last Interaction', key: 'lastInteraction' },
    { title: 'membership Tier', key: 'membershipNumber' },
    { title: 'Actions', key: 'actions', sortable: false },
]

// 👉 Fetching contacts
function displayContacts({ page, sortBy }) {
    loading.value = true;
    if (sortBy[0]?.key === 'emailId') {
        serverContacts.value = serverContacts.value.sort((a, b) => {
            if (sortBy[0]?.order === 'asc') {
                if (a.emailId != null && b.emailId != null)
                    return a.emailId.localeCompare(b.emailId)
                return a.emailId == null ? -1 : 1;
            }

            else {
                if (a.emailId != null && b.emailId != null)
                    return b.emailId.localeCompare(a.emailId)
                return a.emailId == null ? 1 : -1;
            }


        })

    }
    else if (sortBy[0]?.key === 'membershipNumber') {
        serverContacts.value = serverContacts.value.sort((a, b) => {
            if (sortBy[0]?.order === 'asc') {
                if (a.membershipNumber != null && b.membershipNumber != null)
                    return a.membershipNumber.localeCompare(b.membershipNumber)
                return a.membershipNumber == null ? -1 : 1;
            }

            else {
                if (a.membershipNumber != null && b.membershipNumber != null)
                    return b.membershipNumber.localeCompare(a.membershipNumber)
                return a.membershipNumber == null ? 1 : -1;
            }
        })
    }
    else if (sortBy[0]?.key === 'mobilePhone') {
        serverContacts.value = serverContacts.value.sort((a, b) => {
            if (sortBy[0]?.order === 'asc') {
                if (a.mobilePhone != null && b.mobilePhone != null)
                    return a.mobilePhone.localeCompare(b.mobilePhone)
                return a.mobilePhone == null ? -1 : 1;
            }

            else {
                if (a.mobilePhone != null && b.mobilePhone != null)
                    return b.mobilePhone.localeCompare(a.mobilePhone)
                return a.mobilePhone == null ? 1 : -1;
            }
        })
    }
    else if (sortBy[0]?.key === 'firstName') {
        serverContacts.value = serverContacts.value.sort((a, b) => {
            if (sortBy[0]?.order === 'asc') {
                if (a.firstName != null && b.firstName != null)
                    return a.firstName.localeCompare(b.firstName) //write for lastname
                return a.firstName == null ? -1 : 1;
            }

            else {
                if (a.firstName != null && b.firstName != null)
                    return b.firstName.localeCompare(a.firstName)
                return a.firstName == null ? 1 : -1;
            }
        })
    }
    else if (sortBy[0]?.key === 'lastInteraction') {
        serverContacts.value = serverContacts.value.sort((a, b) => {
            if (sortBy[0]?.order === 'asc') {
                if (a.lastInteraction != null && b.lastInteraction != null)
                    return a.lastInteraction.localeCompare(b.lastInteraction) //write for lastname
                return a.lastInteraction == null ? -1 : 1;
            }

            else {
                if (a.lastInteraction != null && b.lastInteraction != null)
                    return b.lastInteraction.localeCompare(a.lastInteraction)
                return a.lastInteraction == null ? 1 : -1;
            }
        })
    }
    const startIndex = (page - 1) * contactPerPage.value;
    const endIndex = startIndex + contactPerPage.value;
    contactsList.value = serverContacts.value.slice(startIndex, endIndex);
    loading.value = false;

}
async function loadContacts({ page, sortBy }) {
    // if (!ability.can('READ', '_Contacts_Menu_StansardUser')) {
    //     if (searchCriteria.value == undefined) {
    //         notify.value = true;
    //         setTimeout(() => {
    //             notify.value = false;
    //         }, 3000)
    //         console.log(searchCriteria.value)
    //         return;
    //     }
    // }
    loading.value = true;
    if (contactPerPage.value.toString() === '-1') contactPerPage.value = totalContacts.value;
    const params = {
        "pageNumber": 0,
        "pageSize": 500,
        "criteria": searchCriteria.value,
        "searchvalue": searchValue.value,
        "firstName": firstName.value,
        "lastName": lastName.value,
    };

    const resp = await axios.get('/api/contacts/search', {

        params: params,


    }).then((resp) => {

        serverContacts.value = resp.data.object;
        const startIndex = (page - 1) * contactPerPage.value;
        const endIndex = startIndex + contactPerPage.value;
        contactsList.value = serverContacts.value.slice(startIndex, endIndex);
        totalContacts.value = serverContacts.value.length;
        userTotalContacts.value = resp.data.totalItems;
        // if(totalContacts.value > 500) totalContacts.value=500;
        loading.value = false;
    }).catch((resp) => console.log(resp.message))

}

function setContacttoLocStrge(item: any) {
    localStorage.removeItem('ContactDetails');
    localStorage.setItem('ContactDetails', JSON.stringify(item.raw));
    // isEditContactClicked.value = !isEditContactClicked.value
}


//searchCriteria tracking
// 👉 search filters
const search_filter = computed(() => {
    let list = [{ title: 'Email id starts with', value: 'EMAILID' },
    { title: 'Mobile Number', value: 'MOBILE' },
    { title: 'Membership Number', value: 'MEMBERSHIPNUMBER' }]
    if (Number(userTotalContacts.value) < 600000) list.push({ title: 'Name contains', value: 'NAME' })
    return list;
})


loadContacts({ page: 1, sortBy: {} })
</script>

<template>
    <div>
        <section>
            <VSnackbar v-model="notify" location="top" timeout="3000" color="success">
                <span>Search with filter to view customers</span>
            </VSnackbar>
            <VRow>
                <VCol cols="12" lg="3" md="4" sm="6">
                    <VCard color="primary">
                        <VCardTitle class="text-h5 text-white">
                            Total Contacts
                        </VCardTitle>
                        <VCardSubtitle class="text-h6 text-white">
                            <p>{{ numberFormatter(userTotalContacts) }}</p>
                        </VCardSubtitle>
                    </VCard>
                </VCol>
            </VRow>
            <VRow>
                <VCol cols="12">
                    <VCard title="Contacts">
                        <!-- 👉 Filters -->
                        <VCardText>
                            <VRow>

                                <VCol cols="12" lg="4" sm="12" md="4">
                                    <AppSelect v-model="searchCriteria" label="Search Criteria" :items="search_filter"
                                        clearable clear-icon="tabler-x" placeholder="Search Criteria" />
                                </VCol>
                                <VCol v-show="searchCriteria != 'NAME'" cols="12" lg="6" md="6" sm="12">
                                    <AppTextField label="Search Value" v-model="searchValue" />
                                </VCol>
                                <VCol v-show="searchCriteria === 'NAME'" cols="12" lg="3" md="3">
                                    <AppTextField label="First Name" v-model="firstName" />
                                </VCol>
                                <VCol v-show="searchCriteria === 'NAME'" cols="12" lg="3" md="3">
                                    <AppTextField label="Last Name" v-model="lastName" />
                                </VCol>
                                <VCol cols="12" lg="2" md="2" class="mt-6">
                                    <VBtn variant="tonal" color="primary" prepend-icon="tabler-filter-search"
                                        @click="loadContacts({ page: 1, sortBy: {} })">
                                        Filter
                                    </VBtn>
                                </VCol>
                            </VRow>
                        </VCardText>
                        <!-- <div class="text-center ma-4 position-sticky" v-if="loading">
                            <VProgressLinear indeterminate color="primary" :rounded="false" height="5" />
                        </div> -->
                        <VDataTableServer v-model:items-per-page="contactPerPage" :headers="table_headers"
                            :items-length="totalContacts" :items="contactsList" class="elevation-1" item-value="name"
                            @update:options="displayContacts" hover :loading="loading">
                            <template v-slot:loading>
                                <div class="text-center">
                                    Loading.... Please wait.
                                </div>
                            </template>
                            <template #item.firstName="{ item }">
                                <RouterLink :to="{
                name: 'customers-view-id',
                params: {
                    id: item.raw.cid,
                }
            }" class="font-weight-medium  ">
                                    <pre><span class="text-subtitle-1 font-weight-medium contact-list-name" >{{ item.raw.firstName }} {{ (item.raw.lastName) ? item.raw.lastName : item.raw.firstName ? ' ' : '-' }}</span></pre>
                                </RouterLink>
                            </template>
                            <template #item.emailId="{ item }">
                                <span class="font-weight-regular">{{ item.raw.emailId ? item.raw.emailId : '-' }}</span>
                            </template>
                            <template #item.mobilePhone="{ item }">
                                <span>{{ item.raw.mobilePhone ? item.raw.mobilePhone : '-' }}</span>

                            </template>
                            <template #item.membershipNumber="{ item }">
                                <div v-if="item.raw.membershipNumber">
                                    <p class="mt-2">
                                        <VBtn color="success" class="text-center px-2" height="23" disabled="true">
                                            <span>{{ item.raw.currentTierName ? item.raw.currentTierName : '-'
                                                }}
                                            </span>
                                            <!-- <span>{{ definetierColor() }}</span>-->

                                        </VBtn>
                                    </p>
                                    <p class="text-subtitle-2 mt-n3">{{ item.raw.membershipNumber ?
                item.raw.membershipNumber : '-' }}
                                    </p>
                                </div>
                                <div v-else class="text-left">-</div>
                            </template>
                            <template #item.lastInteraction="{ item }">
                                {{ dateFormater(item.raw.lastInteraction) ? dateFormater(item.raw.lastInteraction) : '-'
                                }}
                            </template>
                            <!-- Actions  -->
                            <template #item.actions="{ item }">
                                <RouterLink :to="{
                name: 'customers-view-id',
                params: {
                    id: item.raw.cid,
                }
            }" class="font-weight-medium contact-list-icon ">
                                    <VIcon icon="tabler-eye" />
                                </RouterLink>

                                <RouterLink :to="{
                name: 'customers-edit-id',
                params: {
                    id: item.raw.cid,
                }
            }" class="font-weight-medium contact-list-icon ">
                                    <IconBtn @click="setContacttoLocStrge(item)">
                                        <VIcon icon="tabler-edit" />
                                    </IconBtn>
                                </RouterLink>

                            </template>
                        </VDataTableServer>

                    </VCard>
                </vcol>
            </vrow>
        </section>
    </div>
</template>

<style lang="scss" scoped>
.contact-list-icon:not(:hover) {
    color: rgba(var(--v-theme-on-background), var(--v-medium-emphasis-opacity));
}
</style>
<!-- <route lang="yaml">
    meta:
    action: View
    subject: Contacts
</route> -->
