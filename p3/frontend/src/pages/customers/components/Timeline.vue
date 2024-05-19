<script setup lang="ts">
import { avatarText } from '@/@core/utils/formatters'
import { VDataTableServer } from 'vuetify/labs/VDataTable'
import ReAssignToNewCustomer from '@/pages/customers/components/ReAssignToNewCustomer.vue';
import { kFormatter } from '@/@core/utils/formatters'
import axios from '@axios';
import { isEmpty } from '@/@core/utils'
import { getCurrencySymbol } from '@/@core/utils/localeFormatter';
import { dateFormater } from '@/@core/utils/localeFormatter';
import ReceiptItemsPopup from './dailog/ReceiptItemsPopup.vue';

interface Props {
    contact: {
        loyalty:{
        cardNumber: string,
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
        mobilePhone: string,
        cid: string,
        loyaltyId: string
    }
    
    
}

const props = defineProps<Props>();
const currencySymbol = getCurrencySymbol();
const listOfTiers = props.contact.loyalty?.listOfTiers;
let fromDate = ref('');
let toDate = ref('')
const transactionType = ref('TRANSACTIONS')
const PageCount = ref(0);
const totalTimelineItems = ref(0)
const isReAssigntoNew = ref(false)
const itemsPerPage = ref(10)
const pageNumber = ref(1)
const isReceiptVisible = ref(false)
const ereceiptData = ref({})
const loading = ref(false)
//props for reassign the transaction
const transactionObjForProps = ref({
    'membershipNumber': '',
    'receiptNumber': '',
    'docSID': '',
    'wrongMobile': ''
})
const loyaltyId = ref('')
const timelineItems = ref([])

loyaltyId.value = props.contact?.loyaltyId;
// console.log(membershipNumber.value)
//preparing props for reassign to new customer
const reassignToNew = (item: any) => {
    transactionObjForProps.value.membershipNumber = props.contact?.loyalty.cardNumber
    transactionObjForProps.value.receiptNumber = item.raw.receiptNumber
    transactionObjForProps.value.docSID = item.raw.docSID;
    transactionObjForProps.value.wrongMobile = props.contact.mobilePhone;
    isReAssigntoNew.value = !isReAssigntoNew.value
};
//docsid for reassigning the transactions.

//to decide transaction name-(purchase/On enroll/adjustments)
function getNameOfTransaction(receiptNumber: string, typeOfTransaction: string) {
    if (receiptNumber != null) {
        if (receiptNumber == 'On Enroll') return 'On Enroll'
        return 'Purchase'
    }
    return typeOfTransaction;
}

const transactionTypes = [

    { title: 'Transactions', value: 'TRANSACTIONS' },
    // { title: 'Communication', value: 'COMMUNICATION' },
    // { title: 'All', value: 'ALL' }
]
const table_headers = [
    { title: 'TYPE', key: 'name', sortable: false },
    { title: 'NOTES', key: 'transactions', sortable: false },
    { title: 'Actions', key: 'actions', sortable: false },
]

//preapre date for send to backend
function getDate(selectedDate: string, val: boolean) { //parsing to isostring
    if (selectedDate == '') return null;
    const specificDate = new Date(selectedDate);
    if (val) {
        specificDate.setDate(specificDate.getDate() + 1); //adding one more day to date (to get the transactions of that present day)
    }
    return specificDate.toISOString();
}
//loads transactions on page load
function loadTimelineItems() {
    // if (membershipNumber.value) {
        loading.value = true;
    const params = {
        "loyaltyId": loyaltyId.value ? loyaltyId.value : '--',
        "fromDate": getDate(fromDate.value, false),
        "toDate": getDate(toDate.value, true),
        "transactionType": transactionType.value,
        "pageNumber": pageNumber.value - 1,
        "pageSize": itemsPerPage.value == -1 ? totalTimelineItems.value : itemsPerPage.value,
        "cid": props.contact.cid
    }
    axios.get('/api/loyalty-transactions/', {
        params: params,
    })
        .then((resp) => {
            loading.value = false;
            console.log(resp);
            timelineItems.value = resp.data.object;
            totalTimelineItems.value = resp.data.totalItems
            PageCount.value = Math.ceil(totalTimelineItems.value / itemsPerPage.value)
        })
    // }

}
//loads popup with list of items purchased  with that receipt number
async function loadReceipt(item: any) {
    if (isEmpty(item.raw.receiptNumber) && isEmpty(item.raw.docSID)) return
    const params = {
        "receiptNumber": isEmpty(item.raw.receiptNumber) ? '--' : item.raw.receiptNumber,
        "docSid": isEmpty(item.raw.docSID) ? '--' : item.raw.docSID
    }
    try {
        console.log(params)
        const resp = await axios.get('/api/ereceipt/items-list', {
            params: params
        })
        ereceiptData.value = resp.data
        // console.log(ereceiptData.value)
        if (ereceiptData.value.lineItem) //checking if items exist
            isReceiptVisible.value = true;
    } catch (err) {
        console.log(err)
    }

}

function getEreceiptLink(item: any) {
    if (!isEmpty(item.raw.docSID)) {
        const params = {
            "docSid": item.raw.docSID
        }
        axios.get('/api/loyalty-transactions/ereceipt', {
            params: params,
        })
            .then((resp) => {
                console.log(resp.data)
                if (resp.data == 'Ereceipt Link Not Found') {
                    loadReceipt(item)
                }
                else window.open(resp.data, '_blank') //opening ereceipt_link new tab
            })
    }
    else {
        loadReceipt(item)
    }
}

//isPurchase
function isPurchase(itemTransactionType: string) {
    return itemTransactionType != 'On Enroll' ? true : false;
}
//decide to show amount or balance
function showAmount(transaction: any) {
    let amount = transaction.amount;
    let points = transaction.points;
    let typeOfTransaction = transaction.transactionType;
    if (typeOfTransaction in transactionColor) {
        if (amount != null && Number(amount) > 0) return '+' + kFormatter(amount) + currencySymbol;
        return '+' + kFormatter(points.split(' ')[0]); // might be chance of rewards so spliting upto number ex.10 Diamonds
    }
    else {
        if (amount != null && amount != 0) {
            return '-' + kFormatter(amount) + currencySymbol;
        }
        return '-' + kFormatter(points.split(' ')[0]);
    }
}


function getTierName(value: string) {
    const tierName = Object.keys(listOfTiers).filter(key => {
        return listOfTiers[key] == Number(value)
    })
    return tierName.length == 0 ? props.contact.loyalty.currentTierName : tierName[0];
}
watch(pageNumber, () => {
    loadTimelineItems()
})
watch(itemsPerPage, () => { //

    PageCount.value = Math.ceil(totalTimelineItems.value / itemsPerPage.value);
    // loadTimelineItems();
})
watch(PageCount, (newPageCount, oldPageCount) => {
    if (oldPageCount == 0 || newPageCount == 0) {
        return
    }
    else if (pageNumber.value == 1) {
        loadTimelineItems()
    }
    else pageNumber.value = 1
})

const transactionColor = {

    'Issuance': 'success',
    'Reward': 'success',
    'RedemptionReversal': 'success',
    'Bonus': 'success',
    'Add': 'success',
    'Total Amount':'success'
}
loadTimelineItems()
</script>
<template>
    <VCard height="auto" :class="{ 'custom-max-height': $vuetify.display.lgAndUp }" class="scroll">
        <template #title>
            Timeline
        </template>
        <VRow>
            <VCol cols="12" lg="3" md="3" sm="12">
                <AppDateTimePicker v-model="fromDate" placeholder="From" class="ml-3"></AppDateTimePicker>
            </VCol>
            <VCol cols="12" lg="3" md="3" sm="12">
                <AppDateTimePicker v-model="toDate" placeholder="To" class="ml-3"></AppDateTimePicker>
            </VCol>
            <VCol cols="12" lg="3" md="3" sm="12">
                <AppSelect v-model="transactionType" :items="transactionTypes" placeholder="transaction type" clearable
                    class="ml-3">
                </AppSelect>
            </VCol>
            <VCol cols="12" md="2" lg="3" sm="12">
                <VBtn variant="tonal" @click="loadTimelineItems" color="primary" prepend-icon="tabler-filter-search"
                    class="ml-3">Filter
                </VBtn>
            </VCol>
        </VRow>
        <VDataTableServer :headers="table_headers" :items-length="totalTimelineItems" :items="timelineItems"
            class="elevation-1" item-value="name" :loading="loading">
            <template v-slot:loading>
                <div class="text-center">
                    Loading.... Please wait.
                </div>
            </template>
            <template #item.name="{ item }">
                <div class="d-flex align-center  ">
                    <div class="mr-3">
                        <VAvatar :size="35" color="primary" variant="tonal">
                            {{ avatarText(getNameOfTransaction(item.raw.receiptNumber, item.raw.transactionType)) }}
                        </VAvatar>
                    </div>
                    <div>
                        <p class="text-subtitle-1 mt-1 font-weight-medium">
                            {{
                                getNameOfTransaction(item.raw.receiptNumber, item.raw.transactionType) }}</p>
                        <p class="mt-n5 text-body2 font-weight-light" v-if="isPurchase(item.raw.receiptNumber)">{{
                            item.raw.receiptNumber }}</p>
                        <p class="mt-n5 text-body2 font-weight-light">{{ dateFormater(item.raw.createdDate) }}</p>
                    </div>
                </div>
            </template>
            <template #item.transactions="{ item }">
                <VList>
                    <VListItem v-if="item.raw.transactionType.includes('Tier')" class="pl-0"
                        v-for="(transaction, index) in  item.raw.transactions " :key="index">
                        <div class="d-flex justify-space-between">
                            <span>{{ transaction.transactionType }}</span>
                            <VBtn color="success" height="23" disabled="true" class="text-wrap">{{ getTierName(transaction.tierId) }}
                            </VBtn>
                        </div>
                    </VListItem>
                    <VListItem v-else v-for="(transaction, index) in  item.raw.transactions " :key="transaction"  class="pl-0">
                        <div class="d-flex justify-space-between ">
                            <span class="align-self-start">{{ transaction.transactionType }}</span>
                            <VBtn
                                :color="transaction.transactionType in transactionColor ? transactionColor[transaction.transactionType] : 'error'"
                                class="text-center justify-end px-2" height="23" disabled="true">
                                <span>{{ showAmount(transaction) }}</span>
                            </VBtn>
                        </div>
                    </VListItem>
                </VList>
            </template>
            <template #item.actions="{ item }">
                <IconBtn class="contact-list-name" @click="loadReceipt(item)">
                    <VIcon icon="tabler-eye" />
                </IconBtn>

