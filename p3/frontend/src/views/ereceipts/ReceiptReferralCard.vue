<script setup lang="ts">
import { EreceiptType } from '@/@fake-db/types'
import axios from 'axios';
interface props {
    data?: EreceiptType
}
const referralCode = ref();
const redeemCount = ref();
const rewardEarned = ref();
const showCard = ref(true);
const props = withDefaults(defineProps<props>(), {
    data: {
        customer: {
            "mobilePhone": ""
        },
        branding: {
            "referralImage": ""
        },
        customerToken: ""
    }
});

let image = props.data?.branding.referralImage;
const data = {
    "mobileNumber": props.data?.customer.mobilePhone,
    "customerToken": props.data?.customerToken
}
if (props.data?.customer.mobilePhone && props.data?.customerToken) {
    getReferral();
}
function getReferral() {
    axios.post('/api/ereceipt/referral', data, {
    }).then(response => {
        console.log(response)
        referralCode.value = response.data?.REFERRALINFO.REFERRALCODE;
        redeemCount.value = response.data?.REFERRALINFO.REDEEMEDCOUNT;
        rewardEarned.value = response.data?.REFERRALINFO.REWARDEARNED;
    }).catch((err) => {
        console.log(err)
        showCard.value = false;
    })
}
</script>
<template>
    <VCard v-if="showCard">
        <VCardText>
            <p class="wr-text-h1 text-center">Referrals</p>
            <div class="d-flex justify-space-between">
                <div>
                    <p>Referral Code</p>
                    <p>Total No of Sucessful Referrals</p>
                    <p>Reward/Currency Issued</p>
                </div>
                <div>
                    <p>{{ referralCode }}</p>
                    <p>{{ redeemCount }}</p>
                    <p>{{ rewardEarned }}</p>
                </div>
            </div>
            <img :src="image" class="align-center w-100" />
        </VCardText>
    </VCard>
</template> 
