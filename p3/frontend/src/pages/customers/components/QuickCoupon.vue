<script setup lang="ts">
import { dateFormater, getCurrencySymbol } from '@/@core/utils/localeFormatter'
import axios from '@axios'
import { useCouponStore } from '../stores/couponStore'

interface Props {
    couponsList: []
    contactObj: {
        cid: string,
        mobilePhone: string,
        emailId: string
    }
}
interface coupon {
    couponCode: string
    expiryDate: string
    expiryDetails: string
    description: string
    couponCodeId: string
    couponId:Number
}
const props = defineProps<Props>()
const couponList = ref<coupon[]>([])
couponList.value = props.couponsList;
const notifyUser = ref(false)
const isIssueSuccess = ref('error')
const currencySymbol = getCurrencySymbol()
const carouselModel = ref(0)
const couponStore = useCouponStore()
const notificationMsg=ref('Coupon issued successfully')
async function issueCoupon(coupon: coupon) {
    console.log(coupon.couponCodeId)
    try {
        const resp = await axios.put('/api/coupons/issue-coupon', {
            contactId: props.contactObj.cid,
            couponId: coupon.couponId,
            mobile: props.contactObj.mobilePhone,
            emailId: props.contactObj.emailId,
            expiryDetails: coupon.expiryDetails
        })
        const message = resp.data;
        console.log(message);
        showNotification(message,coupon)
    }
    catch (error) {
        console.log(error)
        showNotification('error',coupon)
    }

}
function showNotification(message: string,coupon:coupon) {
    if (message.includes('issued')) {
        isIssueSuccess.value = 'success';
        notificationMsg.value='Coupon issued successfully'
        const code=message.split('::')[1] //coupon code
        let isCodeExist=false;
        couponStore.couponList.forEach(coupon =>{
            if(coupon.couponCode==code) isCodeExist=true;
        })
        console.log(isCodeExist)
        if(!isCodeExist){
            couponStore.couponList.push(
                {
                coupCodeStatus: 'Active',
                coupCodeExpiryDate: '',
                couponExpiryDate: coupon.expiryDate,
                couponStatus: 'Running',
                couponCode: code,
                description: coupon.description,
                issuedOn: new Date().toString(),
                expiryDetails: coupon.expiryDetails}
                );
        }else { //coupon code already issued and not redeemed 
            notificationMsg.value='Coupon already issued !'
        }
    }
    else{
        isIssueSuccess.value='error'
        notificationMsg.value='Error while issuing coupon '
    }
    notifyUser.value = true;
    
}
function calculateDate(couponObj: coupon) {
    if (couponObj.expiryDate) return 'Expires on ' + dateFormater(new Date(couponObj.expiryDate).toString())
    else {
        const expiryDetails = couponObj.expiryDetails; //ex. I;=;13 or B;=;23 or A;=;24
        if (expiryDetails) {
            const noOfDays = Number(expiryDetails.substring(4))
            switch (expiryDetails.charAt(0)) {
                case 'I': { return 'Expires in ' + noOfDays + ' days after Issuance' }
                case 'B': { return 'Expires in ' + noOfDays + ' days after Birthday date' }
                case 'A': { return 'Expires in ' + noOfDays + ' days after Anniversary date' }
            }
        }
    }
}
</script>
<template>
    <VSnackbar location="top" timeout="3000" v-model="notifyUser" :color="isIssueSuccess" height="20" width="auto">
        <div class=" text-center">
            <p class="font-weight-medium ma-0">{{ notificationMsg }}</p>
        </div>
    </VSnackbar>
    <VCard min-height="350">
        <template #title>
            Quick Coupon
        </template>
        <VCardText v-if="couponList.length > 0">
            <div class="d-flex justify-space-between mt-n3">
                <v-btn variant="text" icon="tabler-chevron-left"
                    @click="carouselModel = Math.max(carouselModel - 1, 0)"></v-btn>
                <v-btn variant="text" icon="tabler-chevron-right"
                    @click="carouselModel = Math.min(carouselModel + 1, couponList.length - 1)"></v-btn>
            </div>
            <VCarousel hide-delimiters hide-delimiter-background height="300px" v-model="carouselModel"
                :show-arrows="false">
                <VCarouselItem v-for="(couponObj, i) in couponList" :key="couponObj" :value="i">
                    <div class="d-flex justify-center align-center">
                        <VCard color="#FBE334" :width="$vuetify.display.smAndDown ? 'auto' : 220" height="250"
                            elevation="3">
                            <VCardText>
                                <div>
                                    <div>
                                        <span class="text-subtitle-1 font-weight-medium">{{
                                            couponObj.description.replaceAll('[PHCurr]', currencySymbol) }}</span>
                                    </div>
                                    <div class="text-body-1">{{ couponObj.couponCode }}</div>
                                    <div class="d-flex align-end ">
                                        <p class="text-body-2"> {{ calculateDate(couponObj) }}</p>
                                    </div>
                                </div>

                            </VCardText>
                        </VCard>
                    </div>
                    <div class="text-center ma-3">
                        <VBtn @click="issueCoupon(couponObj)">Issue </VBtn>
                    </div>
                </VCarouselItem>

            </VCarousel>

        </VCardText>
        <VCardText v-else>
            <div class="text-center">
                <p class="text-subtitle-1 font-weight-medium">No Coupons in Inventory.</p>
            </div>

        </VCardText>

    </VCard>
</template>
<style scoped>
.arrow {
    align-items: flex-end;
}
</style>