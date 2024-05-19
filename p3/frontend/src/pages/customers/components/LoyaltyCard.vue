<script setup lang="ts">
import Adjustment from '../components/dailog/Adjustment.vue';
import { getCurrencySymbol } from '@/@core/utils/localeFormatter';
const isAdjustmentDialogVisible = ref(false)
interface Props {
  loyalty: {
    cardNumber: number,
    loyaltyPointBalance: number,
    loyaltyCurrencyBalance: number,
    cummulativePurchaseValue: number,
    totalLoyaltyPointsEarned: number,
    tierLevel: number,
    currentTierName: string,
    currentTierValue: number,
    nextTierName: string,
    tierUpgradeCriteria: string,
    totalLoyaltyRedemption: number,
    holdamountBalance: number,
    holdpointsBalance: number,
    tierUpgradeMileStone: number,
    tierUpgradeValue: number,
    listOfTiers: object
  }
}
const props = defineProps<Props>()
let tierProgressLinearValue = 0;
if (props.loyalty) {
  tierProgressLinearValue = (Number(props.loyalty.currentTierValue) / Number(props.loyalty.tierUpgradeValue)) * 100;
}
const currencySymbol = getCurrencySymbol()
</script>
<template>
  <VCard title='Loyalty' class="mb-5">
    <template #append>
      <VBtn width="10" size="40" @click="isAdjustmentDialogVisible = !isAdjustmentDialogVisible">
        <VIcon icon="tabler-settings" size="30"></VIcon>
      </VBtn>
    </template>
    <VCardText v-if="loyalty">
      <div v-if="loyalty.nextTierName">
        <div>
          <p v-if="loyalty.tierUpgradeCriteria == 'LifetimePoints'"><b>You are {{ loyalty.tierUpgradeMileStone }} points
              away from {{
                loyalty.nextTierName }}!</b></p>
          <p v-else><b>You are {{ currencySymbol }} {{ loyalty.tierUpgradeMileStone }} away from {{
            loyalty.nextTierName }}!</b></p>
        </div>
        <div>
          <p>
            <VRow>
              <VCol variant="elevated" class="text-left">
                <VChip color="success" class="rounded-0">
                  {{ loyalty.currentTierName }}
                </VChip>
              </VCol>

              <VCol variant="elevated" class="text-right">
                <VChip color="primary" class="rounded-0">
                  {{ loyalty.nextTierName }}
                </VChip>
              </VCol>
            </VRow>
          </p>
          <VProgressLinear rounded rounded-bar :model-value="tierProgressLinearValue" height="10" color="primary" />
        </div>
      </div>
      <div v-else>
        <div>
          <p>You are in final tier <b>{{ loyalty.currentTierName }}</b></p>
        </div>
      </div>
      <div class="mt-5">
        <p>Points Balance : {{ loyalty.loyaltyPointBalance ? loyalty.loyaltyPointBalance : 0 }} </p>
        <p class="mt-n3">Points Redeemed : {{ loyalty.totalLoyaltyRedemption ? loyalty.totalLoyaltyRedemption : 0 }} </p>
        <p>Hold Points : {{ loyalty.holdpointsBalance ? loyalty.holdpointsBalance : 0 }} </p>
      </div>
    </VCardText>
    <VCardText v-else>
      <p class=" font-weight-medium text-subtitle-1"> Customer is not a Loyalty member yet !</p>
    </VCardText>
  </VCard>
  <VRow v-if="loyalty">
    <Adjustment v-model:isDialogVisible="isAdjustmentDialogVisible" :loyalty="props.loyalty" />
  </VRow>
</template>

<!--
  * should we develop as per single tier upgrd rule or multiple upgrade rule ? *
  1. Hold Balnce - points or currency?
  2. you are away from xxxx points away from next tier - is based on tier upgrade constraint?
  3. 
-->
