<script setup lang="ts">
import UserBioPanel from '../components/UserBioPanel.vue';
import LoyaltyCard from '../components/LoyaltyCard.vue';
import OrderStatistics from '../components/OrderStatistics.vue';
import LastNpsRating from '../components/LastNpsRating.vue';
import Referrals from '../components/Referrals.vue'
import GiftCards from '../components/GiftCards.vue';
import Coupons from '../components/Coupons.vue';
import Timeline from '../components/Timeline.vue';
import QuickCoupon from '../components/QuickCoupon.vue';
import { ref } from 'vue'
import axios from '@axios'

const route = useRoute()
const contact = ref();
const membershipNumber = ref('')
const rating = ref(0)

//props for popup screen
const avgOrderValue = {
    title: 'Average Order Value',
    color: 'error',
    stats: '',
    icon: 'tabler-briefcase',
}
const maxOrderValue = {
    title: 'Maximum Order Value',
    color: 'success',
    stats: '',
    icon: 'tabler-message-dots',
}


//loads Contact upon screen loading.
function loadContact() {

    const params = {
        "contactId": route.params.id
    }

    axios.get('/api/contacts/', {
        params: params,
    })
        .then((resp) => {

            contact.value = resp.data;
            // console.log(contact.value);
            membershipNumber.value = contact.value.membershipNumber;
            maxOrderValue.stats = contact.value.ordersInfo.maxOrderValue
            avgOrderValue.stats = contact.value.ordersInfo.avgOrderValue
            rating.value = contact.value.lastNpsRating;

        }).catch((resp) => {
            console.log(resp.message)
        })

}
//loads transactions if loyalty_contact

//date Formater 
function dateFormat(dateString) {
    if (dateString == null) return '-'
    const date = new Date(dateString);
    const monthNames = [
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    ];

    const day = date.getDate();
    const month = monthNames[date.getMonth()];
    const year = date.getFullYear();
    const hours = date.getUTCHours();
    const minutes = date.getUTCMinutes();
    const seconds = date.getUTCSeconds();

    const formattedDate = `${day} ${month} ${year} ${hours}:${minutes}:${seconds}`;

    return formattedDate;
}

// exeuctes on transaction or transaction_period selection changes
loadContact();
</script>

<template >
    <div>
        <VRow v-if="contact">
            <VCol cols="12" lg="4">
                <UserBioPanel :contact="contact" />
            </VCol>
            <VCol cols="12" lg="8">
                <VRow>
                    <VCol cols="12" sm="6" lg="3" md="3">
                        <OrderStatistics v-bind="avgOrderValue" />
                    </VCol>
                    <VCol cols="12" sm="6" lg="3" md="3">
                        <OrderStatistics v-bind="maxOrderValue" />
                    </VCol>
                    <VCol cols="12" lg="6" md="6" sm="12">
                        <LastNpsRating :rating="rating" />
                    </VCol>
                </VRow>
                <VRow>
                    <VCol cols="12" lg="12">
                        <Timeline :contact="contact" />
                    </VCol>
                </VRow>
                <!-- <VRow v-if="contact">
                    <VCol cols="12" lg="6" sm="6" md="6">
                        <LoyaltyCard :loyalty="contact.loyalty" />
                    </VCol>
                    <VCol cols="12" lg="6" md="6" sm="6">
                        <VRow>
                            <VCol cols="12" sm="6" lg="6" md="6">
                                <OrderStatistics v-bind="avgOrderValue" />
                            </VCol>
                            <VCol cols="12" sm="6" lg="6" md="6">
                                <OrderStatistics v-bind="maxOrderValue" />
                            </VCol>
                        </VRow>
                        <VRow>
                            <VCol cols="12" lg="12" md="12" sm="12">
                                <LastNpsRating :rating="rating" />
                            </VCol>
                        </VRow>
                    </VCol>
                </VRow> -->
                <!-- <VCol cols="12" lg="4">
                 <VRow>
                    <VCol cols="12" lg="12" md="6">
                        <Referrals :referrals="contact.referralInfo" />
                    </VCol>
                    <VCol cols="12" lg="12" md="6">
                        <GiftCards />
                    </VCol>-->


            </VCol>
        </VRow>
        <VRow v-if="contact">
            <VCol cols="12" lg="5" md="5" sm="12">
                <VRow>
                    <VCol>
                        <Coupons :contactId="contact.cid" />
                    </VCol>
                </VRow>
                <VRow>
                    <VCol>
                        <Referrals :referrals="contact.referralInfo" />
                    </VCol>
                </VRow>
            </VCol>
            <VCol cols="12" lg="3" md="3" sm="5" v-if="contact">
                <QuickCoupon :couponsList="contact.inventoryCoupons" :contactObj="contact" />
            </VCol>
            <VCol cols="12" lg="4" sm="7" md="4">
                <LoyaltyCard :loyalty="contact.loyalty" />
            </VCol>

        </VRow>
    </div>
</template>

<style lang="scss" scoped></style>
