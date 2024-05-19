<script setup lang="ts">
import { getCurrencySymbol } from '@/@core/utils/localeFormatter'
import { avatarText, kFormatter } from '@core/utils/formatters'
import { dateFormater } from '@/@core/utils/localeFormatter'
const isEditContactClicked = ref(false)
interface Props {
    contact: {
        cid: number,
        createdDate: string,
        emailId: string,
        firstName: string,
        lastName: string,
        lastNpsRating: number,
        gender: string,
        anniversary: string,
        birthDay: string,
        ordersInfo: {
            avgOrderValue: number,
            maxOrderValue: number,
            noOfOrders: number,
            totalAmountSpent: number
        },
        membershipNumber: string,
        membershipStatus: string,
        mobilePhone: string
    }

}

const props = defineProps<Props>()
const contactForEdit = props.contact;
function setContacttoLocStrge() {
    localStorage.removeItem('ContactDetails');
    localStorage.setItem('ContactDetails', JSON.stringify(contactForEdit));

}

function getGender(gender) {
    if (gender != null) {
        if (gender.toLowerCase() == 'm'.toLowerCase() || gender.toLowerCase() == 'male'.toLowerCase()) {
            return 'Male'
        }
        return 'Female'
    }
    return '-'
}
function dateFormat(dateString) {
    if (dateString == null) return '-'
    return new Date(dateString);
    // return new Intl.DateTimeFormat(locale).format(date);
}
const monthNames = [
    "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
    "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
];

// const day = date.getDate();
// const month = monthNames[date.getMonth()];
// const year = date.getFullYear();
// const hours = date.getUTCHours();
// const minutes = date.getUTCMinutes();
// const seconds = date.getUTCSeconds();

// const formattedDate = `${day} ${month} ${year} ${hours}:${minutes}:${seconds}`;

// return formattedDate;
// }
const currencySymbol = getCurrencySymbol()

</script>

<template>
    <VRow>
        <!-- SECTION User Details -->
        <VCol cols="12">
            <VCard v-if="props.contact">
                <VCardText class="text-center pt-15">
                    <!-- 👉 Avatar -->
                    <VAvatar :size="60" :color="!props.contact.avatar ? 'primary' : undefined"
                        :variant="!props.contact.avatar ? 'tonal' : undefined">
                        <span class="text-h5 font-weight-medium text-capitalize"
                            v-if="contact.firstName || contact.lastName">
                            {{ contact.firstName ? contact.firstName.substring(0, 1) : '' }}{{ contact.lastName ?
                                contact.lastName.substring(0, 1) : '' }}

                        </span>
                    </VAvatar>
                    <br>
                    <!-- 👉 User fullName -->
                    <span class="text-h5 mt-4 text-capitalize">
                        {{ contact.firstName ?? '' }} {{
                            contact.lastName ?? '' }}
                    </span>
                    <br>
                    <span>{{ contact.membershipNumber }}</span>
                    <!-- 👉  Current Tier chip -->
                    <br>
                    <div>
                        <VBtn color="success" class="text-center px-2" height="23" disabled="true">
                            <span>{{ contact.loyalty ? contact.loyalty.currentTierName ?? '-' : '-' }}</span>
                        </VBtn>
                    </div>
                </VCardText>

                <VCardText class=" d-flex justify-space-between mt-3">
                    <!-- 👉 Orders chip -->
                    <div class="d-flex align-center ">
                        <VAvatar :size="38" rounded color="primary" variant="tonal" class="me-3">
                            <VIcon icon="tabler-shopping-cart-filled" />
                        </VAvatar>

                        <div>
                            <h6 class="text-h6">
                                {{ kFormatter(props.contact.ordersInfo.noOfOrders) }}
                            </h6>
                            <span class="text-sm">No. of Orders</span>
                        </div>
                    </div>

                    <!-- 👉 Total Currency -->
                    <div class="d-flex align-center ">
                        <VAvatar :size="38" rounded color="primary" variant="tonal" class="me-3">
                            <VIcon icon="tabler-wallet" />
                        </VAvatar>

                        <div>
                            <h6 class="text-h6">
                                {{ currencySymbol }} {{ kFormatter(props.contact.ordersInfo.totalAmountSpent) }}
                            </h6>
                            <span class="text-sm">Currency Spent</span>
                        </div>
                    </div>
                </VCardText>

                <VDivider />

                <!-- 👉 Details -->
                <VCardText>
                    <p class="text-sm text-uppercase text-disabled">
                        Details
                    </p>

                    <!-- 👉 User Details list -->
                    <VList class="card-list mt-2">
                        <VListItem>
                            <VListItemTitle>
                                <h6 class="text-body-2 ">
                                    Gender:
                                    <span class="text-body-2 font-weight-medium">
                                        {{ getGender(props.contact.gender) }}
                                    </span>
                                </h6>
                            </VListItemTitle>
                        </VListItem>
                        <VListItem>
                            <VListItemTitle>
                                <h6 class="text-body-2 ">
                                    Mobile :
                                    <span class="text-body-2 font-weight-medium">
                                        {{ props.contact.mobilePhone ?? '-' }}
                                    </span>
                                </h6>
                            </VListItemTitle>
                        </VListItem>
                        <VListItem>
                            <VListItemTitle>
                                <h6 class="text-body-2 ">
                                    Email :
                                    <span class="text-body-2 font-weight-medium">{{ props.contact.emailId }}</span>
                                </h6>
                            </VListItemTitle>
                        </VListItem>

                        <VListItem>
                            <VListItemTitle>
                                <h6 class="text-body-2 ">
                                    Member Since :
                                    <span class="text-body-2 font-weight-medium">{{
                                        dateFormater(props.contact.createdDate)
                                    }}</span>
                                </h6>
                            </VListItemTitle>
                        </VListItem>

                        <VListItem>
                            <VListItemTitle>
                                <h6 class="text-body-2 ">
                                    Birthday :
                                    <span class="text-capitalize text-body-2 font-weight-medium">{{
                                        dateFormater(props.contact.birthDay).toLowerCase()
                                    }}</span>
                                </h6>
                            </VListItemTitle>
                        </VListItem>

                        <VListItem>
                            <VListItemTitle>
                                <h6 class="text-body-2 ">
                                    Anniversary :
                                    <span class="text-body-2 font-weight-medium">
                                        {{ dateFormater(props.contact.anniversary) }}
                                    </span>
                                </h6>
                            </VListItemTitle>
                        </VListItem>
                    </VList>
                </VCardText>

                <!-- 👉 Edit and Suspend button -->
                <VCardText class="d-flex justify-center">
                    <RouterLink :to="{
                        name: 'customers-edit-id',
                        params: {
                            id: contact.cid,
                        }
                    }">
                        <VBtn variant="elevated" class="me-4" @click="setContacttoLocStrge">
                            Edit
                        </VBtn>
                    </RouterLink>

                    <VBtn variant="tonal" color="error">
                        Suspend
                    </VBtn>
                </VCardText>
            </VCard>
        </VCol>
    </VRow>
</template>

<style lang="scss" scoped>
.card-list {
    --v-card-list-gap: 0.75rem;
}

.text-capitalize {
    text-transform: capitalize !important;
}
</style>
