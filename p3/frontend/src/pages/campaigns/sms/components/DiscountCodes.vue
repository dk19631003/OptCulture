<script setup lang="ts">
import axios from '@axios'
import { getCurrencySymbol } from '@/@core/utils/localeFormatter'
interface Props {
    isDialogVisible: boolean,
    color:string
}

interface Emit {
    (e: 'update:isDialogVisible', val: boolean): void
    (e: 'submitDiscount', value: any): void
}
interface discountCode {
    couponName: string,
    description: string,
    couponCode: string,
    couponId: number,
    couponType:string
}

const couponList = ref<discountCode[]>([])
const pageNumber = ref(-1)
const selectedDiscountCodes = ref(new Set())
const isCodesLastPage = ref(false)
const couponName = ref('')
const currencySymbol = getCurrencySymbol()
let isPopupOpen=true;
const props = defineProps<Props>()
const loadingCoupons = ref(false)
const emit = defineEmits<Emit>()

const dialogVisibleUpdate = (val: boolean) => {
    emit('submitDiscount', selectedDiscountCodes.value)
    selectedDiscountCodes.value.clear()
    couponName.value = '';
    couponList.value.length=0
    pageNumber.value=-1
    isPopupOpen=true
    emit('update:isDialogVisible', val)
}
async function fetchDiscountCodes(type: string) {
    try {
        if (type == 'Search') pageNumber.value = 0;
        else pageNumber.value = pageNumber.value + 1;
        loadingCoupons.value = true;
        const resp = await axios.get('/api/coupons/inventory', {
            params: {
                'pageNumber': pageNumber.value,
                'couponName': couponName.value //optional
            }
        })
        console.log(resp);
        if (type == 'Search') couponList.value = resp.data.content //when filter used
        else couponList.value = [...couponList.value, ...(resp).data.content] //adding to existing list
        loadingCoupons.value = false;
        if (resp.data.last) //is this page is last
        {
            isCodesLastPage.value = true;
        }
        else isCodesLastPage.value = false;
    } catch (err) {
    }
}

function setDiscountCode(item: discountCode) {
    const couponMergetag = item.couponType.toLowerCase() == 'multiple' ? '${coupon.CC_' + item.couponId + '}' : item.couponCode;
    const coupon={
        tag:couponMergetag,
        name:item.couponName
    }
    if (selectedDiscountCodes.value.has(coupon)) {
        selectedDiscountCodes.value.delete(coupon)
    }
    else {
        selectedDiscountCodes.value.add(coupon);
        setTimeout(() => { dialogVisibleUpdate(false) }, 1000)
    }
    
}
watchEffect(()=>{
    
    if(props.isDialogVisible && isPopupOpen){
    isPopupOpen=false
    fetchDiscountCodes('');
    }
})

</script>

<template>
    <div>
        <VDialog :model-value="props.isDialogVisible" :width="$vuetify.display.smAndDown ? 'auto' : 1100"
            @update:model-value="dialogVisibleUpdate" persistent>
            <!-- 👉 Dialog close btn -->
            <DialogCloseBtn @click="dialogVisibleUpdate(false)" />

            <VCard>
                <VCardTitle>
                    <div class="text-center text-caption text-wrap"><span class="font-weight-medium text-h6 ma-3">Select
                            Discount
                            Codes</span>
                        <p>These code have already been created you can select one from the existing or create a new code
                            for
                            this campaign</p>
                    </div>
                </VCardTitle>

                <VCardText>
                    <VRow class="d-flex justify-space-between w-100">

                        <VCol cols="12" md="5" lg="5" sm="5" xs="10">
                            <AppTextField placeholder="coupon name" height="20" style="{height: 60px;}"
                                v-model="couponName">
                            </AppTextField>
                        </VCol>
                        <VCol cols="12" md="2" lg="2" sm="2" xs="4">
                            <VBtn :color="props.color" @click="fetchDiscountCodes('Search')">Search</VBtn>
                        </VCol>

                        <VCol cols="12" lg="4" md="4" sm="5" xs="7"
                            :class="{ 'd-flex': true, 'justify-end': !$vuetify.display.smAndDown }">
                            <VBtn prepend-icon="tabler-plus" :color="props.color"> Create Discount Code</VBtn>
                        </VCol>
                    </VRow>
                </VCardText>
                <VCardText v-if="!loadingCoupons">

                    <VRow v-if="couponList.length > 0">
                        <VCol cols="12" lg="4" md="4" sm="6" v-for="(item, index) in couponList" :key="index">
                            <VCard flat border @click="setDiscountCode(item)"
                                :class="[selectedDiscountCodes.has('${coupon.CC_' + item.couponId + '}') ? 'border-custom' : '']"
                                height="150">
                                <VCardTitle>
                                    <div class="text-center">
                                        <p class="text-subtilte-1 font-weight-medium">{{ item.couponName }}</p>

                                    </div>
                                </VCardTitle>
                                <VCardText>
                                    <div class="text-center">
                                        <p class="text-subtilte-2 font-weight-light">{{ item.description &&
                                            item.description.length > 35 ?
                                            item.description.substring(0, 34).replaceAll('[PHCurr]', currencySymbol) + '...' : item.description.replaceAll('[PHCurr]', currencySymbol)
                                        }}&nbsp; </p>
                                        <VChip color="primary" class="rounded-0" v-if="item.couponCode">{{ item.couponCode }}</VChip>
                                        <p v-else>&nbsp;</p>
                                    </div>
                                </VCardText>
                            </VCard>

                        </VCol>
                    </VRow>
                    <!-- <VRow > -->
                    <div class="text-center mt-3" v-else>
                        <p class="text-subtitle-1 font-weight-medium">No Coupons available in Inventory</p>
                    </div>
                    <!-- </VRow> -->
                    <div class="d-flex justify-end mt-6">
                        <!-- <VBtn>Back</VBtn> -->
                        <VBtn height="22" class="px-n1 " variant="tonal" @click="fetchDiscountCodes"
                            :disabled="isCodesLastPage">Load More
                        </VBtn>
                    </div>
                </VCardText>
                <VCardText v-else>
                    <div class="text-center mt-3">
                        <p class="text-subtitle-1 font-weight-medium">Loading coupons ...</p>
                    </div>
                </VCardText>
            </VCard>
        </VDialog>
    </div>
</template>
<style lang="scss" scoped>
.border-custom {
    border-color: rgb(var(--v-theme-primary)) !important;
}</style>
