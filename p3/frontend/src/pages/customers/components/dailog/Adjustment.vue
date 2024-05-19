<script setup lang="ts">

import { ref } from 'vue';
import axios from '@axios';
import { requiredValidator } from '@/@core/utils/validators';
import { isEmpty } from '@/@core/utils';
const balanceType = ref('Points')
const adjustmentType = ref('ADD')
const tierNames = ref([])
const tierIds = ref([])
const tierList = ref([])
const valueCodeList=ref([])
const tierBind = ref('select tier')
const tierAdjustReason = ref('')
const balanceAdjustReason = ref('')
const isbalanceValid=ref(false)
const currentTab = ref('Tier')
const balanceToAdd=ref()
const isTierSelected=ref(false)

interface Props {
    loyalty:{
    listOfTiers: object,
    cardNumber:number
    }
    isDialogVisible: boolean

}
const props = defineProps<Props>()
tierNames.value = Object.keys(props.loyalty.listOfTiers);
tierIds.value = Object.values(props.loyalty.listOfTiers);

const userData= JSON.parse(localStorage.getItem('userData'))

// console.log(userData)
for (let i = 0; i < tierNames.value.length; i++) {
    let obj = {
        title: '',
        value: ''
    }
    obj.title = tierNames.value[i];
    obj.value = tierIds.value[i];
    tierList.value.push(obj);
    //tierBind.value = tierIds.value[i];
}


const Adjustments = [
    { title: 'Points', value: 'Points' },
    { title: 'Amount', value: 'Amount' },
    // {title:'Life time purchase value',value:'LPV'}
]
if(userData){
    valueCodeList.value=userData.valueCodes
    valueCodeList.value.forEach(valueCode=>{
        let obj = {
        title: '',
        value: ''
    }
    obj.title = valueCode;
    obj.value = valueCode;
    // Adjustments.push(obj); //adding valuecodes for balance types dropdown.
    })
}

const actions = [
    { title: 'Add', value: 'ADD' },
    { title: 'Subtract', value: 'SUBTRACT' }
]
interface Emit {
    (e: 'submit', value: ''): void
    (e: 'update:isDialogVisible', value: boolean): void
}

const emit = defineEmits<Emit>()

watch(tierBind, (oldValue,newValue) => {
    console.log(oldValue+'-'+newValue)
    if(oldValue=='select tier')
    isTierSelected.value=true
})

const dialogModelValueUpdate = (val: boolean) => {
  
    emit('update:isDialogVisible', val)
    currentTab.value='Tier'
    balanceToAdd.value=''
    tierBind.value='select tier'
    isTierSelected.value=false
}


async function updateTier(){
 const reqBody={
    cardNumber:props.loyalty.cardNumber,
    tierId:tierBind.value,
    reason:tierAdjustReason.value
 }
   try{
window.location.reload()
    const resp= await axios.post('/api/tier-adjustment',reqBody)
    console.log(resp.data.body)
    
   }catch(err){
    console.log(err)
   }
}

async function updateBalance(){
    const balanceReqBody={
        cardNumber:props.loyalty.cardNumber,
        reason:balanceAdjustReason.value,
        balanceType:balanceType.value,
        adjustmentType:adjustmentType.value,
        value:Number(balanceToAdd.value)
    }
    console.log(balanceReqBody)
    try{
        window.location.reload()
        const resp=await axios.post('/api/balance-adjustment',balanceReqBody)
        console.log(resp)
        
    }catch(err){
        console.log(err)
    }
}
function numberValidator(value:string){
    if (isEmpty(value)){
        isbalanceValid.value=false
        return 'This field is required';
    }
    if(Number(value)==0){
        isbalanceValid.value=false
        return 'Please enter value greater than 0'
    }
     
    let pattern = /^[0-9]+$/;
    const valid=pattern.test(value);
    if(valid){
        isbalanceValid.value=true;
    }
    else {
        isbalanceValid.value=false
        return 'This field must be an Integer'
    }
}

</script>

<template>
    <VDialog :width="$vuetify.display.smAndDown ? 'auto' : 580" :model-value="props.isDialogVisible"
        @update:model-value="dialogModelValueUpdate" persistent>
        <!-- Dialog close btn -->
        <DialogCloseBtn @click="dialogModelValueUpdate(false)" />

        <VCard>
            <template #title>
                <div class="text-center text-h5 font-weight-medium">
                    <p>Loyalty Adjustments</p>
                </div>
            </template>
            <v-tabs v-model="currentTab">
                <v-tab value="Tier">Tier</v-tab>
                <v-tab value="Balance">Balance</v-tab>
                <v-tab value="three">XXYYY</v-tab>
            </v-tabs>

            <!-- <v-card-text> -->
            <v-window v-model="currentTab">
                <v-window-item value="Tier">
                    <VCardText class="pt-6">
                        <VRow>
                            <VCol cols="12" lg="12" md="12" sm="12">
                                <AppSelect v-model="tierBind" label="Tier Adjustment" :items="tierList" class="pt-2">
                                </AppSelect>
                            </VCol>
                        </VRow>
                        <VRow>
                            <VCol>
                                <AppTextarea label="Reason for adjustment" class="pt-2" v-model="tierAdjustReason" />
                            </VCol>
                        </VRow>
                        <VRow>
                            <VCol cols="12" class="text-center">
                                <VBtn class="me-3" @click="updateTier()" >
                                    update
                                </VBtn>
                                <VBtn color="error" variant="tonal" @click="dialogModelValueUpdate(false)">
                                    Cancel
                                </VBtn>
                            </VCol>
                        </VRow>
                    </VCardText>
                </v-window-item>

                <v-window-item value="Balance">
                    <VCardText>
                        <VRow>
                            <VCol>
                                <AppSelect v-model="balanceType" :items="Adjustments" label="Balance Type" class="pt-2">
                                </AppSelect>
                            </VCol>
                            <VCol>
                                <AppSelect v-model="adjustmentType" :items="actions" label="Action" class="pt-2">
                                </AppSelect>
                            </VCol>
                            <VCol>
                                <AppTextField label="Value" v-model="balanceToAdd" class="pt-2" :rules="[numberValidator]"/>
                            </VCol>
                        </VRow>
                        <VRow>
                            <VCol>
                                <AppTextarea label="Reason for adjustment" class="pt-2" v-model="balanceAdjustReason" />
                            </VCol>
                        </VRow>
                        <VCol cols="12" class="text-center">
                            <VBtn class="me-3" @click="updateBalance()" :disabled="!isbalanceValid">
                                update
                            </VBtn>
                            <VBtn color="error" variant="tonal" @click="dialogModelValueUpdate(false)">
                                Cancel
                            </VBtn>
                        </VCol>
                    </VCardText>
                </v-window-item>
                <v-window-item value="three"> Three </v-window-item>
            </v-window>
            <!-- </v-card-text> -->
        </VCard>
    </VDialog>
</template>
