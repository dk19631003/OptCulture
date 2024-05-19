<script setup lang="ts">
import type { EreceiptType } from '@/@fake-db/types'
import { themeConfig } from '@themeConfig'

interface Props {
    data?: EreceiptType
}

const props = withDefaults(defineProps<Props>(), {
    data: {
        customer: {
            "firstName": "First Name",
            "lastName": "Last Name",
            "mobilePhone": "",
            "emailId": "name@email.id"
        },
        loyalty: {
            "cardNumber": "",
            "loyaltyPointBalance": "-",
            "loyaltyCurrencyBalance": "-",
            "cummulativePurchaseValue": 0.0,
            "totalLoyaltyPointsEarned": 0,
            "tierLevel": 0,
            "currentTierName": "",
            "currentTierValue": 0,
            "nextTierName": "",
            "tierUpgradeCriteria": 0
        }
    }
});

let tierProgressLinearValue = 0;
let requiredPointsORCurrency = 0;
if (props.data?.loyalty) {

    if (props.data?.loyalty.tierUpgradeCriteria === "LifetimePoints") {
        tierProgressLinearValue = (Number(props.data?.loyalty.totalLoyaltyPointsEarned) / Number(props.data?.loyalty.tierUpgradeValue)) * 100;
        requiredPointsORCurrency = props.data?.loyalty.tierUpgradeValue - props.data?.loyalty.totalLoyaltyPointsEarned;
    }
    else {
        tierProgressLinearValue = (Number(props.data?.loyalty.totalGiftcardAmount) / Number(props.data?.loyalty.tierUpgradeValue)) * 100;
        requiredPointsORCurrency = props.data?.loyalty.tierUpgradeValue - props.data?.loyalty.totalGiftcardAmount;
    }
}

function isDarkThemeEnabled(): boolean {
    if (themeConfig.app.theme.value == 'dark')
        return true
    else
        return false
}
</script>

<template>
    <VCard class="my-2" :class="{ 'custom-light-card': isDarkThemeEnabled(), 'custom-dark-card': !isDarkThemeEnabled() }"
        v-if="data.loyalty && (data.loyalty.loyaltyPointBalance || data.loyalty.loyaltyCurrencyBalance)" elevation="0">
        <VCardText class="pa-4">
            <VRow>
                <VCol>
                    <p class="text-left wr-text-h1 text-white">Loyalty Information</p>
                </VCol>
            </VRow>
            <VCard style="box-shadow: 0px 2px 6px 2px rgba(0, 0, 0, 0.15), 0px 1px 2px 0px rgba(0, 0, 0, 0.30);">
                <VCard v-if="data.loyalty.loyaltyPointBalance != null" class="ma-2"
                    style="box-shadow: 0px 2px 6px 2px rgba(0, 0, 0, 0.15), 0px 1px 2px 0px rgba(0, 0, 0, 0.30);">
                    <VRow>
                        <VCol cols="7" class="text-left wr-text-h2 ma-2">
                            Your Points Balance</VCol>
                        <VCol class="text-right wr-text-h2 ma-2"> {{ data.loyalty.loyaltyPointBalance }}
                        </VCol>
                    </VRow>
                </VCard>
                <VCard v-if="data.loyalty.loyaltyCurrencyBalance != null" class="ma-2"
                    style="box-shadow: 0px 2px 6px 2px rgba(0, 0, 0, 0.15), 0px 1px 2px 0px rgba(0, 0, 0, 0.30);">
                    <VRow>
                        <VCol cols="7" class="text-left wr-text-h2 ma-2">
                            Your Currency Balance</VCol>
                        <VCol class="text-right wr-text-h2 ma-2"> {{ data.loyalty.loyaltyCurrencyBalance }}
                        </VCol>
                    </VRow>
                </VCard>

                <!--
                <VCardText class="align-center">
                    <div v-if="data.loyalty.currentTierName && data.loyalty.nextTierName">
                        <div>
                            <p class="text-left wr-text-h2" v-if="data.loyalty.nextTierName">
                                You are {{ requiredPointsORCurrency }}
                                points away from {{ data.loyalty.nextTierName }}
                            </p>
                        </div>

                        <VRow>
                            <VCol class="text-left">{{ data.loyalty.previousTierUpgradedValue }}</VCol>
                            <VCol class="text-right">{{ data.loyalty.tierUpgradeValue }}</VCol>
                        </VRow>
                        <VProgressLinear rounded rounded-bar :model-value="tierProgressLinearValue" height="10"
                            color="primary" />
                        <p>
                            <VRow>
                                <VCol class="text-left">{{ data.loyalty.currentTierName }}</VCol>

                                <VCol class="text-right">{{ data.loyalty.nextTierName }}</VCol>
                            </VRow>
                        </p>
                    </div>
                </VCardText>-->
            </VCard>
        </VCardText>
    </VCard>
</template>

<style lang="scss" scoped>
.custom-light-card {
    background-color: white;
    color: black;
}

.custom-dark-card {
    background-color: #1A1A1A;
    color: white;
}
</style>