                <VBtn icon variant="text" size="small" color="medium-emphasis">
                    <VIcon size="24" icon="tabler-dots-vertical" />

                    <VMenu activator="parent">
                        <VList>
                            <VListItem @click="reassignToNew(item)">
                                <template #prepend>
                                    <VIcon icon="tabler-transfer" />
                                </template>

                                <VListItemTitle>Transfer </VListItemTitle>
                            </VListItem>
                        </VList>
                    </VMenu>
                </VBtn>
            </template>
            <template #bottom>
                <VDivider />
                <VRow class="text-center">
                    <VCol cols="12" lg="5" md="5" sm="5">
                        <div class="d-flex justify-space-around">
                            <span class="mx-3 mt-4">Items per Page</span>
                            <AppSelect class="mt-2" v-model="itemsPerPage" :items="[
                                { value: 10, title: '10' },
                                { value: 25, title: '25' },
                                { value: 50, title: '50' },
                                { value: 100, title: '100' },
                                { value: -1, title: 'All' },
                            ]" style="width: 6.25rem;" />
                        </div>
                    </VCol>
                    <VSpacer />
                    <VCol>
                        <VPagination class="mt-1" v-model="pageNumber" :length="PageCount"
                            :total-visible="$vuetify.display.xs ? 1 : Math.ceil(totalTimelineItems / itemsPerPage)">
                        </VPagination>
                    </VCol>
                </VRow>
            </template>
        </VDataTableServer>
        <VCardText>
            <VRow>
                <!-- Dialog boxes -->
                <div>
                    <ReAssignToNewCustomer v-model:isDialogVisible="isReAssigntoNew"
                        :transactionObj="transactionObjForProps" />
                </div>
                <div>
                    <ReceiptItemsPopup v-model:is-dialog-visible="isReceiptVisible" :data="ereceiptData" />
                </div>
            </VRow>
        </VCardText>
    </VCard>
</template>
<style lang="scss" scoped>
.contact-list-name:not(:hover) {
    color: rgba(var(--v-theme-on-background), var(--v-medium-emphasis-opacity));
}

.scroll {
    overflow-y: auto;
}

.custom-max-height {
    max-height: 437px;
    /* Max height for large breakpoints */
}
</style>
