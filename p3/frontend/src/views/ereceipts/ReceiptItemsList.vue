<script setup lang="ts">
import type { EreceiptType } from '@/@fake-db/types'

interface Props {
  data?: EreceiptType
  itemData: { "id": 0, "name": "", "configuration": "" },
}

const props = withDefaults(defineProps<Props>(), {
  data: {
    lineItem: [{
      skuInventory: {
        "description": "NA",
        "listPrice": "0",
        "departmentCode": "ItemName",
        "itemCategory": "ItemName"
      },
      "quantity": "0"

    }
    ],
    receipt: {
      "cashier": "CashierName",
      "invcNum": "InvoiceNo",
      "totaldiscount": "0",
      "total": "0",
      "subtotal": "0",
      "docSID": "--",
      "totalTax": "0",
      "salesDate": "--",
      "receiptNumber": "--"
    },
    // tax: {
    //   "cGstRate": "NA",
    //   "cGstAmt": "NA",
    //   "sGstRate": "NA",
    //   "sGstAmt": "NA",
    // },
    tender: {
      "amount": "NA",
      "type": "NA"
    },
  },
  itemData: { "id": 0, "name": "", "configuration": "" },
});
let itemQty = 0;
for (let i = 0; i < props.data?.lineItem.length; i++) {
  let tempQty = Number(props.data?.lineItem[i].quantity);
  itemQty = Number(itemQty) + Number(tempQty > 0 ? tempQty : -(tempQty));
}
const hasDecimalsInQty = itemQty % 1 !== 0;
itemQty = hasDecimalsInQty ? Number(itemQty.toFixed(2)) : itemQty;

let totalAmountPaid = Number(props.data?.receipt.total).toFixed(2);

// let buttonText = 'Show Details'
// const showAll = ref(false)
// function changeButtonName() {
//   showAll.value = !(showAll.value)
//   if (buttonText == 'Show Details')
//     buttonText = 'Hide Details'
//   else
//     buttonText = 'Show Details'
// }
function dateFormat(dateString) {
  if (dateString == null) return '-'
  const date = new Date(dateString);
  console.log(date)
  const day = date.getDate()
  const month = date.getMonth() + 1;
  const year = date.getFullYear();
  const hours = date.getHours();
  const minutes = date.getMinutes();
  const seconds = date.getSeconds();

  const formattedDate = `${day}-${month}-${year} ${hours}:${minutes}:${seconds}`;

  return formattedDate;
}

