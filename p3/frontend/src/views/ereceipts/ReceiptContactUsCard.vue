<script setup lang="ts">
import type { EreceiptType } from '@/@fake-db/types'

interface Props {
    data?: EreceiptType
}

const props = withDefaults(defineProps<Props>(), {
    data: {
        storeDetails: {
            "emailId": "store@email.id",
            "mobileNo": "mobile-no"
        },

    }
});

</script>

<template>
    <VCard class="my-2 text-center" v-if="data.storeDetails" elevation="0">
        <VCardText class="text-center">
            <p class="wr-text-h1 text-center"><u>Contact Us</u></p>
            <VIcon v-if="data.storeDetails.mobileNo" size="20" icon="tabler-device-mobile"></VIcon>&nbsp;<span
                class="wr-text-h2">{{
                    data.storeDetails.mobileNo }}</span><br>

            <VIcon v-if="data.storeDetails.emailId" size="20" icon="tabler-mail"></VIcon>&nbsp;<span class="wr-text-h2">{{
                data.storeDetails.emailId }}</span><br>

            <span class="wr-text-h2">(Operational Timings Mon-Fri: 10:00am to 6:00pm)</span>
        </VCardText>
    </VCard>
</template>
