<script setup lang="ts">
interface Props {
    referrals: {
        referralCode: string,
        pointsEarned: number,
        redeemedCount: number,
        refProgram: boolean
    }
}
const props = defineProps<Props>()

</script>
<template>
    <VCard title="Referrals" v-if="referrals.refProgram">
        <VCardText>
            <VCard variant="tonal">
                <VCardText class=" d-flex  justify-space-between pa-0 ">
                    <p class="flex-start align-center mt-3 mb-2 pl-2">Referral Code </p>
                    <VCard color="primary" class="mr-3 my-1 px-2 text-center" width="120">
                        <p class="align-center mt-2 mb-2 font-weight-medium">
                            {{ referrals.referralCode.length > 0 ? referrals.referralCode : '--' }}</p>
                    </VCard>
                </VCardText>
            </VCard>
            <div>
                <div class="d-flex justify-space-between mt-7">
                    <p>Points earned from referrals</p>
                    <p class="text-subtitle1">{{ referrals.pointsEarned }}</p>
                </div>
                <div class="d-flex justify-space-between ">
                    <p>Referrals made</p>
                    <p class="text-subtitle1">{{ referrals.redeemedCount }}</p>
                </div>
            </div>
        </VCardText>
    </VCard>
</template>
