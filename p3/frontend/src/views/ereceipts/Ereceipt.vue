<script lang="ts">
import ReceiptBrandCard from '@/views/ereceipts/ReceiptBrandCard.vue'
import ReceiptCustomerCard from '@/views/ereceipts/ReceiptCustomerCard.vue'
import ReceiptGetInvoiceForm from '@/views/ereceipts/ReceiptGetInvoiceForm.vue'
import ReceiptItemsList from '@/views/ereceipts/ReceiptItemsList.vue'
import ReceiptNpsForm from '@/views/ereceipts/ReceiptNpsForm.vue'
import ReceiptOffersCarousel from '@/views/ereceipts/ReceiptOffersCarousel.vue'
import ReceiptPoweredBy from '@/views/ereceipts/ReceiptPoweredBy.vue'
import ReceiptShopOnlineLink from '@/views/ereceipts/ReceiptShopOnlineLink.vue'
import ReceiptSocialIcons from '@/views/ereceipts/ReceiptSocialIcons.vue'
import ReceiptStoreCard from '@/views/ereceipts/ReceiptStoreCard.vue'
import ReceiptTerms from '@/views/ereceipts/ReceiptTerms.vue'
import ReceiptTxnHistoryButton from '@/views/ereceipts/ReceiptTxnHistoryButton.vue'
import ReceiptVideoEmbed from '@/views/ereceipts/ReceiptVideoEmbed.vue'
import ReceiptLoyaltyCard from '@/views/ereceipts/ReceiptLoyaltyCard.vue'
import ReceiptContactUsCard from '@/views/ereceipts/ReceiptContactUsCard.vue'
import ReceiptRecommendationCard from '@/views/ereceipts/ReceiptRecommendationCard.vue'
import ReceiptReferralCard from '@/views/ereceipts/ReceiptReferralCard.vue'
import axios from '@axios'
import { useThemeConfig } from '@core/composable/useThemeConfig'

import type { EreceiptType } from '@/@fake-db/types'
import { themeConfig } from '@themeConfig'
const { theme } = useThemeConfig()
theme.value = 'light'


const receiptData: EreceiptType = ref({})
export default {
    setup() {
        const route = useRoute()
        onMounted(() => {
            const id = route.params.id

            let url = window.location.href;
            let channelType = url.includes('/s/') ? "SMS" : (url.includes('/w/') ? "WHATSAPP" : "UNKNOWN");
            console.log("Channel-Type : ", channelType);
            let params = {
                "channelType": channelType,
            }

            axios.get('/api/ereceipt/' + id,
                { params: params }
            ).then(response => {
                console.log(response.data)
                let receipt = response.data;
                // receipt = JSON.parse(receipt)
                receipt.components = JSON.parse(receipt.components)
                receiptData.value = receipt
                localStorage.setItem('optculture-theme', 'light')
                localStorage.setItem('optculture-initial-loader-bg', '#FFFFFF')
                const root = document.documentElement;
                root.style.setProperty('--text-color-receipts', receiptData.value.branding.balanceCardTextColor)
                // receiptData.components = JSON.parse(receiptData.value.components)
            })
        })
    },
    components: {
        ReceiptBrandCard,
        ReceiptNpsForm,
        ReceiptOffersCarousel,
        ReceiptShopOnlineLink,
        ReceiptTxnHistoryButton,
        ReceiptGetInvoiceForm,
        ReceiptSocialIcons,
        ReceiptStoreCard,
        ReceiptTerms,
        ReceiptVideoEmbed,
        ReceiptCustomerCard,
        ReceiptItemsList,
        ReceiptPoweredBy,
        ReceiptLoyaltyCard,
        ReceiptContactUsCard,
        ReceiptRecommendationCard,
        ReceiptReferralCard
    },
    data() {
        return {
            receiptData: receiptData
        }
    },
    methods: {
        isDarkThemeEnabled(): boolean {
            if (themeConfig.app.theme.value == 'dark')
                return true
            else
                return false
        }
    }
}

</script>

<template>
    <!-- <VContainer style="max-width: 30rem;" v-if="receiptData"
    :class="{ 'custom-dark-card': isDarkThemeEnabled(), 'custom-light-card': !isDarkThemeEnabled() }"> -->
    <VContainer style="max-width: 30rem;" v-if="receiptData" class="pa-1 pa-sm-2"
        :class="isDarkThemeEnabled() ? 'custom-dark-card' : 'custom-light-card'">
        <div v-for="comp in receiptData.components" class="itembox">
            <component :is="comp.name" :data="receiptData" :itemData="comp" />
        </div>
    </VContainer>
</template>

<route lang="yaml">
meta:
  layout: blank
  action: read
  subject: Auth
</route>

<style>
.itembox {
    border-radius: 5px;
    box-shadow: 0 2px 5px 0 #64646459;
}

.custom-light-card {
    background-color: #e2dede;
    color: black;
}

.custom-dark-card {
    background-color: #313d56;
    color: white;
}
</style>
