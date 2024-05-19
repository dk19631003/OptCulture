<script setup lang="ts">
import axios from '@axios';
const mobileNumber = ref('');
const contact = ref('');
const moveButton = ref(false);
const isTransactionNotifyVisible = ref(false);
const isTransactionMovedSuccess = ref(false);
const notificationColor = ref('error');
const movedToMobileNumber = ref('');
const isMobileValid = ref(false);
interface Emit {
    (e: 'update:isDialogVisible', value: boolean): void
}

interface Props {
    isDialogVisible: boolean,
    transactionObj: {
        membershipNumber: string,
        receiptNumber: string,
        docSID: string,
        wrongMobile: string
    }
}

const props = defineProps<Props>()
const emit = defineEmits<Emit>()


const dialogModelValueUpdate = (val: boolean) => {
    //clearing data before closing the popup
    mobileNumber.value = '';
    moveButton.value = false;
    contact.value = '';
    isMobileValid.value = false
    emit('update:isDialogVisible', val)
}


// returns the first contact that matches to the given mobile number.
function searchByMobileNumber() {
    const params = {
        "mobileNumber": mobileNumber.value
    }
    axios.get('/api/contacts/contact-by-mobile', {
        params: params
    }).then((resp) => {

        contact.value = resp.data;
    })
    setTimeout(() => {
        moveButton.value = true
    }, 1500)


}

// reassign the transaction for correct mobile number
function updateMobile() {
    const request_body = {
        wrongMembership: props.transactionObj.membershipNumber,
        correctMobile: mobileNumber.value,
        receiptNumber: props.transactionObj.receiptNumber,
        docsId: props.transactionObj.docSID
    }
    movedToMobileNumber.value = mobileNumber.value
    setTimeout(() => {
        dialogModelValueUpdate(false)
    }, 1000);
    axios.post('/api/update-mobile', request_body, {
    }).then((resp) => {
        console.log(resp.data.message);
        if (resp.data.message) {
            transactionMovedNotify(resp.data.message);
            isTransactionNotifyVisible.value = true;
        }
    }).catch(() => {
        isTransactionMovedSuccess.value = false;
        notificationColor.value = 'error'
        isTransactionNotifyVisible.value = true;
    })
}
function transactionMovedNotify(message) {
    if (message == 'updated') {
        isTransactionMovedSuccess.value = true;
        notificationColor.value = 'success'
    }
    else isTransactionMovedSuccess.value = false;


}
function clearMobileNumber() { //to reset  fields
    setTimeout(() => {
        movedToMobileNumber.value = '';
        isTransactionMovedSuccess.value = false;
        isTransactionNotifyVisible.value = false;
        notificationColor.value = 'error'
    }, 6050)

}
function validateMobileNo(mobileNo) {
    if (mobileNo == props.transactionObj.wrongMobile) {
        isMobileValid.value = false
        return 'Same mobile number not allowed'
    }
    if (mobileNo > 0 && mobileNo.length == 10) isMobileValid.value = true
    else isMobileValid.value = false
    return mobileNo > 0 && mobileNo.length == 10 || 'Invalid Mobile Number';
}

</script>

<template>
    <!-- Notification to know whether transaction moved or not -->
    <VSnackbar v-model="isTransactionNotifyVisible" location="top" timeout="5000" :color="notificationColor">
        {{ isTransactionMovedSuccess ? "Transaction moved to mobile number " + movedToMobileNumber : "Transaction moving " +
            "failed"
        }}{{ clearMobileNumber() }}
    </VSnackbar>
    <VDialog :width="$vuetify.display.smAndDown ? 'auto' : 580" :model-value="props.isDialogVisible"
        @update:model-value="dialogModelValueUpdate" :min-height="400">
        <!-- Dialog close btn -->
        <DialogCloseBtn @click="dialogModelValueUpdate(false)" />

        <VCard class="pa-5 pa-sm-8">
            <!-- 👉 Title -->
            <VCardItem class="text-center">
                <VCardTitle class="text-h6 font-weight-medium ">
                    Reassign Receipt to different Customer
                </VCardTitle>
            </VCardItem>

            <VCardText class="pt-6 ma-1">
                <p>Mobile Number</p>
                <VRow>
                    <AppTextField v-model="mobileNumber" class="mb-3" maxlength="10" minlength="10"
                        :rules="[validateMobileNo]" />
                    <VBtn type="submit" @click="searchByMobileNumber" class="ml-2" :disabled="!isMobileValid">
                        Search
                    </VBtn>
                </VRow>
                <VList v-if="contact" class="text-left ">
                    <VListItem>
                        <span>Name :</span><span class="font-weight-medium">{{ contact.firstName }} {{ contact.lastName
                        }}</span>
                    </VListItem>
                    <VListItem>
                        <span>Contact :</span><span class="font-weight-medium">{{ contact.mobilePhone }} </span>
                    </VListItem>
                    <VListItem>
                        <span>EmailId :</span><span class="font-weight-medium">{{ contact.emailId }} </span>
                    </VListItem>
                    <VListItem>
                        <span> Membershipnumber :</span><span class="font-weight-medium">{{ contact.membershipNumber
                        }}</span>
                    </VListItem>
                </VList>
                <VRow>
                    <!-- <span v-if="!contact && show">No Contact available</span> -->
                </VRow>
                <div>
                    <div></div>
                    <VSpacer />
                    <VBtn class="mt-5 ml-20 text-end" v-show="moveButton" @click="updateMobile()">
                        {{ contact ? 'Move' : 'Create contact and Move' }}
                    </VBtn>
                </div>
            </VCardText>
        </VCard>
    </VDialog>
</template>