function formatDate(date) {
  const dateObject = new Date(date);
  return dateObject.toLocaleString();
}
</script>
<template>
  <VCard class="my-2" elevation="0">
    <VCardText>
      <VRow>
        <VCol class="wr-text-h2 text-center">
          <span class="wr-text-h2">Invoice No:</span> &nbsp; {{ data.receipt.receiptNumber ?? '--' }} <br>
          <span class="wr-text-h2"><b>Date:</b></span> &nbsp;
          <span class="wr-text-h2"> {{ formatDate(data.receipt.salesDate) ?? '--' }}
          </span>
        </VCol>
      </VRow><br>
      <div>
        <div class="" v-for="(item, index) in data.lineItem" :key="index">
          <div class="row-box">
            <div class="d-flex justify-space-between">

              <div class="d-flex justify-start pl-2 align-center" style="overflow: hidden;">
                <div class="ml-1">
                  <p class="wr-text-h2 mb-0"> {{ index + 1 }}.&nbsp; </p>
                </div>
                <div class="mx-2" style="overflow: hidden;">
                  <p class="wr-text-h2 mb-0 posistion-absolute"> {{ item.skuInventory[itemData.configuration.itemName] ?
            item.skuInventory[itemData.configuration.itemName] : item.skuInventory.departmentCode }}
                    x&nbsp;{{ item.quantity }}</p>
                </div>
                <!-- <VIcon size=20 icon="tabler-share" class="rounded" /> -->
              </div>
              <div class="d-flex justify-start align-center pl-2">&#8377;&nbsp;
                <div class="pr-2 wr-text-h2 posistion-absolute">
                  {{ (item.skuInventory.listPrice * item.quantity).toFixed(2) }}
                </div>
                <!-- <VBtn size=40 class="rounded" :icon="item.show ? 'tabler-chevron-up' : 'tabler-chevron-down'"
                  @click="item.show = !item.show"></VBtn> -->
                <VIcon size="30" :icon="item.show ? 'tabler-chevron-up' : 'tabler-chevron-down'"
                  @click="item.show = !item.show" class="mr-1"></VIcon>
              </div>
            </div>

            <div class="details-view" v-show="item.show">
              <div class="justify-start">
                <p class="wr-text-h3 mb-0">Item Price</p>
                <p class="wr-text-h3 mb-0">Quantity</p>
                <p class="wr-text-h3 mb-0"> Bar code</p>
                <p class="wr-text-h3 mb-0"> Discount</p>
                <p class="wr-text-h3 mb-0" v-if="item.tax && item.tax > 0"> Total Tax</p>
              </div>
              <div class="text-right">
                <p class="wr-text-h3 mb-0">{{ item.skuInventory.listPrice ?? '--' }}</p>
                <p class="wr-text-h3 mb-0">{{ item.quantity ?? '--' }}</p>
                <p class="wr-text-h3 mb-0">{{ item.itemSid ?? '--' }}</p>
                <p class="wr-text-h3 mb-0"> {{ item.discount ?? '--' }}</p>
                <p class="wr-text-h3 mb-0" v-if="item.tax && item.tax > 0"> {{ item.tax ?? '--' }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </VCardText>
    <VCardText class="text-center">
      <!--<div>
        <VRow>
          <VCol class="wr-text-h1">AMOUNT BREAKUP</VCol>
        </VRow>
        <div class="d-flex justify-space-between pa-2">
          <div>
            <p class="wr-text-h2 d-flex mb-0">Total Amount</p>
            <p class="wr-text-h2 d-flex mb-0">Bill Discount</p>
            <p class="wr-text-h2 d-flex mb-0">Advance Received</p>
            <p class="wr-text-h2 d-flex mb-0">Amount Payable</p>
            <p class="wr-text-h2 d-flex mb-0">Total Saving</p> 
          </div>
          <div>
            <p class="wr-text-h2 d-flex justify-end mb-0">{{ data.receipt.total ?? '--' }}</p>
            <p class="wr-text-h2 d-flex justify-end mb-0">{{ data.receipt.totalDiscount ?? '--' }}</p>
             <p class="wr-text-h2 d-flex justify-end mb-0">0.00</p>
            <p class="wr-text-h2 d-flex justify-end mb-0">0.00</p>
            <p class="wr-text-h2 d-flex justify-end mb-0">0.00</p> 
          </div>
        </div>
        <div class="dashed-line"></div>
        <VRow>
          <VCol class="wr-text-h1">TENDER</VCol>
        </VRow>
        <div class="d-flex justify-space-between pa-2">
          <div>
            <p class="wr-text-h2 d-flex mb-0"> UPI Transaction</p>
            <p class="wr-text-h2 d-flex mb-0">Credit card</p>
            <p class="wr-text-h2 d-flex mb-0">Credit Note</p>
            <p class="wr-text-h2 d-flex mb-0">TataCliq</p> 
            <p class="wr-text-h2 d-flex mb-0">Cash</p>
          </div>
          <div>
             <p class="wr-text-h2 d-flex justify-end mb-0">0.00</p>
            <p class="wr-text-h2 d-flex justify-end mb-0">0.00</p>
            <p class="wr-text-h2 d-flex justify-end mb-0">0.00</p>
            <p class="wr-text-h2 d-flex justify-end mb-0">0.00</p> 
            <p class="wr-text-h2 d-flex justify-end mb-0">{{ data.tender.amount ?? '--' }}</p>
          </div>
        </div>
        <div class="dashed-line"></div>
        <VRow>
          <VCol class="wr-text-h1">TAX SUMMARY</VCol>
        </VRow>
        <div class="d-flex justify-space-between pa-2">
          <div>
            <p class="wr-text-h2 d-flex mb-0"><b>TAX</b></p>
            <p class="wr-text-h2 d-flex mb-0">CGST</p>
            <p class="wr-text-h2 d-flex mb-0">SGST</p>
          </div>
          <div>
            <p class="wr-text-h2 mb-0"><b>Tax rate%</b></p>
            <p class="wr-text-h2 mb-0">{{ data.tax.cGstRate ?? '--' }}</p>
            <p class="wr-text-h2 mb-0">{{ data.tax.sGstRate ?? '--' }}</p>
          </div>
          <div>
            <p class="wr-text-h2 d-flex justify-end mb-0"><b>Tax amount</b></p>
            <p class="wr-text-h2 d-flex justify-end mb-0">{{ data.tax.cGstAmt ?? '--' }}</p>
            <p class="wr-text-h2 d-flex justify-end mb-0">{{ data.tax.sGstAmt ?? '--' }}</p>
          </div>
        </div>
        <div class="dashed-line"></div>
      </div>-->
      <div>
        <VRow>
          <VCol class="wr-text-h1">Summary</VCol>
        </VRow>
        <div class="d-flex justify-space-between pa-2">
          <div>
            <!-- <p class="wr-text-h2 d-flex mb-0">CashierName</p> -->
            <p class="wr-text-h2 d-flex mb-0">Sub Total</p>
            <p class="wr-text-h2 d-flex mb-0">Total discount</p>
            <p class="wr-text-h2 d-flex mb-0" v-if="data.receipt.totalTax && data.receipt.totalTax > 0">Total tax</p>
            <!-- <p class="wr-text-h2 d-flex mb-0">Transition id</p> -->
          </div>
          <div>
            <!-- <p class="wr-text-h2 d-flex justify-end mb-0">NA</p> -->
            <p class="wr-text-h2 d-flex justify-end mb-0">{{ data.receipt.subTotal ?? '--' }}</p>
            <p class="wr-text-h2 d-flex justify-end mb-0">{{ data.receipt.totalDiscount ?? '--' }}</p>
            <p class="wr-text-h2 d-flex justify-end mb-0" v-if="data.receipt.totalTax && data.receipt.totalTax > 0">{{
            data.receipt.totalTax ?? '--' }}</p>
            <!-- <p class="wr-text-h2 d-flex justify-end mb-0">{{ data.receipt.docSid ?? '--' }}</p> -->
          </div>
        </div>
      </div>

      <div class="dashed-line"></div>
      <div class="mt-2">
        <div class="d-flex justify-space-between mb-0">
          <p class="wr-text-h3">{{ data.receipt.salesDate ? dateFormat(data.receipt.salesDate) : '--' }}</p>
          <p class="wr-text-h3">Total amount paid</p>
        </div>
        <div class="d-flex justify-space-between mt-n3">
          <p class="wr-text-h2 "> {{ itemQty }} item(s)</p>
          <p class="wr-text-h2"> &#8377; {{ totalAmountPaid }}</p>
        </div>
      </div>

      <!-- <V-btn @click="changeButtonName">{{ buttonText }}</V-btn> -->
    </VCardText>
  </VCard>
</template>

<style scoped>
.details-view {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-radius: 4px;
}

.row-box {
  justify-content: space-between;
  align-content: center;
  border-radius: 4px;
  /* box-shadow: 0px 2px 3px 0px #64646459; */
  margin-bottom: 6px;
  padding: 4px;
  /* border: 1px solid grey; */
  box-shadow: 3px 3px 10px 0 rgba(0, 0, 0, 0.5);
}
</style>
