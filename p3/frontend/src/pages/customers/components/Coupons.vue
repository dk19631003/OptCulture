<script setup lang="ts">
import { isEmpty } from '@/@core/utils';
import { dateFormater, getCurrencySymbol } from '@/@core/utils/localeFormatter';
import axios from '@axios';
import { useCouponStore } from '../stores/couponStore'

interface Props {
    contactId: string
}
interface coupon {
    coupCodeStatus: '',
    coupCodeExpiryDate: '',
    couponExpiryDate: '',
    couponStatus: '',
    couponCode: '',
    description: '',
    issuedOn: '',
    expiryDetails: ''
}


const props = withDefaults(defineProps<Props>(), {
    contactId: ''
}
)
const btnObjs = [
    {
        'name': 'Active',
        'value': 'ACTIVE',
        'color': '#51BBD2',
        'variant': 'outlined'
    },
    {
        'name': 'Applied',
        'value': 'APPLIED',
        'color': '#C1BAFE',
        'variant': 'outlined'
    },
    {
        'name': 'Lapsed',
        'value': 'LAPSED',
        'color': '#F39D40',
        'variant': 'outlined'
    }
]
const currencySymbol = getCurrencySymbol()
const couponStore = useCouponStore()

function clearSelection(btnSelected) {
    btnObjs.map((btn) => {
        if (btn.value != btnSelected.value) btn.variant = 'outlined'
    })
}
function filterCouponsByType(btn) { //clear other selections and enable only cliked ones.
    if (btn.value == 'ACTIVE') {
        couponStore.activeType = !couponStore.activeType;
        couponStore.appliedType = false;
        couponStore.lapsedType = false;
        btn.variant = btn.variant == 'elevated' ? 'outlined' : 'elevated'
        clearSelection(btn)

    }
    if (btn.value == 'APPLIED') {
        couponStore.appliedType = !couponStore.appliedType
        couponStore.lapsedType = false;
        couponStore.activeType = false;
        btn.variant = btn.variant == 'elevated' ? 'outlined' : 'elevated'
        clearSelection(btn)
    }
    if (btn.value == 'LAPSED') {
        couponStore.lapsedType = !couponStore.lapsedType;
        couponStore.appliedType = false;
        couponStore.activeType = false;
        btn.variant = btn.variant == 'elevated' ? 'outlined' : 'elevated'
        clearSelection(btn)
    }
    if (couponStore.pageNumber == 1) couponStore.filterCouponsBySearch();
    else couponStore.pageNumber = 1
}

function searchByCode() {
    if (couponStore.pageNumber == 1) couponStore.filterCouponsBySearch();
    else couponStore.pageNumber = 1
}

function getColor(coupCodeStatus: string, couponStatus: string) {
    if (coupCodeStatus == 'Redeemed') return '#C1BAFE'
    else if (coupCodeStatus == 'Expired') return '#F39D40'
    else if (coupCodeStatus == 'Active') {
        if (couponStatus == 'Running') return '#51BBD2'
        else if (couponStatus == 'Expired') return '#F39D40'
    }
}
function getExpiryDate(couponObj: coupon) {
    if (couponObj.couponExpiryDate) return 'Expires on ' + dateFormater(couponObj.couponExpiryDate)
    else if (couponObj.coupCodeExpiryDate) return 'Expires on ' + dateFormater(couponObj.coupCodeExpiryDate)
    else {
        let expiryDetails = couponObj.expiryDetails;
        const noOfDays = couponObj.expiryDetails.substring(4)
        switch (expiryDetails.charAt(0)) {
            case 'I': {
                let issuedDate = new Date(couponObj.issuedOn);
                let expiryDate = new Date();
                expiryDate.setDate(issuedDate.getDate() + Number(noOfDays))
                return 'Expires on ' + dateFormater(expiryDate.toISOString());
            }
            case 'B': { return 'Expires in ' + noOfDays + ' days after Birthday date' }
            case 'A': { return 'Expires in ' + noOfDays + ' days after Anniversary date' }
        }

    }
}

onMounted(() => {
    couponStore.loadCoupons(props.contactId)
})

</script>
<template>
    <VCard title="Coupons">
        <VCardText>
            <VRow>
                <VCol v-for="(btn, index) in btnObjs" :key="index" cols="12" lg="4" md="4" sm="4">
                    <VChip size="30" class="mr-1 px-2 py-1" :color="btn.color" :variant="btn.variant"
                        @click="filterCouponsByType(btn)">
                        {{ btn.name }}
                    </VChip>
                </VCol>
            </VRow>
            <VRow>
                <VCol cols="12" lg="12" md="12" sm="12" class="mt-n4">
                    <AppTextField height="25" style="{height: 60px;}" v-model="couponStore.searchQuery" class=""
                        placeholder="Coupon code">
                        <template #append-inner>
                            <VIcon icon="tabler-search" size="20" @click="searchByCode()"></VIcon>
                        </template>
                    </AppTextField>
                </VCol>
            </VRow>
        </VCardText>
        <div v-if="!couponStore.loadingCoupons">
            <VCard v-if="couponStore.filterCouponList.length > 0" v-for="(coupon) in couponStore.filterCouponList"
                :key="coupon" :color="getColor(coupon.coupCodeStatus, coupon.couponStatus)" class="px-3 mx-3 my-2"
                elevation="2">
                <div class="d-flex justify-space-between white">
                    <div>
                        <p class="text-subtitle-1 font-weight-medium mt-2 white">{{ coupon.couponCode }}</p>
                        <p class="text-caption mt-n5 white">{{ coupon.description.replaceAll('[PHCurr]', currencySymbol) }}
                        </p>
                    </div>
                    <div class="text-center mt-4">
                        <VIcon icon="tabler-dots-vertical" size="18" />
                        <VMenu activator="parent">
                            <VList>
                                <VListItem>
                                    <template #prepend>
                                        <VIcon icon="tabler-transfer" size="20" />
                                    </template>
                                    <VListItemTitle>Send Coupon</VListItemTitle>
                                </VListItem>
                            </VList>
                        </VMenu>

                    </div>
                </div>
                <VDivider class="mt-n4" />
                <div>
                    <p class="text-body-2 mt-2 white"> {{ getExpiryDate(coupon) }}</p>
                </div>
            </VCard>
            <div v-else class="text-center">
                <p class="text-subtitle-1 font-weight-medium">No coupons available </p>
            </div>
        </div>
        <div v-else class="text-center">
                <p class="text-subtitle-1 font-weight-medium">Loading coupons... </p>
            </div>
        <VRow>
            <VCol cols="12" lg="12" md="12" sm="12">
                <div class="d-flex justify-end">
                    <VPagination :length="couponStore.totalPages" v-model="couponStore.pageNumber" size="small"
                        total-visible="2">
                    </VPagination>
                </div>
            </VCol>
        </VRow>
    </VCard>
</template>
<style scoped>
.white {
    color: #FFFFFF;
}</style>
