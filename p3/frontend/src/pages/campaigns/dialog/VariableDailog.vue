<script setup lang="ts">
import { isEmpty, mergeTagsMap } from '@/@core/utils/index'
import DiscountCodes from '@/pages/campaigns/sms/components/DiscountCodes.vue'

interface Props {
    isDialogVisible: boolean,
    channelType: string
}
interface Emit {
    (e: 'update:isDialogVisible', val: boolean): void
    (e: 'submitPlaceholder', value: any): void
}
interface Variable {
    mergeTag: string,
    previewPlacholder: string,
    sampleText: string
}
const props = defineProps<Props>()
const color = props.channelType == 'SMS' ? '#f39d40' : '#66C871'
const emit = defineEmits<Emit>()
const placeholder = ref();
const mergeTag = ref()
const mergeTagsList = ref<Object[]>([])
const showDiscodes = ref(false)
const variableObj = ref<Variable>({})
const dialogVisibleUpdate = (val: boolean) => {

    emit('update:isDialogVisible', val)
}
for (const [key, value] of mergeTagsMap.entries()) {
    const obj = { name: '', object: {} }
    obj.name = key
    obj.object = value
    mergeTagsList.value.push(obj)
}
watch(mergeTag, () => {
    if(isEmpty(mergeTag.value)) return;
    console.log(mergeTag.value)
    placeholder.value = mergeTag.value.object.tag;
    variableObj.value.sampleText = mergeTag.value.object.preview
    variableObj.value.previewPlacholder=mergeTag.value.name
    sendPlaceholder();
})
function setDiscountCodes(value: Set<Object>) { //receiving selected discount codes from popup
    const discountCodes = [...value]
    if (discountCodes.length > 0) {
        // console.log(discountCodes)
        placeholder.value = discountCodes[0].tag
        variableObj.value.previewPlacholder=discountCodes[0].name
        // includes ${coupon.CC_ means
        variableObj.value.sampleText = placeholder.value.includes('${coupon.CC_')? 'AFAZEDNG':placeholder.value
        sendPlaceholder()
    }
}
function sendPlaceholder() {
    variableObj.value.mergeTag = placeholder.value
    if(isEmpty(variableObj.value.sampleText)) variableObj.value.sampleText=placeholder.value // when normal text entered in textfield
    if(isEmpty(variableObj.value.previewPlacholder)) variableObj.value.previewPlacholder=placeholder.value
    console.log(variableObj.value)
    emit('submitPlaceholder', variableObj.value)
    placeholder.value=''
    variableObj.value.sampleText=''
    variableObj.value.previewPlacholder=''
    mergeTag.value=''
    dialogVisibleUpdate(false)
}
</script>
<template>
    <div>
        <VDialog :model-value="props.isDialogVisible" :width="$vuetify.display.smAndDown ? 'auto' : 700"
            @update:model-value="dialogVisibleUpdate">
            <!-- 👉 Dialog close btn -->
            <DialogCloseBtn @click="dialogVisibleUpdate(false)" />

            <VCard>
                <VCardTitle>
                    <div class="text-center">Select Placeholder</div>
                </VCardTitle>
                <VRow :class="{ 'ma-2': !$vuetify.display.smAndDown }">


                    <VCol cols="12" lg="4" md="4" sm="12">
                        <AppTextField placeholder="Variable" v-model="placeholder">
                        </AppTextField>
                    </VCol>
                    <VCol cols="12" lg="4" md="4" sm="12">
                        <VBtn :color="color" class="text-subtitle-3" @click="showDiscodes = !showDiscodes">
                            Discount
                            Codes
                        </VBtn>
                    </VCol>
                    <VCol cols=" 12" lg="4" md="4" sm="12">
                        <V-autocomplete v-model="mergeTag" :items="mergeTagsList" :bg-color="color" item-title="name"
                            class="font-weight-medium" placeholder="Merge Tags" return-object />
                    </VCol>

                </VRow>
                <!-- <VRow> -->
                <div class="text-center ma-3">
                    <VBtn :disabled="isEmpty(placeholder)" @click="sendPlaceholder" :color="color">Apply</VBtn>
                </div>
                <!-- </VRow> -->
            </VCard>
        </VDialog>
        <DiscountCodes v-model:is-dialog-visible="showDiscodes" @submitDiscount="setDiscountCodes" :color="color" />
    </div>
</template>
